package com.example.currencyconverter;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.POJO.ExchangeRates;
import com.example.currencyconverter.RETROFIT.GetCurrencyInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.currencyconverter.CurrencyListAdapter.fillCurrencyList;


public class MainActivity extends AppCompatActivity {
    //флаг, чтобы не вызывать рекурсивные вызовы onTextChanged в TextWatcher
    private boolean converted = true;
    //Курс валют, которые сейчас выбраны
    private BigDecimal exchangeRate = new BigDecimal("1");
    //Map, в котором находятся все курсы валют относительно валюты в firstConverter
    private Map<String, Double> rates;
    //Adapter для RecyclerView
    private CurrencyListAdapter mAdapter;
    //Массив со всем валютами
    private ArrayList<Currency> currencies;
    //ID валюты firstConverter
    private String firstConverterCurrency = "RUB";
    //ID валюты secondConverter
    private String secondConverterCurrency = "USD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currencies = fillCurrencyList(this);
        TextInputEditText firstConverter = findViewById(R.id.first_converter);
        TextInputEditText secondConverter = findViewById(R.id.second_converter);
        //Создание TextWatcher, чтобы конвертировать валюты "в реальном времени"
        TextWatcher textWatcherFirstCurrency = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (converted) {
                    if (firstConverter.getText().length() != 0) {
                        converted = false;
                        BigDecimal firstCurrency = new BigDecimal(firstConverter.getText().toString());
                        firstCurrency = firstCurrency.multiply(exchangeRate);
                        secondConverter.setText(firstCurrency.setScale(2, RoundingMode.HALF_UP).toString());
                    } else {
                        converted = false;
                        firstConverter.setText("");
                        converted = false;
                        secondConverter.setText("");
                    }
                } else {
                    converted = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        TextWatcher textWatcherSecondCurrency = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (converted) {
                    if (secondConverter.getText().length() != 0) {
                        converted = false;
                        StringBuilder secondConverterString = new StringBuilder(secondConverter.getText().toString());
                        //Добавление нулей после точки нужно для того, чтобы деление BigDecimal работало адекватно
                        if (secondConverterString.toString().contains(".")) {
                            int end = secondConverterString.lastIndexOf(".");
                            String endsWith = secondConverterString.subSequence(end+1, secondConverterString.length()).toString();
                            if (endsWith.length() == 1) {
                                secondConverterString.append("0");
                            } else if (endsWith.length() == 0) {
                                secondConverterString.append("00");
                            }

                        } else {
                            secondConverterString.append(".00");
                        }
                        BigDecimal secondCurrency = new BigDecimal(secondConverterString.toString());
                        secondCurrency = secondCurrency.divide(exchangeRate, RoundingMode.HALF_EVEN);
                        firstConverter.setText(secondCurrency.setScale(2, RoundingMode.HALF_EVEN).toString());
                    } else {
                        converted = false;
                        firstConverter.setText("");
                        converted = false;
                        secondConverter.setText("");
                    }
                } else {
                    converted = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        firstConverter.addTextChangedListener(textWatcherFirstCurrency);
        secondConverter.addTextChangedListener(textWatcherSecondCurrency);

        getExchangeRate("RUB", this, firstConverter);

    }

    //Метод, который получает валютные курсы относительно валюты в firstConverter
    private void getExchangeRate(String base, final Context context, TextInputEditText editText) {
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://api.exchangeratesapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()));
        GetCurrencyInterface client = builder.build().create(GetCurrencyInterface.class);
        client.getCurrency(base)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ExchangeRates>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ExchangeRates exchangeRates) {
                        rates = exchangeRates.getRates();
                        exchangeRate = new BigDecimal(rates.get(secondConverterCurrency));
                        //Эти строчки нужны, чтобы затригеррить onTextChange в TextWatcher'ах
                        if (editText != null) {
                            editText.setText(editText.getText().append("1"));
                            editText.setText(editText.getText().subSequence(0, editText.getText().length() - 1));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "Произошла неизвестная ошибка!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    //Метод, который удаляет все "очищает" данные конвертеры
    public void clearConverters(View view) {
        TextInputEditText firstConverter = findViewById(R.id.first_converter);
        TextInputEditText secondConverter = findViewById(R.id.second_converter);
        firstConverter.setText("");
        converted = false;
        secondConverter.setText("");
    }

    //Метод, который вызывается при нажатие на кнопку выбора первой валюты
    public void openFirstConverterSign(View view) {
        //Начинаем создавать диалог, который откроется при нажатие
        LayoutInflater inflater = getLayoutInflater();
        View dialog = inflater.inflate(R.layout.dialog_event, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialog);
        builder.setTitle(getString(R.string.search_toolbar));
        builder.setNegativeButton(getString(R.string.close_dialog), (dialogInterface, i) -> {
        });
        TextInputEditText firstConverter = findViewById(R.id.first_converter);
        SearchView searchView = dialog.findViewById(R.id.search_bar);
        RecyclerView recyclerView = dialog.findViewById(R.id.currency_list);
        //Создаем адаптер для RecyclerView, в котором будет список валют
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new CurrencyListAdapter(this);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        final AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        alert.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        Window window = alert.getWindow();
        window.setGravity(Gravity.CENTER);
        mAdapter.setOnItemClickListener((int position) -> {
            //Удаляем курсы старой валюты
            if (rates != null) {
                rates.clear();
            }
            //Получаем класс Currency из списка(он нам нужен, чтобы узнать ID валюты, чтобы сделать запрос в сеть)
            Currency currentCurrency = mAdapter.getItem(position);
            getExchangeRate(currentCurrency.getId(), this, firstConverter);
            MaterialButton firstCurrency = findViewById(R.id.first_currency);
            firstCurrency.setText(currentCurrency.getCurrencyName());
            alert.dismiss();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            firstConverterCurrency = currentCurrency.getId();
        });
        alert.show();
    }

    //Метод, который вызывается при нажатие на кнопку выбора второй валюты
    public void openSecondConverterSign(View view) {
        //Начинаем создавать диалог, который откроется при нажатие
        LayoutInflater inflater = getLayoutInflater();
        View dialog = inflater.inflate(R.layout.dialog_event, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialog);
        builder.setTitle(getString(R.string.search_toolbar));
        builder.setNegativeButton(getString(R.string.close_dialog), (dialogInterface, i) -> {
        });

        SearchView searchView = dialog.findViewById(R.id.search_bar);
        RecyclerView recyclerView = dialog.findViewById(R.id.currency_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new CurrencyListAdapter(this);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        final AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        alert.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        Window window = alert.getWindow();
        window.setGravity(Gravity.CENTER);
        mAdapter.setOnItemClickListener(position -> {
            //Получаем класс currency валюты, которую мы выбрали
            Currency currentCurrency = mAdapter.getItem(position);
            secondConverterCurrency = currentCurrency.getId();
            //Устанавливаем текущий курс, который мы возьмем из уже полученного списка курсов
            exchangeRate = new BigDecimal(rates.get(currentCurrency.getId()));
            MaterialButton secondCurrency = findViewById(R.id.second_currency);
            secondCurrency.setText(currentCurrency.getCurrencyName());
            TextInputEditText firstConverter = findViewById(R.id.first_converter);
            //Тригеррим onTextChanger в TextWatcher'ах
            firstConverter.setText(firstConverter.getText().append("1"));
            firstConverter.setText(firstConverter.getText().subSequence(0, firstConverter.getText().length() - 1));
            alert.dismiss();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        alert.show();
    }

    //Метод, который вызывается при нажатие на кнопку выбора смена валют
    public void switchCurrencies(View view) {
        //Совершаем запрос в сеть, так как сменилась валюта в firstConverter
        getExchangeRate(secondConverterCurrency, this, null);
        TextInputEditText firstConverter = findViewById(R.id.first_converter);
        TextInputEditText secondConverter = findViewById(R.id.second_converter);
        //Меняем местами значения валют, также меняем флаг, что не триггерить onTextChanged
        Editable first = firstConverter.getText();
        converted = false;
        firstConverter.setText(secondConverter.getText());
        converted = false;
        secondConverter.setText(first);
        //Меняем местами названия
        MaterialButton firstCurrency = findViewById(R.id.first_currency);
        MaterialButton secondCurrency = findViewById(R.id.second_currency);
        String second = secondCurrency.getText().toString();
        secondCurrency.setText(firstCurrency.getText());
        firstCurrency.setText(second);
        //Меняем местами ID первой и второй валюты
        String t = firstConverterCurrency;
        firstConverterCurrency = secondConverterCurrency;
        secondConverterCurrency = t;
    }
}
