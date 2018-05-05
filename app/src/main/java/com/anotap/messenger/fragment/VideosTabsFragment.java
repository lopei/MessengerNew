package com.anotap.messenger.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.ActivityFeatures;
import com.anotap.messenger.activity.ActivityUtils;
import com.anotap.messenger.db.OwnerHelper;
import com.anotap.messenger.fragment.base.BaseFragment;
import com.anotap.messenger.fragment.search.SearchContentType;
import com.anotap.messenger.fragment.search.criteria.VideoSearchCriteria;
import com.anotap.messenger.listener.OnSectionResumeCallback;
import com.anotap.messenger.place.Place;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.settings.CurrentTheme;
import com.anotap.messenger.settings.Settings;

public class VideosTabsFragment extends BaseFragment {

    private int accountId;
    private int ownerId;
    private String action;

    public static Bundle buildArgs(int accountId, int ownerId, String action) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.OWNER_ID, ownerId);
        args.putString(Extra.ACTION, action);
        return args;
    }

    public static VideosTabsFragment newInstance(int accountId, int ownerId, String action) {
        return newInstance(buildArgs(accountId, ownerId, action));
    }

    public static VideosTabsFragment newInstance(Bundle args) {
        VideosTabsFragment fragment = new VideosTabsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        accountId = getArguments().getInt(Extra.ACCOUNT_ID);
        ownerId = getArguments().getInt(Extra.OWNER_ID);
        action = getArguments().getString(Extra.ACTION);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_videos_tabs, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        ViewPager viewPager = view.findViewById(R.id.fragment_videos_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = view.findViewById(R.id.fragment_videos_tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        int tabColorPrimary = CurrentTheme.getPrimaryTextColorOnColoredBackgroundCode(getActivity());
        int tabColorSecondary = CurrentTheme.getSecondaryTextColorOnColoredBackgroundCode(getActivity());
        tabLayout.setTabTextColors(tabColorSecondary, tabColorPrimary);
        tabLayout.setupWithViewPager(viewPager);
    }

    public int getAccountId() {
        return accountId;
    }

    private void setupViewPager(ViewPager viewPager) {
        VideosFragment fragment = VideosFragment.newInstance(getAccountId(), ownerId, 0, action, null);
        fragment.getArguments().putBoolean(VideosFragment.EXTRA_IN_TABS_CONTAINER, true);

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(fragment, getString(R.string.videos_my));
        adapter.addFragment(VideoAlbumsFragment.newInstance(getAccountId(), ownerId, action), getString(R.string.videos_albums));
        viewPager.setAdapter(adapter);
    }

    private boolean isMy() {
        return getAccountId() == ownerId;
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.get().ui().notifyPlaceResumed(Place.VIDEOS);

        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (actionBar != null) {
            actionBar.setTitle(R.string.videos);
            actionBar.setSubtitle(isMy() ? null : OwnerHelper.loadOwnerFullName(getActivity(), getAccountId(), ownerId));
        }

        if (getActivity() instanceof OnSectionResumeCallback) {
            if (isMy()) {
                ((OnSectionResumeCallback) getActivity()).onSectionResume(NavigationFragment.SECTION_ITEM_VIDEOS);
            } else {
                ((OnSectionResumeCallback) getActivity()).onClearSelection();
            }
        }

        new ActivityFeatures.Builder()
                .begin()
                .setBlockNavigationDrawer(false)
                .setStatusBarColored(getActivity(),true)
                .build()
                .apply(getActivity());
    }

    static class Adapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                VideoSearchCriteria criteria = new VideoSearchCriteria("");
                PlaceFactory.getSingleTabSearchPlace(getAccountId(), SearchContentType.VIDEOS, criteria).tryOpenWith(getActivity());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_video_main, menu);
    }
}