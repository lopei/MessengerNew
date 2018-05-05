package com.anotap.messenger.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.anotap.messenger.R;
import com.anotap.messenger.fragment.EnterPinFragment;
import com.anotap.messenger.util.Utils;

/**
 * Created by ruslan.kolbasa on 30-May-16.
 * mobilebankingandroid
 */
public class EnterPinActivity extends NoMainActivity {

    public static Class getClass(Context context){
        return Utils.is600dp(context) ? EnterPinActivity.class : EnterPinActivityPortraitOnly.class;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, EnterPinFragment.newInstance())
                    .commit();
        }
    }
}
