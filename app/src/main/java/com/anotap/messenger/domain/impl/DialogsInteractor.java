package com.anotap.messenger.domain.impl;

import com.anotap.messenger.api.interfaces.INetworker;
import com.anotap.messenger.db.interfaces.IStores;
import com.anotap.messenger.domain.IDialogsInteractor;
import com.anotap.messenger.domain.mappers.Dto2Model;
import com.anotap.messenger.exception.NotFoundException;
import com.anotap.messenger.model.Chat;
import com.anotap.messenger.model.Peer;
import io.reactivex.Single;

import static com.anotap.messenger.util.Utils.isEmpty;

/**
 * Created by admin on 19.03.2017.
 * phoenix
 */
public class DialogsInteractor implements IDialogsInteractor {

    private final INetworker networker;

    private final IStores repositories;

    public DialogsInteractor(INetworker networker, IStores repositories) {
        this.networker = networker;
        this.repositories = repositories;
    }

    @Override
    public Single<Chat> getChatById(int accountId, int peerId) {
        return repositories.dialogs()
                .findChatById(accountId, peerId)
                .flatMap(optional -> {
                    if(optional.nonEmpty()){
                        return Single.just(optional.get());
                    }

                    final int chatId = Peer.toChatId(peerId);
                    return networker.vkDefault(accountId)
                            .messages()
                            .getChat(chatId, null, null, null)
                            .map(chats -> {
                                if(isEmpty(chats)){
                                    throw new NotFoundException();
                                }

                                return chats.get(0);
                            })
                            .map(Dto2Model::transform);
                });
    }
}