package com.anotap.messenger.fragment.attachments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.ActivityFeatures;
import com.anotap.messenger.activity.ActivityUtils;
import com.anotap.messenger.model.Comment;
import com.anotap.messenger.mvp.presenter.CommentEditPresenter;
import com.anotap.messenger.mvp.view.ICommentEditView;
import com.anotap.mvp.core.IPresenterFactory;

import static com.anotap.messenger.util.Objects.nonNull;

/**
 * Created by admin on 06.05.2017.
 * phoenix
 */
public class CommentEditFragment extends AbsAttachmentsEditFragment<CommentEditPresenter, ICommentEditView>
        implements ICommentEditView {

    public static CommentEditFragment newInstance(int accountId, Comment comment) {
        Bundle args = new Bundle();
        args.putParcelable(Extra.COMMENT, comment);
        args.putInt(Extra.ACCOUNT_ID, accountId);
        CommentEditFragment fragment = new CommentEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public IPresenterFactory<CommentEditPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int aid = getArguments().getInt(Extra.ACCOUNT_ID);
            Comment comment = getArguments().getParcelable(Extra.COMMENT);
            return new CommentEditPresenter(comment, aid, saveInstanceState);
        };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_attchments, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ready:
                getPresenter().fireReadyClick();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        ActivityUtils.setToolbarTitle(this, R.string.comment_editing_title);
        ActivityUtils.setToolbarSubtitle(this, null);

        new ActivityFeatures.Builder()
                .begin()
                .setBlockNavigationDrawer(true)
                .setStatusBarColored(getActivity(),true)
                .build()
                .apply(getActivity());
    }

    @Override
    protected String tag() {
        return CommentEditFragment.class.getSimpleName();
    }

    @Override
    public boolean onBackPressed() {
        return getPresenter().onBackPressed();
    }

    @Override
    public void goBackWithResult(@Nullable Comment comment) {
        Intent data = new Intent();
        data.putExtra(Extra.COMMENT, comment);

        if(nonNull(getTargetFragment())){
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
        }

        getActivity().onBackPressed();
    }

    @Override
    public void showConfirmWithoutSavingDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.confirmation)
                .setMessage(R.string.save_changes_question)
                .setPositiveButton(R.string.button_yes, (dialog, which) -> getPresenter().fireReadyClick())
                .setNegativeButton(R.string.button_no, (dialog, which) -> getPresenter().fireSavingCancelClick())
                .setNeutralButton(R.string.button_cancel, null)
                .show();
    }

    @Override
    public void goBack() {
        getActivity().onBackPressed();
    }
}