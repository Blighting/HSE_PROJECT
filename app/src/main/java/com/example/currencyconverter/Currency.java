package com.example.currencyconverter;

public class Currency {
    private int countryFlag;
    private String currencyName;
    private String id;

    public Currency(int countryFlag, String currencyName, String id) {
        this.countryFlag = countryFlag;
        this.currencyName = currencyName;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getCountryFlag() {
        return countryFlag;
    }


    public String getCurrencyName() {
        return currencyName;
    }

}
