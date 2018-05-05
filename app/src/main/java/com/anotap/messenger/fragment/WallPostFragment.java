package com.anotap.messenger.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Transformation;

import com.anotap.messenger.Constants;
import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.ActivityFeatures;
import com.anotap.messenger.adapter.AttachmentsHolder;
import com.anotap.messenger.adapter.AttachmentsViewBinder;
import com.anotap.messenger.fragment.base.PlaceSupportPresenterFragment;
import com.anotap.messenger.fragment.search.SearchContentType;
import com.anotap.messenger.fragment.search.criteria.NewsFeedCriteria;
import com.anotap.messenger.link.internal.LinkActionAdapter;
import com.anotap.messenger.link.internal.OwnerLinkSpanFactory;
import com.anotap.messenger.listener.OnSectionResumeCallback;
import com.anotap.messenger.model.ParcelableOwnerWrapper;
import com.anotap.messenger.model.Post;
import com.anotap.messenger.model.User;
import com.anotap.messenger.mvp.presenter.WallPostPresenter;
import com.anotap.messenger.mvp.view.IWallPostView;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.place.PlaceUtil;
import com.anotap.messenger.settings.CurrentTheme;
import com.anotap.messenger.util.AppTextUtils;
import com.anotap.messenger.util.ViewUtils;
import com.anotap.messenger.view.CircleCounterButton;
import com.anotap.messenger.view.emoji.EmojiconTextView;
import com.anotap.mvp.core.IPresenterFactory;

import static com.anotap.messenger.util.Objects.isNull;
import static com.anotap.messenger.util.Objects.nonNull;

