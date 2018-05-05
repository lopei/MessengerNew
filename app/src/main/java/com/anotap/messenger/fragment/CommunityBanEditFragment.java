package com.anotap.messenger.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.ActivityFeatures;
import com.anotap.messenger.activity.ActivityUtils;
import com.anotap.messenger.fragment.base.BasePresenterFragment;
import com.anotap.messenger.listener.TextWatcherAdapter;
import com.anotap.messenger.model.Banned;
import com.anotap.messenger.model.IdOption;
import com.anotap.messenger.model.User;
import com.anotap.messenger.mvp.presenter.CommunityBanEditPresenter;
import com.anotap.messenger.mvp.view.ICommunityBanEditView;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.util.FormatUtil;
import com.anotap.messenger.util.Objects;
import com.anotap.messenger.util.RoundTransformation;
import com.anotap.messenger.util.Utils;
import com.anotap.messenger.util.ViewUtils;
import com.anotap.messenger.view.MySpinnerView;
import com.anotap.messenger.view.OnlineView;
import com.anotap.mvp.core.IPresenterFactory;

/**
 * Created by admin on 17.06.2017.
 * phoenix
 */
public class CommunityBanEditFragment extends BasePresenterFragment<CommunityBanEditPresenter, ICommunityBanEditView>
        implements ICommunityBanEditView {

    public static CommunityBanEditFragment newInstance(int accountId, int groupId, Banned banned) {
        return newInstance(accountId, groupId, banned, null);
    }

    public static CommunityBanEditFragment newInstance(int accountId, int groupId, ArrayList<User> users) {
        return newInstance(accountId, groupId, null, users);
    }

    private static CommunityBanEditFragment newInstance(int accountId, int groupId, Banned banned, ArrayList<User> users) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.GROUP_ID, groupId);
        args.putParcelableArrayList(Extra.USERS, users);
        args.putParcelable(Extra.BANNED, banned);
        CommunityBanEditFragment fragment = new CommunityBanEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ImageView mAvatar;
    private OnlineView mOnlineView;
    private TextView mName;
    private TextView mDomain;

    private TextView mBanStatus;

    private MySpinnerView mBlockFor;
    private MySpinnerView mReason;

    private EditText mComment;
    private CheckBox mShowComment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_community_ban_edit, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        mAvatar = root.findViewById(R.id.avatar);
        mAvatar.setOnClickListener(v -> getPresenter().fireAvatarClick());

        mOnlineView = root.findViewById(R.id.online);
        mName = root.findViewById(R.id.name);
        mDomain = root.findViewById(R.id.domain);

        mBanStatus = root.findViewById(R.id.status);

        mBlockFor = root.findViewById(R.id.spinner_block_for);
        mBlockFor.setIconOnClickListener(v -> getPresenter().fireBlockForClick());

        mReason = root.findViewById(R.id.spinner_reason);
        mReason.setIconOnClickListener(v -> getPresenter().fireResonClick());

        mComment = root.findViewById(R.id.community_ban_comment);
        mComment.addTextChangedListener(new TextWatcherAdapter(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPresenter().fireCommentEdit(s);
            }
        });

        mShowComment = root.findViewById(R.id.community_ban_show_comment_to_user);
        mShowComment.setOnCheckedChangeListener((buttonView, checked) -> getPresenter().fireShowCommentCheck(checked));
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.community_ban_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_save){
            getPresenter().fireButtonSaveClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityUtils.setToolbarTitle(this, R.string.block_user);
        ActivityUtils.setToolbarSubtitle(this, null);

        new ActivityFeatures.Builder()
                .begin()
                .setBlockNavigationDrawer(true)
                .setStatusBarColored(getActivity(),true)
                .build()
                .apply(getActivity());
    }

    @Override
    public IPresenterFactory<CommunityBanEditPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int accountId = getArguments().getInt(Extra.ACCOUNT_ID);
            int groupId = getArguments().getInt(Extra.GROUP_ID);
            Banned banned = getArguments().getParcelable(Extra.BANNED);
            ArrayList<User> users = getArguments().getParcelableArrayList(Extra.USERS);
            return Objects.nonNull(banned) ? new CommunityBanEditPresenter(accountId, groupId, banned, saveInstanceState)
                    : new CommunityBanEditPresenter(accountId, groupId, users, saveInstanceState);
        };
    }

    @Override
    protected String tag() {
        return CommunityBanEditFragment.class.getSimpleName();
    }

    @Override
    public void displayUserInfo(User user) {
        if(Objects.nonNull(mAvatar)){
            ViewUtils.displayAvatar(mAvatar, new RoundTransformation(), user.getMaxSquareAvatar(), null);
        }

        safelySetText(mName, user.getFullName());

        Integer iconRes = ViewUtils.getOnlineIcon(user.isOnline(), user.isOnlineMobile(), user.getPlatform(), user.getOnlineApp());

        if(Objects.nonNull(mOnlineView)){
            mOnlineView.setVisibility(Objects.nonNull(iconRes) ? View.VISIBLE : View.INVISIBLE);

            if(Objects.nonNull(iconRes)){
                mOnlineView.setIcon(iconRes);
            }
        }

        if(Utils.nonEmpty(user.getDomain())){
            safelySetText(mDomain, "@" + user.getDomain());
        } else {
            safelySetText(mDomain, "@id" + user.getId());
        }
    }

    @Override
    public void displayBanStatus(int adminId, String adminName, long endDate) {
        if(Objects.nonNull(mBanStatus)){
            try {
                Context context = mBanStatus.getContext();
                Spannable spannable = FormatUtil.formatCommunityBanInfo(context, adminId, adminName, endDate, null);
                mBanStatus.setText(spannable, TextView.BufferType.SPANNABLE);
                mBanStatus.setMovementMethod(LinkMovementMethod.getInstance());
            } catch (Exception ignored){
                ignored.printStackTrace();
            }
        }
    }

    @Override
    public void displayBlockFor(String blockFor) {
        if(Objects.nonNull(mBlockFor)){
            mBlockFor.setValue(blockFor);
        }
    }

    @Override
    public void displayReason(String reason) {
        if(Objects.nonNull(mReason)){
            mReason.setValue(reason);
        }
    }

    @Override
    public void diplayComment(String comment) {
        safelySetText(mComment, comment);
    }

    @Override
    public void setShowCommentChecked(boolean checked) {
        safelySetCheched(mShowComment, checked);
    }

    @Override
    public void goBack() {
        getActivity().onBackPressed();
    }

    @Override
    public void displaySelectOptionDialog(int requestCode, List<IdOption> options) {
        String[] strings = new String[options.size()];
        for(int i = 0; i < options.size(); i++){
            strings[i] = options.get(i).getTitle();
        }

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.select_from_list_title)
                .setItems(strings, (dialog, which) -> getPresenter().fireOptionSelected(requestCode, options.get(which)))
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    @Override
    public void openProfile(int accountId, User user) {
        PlaceFactory.getOwnerWallPlace(accountId, user).tryOpenWith(getActivity());
    }
}