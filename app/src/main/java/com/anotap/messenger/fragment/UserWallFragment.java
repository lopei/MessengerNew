package com.anotap.messenger.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.ActivityUtils;
import com.anotap.messenger.adapter.horizontal.HorizontalOptionsAdapter;
import com.anotap.messenger.api.PicassoInstance;
import com.anotap.messenger.model.FriendsCounters;
import com.anotap.messenger.model.ParcelableOwnerWrapper;
import com.anotap.messenger.model.PostFilter;
import com.anotap.messenger.model.User;
import com.anotap.messenger.model.UserDetails;
import com.anotap.messenger.mvp.presenter.UserWallPresenter;
import com.anotap.messenger.mvp.view.IUserWallView;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.util.AssertUtils;
import com.anotap.messenger.util.InputTextDialog;
import com.anotap.messenger.util.RoundTransformation;
import com.anotap.messenger.util.ViewUtils;
import com.anotap.messenger.view.OnlineView;
import com.anotap.mvp.core.IPresenterFactory;

import static com.anotap.messenger.util.Objects.isNull;
import static com.anotap.messenger.util.Objects.nonNull;
import static com.anotap.messenger.util.Utils.nonEmpty;

/**
 * Created by ruslan.kolbasa on 23.01.2017.
 * phoenix
 */
