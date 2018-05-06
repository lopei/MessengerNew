package com.anotap.messenger.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.anotap.messenger.Constants;
import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.adapter.AccountAdapter;
import com.anotap.messenger.api.Auth;
import com.anotap.messenger.dialog.DirectAuthDialog;
import com.anotap.messenger.domain.IOwnersInteractor;
import com.anotap.messenger.domain.InteractorFactory;
import com.anotap.messenger.fragment.AccountsFragment;
import com.anotap.messenger.fragment.ProxyFragment;
import com.anotap.messenger.model.Account;
import com.anotap.messenger.settings.CurrentTheme;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.RxUtils;
import com.anotap.messenger.util.Utils;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

public class AccountsActivity extends AppCompatActivity {

    private static final int REQEUST_DIRECT_LOGIN = 108;
    private static final int REQUEST_LOGIN = 107;
    private static final String SAVE_DATA = "save_data";

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private IOwnersInteractor mOwnersInteractor;

    private AccountAdapter mAdapter;
    private ArrayList<Account> mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Settings.get()
                .ui()
                .getMainTheme());

        setContentView(R.layout.activity_no_main);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View kitkatStatusBarView = findViewById(R.id.fake_statusbar);
            if (kitkatStatusBarView != null) {
                kitkatStatusBarView.getLayoutParams().height = Utils.getStatusBarHeight(this);
            }
        }

        if (Utils.hasLollipop()) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.setStatusBarColor(CurrentTheme.getColorPrimaryDark(this));

            if (Settings.get().ui().isNavigationbarColored()) {
                w.setNavigationBarColor(CurrentTheme.getNavigationBarColor(this));
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(Utils.dpToPx(0, this));
        }

        if (savedInstanceState == null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment, new AccountsFragment())
//                    .commit();
            ProxyFragment fragment = new ProxyFragment();
            fragment.setListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoginViaWeb();
                }
            });

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();

            if (mData == null) {
                mData = new ArrayList<>();
            }
        } else {
            mData = savedInstanceState.getParcelableArrayList(SAVE_DATA);
        }
        this.mOwnersInteractor = InteractorFactory.createOwnerInteractor();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQEUST_DIRECT_LOGIN:
            case REQUEST_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    if (DirectAuthDialog.ACTION_LOGIN_VIA_WEB.equals(data.getAction())) {
                        startLoginViaWeb();
                    } else if (DirectAuthDialog.ACTION_LOGIN_COMPLETE.equals(data.getAction())) {
                        int uid = data.getExtras().getInt(Extra.USER_ID);
                        String token = data.getStringExtra(Extra.TOKEN);
                        processNewAccount(uid, token);
                        finish();
                    }
                }
                break;
        }
    }

    private void processNewAccount(final int uid, final String token) {
        //Accounts account = new Accounts(token, uid);

        // важно!! Если мы получили новый токен, то необходимо удалить запись
        // о регистрации push-уведомлений
        //PushSettings.unregisterFor(getContext(), account);

        Settings.get()
                .accounts()
                .storeAccessToken(uid, token);

        Settings.get()
                .accounts()
                .registerAccountId(uid, true);

        merge(new Account(uid, null));

        mCompositeDisposable.add(mOwnersInteractor.getBaseOwnerInfo(uid, uid, IOwnersInteractor.MODE_ANY)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(owner -> merge(new Account(uid, owner)), t -> {/*ignored*/}));
    }

    private void merge(Account account) {
        int index = indexOf(account.getId());

        if (index != -1) {
            mData.set(index, account);
        } else {
            mData.add(account);
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private int indexOf(int uid) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getId() == uid) {
                return i;
            }
        }

        return -1;
    }

//    private void startDirectLogin() {
//        DirectAuthDialog.newInstance()
//                .show(getSupportFragmentManager(), "direct-login");
//    }

    private void startLoginViaWeb() {
        Intent intent = LoginActivity.createIntent(this, String.valueOf(Constants.API_ID), Auth.getScope());
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    @Override
    public void onDestroy() {
        mCompositeDisposable.dispose();
        super.onDestroy();
    }

}