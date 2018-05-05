package com.anotap.messenger.fragment.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

import com.anotap.messenger.activity.SendAttachmentsActivity;
import com.anotap.messenger.adapter.AttachmentsViewBinder;
import com.anotap.messenger.adapter.listener.OwnerClickListener;
import com.anotap.messenger.dialog.PostShareDialog;
import com.anotap.messenger.domain.ILikesInteractor;
import com.anotap.messenger.fragment.search.SearchContentType;
import com.anotap.messenger.fragment.search.criteria.BaseSearchCriteria;
import com.anotap.messenger.link.LinkHelper;
import com.anotap.messenger.model.Audio;
import com.anotap.messenger.model.Commented;
import com.anotap.messenger.model.Document;
import com.anotap.messenger.model.Link;
import com.anotap.messenger.model.Message;
import com.anotap.messenger.model.Peer;
import com.anotap.messenger.model.Photo;
import com.anotap.messenger.model.Poll;
import com.anotap.messenger.model.Post;
import com.anotap.messenger.model.Video;
import com.anotap.messenger.model.WikiPage;
import com.anotap.messenger.mvp.presenter.base.PlaceSupportPresenter;
import com.anotap.messenger.mvp.view.IAttachmentsPlacesView;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.player.MusicPlaybackService;
import com.anotap.messenger.util.AssertUtils;
import com.anotap.messenger.util.Utils;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by ruslan.kolbasa on 04.10.2016.
 * phoenix
 */
