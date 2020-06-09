package com.example.currencyconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.CurrencyViewHolder> implements Filterable {
    private OnItemClickedListener mListener;
    private ArrayList<Currency> mCurrencies;
    private ArrayList<Currency> mCurrenciesFull;
    //Фильтр для поиска
    private Filter currencyFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Currency> filteredCurrencyList = new ArrayList<>();
            //Если в строке нет ничего кроме пробелов, то просто добавляем все валюты
            if (constraint == null || constraint.toString().trim().length() == 0) {
                filteredCurrencyList.addAll(mCurrenciesFull);
            }
            //Иначе добавляем только те валюты, которые содержат в себе введнные символы
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Currency item : mCurrenciesFull) {
                    if (item.getCurrencyName().toLowerCase().contains(filterPattern)) {
                        filteredCurrencyList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredCurrencyList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //Показываем список "отфильтрованных" валют
            mCurrencies.clear();
            mCurrencies.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    public CurrencyListAdapter(Context context) {
        mCurrencies = fillCurrencyList(context);
        mCurrenciesFull = new ArrayList<>(mCurrencies);
    }

    public static ArrayList<Currency> fillCurrencyList(Context context) {
        //Просто хардкод, который добавляем все валюты
        ArrayList<Currency> currencies = new ArrayList<>();
        currencies.add(new Currency(R.drawable.russia_flag_square_icon_32, context.getString(R.string.RUB), "RUB"));
        currencies.add(new Currency(R.drawable.united_states_of_america_flag_square_icon_32, context.getString(R.string.USD), "USD"));
        currencies.add(new Currency(R.drawable.european_union, context.getString(R.string.EUR), "EUR"));
        currencies.add(new Currency(R.drawable.united_kingdom_flag_square_icon_32, context.getString(R.string.GBP), "GBP"));
        currencies.add(new Currency(R.drawable.china_flag_square_icon_32, context.getString(R.string.CNY), "CNY"));
        currencies.add(new Currency(R.drawable.south_korea_flag_square_icon_32, context.getString(R.string.KRW), "KRW"));
        currencies.add(new Currency(R.drawable.switzerland_flag_square_icon_32, context.getString(R.string.CHF), "CHF"));
        currencies.add(new Currency(R.drawable.canada_flag_square_icon_32, context.getString(R.string.CAD), "CAD"));
        currencies.add(new Currency(R.drawable.hongkong, context.getString(R.string.HKD), "HKD"));
        currencies.add(new Currency(R.drawable.iceland_flag_square_icon_32, context.getString(R.string.ISK), "ISK"));
        currencies.add(new Currency(R.drawable.philippines_flag_square_icon_32, context.getString(R.string.PHP), "PHP"));
        currencies.add(new Currency(R.drawable.denmark_flag_square_icon_32, context.getString(R.string.DKK), "DKK"));
        currencies.add(new Currency(R.drawable.czech_republic_flag_square_icon_32, context.getString(R.string.CZK), "CZK"));
        currencies.add(new Currency(R.drawable.romania_flag_square_icon_32, context.getString(R.string.RON), "RON"));
        currencies.add(new Currency(R.drawable.sweden_flag_square_icon_32, context.getString(R.string.SEK), "SEK"));
        currencies.add(new Currency(R.drawable.indonesia_flag_square_icon_32, context.getString(R.string.IDR), "IDR"));
        currencies.add(new Currency(R.drawable.india_flag_square_icon_32, context.getString(R.string.INR), "INR"));
        currencies.add(new Currency(R.drawable.brazil_flag_square_icon_32, context.getString(R.string.BRL), "BRL"));
        currencies.add(new Currency(R.drawable.croatia_flag_square_icon_32, context.getString(R.string.HRK), "HRK"));
        currencies.add(new Currency(R.drawable.japan_flag_square_icon_32, context.getString(R.string.JPY), "JPY"));
        currencies.add(new Currency(R.drawable.taiwan_flag_square_icon_32, context.getString(R.string.THB), "THB"));
        currencies.add(new Currency(R.drawable.malaysia_flag_square_icon_32, context.getString(R.string.MYR), "MYR"));
        currencies.add(new Currency(R.drawable.bulgaria_flag_square_icon_32, context.getString(R.string.BGN), "BGN"));
        currencies.add(new Currency(R.drawable.turkey_flag_square_icon_32, context.getString(R.string.TRY), "TRY"));
        currencies.add(new Currency(R.drawable.norway_flag_square_icon_32, context.getString(R.string.NOK), "NOK"));
        currencies.add(new Currency(R.drawable.new_zealand_flag_square_icon_32, context.getString(R.string.NZD), "NZD"));
        currencies.add(new Currency(R.drawable.south_africa_flag_square_icon_32, context.getString(R.string.ZAR), "ZAR"));
        currencies.add(new Currency(R.drawable.mexico_flag_square_icon_32, context.getString(R.string.MXN), "MXN"));
        currencies.add(new Currency(R.drawable.singapore_flag_square_icon_32, context.getString(R.string.SGD), "SGD"));
        currencies.add(new Currency(R.drawable.australia_flag_square_icon_32, context.getString(R.string.AUD), "AUD"));
        currencies.add(new Currency(R.drawable.israel_flag_square_icon_32, context.getString(R.string.ILS), "ILS"));
        currencies.add(new Currency(R.drawable.poland_flag_square_icon_32, context.getString(R.string.PLN), "PLN"));
        return currencies;
    }

    void setOnItemClickListener(OnItemClickedListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency, parent, false);
        CurrencyViewHolder currencyViewHolder = new CurrencyViewHolder(v, mListener);
        return currencyViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyViewHolder holder, int position) {
        Currency currency = mCurrencies.get(position);
        holder.countryFlag.setImageResource(currency.getCountryFlag());
        holder.currencyName.setText(currency.getCurrencyName());
    }

    public Currency getItem(int position) {
        return mCurrencies.get(position);
    }

    @Override
    public int getItemCount() {
        return mCurrencies.size();
    }

    @Override
    public Filter getFilter() {
        return currencyFilter;
    }

    public interface OnItemClickedListener {
        void OnItemClick(int position);
    }

    public static class CurrencyViewHolder extends RecyclerView.ViewHolder {
        ImageView countryFlag;
        TextView currencyName;

        CurrencyViewHolder(@NonNull View itemView, final OnItemClickedListener listener) {
            super(itemView);
            countryFlag = itemView.findViewById(R.id.flag);
            currencyName = itemView.findViewById(R.id.currency_name);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.OnItemClick(position);
                    }
                }
            });
        }
    }
}
