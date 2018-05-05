package com.anotap.messenger.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.ActivityFeatures;
import com.anotap.messenger.activity.ActivityUtils;
import com.anotap.messenger.media.gif.IGifPlayer;
import com.anotap.messenger.model.Document;
import com.anotap.messenger.mvp.presenter.GifPagerPresenter;
import com.anotap.messenger.mvp.view.IGifPagerView;
import com.anotap.messenger.util.AssertUtils;
import com.anotap.messenger.util.Objects;
import com.anotap.messenger.view.AlternativeAspectRatioFrameLayout;
import com.anotap.messenger.view.CircleCounterButton;
import com.anotap.messenger.view.FlingRelativeLayout;
import com.anotap.messenger.view.pager.AbsPagerHolder;
import com.anotap.messenger.view.pager.CloseOnFlingListener;
import com.anotap.mvp.core.IPresenterFactory;

/**
 * Created by ruslan.kolbasa on 11.10.2016.
 * phoenix
 */
public class GifPagerFragment extends AbsDocumentPreviewFragment<GifPagerPresenter, IGifPagerView>
        implements IGifPagerView {

    private static final String TAG = GifPagerFragment.class.getSimpleName();

    private ViewPager mViewPager;

    private Toolbar mToolbar;
    private View mButtonsRoot;
    private CircleCounterButton mButtonAddOrDelete;

    public static GifPagerFragment newInstance(Bundle args) {
        GifPagerFragment fragment = new GifPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static Bundle buildArgs(int aid, @NonNull ArrayList<Document> documents, int index) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, aid);
        args.putInt(Extra.INDEX, index);
        args.putParcelableArrayList(Extra.DOCS, documents);
        return args;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mFullscreen = savedInstanceState.getBoolean("mFullscreen");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gif_pager, container, false);

        mToolbar = root.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);

        mButtonsRoot = root.findViewById(R.id.buttons);

        mButtonAddOrDelete = root.findViewById(R.id.button_add_or_delete);
        mButtonAddOrDelete.setOnClickListener(v -> getPresenter().fireAddDeleteButtonClick());

        mViewPager = root.findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getPresenter().firePageSelected(position);
            }
        });

        root.findViewById(R.id.button_share).setOnClickListener(v -> getPresenter().fireShareButtonClick());
        root.findViewById(R.id.button_download).setOnClickListener(v -> getPresenter().fireDownloadButtonClick());



        resolveFullscreenViews();
        return root;
    }

    private void goBack() {
        requireActivity().onBackPressed();
    }

    private boolean mFullscreen;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mFullscreen", mFullscreen);
    }

    @Override
    public void onResume() {
        super.onResume();
        new ActivityFeatures.Builder()
                .begin()
                .setBlockNavigationDrawer(true)
                .setStatusBarColored(false, false)
                .build()
                .apply(requireActivity());
    }

    private void toggleFullscreen() {
        mFullscreen = !mFullscreen;
        resolveFullscreenViews();
    }

    private void resolveFullscreenViews() {
        if (Objects.nonNull(mToolbar)) {
            mToolbar.setVisibility(mFullscreen ? View.GONE : View.VISIBLE);
        }

        if (Objects.nonNull(mButtonsRoot)) {
            mButtonsRoot.setVisibility(mFullscreen ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected String tag() {
        return TAG;
    }

    @Override
    public IPresenterFactory<GifPagerPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int aid = getArguments().getInt(Extra.ACCOUNT_ID);
            int index = getArguments().getInt(Extra.INDEX);

            ArrayList<Document> documents = getArguments().getParcelableArrayList(Extra.DOCS);
            AssertUtils.requireNonNull(documents);

            // todo TEMP SOLUTION !!! FIX IT
            if(documents.size() > 50){
                getArguments().remove(Extra.DOCS);
            }

            return new GifPagerPresenter(aid, documents, index, saveInstanceState);
        };
    }

    @Override
    public void displayData(int pageCount, int selectedIndex) {
        if (Objects.nonNull(mViewPager)) {
            Adapter adapter = new Adapter(pageCount);
            mViewPager.setAdapter(adapter);
            mViewPager.setCurrentItem(selectedIndex);
        }
    }

    @Override
    public void setAspectRatioAt(int position, int w, int h) {
        Holder holder = mHolderSparseArray.get(position);
        if (Objects.nonNull(holder)) {
            holder.mAspectRatioLayout.setAspectRatio(w, h);
        }
    }

    @Override
    public void setPreparingProgressVisible(int position, boolean preparing) {
        for (int i = 0; i < mHolderSparseArray.size(); i++) {
            int key = mHolderSparseArray.keyAt(i);
            Holder holder = mHolderSparseArray.get(key);

            if (Objects.nonNull(holder)) {
                boolean isCurrent = position == key;
                boolean progressVisible = isCurrent && preparing;

                holder.setProgressVisible(progressVisible);
                holder.mSurfaceView.setVisibility(isCurrent && !preparing ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    public void setupAddRemoveButton(boolean addEnable) {
        if (Objects.nonNull(mButtonAddOrDelete)) {
            mButtonAddOrDelete.setIcon(addEnable ? R.drawable.plus : R.drawable.delete);
        }
    }

    @Override
    public void attachDisplayToPlayer(int adapterPosition, IGifPlayer gifPlayer) {
        Holder holder = mHolderSparseArray.get(adapterPosition);
        if (Objects.nonNull(holder) && Objects.nonNull(gifPlayer) && holder.isSurfaceReady()) {
            gifPlayer.setDisplay(holder.mSurfaceHolder);
        }
    }

    @Override
    public void setToolbarTitle(@StringRes int titleRes, Object... params) {
        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (Objects.nonNull(actionBar)) {
            actionBar.setTitle(getString(titleRes, params));
        }
    }

    @Override
    public void setToolbarSubtitle(@StringRes int titleRes, Object... params) {
        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (Objects.nonNull(actionBar)) {
            actionBar.setSubtitle(getString(titleRes, params));
        }
    }

    @Override
    public void configHolder(int adapterPosition, boolean progress, int aspectRatioW, int aspectRatioH) {
        Holder holder = mHolderSparseArray.get(adapterPosition);
        if(Objects.nonNull(holder)){
            holder.setProgressVisible(progress);
            holder.mAspectRatioLayout.setAspectRatio(aspectRatioW, aspectRatioH);
            holder.mSurfaceView.setVisibility(progress ? View.GONE : View.VISIBLE);
        }
    }

    private void fireHolderDestroy(@NonNull Holder holder) {

    }

    private void fireHolderCreate(@NonNull Holder holder) {
        getPresenter().fireHolderCreate(holder.getAdapterPosition());
    }

    private final class Holder extends AbsPagerHolder implements SurfaceHolder.Callback {

        SurfaceView mSurfaceView;
        SurfaceHolder mSurfaceHolder;
        ProgressBar mProgressBar;
        AlternativeAspectRatioFrameLayout mAspectRatioLayout;
        boolean mSurfaceReady;

        Holder(int adapterPosition, View rootView) {
            super(adapterPosition, rootView);
            FlingRelativeLayout flingRelativeLayout = rootView.findViewById(R.id.fling_root_view);
            flingRelativeLayout.setOnClickListener(v -> toggleFullscreen());
            flingRelativeLayout.setOnSingleFlingListener(new CloseOnFlingListener(rootView.getContext()) {
                @Override
                public boolean onVerticalFling(float distanceByY) {
                    goBack();
                    return true;
                }
            });

            mSurfaceView = rootView.findViewById(R.id.surface_view);
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(this);

            mAspectRatioLayout = rootView.findViewById(R.id.aspect_ratio_layout);
            mProgressBar = rootView.findViewById(R.id.preparing_progress_bar);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceReady = true;
            if(isPresenterPrepared()){
                getPresenter().fireSurfaceCreated(getAdapterPosition());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceReady = false;
        }

        boolean isSurfaceReady() {
            return mSurfaceReady;
        }

        void setProgressVisible(boolean visible){
            mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    private SparseArray<Holder> mHolderSparseArray = new SparseArray<>();

    private class Adapter extends PagerAdapter {

        int mPageCount;

        Adapter(int count) {
            super();
            mPageCount = count;
            mHolderSparseArray.clear();
        }

        @Override
        public int getCount() {
            return mPageCount;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View stepView = LayoutInflater.from(getActivity()).inflate(R.layout.content_gif_page, container, false);

            final Holder holder = new Holder(position, stepView);

            mHolderSparseArray.put(position, holder);

            fireHolderCreate(holder);
            container.addView(stepView);
            return stepView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
            Holder holder = mHolderSparseArray.get(position);
            if (holder != null) {
                fireHolderDestroy(holder);
            }

            mHolderSparseArray.remove(position);
            container.removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object key) {
            return key == view;
        }
    }
}