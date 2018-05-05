package com.anotap.messenger.mvp.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import com.anotap.messenger.fragment.search.SearchContentType;
import com.anotap.messenger.fragment.search.criteria.BaseSearchCriteria;
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

/**
 * Created by admin on 03.10.2016.
 * phoenix
 */
public interface IAttachmentsPlacesView {

    void openChatWith(int accountId, int messagesOwnerId, @NonNull Peer peer);

    void openLink(int accountId, @NonNull Link link);

    void openWikiPage(int accountId, @NonNull WikiPage page);

    void openSimplePhotoGallery(int accountId, @NonNull ArrayList<Photo> photos, int index, boolean needUpdate);

    void openPost(int accountId, @NonNull Post post);

    void openDocPreview(int accountId, @NonNull Document document);

    void openOwnerWall(int accountId, int ownerId);

    void openForwardMessages(int accountId, @NonNull ArrayList<Message> messages);

    void playAudioList(int accountId, int position, @NonNull ArrayList<Audio> apiAudio);

    void openVideo(int accountId, @NonNull Video apiVideo);

    void openPoll(int accoountId, @NonNull Poll apiPoll);

    void openSearch(int accountId, @SearchContentType int type, @Nullable BaseSearchCriteria criteria);

    void openComments(int accountId, Commented commented, Integer focusToCommentId);

    void goToLikes(int accountId, String type, int ownerId, int id);

    void goToReposts(int accountId, String type, int ownerId, int id);

    void repostPost(int accountId, @NonNull Post post);
}
