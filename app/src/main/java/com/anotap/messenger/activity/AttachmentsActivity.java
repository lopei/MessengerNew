package com.anotap.messenger.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.fragment.DocsFragment;
import com.anotap.messenger.fragment.VideosFragment;
import com.anotap.messenger.fragment.VideosTabsFragment;
import com.anotap.messenger.model.Types;
import com.anotap.messenger.mvp.presenter.DocsListPresenter;
import com.anotap.messenger.place.Place;
import com.anotap.messenger.place.PlaceProvider;
import com.anotap.messenger.upload.UploadUtils;

public class AttachmentsActivity extends NoMainActivity implements PlaceProvider {

    private UploadUtils.ServiceToken mUploadServiceToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUploadServiceToken = UploadUtils.bindToService(this, null);

        if (savedInstanceState == null) {
            Fragment fragment = null;

            int type = getIntent().getExtras().getInt(Extra.TYPE);
            int accountId = getIntent().getExtras().getInt(Extra.ACCOUNT_ID);

            switch (type){
                case Types.DOC:
                    fragment = DocsFragment.newInstance(accountId, accountId, DocsListPresenter.ACTION_SELECT);
                    break;

                case Types.VIDEO:
                    fragment = VideosTabsFragment.newInstance(accountId, accountId, VideosFragment.ACTION_SELECT);
                    break;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public static Intent createIntent(Context context, int accountId, int type){
        return new Intent(context, AttachmentsActivity.class)
                .putExtra(Extra.TYPE, type)
                .putExtra(Extra.ACCOUNT_ID, accountId);
    }

    @Override
    protected void onDestroy() {
        UploadUtils.unbindFromService(mUploadServiceToken);
        super.onDestroy();
    }

    @Override
    public void openPlace(Place place) {
        if(place.type == Place.VIDEO_ALBUM){
            Fragment fragment = VideosFragment.newInstance(place.getArgs());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .addToBackStack("video_album")
                    .commit();
        }
    }
}