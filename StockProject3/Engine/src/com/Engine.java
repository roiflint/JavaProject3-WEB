package com;

import generated.*;
import javafx.collections.FXCollections;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Engine implements EngineInter {
    private final StockDB DB;
    private final Map<String, Stock> stocks;
    private final Map<String, User> Users;


    public Engine()
    {
        this.DB = new StockDB();
        this.stocks = new HashMap<>();
        this.Users = new HashMap<>();
    }
    public List<StocksDTO> GetAllStocksInfo()
    {
        List<StocksDTO> dto = new LinkedList<>();
        for (Stock s: this.stocks.values())
        {
            dto.add(s.getDto(0));
        }
        return dto;
    }

    public int getStockPrice(String symbol) {
        if (!IsStockExist(symbol.toUpperCase()))    //check if the stock exist
        {
            throw new InvalidParameterException("Stock does not exist in the system, please enter a different stock");
        }
        return stocks.get(symbol.toUpperCase()).getPrice();     //return the stock price
    }

    public StocksDTO getSingleStockInfo(String StockName, String username, boolean admin) {
        Stock s = this.stocks.get(StockName.toUpperCase());
        if (s == null) {
            return null;
        }
        if(admin){
            return s.getDto(0);  //return a generated dto that represent the stock information
        }
        int holdings = this.Users.get(username).getStockHoldings(StockName);
        return s.getDto(holdings);
    }

    public synchronized ErrorDto LoadXML(InputStream inputStream, String username) throws Exception {
        try {
            if (!isUserExist(username)){
                return new ErrorDto(false, "User does not exist in the system");
            }
            //InputStream inputStream = new FileInputStream((new File(FileName)));
            RizpaStockExchangeDescriptor items = ItemsdeserializeFrom(inputStream);
            RseStocks stocks = items.getRseStocks();
            RseHoldings holdings = items.getRseHoldings();
            Map<String,Stock> stocksOnFile = new HashMap<>();
            Map<String,Integer> stockHoldings = new HashMap<>();
            for (RseStock o:stocks.getRseStock()){    //go over all the stocks in the file
                Stock s = new Stock(o);
                for (Stock stock:stocksOnFile.values())      //check if a stock already exist in the file
                {
                    if (stock.equals(s))        /////error multiple stocks
                    {
                        stocksOnFile.clear();
                        stockHoldings.clear();
                        return new ErrorDto(false,"Duplicate Stocks in file, load a new XML");
                    }
                }
               stocksOnFile.put(s.getSymbol().toUpperCase(),s);     //add stock to tmp stocks
            }

            for (RseItem o:holdings.getRseItem()){          //go over all users in file
                if (!stocksOnFile.containsKey(o.getSymbol().toUpperCase())) {       //check holding does not exist on file
                    stocksOnFile.clear();
                    stockHoldings.clear();
                    return new ErrorDto(false, "Stock holding does not exist on file");
                }
                stockHoldings.put(o.getSymbol().toUpperCase(),o.getQuantity());
            }
            for (Stock s:stocksOnFile.values()){        //insert new stocks to system
                if (!this.stocks.containsKey(s.getSymbol())){
                    this.stocks.put(s.getSymbol(),s);
                }
            }
            this.Users.get(username).addStocksFromXml(stockHoldings);
            return new ErrorDto(true,"File loaded successfully");
        } catch (JAXBException | InvalidParameterException e) {
            System.out.println(e.getMessage());
            return new ErrorDto(false,e.getMessage());
        }
    }

    private synchronized static RizpaStockExchangeDescriptor ItemsdeserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance("generated");
        Unmarshaller u = jc.createUnmarshaller();
        return (RizpaStockExchangeDescriptor) u.unmarshal(in);
    }


    public List<TransactionDTO> GetBuyList(String Symbol){return this.DB.GetBuyTransactions(Symbol); }

    public List<TransactionDTO> GetSellList(String Symbol){ return this.DB.GetSellTransactions(Symbol);}

    public List<TransactionDTO> GetPreviousList(String Symbol){return this.DB.GetPreviousTransactions(Symbol); }

    public List<TransactionDTO> MakeTransaction(Transaction transaction, boolean buy) {
        List<TransactionDTO> dto = new ArrayList<>();
        if (!IsStockExist(transaction.getSymbol().toUpperCase())) {
            dto.add(new TransactionDTO(false, "The stock name is not in the system, please enter a different stock"));
            return dto;
        }
        if (transaction.getCost() < 1) {

            dto.add(new TransactionDTO(false, "Enter a positive price only"));
            return dto;
        }
        if (!transaction.getBuyer().equals("") && !this.Users.containsKey(transaction.getBuyer())) {
            dto.add(new TransactionDTO(false, "Buyer does not exist in the system, please enter a different buyer"));
            return dto;
        }
        if (!transaction.getSeller().equals("") && !this.Users.containsKey(transaction.getSeller())){
            dto.add(new TransactionDTO(false, "Seller does not exist in the system, please enter a different buyer"));
            return dto;
        }
        if (!transaction.getSeller().equals("") && this.Users.get(transaction.getSeller()).getUserStocks().get(transaction.getSymbol()) < transaction.getQuantity()){
            dto.add(new TransactionDTO(false,"Seller does not have enough stocks to sell"));
            return dto;
        }
        if (transaction.getQuantity() < 1) {

            dto.add(new TransactionDTO(false, "Number of stocks must be positive"));
            return dto;
        }
        if (buy){
            if (transaction.getAction().equalsIgnoreCase("fok")){
                if (checkFok(transaction,true)){
                    return BUY(transaction);
                }
                else{
                    dto.add(new TransactionDTO(false,"Could not complete the FOK action"));
                    return dto;
                }
            }
            else{
                if(transaction.getAction().equalsIgnoreCase("mkt")) {
                    transaction.setCost(this.stocks.get(transaction.getSymbol()).getPrice());
                }
                return BUY(transaction);
            }
        }
        else{
            if (transaction.getAction().equalsIgnoreCase("fok")){
                if (checkFok(transaction,false)){
                    return BUY(transaction);
                }
                else{
                    dto.add(new TransactionDTO(false,"Could not complete the FOK action"));
                    return dto;
                }
            }
            else{
                if(transaction.getAction().equalsIgnoreCase("mkt")) {
                    transaction.setCost(this.stocks.get(transaction.getSymbol()).getPrice());
                }
                return SELL(transaction);
            }
        }
    }

    public synchronized List<TransactionDTO> BUY(Transaction transaction)  {
        String companyName;
        List<TransactionDTO> dto = new ArrayList<>();
        List<Transaction> remove = new ArrayList<>();
        List<Transaction> sell = DB.getSell(transaction.getSymbol().toUpperCase());
        if (sell==null) {//no items in sell list to buy from
            if (!transaction.getAction().equalsIgnoreCase("ioc")) {
                DB.insertBuyTransaction(transaction);       //insert to buy pending
                DB.getBuy(transaction.getSymbol().toUpperCase()).sort(new TransactionComperator());
                dto.add(new TransactionDTO(false, "Transaction added to the pending buy actions"));
                return dto;
            }
            else{
                dto.add(new TransactionDTO(false, "No transactions were made"));
                return dto;
            }
        }
        for (Object o:sell) {       //go over sell pending list
            Transaction t = (Transaction) o;
            if(transaction.getAction().equalsIgnoreCase("mkt")){
                transaction.setCost(t.getCost());
            }
            if (t.getCost() <= transaction.getCost()) {     //if the sell cost is lower than the buy cost
                if (t.getQuantity() >= transaction.getQuantity()) {     //check number of stocks to sell
                    t.setQuantity(t.getQuantity()- transaction.getQuantity());        //set number of stocks after the action
                    companyName = getCompanyNameBySymbol(transaction.getSymbol().toUpperCase());
                    Transaction newTrans = new Transaction(transaction.getQuantity(),t.getCost(),transaction.getSymbol(),transaction.getAction(),transaction.getBuyer(),t.getSeller(),companyName);
                    transactionManager(newTrans);
                    dto.add(newTrans.getDto(true,"All stocks were bought"));
                    if (t.getQuantity() == 0) {       //remove the transaction from the sell pending
                        sell.remove(o);
                    }
                    this.DB.getSell(transaction.getSymbol().toUpperCase()).removeAll(remove);
                    remove.clear();
                    return dto;
                }
                else {      //make partial transaction
                    companyName = getCompanyNameBySymbol(transaction.getSymbol()).toUpperCase();
                    Transaction newTrans = new Transaction(t.getQuantity(),t.getCost(), transaction.getSymbol(), transaction.getAction(),transaction.getBuyer(),t.getSeller(),companyName);
                    transactionManager(newTrans);
                    dto.add(newTrans.getDto(true,""));
                    remove.add(t);
                    transaction.setQuantity(transaction.getQuantity()-t.getQuantity());
                    if (transaction.getQuantity() == 0) {
                        if (dto.size() ==0){
                            dto.add(new TransactionDTO(false,"Transaction added to the pending sell actions"));
                            return dto;
                        }
                        else {
                            dto.get(0).setMessage("All stocks bought successfully");
                            this.DB.getSell(transaction.getSymbol().toUpperCase()).removeAll(remove);
                            remove.clear();
                            return dto;
                        }
                    }
                }
            }
        }
        this.DB.getSell(transaction.getSymbol().toUpperCase()).removeAll(remove);
        remove.clear();
        if (transaction.getQuantity() != 0) {//check if all the stocks were bought
            if (!transaction.getAction().equalsIgnoreCase("ioc")) {
                DB.insertBuyTransaction(transaction);
                DB.getBuy(transaction.getSymbol().toUpperCase()).sort(new TransactionComperator());
                if (dto.size() == 0) {
                    dto.add(new TransactionDTO(false, "Transaction added to the pending sell actions"));
                    return dto;
                }
                dto.get(0).setMessage(transaction.getQuantity() + " Stocks were not bought and added to the pending buy actions");
                return dto;
            }
            else{
                if (dto.size() == 0) {
                    dto.add(new TransactionDTO(false, "No transactions were made"));
                    return dto;
                }
                else {
                    dto.get(0).setMessage(transaction.getQuantity() + " Stocks were not bought");
                    return dto;
                }
            }
        }
        return dto;
    }

    public synchronized List<TransactionDTO> SELL(Transaction transaction) {
        List<TransactionDTO> dto = FXCollections.observableArrayList();
        String companyName;
        List<Transaction> remove = new ArrayList<>();
        List<Transaction> buy = this.DB.getBuy(transaction.getSymbol().toUpperCase());
        if (buy==null) {        //check if there are transactions pending to sell to
            if (!transaction.getAction().equalsIgnoreCase("ioc")) {
                this.DB.insertSellTransaction(transaction);
                DB.getSell(transaction.getSymbol().toUpperCase()).sort(new TransactionComperator());
                dto.add(new TransactionDTO(false, "Transaction added to the pending sell actions"));
                return dto;
            }
            else{
                dto.add(new TransactionDTO(false, "No transactions were made"));
                return dto;
            }
        }
        for (Object o:buy) {        //check all pending buy transactions
            Transaction t = (Transaction) o;
            if(transaction.getAction().equalsIgnoreCase("mkt")){
                transaction.setCost(t.getCost());
            }
            if (t.getCost() >= transaction.getCost()) {     //check if the buy price is higher than the sell price
                if (t.getQuantity() >= transaction.getQuantity()) {     //check number of stocks to be bought
                    t.setQuantity(t.getQuantity()- transaction.getQuantity());
                    companyName = getCompanyNameBySymbol(transaction.getSymbol().toUpperCase());
                    Transaction newTrans = new Transaction(transaction.getQuantity(),transaction.getCost(),transaction.getSymbol(),transaction.getAction(),t.getBuyer(),transaction.getSeller(),companyName);
                    transactionManager(newTrans);
                    dto.add((newTrans.getDto(true,"All stocks were sold")));
                    if (t.getQuantity() == 0) {
                        buy.remove(o);
                    }
                    this.DB.getBuy(transaction.getSymbol().toUpperCase()).removeAll(remove);
                    remove.clear();
                    return dto;
                }
                else {      //make partial transaction
                    companyName = getCompanyNameBySymbol(transaction.getSymbol()).toUpperCase();
                    Transaction newTrans = new Transaction(t.getQuantity(),transaction.getCost(), transaction.getSymbol(), transaction.getAction(),t.getBuyer(),transaction.getSeller(),companyName);
                    transactionManager(newTrans);
                    dto.add(newTrans.getDto(true,""));
                    remove.add(t);
                    transaction.setQuantity(transaction.getQuantity()-t.getQuantity());
                    if (transaction.getQuantity() == 0) {
                        if (dto.size() ==0){
                            dto.add(new TransactionDTO(false,"Transaction added to the pending sell actions"));
                            return dto;
                        }
                        else {
                            dto.get(0).setMessage("All stocks sold successfully");
                            this.DB.getBuy(transaction.getSymbol().toUpperCase()).removeAll(remove);
                            remove.clear();
                            return dto;
                        }
                    }
                }
            }
        }
        this.DB.getBuy(transaction.getSymbol().toUpperCase()).removeAll(remove);
        remove.clear();
        if (transaction.getQuantity() != 0) {     //check if all stoccks were sold
            if (!transaction.getAction().equalsIgnoreCase("ioc")) {
                DB.insertSellTransaction(transaction);
                DB.getSell(transaction.getSymbol().toUpperCase()).sort(new TransactionComperator());
                int location = dto.size();
                if (location == 0) {
                    dto.add(new TransactionDTO(false, "Transaction added to the pending sell actions"));
                    return dto;
                }

                dto.get(location - 1).setMessage(transaction.getQuantity() + " Stocks were not sold and added to the pending sell actions");
                return dto;
            }
            else{
                if (dto.size() == 0) {
                    dto.add(new TransactionDTO(false, "No transactions were made"));
                    return dto;
                }
                else {
                    dto.get(0).setMessage(transaction.getQuantity() + " Stocks were not sold");
                    return dto;
                }
            }
        }
        return dto;
    }

    public boolean IsStockExist(String symbol) {
        return this.stocks.containsKey(symbol);
    }

    public String getCompanyNameBySymbol(String Symbol){return stocks.get(Symbol).getCompanyName(); }

    public String CalculateTotalCycle(String user){
        int sum = 0;
        if (this.Users.containsKey(user)){
            sum = 0;
            for (String s:this.Users.get(user).getUserStocks().keySet()) {      //sum the total cycle
                sum += getStockPrice(s.toUpperCase())*this.Users.get(user).getUserStocks().get(s);
            }
        }
        return Integer.toString(sum);
    }

    public boolean isUserExist(String user){return this.Users.containsKey(user);}

    public ErrorDto checkTransactionForm(String user, String symbol, String price, String quantity){
        if (!isUserExist(user))
            return new ErrorDto(false,"Please choose a user");
        if (!IsStockExist(symbol))
            return new ErrorDto(false, "Please choose a stock");
        try {
            int cost = Integer.parseInt(price);
            int amount = Integer.parseInt(quantity);
            if (cost < 1)
                return new ErrorDto(false, "Price must be positive");
            if (amount < 1)
                return new ErrorDto(false, "Quantity must be positive");
            return new ErrorDto(true, "");
    } catch (NumberFormatException e) {
        return new ErrorDto(false, "Price and quantity must be round numbers");
    }
    }

    public synchronized boolean createNewStock(String username, String symbol, String companyName, int quantity, int price){
        Stock s = new Stock(companyName,symbol.toUpperCase(),price);
        if (checkNewStock(s)){
            this.stocks.put(symbol.toUpperCase(),new Stock(companyName,symbol.toUpperCase(),price));
            this.Users.get(username).addUserStocks(symbol.toUpperCase(),quantity);
            return true;
        }
        return false;
    }

    public boolean checkFok(Transaction transaction, boolean buy){
        int quantity = transaction.getQuantity();
        if (buy){
            List<Transaction> sellList = this.DB.getSell(transaction.getSymbol().toUpperCase());
            if(sellList == null){
                return false;
            }
            else{
                for (Object o:sellList) {       //go over sell pending list
                    Transaction t = (Transaction) o;
                    if (t.getCost() <= transaction.getCost()) {     //if the sell cost is lower than the buy cost
                        if (t.getQuantity() >= transaction.getQuantity()) {     //check number of stocks to sell
                            return true;
                        }
                        else {      //make partial transaction
                            quantity -= t.getQuantity();
                            if (quantity == 0) {
                                return true;
                                }
                            }
                        }
                    }
                }
            }
        else{
            List<Transaction> buyList = this.DB.getBuy(transaction.getSymbol().toUpperCase());
            if (buyList == null){
                return false;
            }
            else{
                    for (Object o:buyList) {       //go over sell pending list
                        Transaction t = (Transaction) o;
                        if (t.getCost() >= transaction.getCost()) {     //if the sell cost is lower than the buy cost
                            if (t.getQuantity() >= transaction.getQuantity()) {     //check number of stocks to sell
                                return true;
                            }
                            else {      //make partial transaction
                                quantity -= t.getQuantity();
                                if (quantity == 0) {
                                    return true;
                                }
                            }
                        }
                    }
            }
        }
        return false;
    }

    public synchronized boolean addUser(String name, boolean admin){
        if (isUserExist(name)){
            return false;
        }
        else{
            this.Users.put(name,new User(name,admin));
            return true;
        }
    }

    public synchronized void transactionManager (Transaction newTransaction){
        //Handle seller
        int quantity = this.Users.get(newTransaction.getSeller()).getUserStocks().get(newTransaction.getSymbol()); //seller number of stock
        if (quantity-newTransaction.getQuantity()<=0){ //if seller ran out of the stock
            this.Users.get(newTransaction.getSeller()).getUserStocks().remove(newTransaction.getSymbol());
        }
        else{
            this.Users.get(newTransaction.getSeller()).getUserStocks().put(newTransaction.getSymbol(),quantity-newTransaction.getQuantity());
        }
        int oldBalance = this.Users.get(newTransaction.getSeller()).getWallet();
        int newBalance = oldBalance + newTransaction.getCycle();
        this.Users.get(newTransaction.getSeller()).setWallet(newTransaction.getCycle());
        this.Users.get(newTransaction.getSeller()).addUserTransaction(new userTransactionDto("Sell",newTransaction.getSymbol(),newTransaction.getCycle(),oldBalance,newBalance,newTransaction.getDate()));

        //Handle Buyer
        if (this.Users.get(newTransaction.getBuyer()).getUserStocks().containsKey(newTransaction.getSymbol())){ //if buyer has the stock
            quantity = this.Users.get(newTransaction.getBuyer()).getUserStocks().get(newTransaction.getSymbol());
            this.Users.get(newTransaction.getBuyer()).getUserStocks().put(newTransaction.getSymbol(), quantity + newTransaction.getQuantity());
        }
        else{
            this.Users.get(newTransaction.getBuyer()).getUserStocks().put(newTransaction.getSymbol(),newTransaction.getQuantity());
        }
        oldBalance = this.Users.get(newTransaction.getBuyer()).getWallet();
        newBalance = oldBalance - newTransaction.getCycle();
        this.Users.get(newTransaction.getBuyer()).setWallet(-newTransaction.getCycle());
        this.Users.get(newTransaction.getBuyer()).addUserTransaction(new userTransactionDto("Buy",newTransaction.getSymbol(),newTransaction.getCycle(),oldBalance,newBalance,newTransaction.getDate()));

        this.stocks.get(newTransaction.getSymbol().toUpperCase()).setPrice(newTransaction.getCost());
        this.stocks.get(newTransaction.getSymbol().toUpperCase()).setNumberOfTransactions();
        this.stocks.get(newTransaction.getSymbol().toUpperCase()).setCycle(newTransaction.getQuantity()*newTransaction.getCost());
        this.DB.insertDoneTransaction(newTransaction);
        addUsersUpdates(newTransaction);
    }

    public void updateWallet(String username, int currency){
        int oldBalance = this.Users.get(username).getWallet();
        int newBalance = oldBalance + currency;
        this.Users.get(username).setWallet(currency);
        this.Users.get(username).addUserTransaction(new userTransactionDto("Funds","",currency,oldBalance,newBalance, DateTimeFormatter.ofPattern("HH:mm:ss:SSS").format(LocalDateTime.now())));
    }

    public int getWallet(String username){
        return this.Users.get(username).getWallet();
    }

    public List<StocksDTO> getAllStocks(){
        List<StocksDTO> dto = new ArrayList<>();
        for (Stock s:this.stocks.values()) {
            dto.add(s.getDto(0));
        }
        return dto;
    }

    public List<userTransactionDto> getUserTransactionDto(String username){ return new ArrayList<>(this.Users.get(username).getUserTransactions()); }

    public boolean checkNewStock(Stock s){
        for (Stock stock:this.stocks.values()) {
            if (stock.equals(s)){
                return false;
            }
        }
        return true;
    }

    public List<String> getUserUpdates(String username){
        return this.Users.get(username).getUserUpdates();
    }

    public void addUsersUpdates(Transaction transaction){
        String buyerUpdate = "You bought " + transaction.getQuantity() + " " + transaction.getSymbol() + " stocks for " + transaction.getCost() + " each for a total of -" + transaction.getCycle() + ". Date of deal: " + transaction.getDate();
        String sellerUpdate = "You sold " + transaction.getQuantity() + " " + transaction.getSymbol() + " stocks for " + transaction.getCost() + " each for a total of " + transaction.getCycle() + ". Date of deal: " + transaction.getDate();
        this.Users.get(transaction.getBuyer()).addUserUpdate(buyerUpdate);
        this.Users.get(transaction.getSeller()).addUserUpdate(sellerUpdate);
    }
}






