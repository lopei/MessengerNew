package com.anotap.messenger.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.anotap.messenger.R;
import com.anotap.messenger.fragment.FileManagerFragment;
import com.anotap.messenger.listener.BackPressCallback;
import com.anotap.messenger.settings.CurrentTheme;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.Utils;

public class FileManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Settings.get()
                .ui()
                .getMainTheme());

        setContentView(R.layout.activity_no_main);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View fakeStatusbarView = findViewById(R.id.fake_statusbar);
            if(fakeStatusbarView != null){
                fakeStatusbarView.getLayoutParams().height = Utils.getStatusBarHeight(this);
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

        if (toolbar != null) {
            toolbar.setNavigationIcon(CurrentTheme.getDrawableFromAttribute(this, R.attr.toolbarCloseIcon));
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        if (savedInstanceState == null) {
            attachFragment();
        }
    }

    private void attachFragment(){
        FileManagerFragment ignoredFragment = new FileManagerFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, ignoredFragment)
                .commit();
    }

    @Override
    public void onBackPressed(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if(fragment != null && fragment instanceof BackPressCallback){
            if(((BackPressCallback)fragment).onBackPressed()){
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}
