package com.anotap.messenger.activity;

import android.os.Bundle;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.fragment.LocalImageAlbumsFragment;
import com.anotap.messenger.fragment.LocalPhotosFragment;
import com.anotap.messenger.model.LocalImageAlbum;
import com.anotap.messenger.place.Place;
import com.anotap.messenger.place.PlaceProvider;

public class PhotosActivity extends NoMainActivity implements PlaceProvider {

    public static final String EXTRA_MAX_SELECTION_COUNT = "max_selection_count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            attachAlbumsFragment();
        }
    }

    private void attachAlbumsFragment(){
        LocalImageAlbumsFragment ignoredFragment = new LocalImageAlbumsFragment();
        ignoredFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, ignoredFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void openPlace(Place place) {
        if(place.type == Place.LOCAL_IMAGE_ALBUM){
            int maxSelectionCount = getIntent().getIntExtra(EXTRA_MAX_SELECTION_COUNT, 10);
            LocalImageAlbum album = place.getArgs().getParcelable(Extra.ALBUM);
            LocalPhotosFragment localPhotosFragment = LocalPhotosFragment.newInstance(maxSelectionCount, album);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, localPhotosFragment)
                    .addToBackStack("photos")
                    .commit();
        }
    }
}
