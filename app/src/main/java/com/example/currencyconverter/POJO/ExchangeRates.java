package com.example.currencyconverter.POJO;

import java.util.Map;

public class ExchangeRates {
    private Map<String, Double> rates;
    private String base;
    private String date;

    public Map<String, Double> getRates() {
        return rates;
    }

}
