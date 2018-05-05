package com.anotap.messenger.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.anotap.messenger.R;
import com.anotap.messenger.fragment.AccountsFragment;
import com.anotap.messenger.settings.CurrentTheme;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.Utils;

public class AccountsActivity extends AppCompatActivity {

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
            if(kitkatStatusBarView != null){
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

        if(getSupportActionBar() != null){
            getSupportActionBar().setElevation(Utils.dpToPx(0, this));
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, new AccountsFragment())
                    .commit();
        }
    }

}