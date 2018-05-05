package com.anotap.messenger.longpoll;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import com.anotap.messenger.R;
import com.anotap.messenger.api.model.VKApiMessage;
import com.anotap.messenger.api.model.longpoll.InputMessagesSetReadUpdate;
import com.anotap.messenger.api.model.longpoll.MessageFlagsResetUpdate;
import com.anotap.messenger.api.model.longpoll.VkApiLongpollUpdates;
import com.anotap.messenger.model.Message;
import com.anotap.messenger.settings.ISettings;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.Logger;

import static com.anotap.messenger.util.Utils.hasFlag;
import static com.anotap.messenger.util.Utils.isEmpty;
import static com.anotap.messenger.util.Utils.nonEmpty;

public class LongPollNotificationHelper {

    public static final String TAG = LongPollNotificationHelper.class.getSimpleName();

    /**
     * Действие при добавлении нового сообщения в диалог или чат
     *
     * @param message нотификация с сервера
     */
    public static void notifyAbountNewMessage(Context context, final Message message) {
        if (message.isOut()) {
            return;
        }

        if (message.isRead()) {
            return;
        }

        //boolean needSendNotif = needNofinicationFor(message.getAccountId(), message.getPeerId());
        //if(!needSendNotif){
        //    return;
        //}

        String messageText = isEmpty(message.getDecryptedBody()) ? (isEmpty(message.getBody())
                ? context.getString(R.string.attachments) : message.getBody()) : message.getDecryptedBody();

        notifyAbountNewMessage(context, message.getAccountId(), messageText, message.getPeerId(), message.getId(), message.getDate());
    }

    private static void notifyAbountNewMessage(Context context, int accountId, String body, int peerId, int messageId, long date){
        int mask = Settings.get().notifications().getNotifPref(accountId, peerId);
        if (!hasFlag(mask, ISettings.INotificationSettings.FLAG_SHOW_NOTIF)) {
            return;
        }

        if (Settings.get().accounts().getCurrent() != accountId) {
            Logger.d(TAG, "notifyAbountNewMessage, Attempting to send a notification does not in the current account!!!");
            return;
        }

        NotificationHelper.notifNewMessage(context, accountId, body, peerId, messageId, date);
    }


    public static void fireUpdates(Context context, int accountId, VkApiLongpollUpdates item) {
        if (nonEmpty(item.message_flags_reset_updates)) {
            fireMessagesFlagsReset(context, accountId, item.message_flags_reset_updates);
        }

        if (nonEmpty(item.input_messages_set_read_updates)) {
            fireMessagesRead(context, accountId, item.input_messages_set_read_updates);
        }
    }

    private static void fireMessagesRead(Context context, int accountId, @NonNull List<InputMessagesSetReadUpdate> updates) {
        for (InputMessagesSetReadUpdate update : updates) {
            NotificationHelper.tryCancelNotificationForPeer(context, accountId, update.peer_id);
        }
    }

    private static void fireMessagesFlagsReset(Context context, int accountId, List<MessageFlagsResetUpdate> updates) {
        for (MessageFlagsResetUpdate update : updates) {
            if (update.mask == VKApiMessage.FLAG_UNREAD) {
                NotificationHelper.tryCancelNotificationForPeer(context, accountId, update.peer_id);
            }
        }
    }
}