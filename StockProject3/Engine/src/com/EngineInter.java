package com;

import com.ErrorDto;
import com.StocksDTO;
import com.TransactionDTO;
import javafx.collections.ObservableList;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

public interface EngineInter {
    //
    //    //Return a string containing information n the fields of every stock in the system ready to be printed
    public List<StocksDTO> GetAllStocksInfo();

    //Receives a stock symbol, returns a string containing information on the stock fields and every
    //transaction made on that stock ready to be printed
    StocksDTO getSingleStockInfo(String StockName, String username, boolean admin);

    //Receives a path for a xml file, if the file is applicable the file is loaded to the system and
    //override any information stored before the file was loaded
    public ErrorDto LoadXML(InputStream inputStream, String username) throws InterruptedException, Exception;

    public List<TransactionDTO> GetBuyList(String Symbol);

    public List<TransactionDTO> GetSellList(String Symbol);

    public List<TransactionDTO> GetPreviousList(String Symbol);

    public List<TransactionDTO> MakeTransaction(Transaction transaction, boolean buy);

    //Receives a stock symbol and checking if the stock exists in the system
    public boolean IsStockExist(String symbol);

    //Receives a stock symbol and return the stock current price
    public int getStockPrice(String symbol);

    //Receives a stock symbol and return the company name of that sock symbol
    public String getCompanyNameBySymbol(String Symbol);


    //Calculate the total cycle of a specific user
    public String CalculateTotalCycle(String user);

    //Check the transaction form for legality and transfer the data to the buy or sell action
    public ErrorDto checkTransactionForm(String user, String symbol, String price, String quantity);

    //create a new stock and add all the quantity to a specified user
    public boolean createNewStock(String username, String symbol, String companyName, int quantity, int price);

    //add new user to the system
    public boolean addUser(String name, boolean admin);

    //add currency to a username wallet
    public void updateWallet(String username, int currency);

    //return all stocks info
    public List<StocksDTO> getAllStocks();

    //return the user transactions list
    public List<userTransactionDto> getUserTransactionDto(String username);

    //return the user wallet
    public int getWallet(String username);

    //return the requested user update list
    public List<String> getUserUpdates(String username);

}

