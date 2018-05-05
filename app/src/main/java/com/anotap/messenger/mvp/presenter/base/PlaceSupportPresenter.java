package com.anotap.messenger.mvp.presenter.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import com.anotap.messenger.domain.ILikesInteractor;
import com.anotap.messenger.fragment.search.SearchContentType;
import com.anotap.messenger.fragment.search.criteria.NewsFeedCriteria;
import com.anotap.messenger.model.Audio;
import com.anotap.messenger.model.Commented;
import com.anotap.messenger.model.Document;
import com.anotap.messenger.model.Link;
import com.anotap.messenger.model.Message;
import com.anotap.messenger.model.Photo;
import com.anotap.messenger.model.Poll;
import com.anotap.messenger.model.Post;
import com.anotap.messenger.model.Video;
import com.anotap.messenger.model.WikiPage;
import com.anotap.messenger.mvp.view.IAttachmentsPlacesView;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by ruslan.kolbasa on 04.10.2016.
 * phoenix
 */
public abstract class PlaceSupportPresenter<V extends IMvpView & IAttachmentsPlacesView & IAccountDependencyView>
        extends AccountDependencyPresenter<V> {

    public PlaceSupportPresenter(int accountId, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
    }

    public void fireLinkClick(@NonNull Link link){
        getView().openLink(getAccountId(), link);
    }

    public void fireWikiPageClick(@NonNull WikiPage page){
        getView().openWikiPage(getAccountId(), page);
    }

    public void firePhotoClick(@NonNull ArrayList<Photo> photos, int index){
        getView().openSimplePhotoGallery(getAccountId(), photos, index, true);
    }

    public void firePostClick(@NonNull Post post){
        getView().openPost(getAccountId(), post);
    }

    public void fireDocClick(@NonNull Document document){
        getView().openDocPreview(getAccountId(), document);
    }

    public void fireOwnerClick(int ownerId){
        getView().openOwnerWall(getAccountId(), ownerId);
    }

    public void fireForwardMessagesClick(@NonNull ArrayList<Message> messages){
        getView().openForwardMessages(getAccountId(), messages);
    }

    public void fireAudioPlayClick(int position, @NonNull ArrayList<Audio> apiAudio){
        getView().playAudioList(getAccountId(), position, apiAudio);
    }

    public void fireVideoClick(@NonNull Video apiVideo){
        getView().openVideo(getAccountId(), apiVideo);
    }

    public void firePollClick(@NonNull Poll poll){
        getView().openPoll(getAccountId(), poll);
    }

    public void fireHashtagClick(String hashTag) {
        getView().openSearch(getAccountId(), SearchContentType.NEWS, new NewsFeedCriteria(hashTag));
    }

    public void fireShareClick(Post post) {
        getView().repostPost(getAccountId(), post);
    }

    public void fireCommentsClick(Post post) {
        getView().openComments(getAccountId(), Commented.from(post), null);
    }

    public final void fireCopiesLikesClick(String type, int ownerId, int itemId, String filter){
        if(ILikesInteractor.FILTER_LIKES.equals(filter)){
            getView().goToLikes(getAccountId(), type, ownerId, itemId);
        } else if(ILikesInteractor.FILTER_COPIES.equals(filter)){
            getView().goToReposts(getAccountId(), type, ownerId, itemId);
        }
    }
}