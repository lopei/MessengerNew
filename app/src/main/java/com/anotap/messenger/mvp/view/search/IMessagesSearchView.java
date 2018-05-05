package com.anotap.messenger.mvp.view.search;

import com.anotap.messenger.model.Message;

/**
 * Created by admin on 01.05.2017.
 * phoenix
 */
public interface IMessagesSearchView extends IBaseSearchView<Message> {

    void goToMessagesLookup(int accountId, int peerId, int messageId);
}
