package com.anotap.messenger.fragment.attachments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.ActivityFeatures;
import com.anotap.messenger.model.Post;
import com.anotap.messenger.model.WallEditorAttrs;
import com.anotap.messenger.mvp.presenter.PostEditPresenter;
import com.anotap.messenger.mvp.view.IPostEditView;
import com.anotap.messenger.util.AssertUtils;
import com.anotap.mvp.core.IPresenterFactory;

/**
 * Created by admin on 21.01.2017.
 * phoenix
 */
public class PostEditFragment extends AbsPostEditFragment<PostEditPresenter, IPostEditView>
        implements IPostEditView {

    public static PostEditFragment newInstance(Bundle args){
        PostEditFragment fragment = new PostEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public static Bundle buildArgs(int accountId, @NonNull Post post, @NonNull WallEditorAttrs attrs) {
        Bundle args = new Bundle();
        args.putParcelable(Extra.POST, post);
        args.putParcelable(Extra.ATTRS, attrs);
        args.putInt(Extra.ACCOUNT_ID, accountId);
        return args;
    }

    @Override
    public IPresenterFactory<PostEditPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            Post post = getArguments().getParcelable(Extra.POST);
            int accountId = getArguments().getInt(Extra.ACCOUNT_ID);
            WallEditorAttrs attrs = getArguments().getParcelable(Extra.ATTRS);

            AssertUtils.requireNonNull(post);
            AssertUtils.requireNonNull(attrs);
            return new PostEditPresenter(accountId, post, attrs, saveInstanceState);
        };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_attchments, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        new ActivityFeatures.Builder()
                .begin()
                .setBlockNavigationDrawer(true)
                .setStatusBarColored(getActivity(),true)
                .build()
                .apply(getActivity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ready:
                getPresenter().fireReadyClick();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String tag() {
        return PostEditFragment.class.getSimpleName();
    }

    @Override
    public void closeAsSuccess() {
        getActivity().onBackPressed();
    }

    @Override
    public void showConfirmExitDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.confirmation)
                .setMessage(R.string.save_changes_question)
                .setPositiveButton(R.string.button_yes, (dialog, which) -> getPresenter().fireExitWithSavingConfirmed())
                .setNegativeButton(R.string.button_no, (dialog, which) -> getPresenter().fireExitWithoutSavingClick())
                .setNeutralButton(R.string.button_cancel, null)
                .show();
    }

    @Override
    public boolean onBackPressed() {
        return getPresenter().onBackPressed();
    }
}