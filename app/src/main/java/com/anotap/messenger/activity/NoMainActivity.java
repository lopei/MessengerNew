package com.anotap.messenger.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.anotap.messenger.R;
import com.anotap.messenger.listener.BackPressCallback;
import com.anotap.messenger.settings.CurrentTheme;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.Objects;
import com.anotap.messenger.util.Utils;

/**
 * Created by admin on 03.10.2016.
 * phoenix
 */
public abstract class NoMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Settings.get()
                .ui()
                .getMainTheme());

        setContentView(R.layout.activity_no_main);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View statusBarView = findViewById(R.id.fake_statusbar);
            if (statusBarView != null) {
                statusBarView.getLayoutParams().height = Utils.getStatusBarHeight(this);
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

        getSupportFragmentManager().addOnBackStackChangedListener(mBackStackListener);
    }

    @IdRes
    protected int getMainContainerViewId(){
        return R.id.fragment;
    }

    private FragmentManager.OnBackStackChangedListener mBackStackListener = this::resolveToolbarNavigationIcon;

    private Toolbar mToolbar;

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        mToolbar = toolbar;
        resolveToolbarNavigationIcon();
    }

    private void resolveToolbarNavigationIcon() {
        if (Objects.isNull(mToolbar)) return;

        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 1) {
            mToolbar.setNavigationIcon(CurrentTheme.getDrawableFromAttribute(this, R.attr.toolbarBackIcon));
        } else {
            mToolbar.setNavigationIcon(CurrentTheme.getDrawableFromAttribute(this, R.attr.toolbarCloseIcon));
        }

        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();

        Fragment front = getSupportFragmentManager().findFragmentById(getMainContainerViewId());
        if (front instanceof BackPressCallback) {
            if (!(((BackPressCallback) front).onBackPressed())) {
                return;
            }
        }

        if (fm.getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            supportFinishAfterTransition();
        }
    }

    @Override
    protected void onDestroy() {
        getSupportFragmentManager().removeOnBackStackChangedListener(mBackStackListener);
        super.onDestroy();
    }
}