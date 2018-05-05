package com.anotap.messenger.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Collection;

import com.anotap.messenger.R;
import com.anotap.messenger.activity.ActivityFeatures;
import com.anotap.messenger.activity.ActivityUtils;
import com.anotap.messenger.activity.CreatePinActivity;
import com.anotap.messenger.crypt.KeyLocationPolicy;
import com.anotap.messenger.db.Stores;
import com.anotap.messenger.listener.OnSectionResumeCallback;
import com.anotap.messenger.settings.ISettings;
import com.anotap.messenger.settings.SecuritySettings;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.AssertUtils;

public class SecurityPreferencesFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private SwitchPreference mUsePinForSecurityPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.security_settings);

        mUsePinForSecurityPreference = (SwitchPreference) findPreference(SecuritySettings.KEY_USE_PIN_FOR_SECURITY);
        mUsePinForSecurityPreference.setOnPreferenceChangeListener(this);

        Preference changePinPreference = findPreference(SecuritySettings.KEY_CHANGE_PIN);
        changePinPreference.setOnPreferenceClickListener(preference -> {
            startActivityForResult(new Intent(getActivity(), CreatePinActivity.class), REQUEST_CHANGE_PIN);
            return true;
        });

        Preference clearKeysPreference = findPreference(SecuritySettings.KEY_DELETE_KEYS);
        AssertUtils.requireNonNull(clearKeysPreference);
        clearKeysPreference.setOnPreferenceClickListener(preference -> {
            onClearKeysClick();
            return true;
        });

        findPreference("encryption_terms_of_use").setOnPreferenceClickListener(preference -> {
            View view = View.inflate(getActivity(), R.layout.content_encryption_terms_of_use, null);
            new AlertDialog.Builder(getActivity())
                    .setView(view)
                    .setTitle(R.string.phoenix_encryption)
                    .setNegativeButton(R.string.button_cancel, null)
                    .show();
            return true;
        });
    }

    private void onClearKeysClick() {
        String[] items = {getString(R.string.for_the_current_account), getString(R.string.for_all_accounts)};
        new AlertDialog.Builder(getActivity())
                .setItems(items, (dialog, which) -> onClearKeysClick(which == 1))
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    private void onClearKeysClick(boolean allAccount) {
        if (allAccount) {
            Collection<Integer> accountIds = Settings.get()
                    .accounts()
                    .getRegistered();

            for (Integer accountId : accountIds) {
                removeKeysFor(accountId);
            }
        } else {
            int currentAccountId = Settings.get()
                    .accounts()
                    .getCurrent();

            if (ISettings.IAccountsSettings.INVALID_ID != currentAccountId) {
                removeKeysFor(currentAccountId);
            }
        }

        Toast.makeText(getActivity(), R.string.deleted, Toast.LENGTH_LONG).show();
    }

    private void removeKeysFor(int accountId) {
        Stores.getInstance()
                .keys(KeyLocationPolicy.PERSIST)
                .deleteAll(accountId)
                .blockingAwait();

        Stores.getInstance()
                .keys(KeyLocationPolicy.RAM)
                .deleteAll(accountId)
                .blockingAwait();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.preference_list_fragment, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (actionBar != null) {
            actionBar.setTitle(R.string.settings);
            actionBar.setSubtitle(R.string.security);
        }

        if (getActivity() instanceof OnSectionResumeCallback) {
            ((OnSectionResumeCallback) getActivity()).onSectionResume(NavigationFragment.SECTION_ITEM_SETTINGS);
        }

        new ActivityFeatures.Builder()
                .begin()
                .setBlockNavigationDrawer(false)
                .setStatusBarColored(getActivity(),true)
                .build()
                .apply(getActivity());
    }

    private static final int REQUEST_CREATE_PIN = 1786;
    private static final int REQUEST_CHANGE_PIN = 1787;

    private void startCreatePinActivity() {
        startActivityForResult(new Intent(getActivity(), CreatePinActivity.class), REQUEST_CREATE_PIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CREATE_PIN && resultCode == Activity.RESULT_OK) {
            int[] values = CreatePinFragment.extractValueFromIntent(data);
            Settings.get()
                    .security()
                    .setPin(values);
            mUsePinForSecurityPreference.setChecked(true);
        }

        if (requestCode == REQUEST_CHANGE_PIN && resultCode == Activity.RESULT_OK) {
            int[] values = CreatePinFragment.extractValueFromIntent(data);
            Settings.get()
                    .security()
                    .setPin(values);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case SecuritySettings.KEY_USE_PIN_FOR_SECURITY:
                Boolean usePinForSecurity = (Boolean) newValue;
                if (usePinForSecurity) {
                    if (!Settings.get().security().hasPinHash()) {
                        startCreatePinActivity();
                        return false;
                    } else {
                        // при вызове mUsePinForSecurityPreference.setChecked(true) мы опять попадем в этот блок
                        return true;
                    }
                } else {
                    Settings.get().security().setPin(null);
                    return true;
                }
        }

        return false;
    }
}
