package com.anotap.messenger.mvp.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.domain.IDatabaseInteractor;
import com.anotap.messenger.domain.InteractorFactory;
import com.anotap.messenger.model.database.Country;
import com.anotap.messenger.mvp.presenter.base.RxSupportPresenter;
import com.anotap.messenger.mvp.view.ICountriesView;
import com.anotap.messenger.util.Objects;
import com.anotap.messenger.util.RxUtils;
import com.anotap.mvp.reflect.OnGuiCreated;

import static com.anotap.messenger.util.Utils.getCauseIfRuntime;
import static com.anotap.messenger.util.Utils.isEmpty;

/**
 * Created by Ruslan Kolbasa on 20.09.2017.
 * phoenix
 */
public class CountriesPresenter extends RxSupportPresenter<ICountriesView> {

    private final int accountId;

    private final IDatabaseInteractor databaseInteractor;

    private List<Country> countries;

    private final List<Country> filtered;

    private String filter;

    public CountriesPresenter(int accountId, @Nullable Bundle savedInstanceState) {
        super(savedInstanceState);
        this.accountId = accountId;
        this.countries = new ArrayList<>();
        this.filtered = new ArrayList<>();
        this.databaseInteractor = InteractorFactory.createDatabaseInteractor();

        requestData();
    }

    @Override
    public void onGuiCreated(@NonNull ICountriesView viewHost) {
        super.onGuiCreated(viewHost);
        viewHost.displayData(this.filtered);
    }

    private boolean loadingNow;

    private void setLoadingNow(boolean loadingNow) {
        this.loadingNow = loadingNow;
        resolveLoadingView();
    }

    @OnGuiCreated
    private void resolveLoadingView() {
        if (isGuiReady()) {
            getView().displayLoading(loadingNow);
        }
    }

    private void onDataReceived(List<Country> countries) {
        setLoadingNow(false);

        this.countries = countries;

        reFillFilteredData();
        callView(ICountriesView::notifyDataSetChanged);
    }

    public void fireFilterEdit(CharSequence text){
        if(Objects.safeEquals(text.toString(), this.filter)){
            return;
        }

        this.filter = text.toString();

        reFillFilteredData();
        callView(ICountriesView::notifyDataSetChanged);
    }

    private void reFillFilteredData(){
        filtered.clear();

        if (isEmpty(filter)) {
            filtered.addAll(countries);
            return;
        }

        String lowerFilter = filter.toLowerCase();

        for (Country country : countries) {
            if(country.getTitle().toLowerCase().contains(lowerFilter)){
                filtered.add(country);
            }
        }
    }

    private void onDataGetError(Throwable t) {
        setLoadingNow(false);
        showError(getView(), getCauseIfRuntime(t));
    }

    private void requestData() {
        setLoadingNow(true);
        appendDisposable(databaseInteractor.getCountries(accountId, false)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onDataReceived, this::onDataGetError));
    }

    @Override
    protected String tag() {
        return CountriesPresenter.class.getSimpleName();
    }

    public void fireCountryClick(Country country) {
        getView().returnSelection(country);
    }
}