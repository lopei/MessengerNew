package com.anotap.messenger.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import com.anotap.messenger.Extra;
import com.anotap.messenger.fragment.attachments.PostCreateFragment;
import com.anotap.messenger.model.EditingPostType;
import com.anotap.messenger.model.WallEditorAttrs;
import com.anotap.messenger.util.AssertUtils;
import com.anotap.messenger.util.Objects;

/**
 * Created by admin on 3/26/2018.
 * Phoenix-for-VK
 */
public class PostCreateActivity extends NoMainActivity {

    public static Intent newIntent(@NonNull Context context, int accountId, @NonNull WallEditorAttrs attrs, @Nullable ArrayList<Uri> streams) {
        return new Intent(context, PostCreateActivity.class)
                .putExtra(Extra.ACCOUNT_ID, accountId)
                .putParcelableArrayListExtra("streams", streams)
                .putExtra("attrs", attrs);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Objects.isNull(savedInstanceState)) {
            AssertUtils.requireNonNull(getIntent().getExtras());

            int accountId = getIntent().getExtras().getInt(Extra.ACCOUNT_ID);
            ArrayList<Uri> streams = getIntent().getParcelableArrayListExtra("streams");
            WallEditorAttrs attrs = getIntent().getParcelableExtra("attrs");

            Bundle args = PostCreateFragment.buildArgs(accountId, attrs.getOwner().getOwnerId(), EditingPostType.TEMP, null, attrs, streams, null);

            PostCreateFragment fragment = PostCreateFragment.newInstance(args);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(getMainContainerViewId(), fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }
}