package com.anotap.messenger.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.adapter.vkdatabase.CountriesAdapter;
import com.anotap.messenger.fragment.base.BasePresenterDialogFragment;
import com.anotap.messenger.listener.TextWatcherAdapter;
import com.anotap.messenger.model.database.Country;
import com.anotap.messenger.mvp.presenter.CountriesPresenter;
import com.anotap.messenger.mvp.view.ICountriesView;
import com.anotap.mvp.core.IPresenterFactory;

import static com.anotap.messenger.util.Objects.nonNull;

public class SelectCountryDialog extends BasePresenterDialogFragment<CountriesPresenter, ICountriesView>
        implements CountriesAdapter.Listener, ICountriesView {

    @Override
    protected String tag() {
        return SelectCountryDialog.class.getSimpleName();
    }

    private CountriesAdapter mAdapter;
    private View mLoadingView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.dialog_countries, null);

        Dialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.countries_title)
                .setView(view)
                .setNegativeButton(R.string.button_cancel, null)
                .create();

        EditText filterView = view.findViewById(R.id.input);
        filterView.addTextChangedListener(new TextWatcherAdapter(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPresenter().fireFilterEdit(s);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new CountriesAdapter(getActivity(), Collections.emptyList());
        mAdapter.setListener(this);

        recyclerView.setAdapter(mAdapter);

        mLoadingView = view.findViewById(R.id.progress_root);

        fireViewCreated();
        return dialog;
    }

    @Override
    public void onClick(Country country) {
        getPresenter().fireCountryClick(country);
    }

    @Override
    public void displayData(List<Country> countries) {
        if (nonNull(mAdapter)) {
            mAdapter.setData(countries);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (nonNull(mAdapter)) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void displayLoading(boolean loading) {
        if (nonNull(mLoadingView)) {
            mLoadingView.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void returnSelection(Country country) {
        Intent intent = new Intent();
        intent.putExtra(Extra.COUNTRY, country);
        intent.putExtra(Extra.ID, country.getId());
        intent.putExtra(Extra.TITLE, country.getTitle());

        if (getArguments() != null) {
            intent.putExtras(getArguments());
        }

        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        dismiss();
    }

    @Override
    public IPresenterFactory<CountriesPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new CountriesPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                saveInstanceState
        );
    }
}