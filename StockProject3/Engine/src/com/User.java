package com;

import generated.RseItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class User {
    private String Name;
    private boolean Admin;
    private int wallet;
    private Map<String,Integer> UserStocks;
    private List<userTransactionDto> userTransactions;
    private List<String> updates;

    public User(String name,boolean admin){
        this.Name = name;
        this.Admin = admin;
        this.wallet = 0;
        this.UserStocks = new HashMap<>();
        this.userTransactions = new ArrayList<>();
        this.updates = new ArrayList<>();
    }
    public List<String> getUserUpdates(){return this.updates;}
    public void addUserUpdate(String update){this.updates.add(0,update);}
    public List<userTransactionDto> getUserTransactions(){return this.userTransactions;}
    public void addUserTransaction(userTransactionDto transaction){ this.userTransactions.add(0,transaction); }
    public String getName(){return this.Name;}
    public Map<String,Integer> getUserStocks(){return this.UserStocks;}
    public int getStockHoldings(String symbol){
        if (this.UserStocks.containsKey(symbol.toUpperCase())){
            return this.UserStocks.get(symbol.toUpperCase());
        }
        return 0;
    }
    public synchronized void addUserStocks(String symbol, Integer quantity){this.UserStocks.put(symbol, quantity);}
    public boolean isAdmin(){return this.Admin;}
    public int getWallet(){return this.wallet;}
    public void setWallet(int currency){this.wallet+=currency;}
    public synchronized void addStocksFromXml(Map<String, Integer> stocks){
        for (String s:stocks.keySet()) {
            if (this.UserStocks.containsKey(s)){
                int sum = stocks.get(s) + this.UserStocks.get(s);
                this.UserStocks.put(s,sum);
            }
            else{
                this.UserStocks.put(s,stocks.get(s));
            }
        }
    }



}
