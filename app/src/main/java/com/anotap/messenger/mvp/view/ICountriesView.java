package com.anotap.messenger.mvp.view;

import java.util.List;

import com.anotap.messenger.model.database.Country;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by Ruslan Kolbasa on 20.09.2017.
 * phoenix
 */
public interface ICountriesView extends IMvpView, IErrorView {
    void displayData(List<Country> countries);
    void notifyDataSetChanged();
    void displayLoading(boolean loading);

    void returnSelection(Country country);
}