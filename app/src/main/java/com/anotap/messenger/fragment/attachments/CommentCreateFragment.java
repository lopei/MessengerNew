package com.anotap.messenger.fragment.attachments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.ActivityFeatures;
import com.anotap.messenger.activity.ActivityUtils;
import com.anotap.messenger.listener.OnSectionResumeCallback;
import com.anotap.messenger.mvp.presenter.CommentCreatePresenter;
import com.anotap.messenger.mvp.view.ICreateCommentView;
import com.anotap.mvp.core.IPresenterFactory;

import static com.anotap.messenger.util.Objects.nonNull;

/**
 * Created by admin on 27.03.2017.
 * phoenix
 */
public class CommentCreateFragment extends AbsAttachmentsEditFragment<CommentCreatePresenter, ICreateCommentView>
        implements ICreateCommentView {

    public static CommentCreateFragment newInstance(int accountId, int commentDbid, int sourceOwnerId, String body) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.COMMENT_ID, commentDbid);
        args.putInt(Extra.OWNER_ID, sourceOwnerId);
        args.putString(Extra.BODY, body);
        CommentCreateFragment fragment = new CommentCreateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
    public IPresenterFactory<CommentCreatePresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int accountId = getArguments().getInt(Extra.ACCOUNT_ID);
            int commentDbid = getArguments().getInt(Extra.COMMENT_ID);
            int sourceOwnerId = getArguments().getInt(Extra.COMMENT_ID);
            String body = getArguments().getString(Extra.BODY);
            return new CommentCreatePresenter(accountId, commentDbid, sourceOwnerId, body, saveInstanceState);
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        ActivityUtils.setToolbarTitle(this, R.string.new_comment);
        ActivityUtils.setToolbarSubtitle(this, null);

        new ActivityFeatures.Builder()
                .begin()
                .setBlockNavigationDrawer(true)
                .setStatusBarColored(getActivity(),true)
                .build()
                .apply(getActivity());

        if(getActivity() instanceof OnSectionResumeCallback){
            ((OnSectionResumeCallback) getActivity()).onClearSelection();
        }
    }

    @Override
    protected String tag() {
        return CommentCreateFragment.class.getSimpleName();
    }

    @Override
    public boolean onBackPressed() {
        return getPresenter().onBackPressed();
    }

    @Override
    public void returnDataToParent(String textBody) {
        Intent data = new Intent();
        data.putExtra(Extra.BODY, textBody);

        if(nonNull(getTargetFragment())){
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
        }
    }

    @Override
    public void goBack() {
        getActivity().onBackPressed();
    }
}
