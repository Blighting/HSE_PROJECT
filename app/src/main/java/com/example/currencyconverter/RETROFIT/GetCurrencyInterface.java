package com.example.currencyconverter.RETROFIT;

import com.example.currencyconverter.POJO.ExchangeRates;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetCurrencyInterface {
    @GET("latest?base=")
    Observable<ExchangeRates> getCurrency(@Query("base") String base);
}