public abstract class PlaceSupportPresenterFragment<P extends PlaceSupportPresenter<V>, V extends IMvpView & IAttachmentsPlacesView & IAccountDependencyView>
        extends BasePresenterFragment<P, V> implements AttachmentsViewBinder.OnAttachmentsActionCallback, IAttachmentsPlacesView, OwnerClickListener {

    private static final int REQUEST_POST_SHARE = 45;

    @Override
    public void savePresenterState(@NonNull P presenter, @NonNull Bundle outState) {
        presenter.saveState(outState);
    }

    @Override
    public void onOwnerClick(int ownerId) {
        getPresenter().fireOwnerClick(ownerId);
    }

    @Override
    public void openChatWith(int accountId, int messagesOwnerId, @NonNull Peer peer) {
        PlaceFactory.getChatPlace(accountId, messagesOwnerId, peer).tryOpenWith(requireActivity());
    }

    @Override
    public void onPollOpen(@NonNull Poll apiPoll) {
        getPresenter().firePollClick(apiPoll);
    }

    @Override
    public void onVideoPlay(@NonNull Video video) {
        getPresenter().fireVideoClick(video);
    }

    @Override
    public void onAudioPlay(int position, @NonNull ArrayList<Audio> apiAudio) {
        getPresenter().fireAudioPlayClick(position, apiAudio);
    }

    @Override
    public void onForwardMessagesOpen(@NonNull ArrayList<Message> messages) {
        getPresenter().fireForwardMessagesClick(messages);
    }

    @Override
    public void onOpenOwner(int ownerId) {
        getPresenter().fireOwnerClick(ownerId);
    }

    @Override
    public void onDocPreviewOpen(@NonNull Document document) {
        getPresenter().fireDocClick(document);
    }

    @Override
    public void onPostOpen(@NonNull Post post) {
        getPresenter().firePostClick(post);
    }

    @Override
    public void onLinkOpen(@NonNull Link link) {
        getPresenter().fireLinkClick(link);
    }

    @Override
    public void onWikiPageOpen(@NonNull WikiPage page) {
        getPresenter().fireWikiPageClick(page);
    }

    @Override
    public void onPhotosOpen(@NonNull ArrayList<Photo> photos, int index) {
        getPresenter().firePhotoClick(photos, index);
    }

    @Override
    public void openLink(int accountId, @NonNull Link link) {
        LinkHelper.openLinkInBrowser(getActivity(), link.getUrl());
    }

    @Override
    public void openWikiPage(int accountId, @NonNull WikiPage page) {
        PlaceFactory.getWikiPagePlace(accountId, page.getViewUrl())
                .tryOpenWith(requireActivity());
    }

    @Override
    public void openSimplePhotoGallery(int accountId, @NonNull ArrayList<Photo> photos, int index, boolean needUpdate) {
        PlaceFactory.getSimpleGalleryPlace(accountId, photos, index, true).tryOpenWith(requireActivity());
    }

    @Override
    public void openPost(int accountId, @NonNull Post post) {
        PlaceFactory.getPostPreviewPlace(accountId, post.getVkid(), post.getOwnerId(), post).tryOpenWith(requireActivity());
    }

    @Override
    public void openDocPreview(int accountId, @NonNull Document document) {
        PlaceFactory.getDocPreviewPlace(accountId, document).tryOpenWith(requireActivity());
    }

    @Override
    public void openOwnerWall(int accountId, int ownerId) {
        PlaceFactory.getOwnerWallPlace(accountId, ownerId, null).tryOpenWith(requireActivity());
    }

    @Override
    public void openForwardMessages(int accountId, @NonNull ArrayList<Message> messages) {
        PlaceFactory.getForwardMessagesPlace(accountId, messages).tryOpenWith(requireActivity());
    }

    @Override
    public void playAudioList(int accountId, int position, @NonNull ArrayList<Audio> apiAudio) {
        MusicPlaybackService.startForPlayList(getActivity(), apiAudio, position, false);
        PlaceFactory.getPlayerPlace(accountId).tryOpenWith(requireActivity());
    }

    @Override
    public void openVideo(int accountId, @NonNull Video apiVideo) {
        PlaceFactory.getVideoPreviewPlace(accountId, apiVideo).tryOpenWith(requireActivity());
    }

    @Override
    public void openPoll(int accoountId, @NonNull Poll poll) {
        PlaceFactory.getPollPlace(accoountId, poll)
                .tryOpenWith(requireActivity());
    }

    @Override
    public void openComments(int accountId, Commented commented, Integer focusToCommentId) {
        PlaceFactory.getCommentsPlace(accountId, commented, focusToCommentId)
                .tryOpenWith(requireActivity());
    }

    @Override
    public void openSearch(int accountId, @SearchContentType int type, @Nullable BaseSearchCriteria criteria) {
        PlaceFactory.getSingleTabSearchPlace(accountId, type, criteria).tryOpenWith(requireActivity());
    }

    @Override
    public void goToLikes(int accountId, String type, int ownerId, int id) {
        PlaceFactory.getLikesCopiesPlace(accountId, type, ownerId, id, ILikesInteractor.FILTER_LIKES)
                .tryOpenWith(requireActivity());
    }

    @Override
    public void goToReposts(int accountId, String type, int ownerId, int id) {
        PlaceFactory.getLikesCopiesPlace(accountId, type, ownerId, id, ILikesInteractor.FILTER_COPIES)
                .tryOpenWith(requireActivity());
    }

    @Override
    public void repostPost(int accountId, @NonNull Post post) {
        FragmentManager fm = requireFragmentManager();

        PostShareDialog.newInstance(accountId, post)
                .targetTo(this, REQUEST_POST_SHARE)
                .show(fm, "post-sharing");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_POST_SHARE && resultCode == Activity.RESULT_OK) {
            int method = PostShareDialog.extractMethod(data);
            int accountId = PostShareDialog.extractAccountId(data);
            Post post = PostShareDialog.extractPost(data);

            AssertUtils.requireNonNull(post);

            switch (method) {
                case PostShareDialog.Methods.SHARE_LINK:
                    Utils.shareLink(requireActivity(), post.generateVkPostLink(), post.getText());
                    break;
                case PostShareDialog.Methods.REPOST_YOURSELF:
                    PlaceFactory.getRepostPlace(accountId, null, post).tryOpenWith(requireActivity());
                    break;
                case PostShareDialog.Methods.SEND_MESSAGE:
                    SendAttachmentsActivity.startForSendAttachments(requireActivity(), accountId, post);
                    break;
                case PostShareDialog.Methods.REPOST_GROUP:
                    int ownerId = PostShareDialog.extractOwnerId(data);
                    PlaceFactory.getRepostPlace(accountId, Math.abs(ownerId), post).tryOpenWith(requireActivity());
                    break;
            }
        }
    }
}