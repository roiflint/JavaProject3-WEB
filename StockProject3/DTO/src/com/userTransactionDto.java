package com;

public class userTransactionDto {
    private String action;
    private String symbol;
    private int price;
    private int oldBalance;
    private int newBalance;
    private String date;

    public userTransactionDto(String action, String symbol, int price, int oldBalance, int newBalance, String date) {
        this.action = action;
        this.symbol = symbol;
        this.price = price;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getPrice() {
        return price;
    }

    public int getOldBalance() {
        return oldBalance;
    }

    public int getNewBalance() {
        return newBalance;
    }

    public String getDate() {
        return this.date;
    }
}