public class WallPostFragment extends PlaceSupportPresenterFragment<WallPostPresenter, IWallPostView>
        implements EmojiconTextView.OnHashTagClickListener, IWallPostView {

    private TextView mSignerNameText;
    private View mSignerRootView;
    private ImageView mSignerAvatar;

    private CircleCounterButton mShareButton;
    private CircleCounterButton mCommentsButton;
    private CircleCounterButton mLikeButton;

    private EmojiconTextView mText;
    private AttachmentsViewBinder attachmentsViewBinder;
    private Transformation transformation;
    private ViewGroup root;

    public static WallPostFragment newInstance(Bundle args) {
        WallPostFragment fragment = new WallPostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static Bundle buildArgs(int accountId, int postId, int ownerId, Post post) {
        Bundle bundle = new Bundle();
        bundle.putInt(Extra.ACCOUNT_ID, accountId);
        bundle.putInt(Extra.POST_ID, postId);
        bundle.putInt(Extra.OWNER_ID, ownerId);
        bundle.putParcelable(Extra.POST, post);
        return bundle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        this.attachmentsViewBinder = new AttachmentsViewBinder(getActivity(), this);
        this.attachmentsViewBinder.setOnHashTagClickListener(this);
        this.transformation = CurrentTheme.createTransformationForAvatar(getActivity());
    }

    @Override
    public void displayPinComplete(boolean pinned) {
        Toast.makeText(getActivity(), pinned ? R.string.pin_result : R.string.unpin_result, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayDeleteOrRestoreComplete(boolean deleted) {
        Toast.makeText(getActivity(), deleted ? R.string.delete_result : R.string.restore_result, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof OnSectionResumeCallback) {
            ((OnSectionResumeCallback) getActivity()).onClearSelection();
        }

        new ActivityFeatures.Builder()
                .begin()
                .setBlockNavigationDrawer(false)
                .setStatusBarColored(getActivity(),true)
                .build()
                .apply(getActivity());
    }

    private AttachmentsHolder mAttachmentsViews;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_post, container, false);
        mAttachmentsViews = AttachmentsHolder.forPost(root);

        ((AppCompatActivity) getActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        mShareButton = root.findViewById(R.id.share_button);
        mCommentsButton = root.findViewById(R.id.comments_button);
        mLikeButton = root.findViewById(R.id.like_button);

        mText = root.findViewById(R.id.fragment_post_text);
        mText.setMovementMethod(LinkMovementMethod.getInstance());
        mText.setOnHashTagClickListener(this);

        mSignerRootView = root.findViewById(R.id.item_post_signer_root);
        mSignerAvatar = root.findViewById(R.id.item_post_signer_icon);
        mSignerNameText = root.findViewById(R.id.item_post_signer_name);

        mLikeButton.setOnClickListener(view -> getPresenter().fireLikeClick());

        mLikeButton.setOnLongClickListener(view -> {
            getPresenter().fireLikeLongClick();
            return true;
        });

        mShareButton.setOnClickListener(view -> getPresenter().fireShareClick());
        mShareButton.setOnLongClickListener(view -> {
            getPresenter().fireRepostLongClick();
            return true;
        });

        root.findViewById(R.id.try_again_button).setOnClickListener(v -> getPresenter().fireTryLoadAgainClick());

        mCommentsButton.setOnClickListener(view -> getPresenter().fireCommentClick());
        resolveTextSelection();
        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_post:
                getPresenter().fireDeleteClick();
                return true;

            case R.id.restore_post:
                getPresenter().fireRestoreClick();
                return true;

            case R.id.pin_post:
                getPresenter().firePinClick();
                return true;

            case R.id.unpin_post:
                getPresenter().fireUnpinClick();
                return true;

            case R.id.goto_user_post:
                getPresenter().fireGoToOwnerClick();
                return true;

            case R.id.copy_url_post:
                getPresenter().fireCopyLinkClink();
                return true;

            case R.id.copy_text:
                getPresenter().fireCopyTextClick();
                return true;

            case R.id.action_allow_text_selection:
                mTextSelectionAllowed = true;
                resolveTextSelection();
                return true;

            case R.id.edit_post:
                getPresenter().firePostEditClick();
                return true;

            case R.id.refresh:
                getPresenter().fireRefresh();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void copyLinkToClipboard(String link) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.link), link);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getActivity(), R.string.copied_url, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void copyTextToClipboard(String text) {
        ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(getString(R.string.post_text), text);
        manager.setPrimaryClip(clipData);

        Toast.makeText(getActivity(), R.string.copied_text, Toast.LENGTH_SHORT).show();
    }

    private boolean mTextSelectionAllowed;

    private void resolveTextSelection(){
        if(nonNull(mText)){
            mText.setTextIsSelectable(mTextSelectionAllowed);
        }

        ViewGroup copiesRoot = mAttachmentsViews.getVgPosts();

        for(int i = 0; i < copiesRoot.getChildCount(); i++){
            ViewGroup copyRoot = (ViewGroup) copiesRoot.getChildAt(i);
            TextView textView = copyRoot.findViewById(R.id.item_post_copy_text);
            if(nonNull(textView)){
                textView.setTextIsSelectable(mTextSelectionAllowed);
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        OptionView optionView = new OptionView();
        getPresenter().fireOptionViewCreated(optionView);

        menu.findItem(R.id.edit_post).setVisible(optionView.canEdit);
        menu.findItem(R.id.unpin_post).setVisible(optionView.canUnpin);
        menu.findItem(R.id.pin_post).setVisible(optionView.canPin);
        menu.findItem(R.id.delete_post).setVisible(optionView.canDelete);
        menu.findItem(R.id.restore_post).setVisible(optionView.canRestore);
    }

    private static final class OptionView implements IWallPostView.IOptionView {

        boolean canDelete;
        boolean canRestore;
        boolean canPin;
        boolean canUnpin;
        boolean canEdit;

        @Override
        public void setCanDelete(boolean can) {
            this.canDelete = can;
        }

        @Override
        public void setCanRestore(boolean can) {
            this.canRestore = can;
        }

        @Override
        public void setCanPin(boolean can) {
            this.canPin = can;
        }

        @Override
        public void setCanUnpin(boolean can) {
            this.canUnpin = can;
        }

        @Override
        public void setCanEdit(boolean can) {
            this.canEdit = can;
        }
    }

    /*private boolean canEdit() {
        return post.isCanEdit();

        boolean canEditAsAdmin = false;

        if(nonNull(owner) && owner.admin_level >= VKApiCommunity.AdminLevel.EDITOR){
            if(owner.type == VKApiCommunity.Type.GROUP){
                // нельзя редактировать чужие посты в GROUP
                canEditAsAdmin = post.getCreatorId() == getAccountId() && post.getSignerId() == getAccountId();
            }

            if(owner.type == VKApiCommunity.Type.PAGE){
                canEditAsAdmin = true;
            }
        }

        boolean canEdit = post.getAuthorId() == getAccountId() || canEditAsAdmin;

        if (!canEdit) {
            return false;
        }

        long currentUnixtime = System.currentTimeMillis() / 1000;
        return (currentUnixtime - post.getDate()) < Constants.HOURS_24_IN_SECONDS;
    }*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.single_post_menu, menu);
    }

    @Override
    protected String tag() {
        return WallPostFragment.class.getSimpleName();
    }

    @Override
    public void displayDefaultToolbaTitle() {
        super.setToolbarTitle(getString(R.string.wall_post));
    }

    @Override
    public void displayToolbarTitle(String title) {
        super.setToolbarTitle(title);
    }

    @Override
    public void displayToolbatSubtitle(int subtitleType, long datetime) {
        String formattedDate = AppTextUtils.getDateFromUnixTime(getActivity(), datetime);

        switch (subtitleType){
            case SUBTITLE_NORMAL:
                super.setToolbarSubtitle(formattedDate);
                break;

            case SUBTITLE_STATUS_UPDATE:
                super.setToolbarSubtitle(getString(R.string.updated_status_at, formattedDate));
                break;

            case SUBTITLE_PHOTO_UPDATE:
                super.setToolbarSubtitle(getString(R.string.updated_profile_photo_at, formattedDate));
                break;
        }
    }

    @Override
    public void displayDefaultToolbaSubitle() {
        super.setToolbarSubtitle(null);
    }

    @Override
    public void displayPostInfo(Post post) {
        if(isNull(root)){
            return;
        }

        if (post.isDeleted()) {
            root.findViewById(R.id.fragment_post_deleted).setVisibility(View.VISIBLE);
            root.findViewById(R.id.post_content).setVisibility(View.GONE);
            root.findViewById(R.id.post_loading_root).setVisibility(View.GONE);
            return;
        }

        root.findViewById(R.id.fragment_post_deleted).setVisibility(View.GONE);
        root.findViewById(R.id.post_content).setVisibility(View.VISIBLE);
        root.findViewById(R.id.post_loading_root).setVisibility(View.GONE);

        mText.setVisibility(post.hasText() ? View.VISIBLE : View.GONE);

        Spannable spannableText = OwnerLinkSpanFactory.withSpans(post.getText(), true, false, new LinkActionAdapter() {
            @Override
            public void onOwnerClick(int ownerId) {
                onOpenOwner(ownerId);
            }
        });

        mText.setText(spannableText, TextView.BufferType.SPANNABLE);

        boolean displaySigner = post.getSignerId() > 0 && nonNull(post.getCreator());
        mSignerRootView.setVisibility(displaySigner ? View.VISIBLE : View.GONE);

        if (displaySigner) {
            User creator = post.getCreator();
            mSignerNameText.setText(creator.getFullName());

            ViewUtils.displayAvatar(mSignerAvatar, transformation, creator.get100photoOrSmaller(), Constants.PICASSO_TAG);
            mSignerRootView.setOnClickListener(v -> onOpenOwner(post.getSignerId()));
        }

        attachmentsViewBinder.displayAttachments(post.getAttachments(), mAttachmentsViews, false);
        attachmentsViewBinder.displayCopyHistory(post.getCopyHierarchy(), mAttachmentsViews.getVgPosts(),
                false, R.layout.item_copy_history_post);
    }

    @Override
    public void displayLoading() {
        if(nonNull(root)){
            root.findViewById(R.id.fragment_post_deleted).setVisibility(View.GONE);
            root.findViewById(R.id.post_content).setVisibility(View.GONE);
            root.findViewById(R.id.post_loading_root).setVisibility(View.VISIBLE);

            root.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            root.findViewById(R.id.post_loading_text).setVisibility(View.VISIBLE);
            root.findViewById(R.id.try_again_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void displayLoadingFail() {
        if (nonNull(root)) {
            root.findViewById(R.id.fragment_post_deleted).setVisibility(View.GONE);
            root.findViewById(R.id.post_content).setVisibility(View.GONE);
            root.findViewById(R.id.post_loading_root).setVisibility(View.VISIBLE);

            root.findViewById(R.id.progressBar).setVisibility(View.GONE);
            root.findViewById(R.id.post_loading_text).setVisibility(View.GONE);
            root.findViewById(R.id.try_again_button).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void displayLikes(int count, boolean userLikes) {
        if(nonNull(mLikeButton)){
            mLikeButton.setActive(userLikes);
            mLikeButton.setCount(count);
            mLikeButton.setIcon(userLikes ? R.drawable.heart : R.drawable.heart_outline);
        }
    }

    @Override
    public void setCommentButtonVisible(boolean visible) {
        if(nonNull(mCommentsButton)){
            mCommentsButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void displayCommentCount(int count) {
        if(nonNull(mCommentsButton)){
            mCommentsButton.setCount(count);
        }
    }

    @Override
    public void displayReposts(int count, boolean userReposted) {
        if(nonNull(mShareButton)){
            mShareButton.setCount(count);
            mShareButton.setActive(userReposted);
        }
    }

    @Override
    public void goToPostEditing(int accountId, @NonNull Post post) {
        PlaceUtil.goToPostEditor(getActivity(), accountId, post);
    }

    @Override
    public void showPostNotReadyToast() {
        Toast.makeText(getActivity(), R.string.wall_post_is_not_yet_initialized, Toast.LENGTH_LONG).show();
    }

    @Override
    public IPresenterFactory<WallPostPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            ParcelableOwnerWrapper wrapper = getArguments().getParcelable(Extra.OWNER);
            return new WallPostPresenter(
                    getArguments().getInt(Extra.ACCOUNT_ID),
                    getArguments().getInt(Extra.POST_ID),
                    getArguments().getInt(Extra.OWNER_ID),
                    getArguments().getParcelable(Extra.POST),
                    nonNull(wrapper) ? wrapper.get() : null,
                    saveInstanceState
            );
        };
    }

    @Override
    public void goToNewsSearch(int accountId, String hashTag) {
        NewsFeedCriteria criteria = new NewsFeedCriteria(hashTag);
        PlaceFactory.getSingleTabSearchPlace(accountId, SearchContentType.NEWS, criteria).tryOpenWith(getActivity());
    }

    @Override
    public void onHashTagClicked(String hashTag) {
        getPresenter().fireHasgTagClick(hashTag);
    }
}