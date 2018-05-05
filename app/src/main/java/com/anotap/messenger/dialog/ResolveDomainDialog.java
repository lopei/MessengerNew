package com.anotap.messenger.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.dialog.base.AccountDependencyDialogFragment;
import com.anotap.messenger.domain.IUtilsInteractor;
import com.anotap.messenger.domain.InteractorFactory;
import com.anotap.messenger.model.Owner;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.util.Optional;
import com.anotap.messenger.util.RxUtils;
import com.anotap.messenger.util.Utils;

public class ResolveDomainDialog extends AccountDependencyDialogFragment {

    public static Bundle buildArgs(int aid, String url, String domain){
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, aid);
        args.putString(Extra.URL, url);
        args.putString(Extra.DOMAIN, domain);
        return args;
    }

    public static ResolveDomainDialog newInstance(int aid, String url, String domain){
        return newInstance(buildArgs(aid, url, domain));
    }

    public static ResolveDomainDialog newInstance(Bundle args){
        ResolveDomainDialog domainDialog = new ResolveDomainDialog();
        domainDialog.setArguments(args);
        return domainDialog;
    }

    private int mAccountId;
    private String url;
    private String domain;
    private IUtilsInteractor mUtilsInteractor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAccountId = getArguments().getInt(Extra.ACCOUNT_ID);
        this.mUtilsInteractor = InteractorFactory.createUtilsInteractor();
        this.url = getArguments().getString(Extra.URL);
        this.domain = getArguments().getString(Extra.DOMAIN);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(this);

        request();
        return progressDialog;
    }

    private void request(){
        appendDisposable(mUtilsInteractor.resolveDomain(mAccountId, domain)
        .compose(RxUtils.applySingleIOToMainSchedulers())
        .subscribe(this::onResolveResult, this::onResolveError));
    }

    private void onResolveError(Throwable t){
        showErrorAlert(Utils.getCauseIfRuntime(t).getMessage());
    }

    private void onResolveResult(Optional<Owner> optionalOwner){
        if(optionalOwner.isEmpty()){
            PlaceFactory.getExternalLinkPlace(getAccountId(), url).tryOpenWith(getActivity());
        } else {
            PlaceFactory.getOwnerWallPlace(mAccountId, optionalOwner.get()).tryOpenWith(getActivity());
        }

        dismiss();
    }

    private void showErrorAlert(String error){
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.error)
                .setMessage(error).setPositiveButton(R.string.try_again, (dialog, which) -> request())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dismiss())
                .show();
    }
}
