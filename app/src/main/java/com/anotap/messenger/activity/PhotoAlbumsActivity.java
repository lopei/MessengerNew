package com.anotap.messenger.activity;

import android.content.Intent;
import android.os.Bundle;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.fragment.VKPhotoAlbumsFragment;
import com.anotap.messenger.fragment.VKPhotosFragment;
import com.anotap.messenger.place.Place;
import com.anotap.messenger.place.PlaceProvider;

public class PhotoAlbumsActivity extends NoMainActivity implements PlaceProvider {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Intent intent = getIntent();

            final int accountId = intent.getExtras().getInt(Extra.ACCOUNT_ID);
            final int ownerId = intent.getExtras().getInt(Extra.OWNER_ID);
            final String action = intent.getStringExtra(Extra.ACTION);

            VKPhotoAlbumsFragment fragment = VKPhotoAlbumsFragment.newInstance(accountId, ownerId, action, null);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void openPlace(Place place) {
        switch (place.type){
            case Place.VK_PHOTO_ALBUM:
                VKPhotosFragment fragment = VKPhotosFragment.newInstance(place.getArgs());
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .addToBackStack("photos")
                        .commit();
                break;
        }
    }
}