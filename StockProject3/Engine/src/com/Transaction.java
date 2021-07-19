package com;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class Transaction {
    private String Date;
    private int Quantity;
    private int Cost;
    private String Symbol;
    private String Action;
    private String Buyer;
    private String Seller;
    private String CompanyName;
    private int Cycle;
    private int PrevWallet;
    private int NewWallet;

    public Transaction(String date, int Quantity, int cost, String symbol, String action,String buyer, String seller, String companyName) {
        this.Date = date;
        this.Quantity = Quantity;
        this.Cost = cost;
        this.Symbol = symbol.toUpperCase();
        this.Action = action;
        this.Buyer = buyer;
        this.Seller = seller;
        this.CompanyName = companyName;
        this.Cycle = this.Cost * this.Quantity;
    }
    public Transaction(int Quantity, int cost, String symbol, String action, String buyer,String seller, String companyName) {
        this.Date  = DateTimeFormatter.ofPattern("HH:mm:ss:SSS").format(LocalDateTime.now());
        this.Quantity = Quantity;
        this.Cost = cost;
        this.Symbol = symbol.toUpperCase();
        this.Action = action;
        this.Buyer = buyer;
        this.Seller = seller;
        this.CompanyName = companyName;
        this.Cycle = this.Cost * this.Quantity;
    }
    public String getCompanyName(){return this.CompanyName;}
    public String getDate() {
        return this.Date;
    }

    private int getPrevWallet(){return this.PrevWallet;}

    public int getQuantity() {
        return this.Quantity;
    }

    public int getCost() {
        return this.Cost;
    }

    public String getSymbol() {return this.Symbol;}

    public void setQuantity(int newQuantity){this.Quantity = newQuantity;}

    public String getAction(){return this.Action;}

    public void setCost(int cost){this.Cost = cost;}

    public int getCycle(){return this.Cycle;}

    public String getBuyer(){return  this.Buyer;}
    public String getSeller(){return this.Seller;}


    @Override
    public String toString() {
        String buffer = "Transaction Date: " + this.Date + "\n";
        buffer += "Action: " + this.Action+"\n";
        buffer += "Number of Stocks: " + this.Quantity + "\n";
        buffer += "Price per Stock: " + this.Cost + "\n";
        buffer += "Transaction Value: " + (this.getQuantity() * this.Cost) + "\n\n";
        return buffer;
    }

    public TransactionDTO getDto(Boolean success, String message){
        if (success) {
            return new TransactionDTO(this.Date, this.Quantity, this.Cost, this.Symbol, this.CompanyName,this.Action,message,this.Buyer,this.Seller,true,this.getCycle());
        }
        else{
            return new TransactionDTO("",0,0,"","","",message,"","",false,0);
        }

    }
}

class TransactionComperator implements Comparator<Transaction>
{
    @Override
    public int compare(Transaction o1, Transaction o2) {
        if (o1.getAction().compareToIgnoreCase("sell")==0) {
            if (o1.getCost() > o2.getCost()) {
                return 1;
            } else if (o1.getCost() == o2.getCost()) {
                if (o1.getDate().compareTo(o2.getDate()) < 0) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return -1;
            }
        } else {
            if (o1.getCost() < o2.getCost()) {
                return 1;
            } else if (o1.getCost() == o2.getCost()) {
                int comp = o1.getDate().compareTo(o2.getDate());
                if (comp < 0) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return -1;
            }
        }
    }
}