public class UserWallFragment extends AbsWallFragment<IUserWallView, UserWallPresenter>
        implements IUserWallView {

    private UserHeaderHolder mHeaderHolder;

    @Override
    public void displayBaseUserInfo(User user) {
        if(isNull(mHeaderHolder)) return;

        mHeaderHolder.tvName.setText(user.getFullName());
        mHeaderHolder.tvLastSeen.setText(UserInfoResolveUtil.getUserActivityLine(getContext(), user));

        String screenName = nonEmpty(user.getDomain()) ? "@" + user.getDomain() : null;
        mHeaderHolder.tvScreenName.setText(screenName);

        String photoUrl = user.getMaxSquareAvatar();

        if (nonEmpty(photoUrl)) {
            PicassoInstance.with()
                    .load(photoUrl)
                    .transform(new RoundTransformation())
                    .into(mHeaderHolder.ivAvatar);
        }

        Integer onlineIcon = ViewUtils.getOnlineIcon(user.isOnline(), user.isOnlineMobile(), user.getPlatform(), user.getOnlineApp());
        mHeaderHolder.ivOnline.setVisibility(user.isOnline() ? View.VISIBLE : View.GONE);

        if (onlineIcon != null) {
            mHeaderHolder.ivOnline.setIcon(onlineIcon);
        }
    }

    @Override
    public void openUserDetails(int accountId, @NonNull User user, @NonNull UserDetails details) {
        PlaceFactory.getUserDetailsPlace(accountId, user, details).tryOpenWith(requireActivity());
    }

    /*@Override
    public void displayOwnerData(User user) {
        if (isNull(mHeaderHolder)) return;

        mHeaderHolder.tvName.setText(user.getFullName());
        mHeaderHolder.tvLastSeen.setText(UserInfoResolveUtil.getUserActivityLine(getActivity(), user));
        mHeaderHolder.tvLastSeen.setAllCaps(false);

        String screenName = "@" + user.screen_name;
        mHeaderHolder.tvScreenName.setText(screenName);

        if (isNull(user.status_audio)) {
            String status = "\"" + user.status + "\"";
            mHeaderHolder.tvStatus.setText(status);
        } else {
            String status = user.status_audio.artist + '-' + user.status_audio.title;
            mHeaderHolder.tvStatus.setText(status);
        }

        mHeaderHolder.tvStatus.setVisibility(isEmpty(user.status) && user.status_audio == null ? View.GONE : View.VISIBLE);

        String photoUrl = user.getMaxSquareAvatar();

        if (nonEmpty(photoUrl)) {
            PicassoInstance.with()
                    .load(photoUrl)
                    .transform(new RoundTransformation())
                    .into(mHeaderHolder.ivAvatar);
        }

        Integer onlineIcon = ViewUtils.getOnlineIcon(user.online, user.online_mobile, user.platform, user.online_app);
        mHeaderHolder.ivOnline.setVisibility(user.online ? View.VISIBLE : View.GONE);

        if (onlineIcon != null) {
            mHeaderHolder.ivOnline.setIcon(onlineIcon);
        }

        *//*View mainUserInfoView = mHeaderHolder.infoSections.findViewById(R.id.section_contact_info);
        UserInfoResolveUtil.fillMainUserInfo(getActivity(), mainUserInfoView, user, new LinkActionAdapter() {
            @Override
            public void onOwnerClick(int ownerId) {
                onOpenOwner(ownerId);
            }
        });

        UserInfoResolveUtil.fill(getActivity(), mHeaderHolder.infoSections.findViewById(R.id.section_beliefs), user);
        UserInfoResolveUtil.fillPersonalInfo(getActivity(), mHeaderHolder.infoSections.findViewById(R.id.section_personal), user);*//*

        SelectionUtils.addSelectionProfileSupport(getContext(), mHeaderHolder.avatarRoot, user);
    }*/

    @Override
    public void displayCounters(int friends, int followers, int groups, int photos, int audios, int videos) {
        if(nonNull(mHeaderHolder)){
            setupCounter(mHeaderHolder.bFriends, friends);
            setupCounter(mHeaderHolder.bFollowers, followers);
            setupCounter(mHeaderHolder.bGroups, groups);
            setupCounter(mHeaderHolder.bPhotos, photos);
            setupCounter(mHeaderHolder.bAudios, audios);
            setupCounter(mHeaderHolder.bVideos, videos);
        }
    }

    @Override
    public void displayUserStatus(String statusText) {
        if(nonNull(mHeaderHolder)){
            mHeaderHolder.tvStatus.setText(statusText);
        }
    }

    @Override
    protected String tag() {
        return UserWallFragment.class.getSimpleName();
    }

    @Override
    protected int headerLayout() {
        return R.layout.header_user_profile;
    }

    @Override
    protected void onHeaderInflated(View headerRootView) {
        mHeaderHolder = new UserHeaderHolder(headerRootView);
        mHeaderHolder.ivAvatar.setOnClickListener(v -> getPresenter().fireAvatarClick());
    }

    @Override
    public IPresenterFactory<UserWallPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            AssertUtils.requireNonNull(getArguments());

            int accoutnId = getArguments().getInt(Extra.ACCOUNT_ID);
            int ownerId = getArguments().getInt(Extra.OWNER_ID);

            ParcelableOwnerWrapper wrapper = getArguments().getParcelable(Extra.OWNER);
            AssertUtils.requireNonNull(wrapper);

            return new UserWallPresenter(accoutnId, ownerId, (User) wrapper.get(), saveInstanceState);
        };
    }

    @Override
    public void displayWallFilters(List<PostFilter> filters) {
        if (nonNull(mHeaderHolder)) {
            mHeaderHolder.mPostFilterAdapter.setItems(filters);
        }
    }

    @Override
    public void notifyWallFiltersChanged() {
        if (nonNull(mHeaderHolder)) {
            mHeaderHolder.mPostFilterAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setupPrimaryActionButton(@StringRes Integer title) {
        if (nonNull(mHeaderHolder)) {
            if(nonNull(title)){
                mHeaderHolder.bPrimaryAction.setText(title);
            } else {
                mHeaderHolder.bPrimaryAction.setText(null);
            }
        }
    }

    @Override
    public void openFriends(int accountId, int userId, int tab, FriendsCounters counters) {
        PlaceFactory.getFriendsFollowersPlace(accountId, userId, tab, counters).tryOpenWith(requireActivity());
    }

    @Override
    public void openGroups(int accountId, int userId, @Nullable User user) {
        PlaceFactory.getCommunitiesPlace(accountId, userId)
                .withParcelableExtra(Extra.USER, user)
                .tryOpenWith(requireActivity());
    }

    @Override
    public void showEditStatusDialog(String initialValue) {
        new InputTextDialog.Builder(getActivity())
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .setTitleRes(R.string.edit_status)
                .setHint(R.string.enter_your_status)
                .setValue(initialValue)
                .setAllowEmpty(true)
                .setCallback(newValue -> getPresenter().fireNewStatusEntered(newValue))
                .show();
    }

    @Override
    public void showAddToFriendsMessageDialog() {
        new InputTextDialog.Builder(getActivity())
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .setTitleRes(R.string.add_to_friends)
                .setHint(R.string.attach_message)
                .setAllowEmpty(true)
                .setCallback(newValue -> getPresenter().fireAddToFrindsClick(newValue))
                .show();
    }

    @Override
    public void showAvatarContextMenu() {
        //String[] items = {getString(R.string.open_photo_album), getString(R.string.upload_new_photo)};
        String[] items = {getString(R.string.open_photo_album)};

        new AlertDialog.Builder(getActivity()).setItems(items, (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    getPresenter().fireOpenAvatarsPhotoAlbum();
                    break;
                //case 1:
                    //Intent attachPhotoIntent = new Intent(getActivity(), PhotosActivity.class);
                    //attachPhotoIntent.putExtra(PhotosActivity.EXTRA_MAX_SELECTION_COUNT, 1);
                    //startActivityForResult(attachPhotoIntent, REQUEST_UPLOAD_AVATAR);

                    // TODO: 26.03.2017
                    //break;
            }
        }).setCancelable(true).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityUtils.setToolbarTitle(this, R.string.profile);
        ActivityUtils.setToolbarSubtitle(this, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(R.string.add_to_bookmarks).setOnMenuItemClickListener(item -> {
            getPresenter().fireAddToBookmarks();
            return true;
        });

        menu.add(R.string.add_to_blacklist).setOnMenuItemClickListener(item -> {
            getPresenter().fireAddToBlacklistClick();
            return true;
        });
    }

    private class UserHeaderHolder {

        ViewGroup avatarRoot;
        ImageView ivAvatar;
        TextView tvName;
        TextView tvScreenName;
        TextView tvStatus;
        TextView tvLastSeen;
        OnlineView ivOnline;

        TextView bFriends;
        TextView bFollowers;
        TextView bGroups;
        TextView bPhotos;
        TextView bAudios;
        TextView bVideos;

        FloatingActionButton fabMessage;
        Button bToggleInfo;
        Button bPrimaryAction;

        HorizontalOptionsAdapter<PostFilter> mPostFilterAdapter;

        UserHeaderHolder(@NonNull View root) {
            tvStatus = root.findViewById(R.id.fragment_user_profile_status);
            tvName = root.findViewById(R.id.fragment_user_profile_name);
            tvScreenName = root.findViewById(R.id.fragment_user_profile_id);
            tvLastSeen = root.findViewById(R.id.fragment_user_profile_activity);
            avatarRoot = root.findViewById(R.id.fragment_user_profile_avatar_container);
            ivAvatar = root.findViewById(R.id.avatar);
            ivOnline = root.findViewById(R.id.header_navi_menu_online);
            bFriends = root.findViewById(R.id.fragment_user_profile_bfriends);
            bFollowers = root.findViewById(R.id.fragment_user_profile_bfollowers);
            bGroups = root.findViewById(R.id.fragment_user_profile_bgroups);
            bPhotos = root.findViewById(R.id.fragment_user_profile_bphotos);
            bAudios = root.findViewById(R.id.fragment_user_profile_baudios);
            bVideos = root.findViewById(R.id.fragment_user_profile_bvideos);
            fabMessage = root.findViewById(R.id.header_user_profile_fab_message);
            bToggleInfo = root.findViewById(R.id.info_btn);
            bPrimaryAction = root.findViewById(R.id.subscribe_btn);

            RecyclerView filtersList = root.findViewById(R.id.post_filter_recyclerview);
            filtersList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

            mPostFilterAdapter = new HorizontalOptionsAdapter<>(Collections.emptyList());
            mPostFilterAdapter.setListener(entry -> getPresenter().fireFilterClick(entry));
            filtersList.setAdapter(mPostFilterAdapter);

            tvStatus.setOnClickListener(v -> getPresenter().fireStatusClick());

            bToggleInfo.setOnClickListener(v -> getPresenter().fireMoreInfoClick());
            bPrimaryAction.setOnClickListener(v -> getPresenter().firePrimaryActionsClick());
            fabMessage.setOnClickListener(v -> getPresenter().fireChatClick());

            root.findViewById(R.id.header_user_profile_photos_container).setOnClickListener(v -> getPresenter().fireHeaderPhotosClick());
            root.findViewById(R.id.header_user_profile_audios_container).setOnClickListener(v -> getPresenter().fireHeaderAudiosClick());
            root.findViewById(R.id.header_user_profile_friends_container).setOnClickListener(v -> getPresenter().fireHeaderFriendsClick());
            root.findViewById(R.id.header_user_profile_followers_container).setOnClickListener(v -> getPresenter().fireHeaderFollowersClick());
            root.findViewById(R.id.header_user_profile_groups_container).setOnClickListener(v -> getPresenter().fireHeaderGroupsClick());
            root.findViewById(R.id.header_user_profile_videos_container).setOnClickListener(v -> getPresenter().fireHeaderVideosClick());
        }
    }
}