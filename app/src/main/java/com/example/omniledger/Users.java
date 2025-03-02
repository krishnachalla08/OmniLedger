package com.example.omniledger;

public class Users {

    String bank,item,transaction,datetym,location,amount,filename;



    public Users() {
    }

    public Users(String bank, String item, String transaction, String datetym, String location, String amount, String filename) {
        this.bank = bank;
        this.item = item;
        this.transaction = transaction;
        this.datetym = datetym;
        this.location = location;
        this.amount = amount;
        this.filename = filename;
    }

    public String getBank() {return bank;}

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getDatetym() {
        return datetym;
    }

    public void setDatetym(String datetym) {
        this.datetym = datetym;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
