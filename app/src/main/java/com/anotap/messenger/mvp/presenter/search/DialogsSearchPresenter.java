package com.anotap.messenger.mvp.presenter.search;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import com.anotap.messenger.domain.IMessagesInteractor;
import com.anotap.messenger.domain.InteractorFactory;
import com.anotap.messenger.fragment.search.criteria.DialogsSearchCriteria;
import com.anotap.messenger.fragment.search.criteria.MessageSeachCriteria;
import com.anotap.messenger.fragment.search.nextfrom.IntNextFrom;
import com.anotap.messenger.model.Chat;
import com.anotap.messenger.model.Community;
import com.anotap.messenger.model.Message;
import com.anotap.messenger.model.Peer;
import com.anotap.messenger.model.User;
import com.anotap.messenger.mvp.view.search.IDialogsSearchView;
import com.anotap.messenger.util.Pair;
import com.anotap.messenger.util.Utils;
import io.reactivex.Single;

/**
 * Created by admin on 02.05.2017.
 * phoenix
 */
public class DialogsSearchPresenter extends AbsSearchPresenter<IDialogsSearchView, DialogsSearchCriteria, Object, IntNextFrom> {

    private final IMessagesInteractor messagesInteractor;

    public DialogsSearchPresenter(int accountId, @Nullable DialogsSearchCriteria criteria, @Nullable Bundle savedInstanceState) {
        super(accountId, criteria, savedInstanceState);
        this.messagesInteractor = InteractorFactory.createMessagesInteractor();
    }

    @Override
    protected String tag() {
        return DialogsSearchPresenter.class.getSimpleName();
    }

    @Override
    IntNextFrom getInitialNextFrom() {
        return new IntNextFrom(0);
    }

    @Override
    boolean isAtLast(IntNextFrom startFrom) {
        return startFrom.getOffset() == 0;
    }

    @Override
    Single<Pair<List<Object>, IntNextFrom>> doSearch(int accountId, DialogsSearchCriteria criteria, IntNextFrom startFrom) {
        MessageSeachCriteria newCriteria = new MessageSeachCriteria(criteria.getQuery());
        return messagesInteractor.searchMessages(accountId, null, 20, 0, newCriteria.getQuery())
                .map((List models) -> {
                    // null because load more not supported
                    return Pair.create(models, null);
                });
//        return messagesInteractor.searchDialogs(accountId, 20, criteria.getQuery())
//                .map(models -> {
//                    // null because load more not supported
//                    return Pair.create(models, null);
//                });
    }


    @Override
    DialogsSearchCriteria instantiateEmptyCriteria() {
        return new DialogsSearchCriteria("");
    }

    @Override
    boolean canSearch(DialogsSearchCriteria criteria) {
        return Utils.trimmedNonEmpty(criteria.getQuery());
    }

    public void fireEntryClick(Object o) {
        final int accountId = super.getAccountId();
        final int messagesOwnerId = super.getAccountId(); // todo Community dialogs seacrh !!!

        if(o instanceof User){
            User user = (User) o;
            final Peer peer = new Peer(Peer.fromUserId(user.getId())).setTitle(user.getFullName()).setAvaUrl(user.getMaxSquareAvatar());
            getView().openChatWith(accountId, messagesOwnerId, peer);
        } else if(o instanceof Community){
            Community group = (Community) o;
            final Peer peer = new Peer(Peer.fromGroupId(group.getId())).setTitle(group.getFullName()).setAvaUrl(group.getMaxSquareAvatar());
            getView().openChatWith(accountId, messagesOwnerId, peer);
        } else if(o instanceof Chat){
            Chat chat = (Chat) o;
            final Peer peer = new Peer(Peer.fromChatId(chat.getId())).setTitle(chat.getTitle()).setAvaUrl(chat.getMaxSquareAvatar());
            getView().openChatWith(accountId, messagesOwnerId, peer);
        }
    }

    public void firePublishData() {
        if (isGuiReady()) {
            getView().displayData(data);
        }
    }
}