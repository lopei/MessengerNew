package com.anotap.messenger;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.anotap.messenger.db.Stores;
import com.anotap.messenger.push.CollapseKey;
import com.anotap.messenger.push.IPushRegistrationResolver;
import com.anotap.messenger.push.message.BirtdayGcmMessage;
import com.anotap.messenger.push.message.CommentGCMMessage;
import com.anotap.messenger.push.message.FriendAcceptedGCMMessage;
import com.anotap.messenger.push.message.FriendGCMMessage;
import com.anotap.messenger.push.message.GCMMessage;
import com.anotap.messenger.push.message.GroupInviteGCMMessage;
import com.anotap.messenger.push.message.LikeGcmMessage;
import com.anotap.messenger.push.message.NewPostPushMessage;
import com.anotap.messenger.push.message.ReplyGCMMessage;
import com.anotap.messenger.push.message.WallPostGCMMessage;
import com.anotap.messenger.push.message.WallPublishGCMMessage;
import com.anotap.messenger.realtime.Processors;
import com.anotap.messenger.realtime.QueueContainsException;
import com.anotap.messenger.settings.ISettings;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.Logger;
import com.anotap.messenger.util.PersistentLogger;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static com.anotap.messenger.util.Utils.isEmpty;

public class MyGcmListenerService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Bundle extras = new Bundle();
        for (String key : remoteMessage.getData().keySet()) {
            extras.putString(key, remoteMessage.getData().get(key));
        }

        Context context = getApplicationContext();
        String collapseKey = extras.getString("collapse_key");

        Logger.d(TAG, "onMessage, from: " + remoteMessage.getFrom() + ", collapseKey: " + collapseKey + ", extras: " + extras);
        if (isEmpty(collapseKey)) {
            collapseKey = extras.getString("type");
        }
        if (isEmpty(collapseKey)) {
            return;
        }

        StringBuilder bundleDump = new StringBuilder();
        for (String key : extras.keySet()) {
            try {
                Object value = extras.get(key);
                String line = "key: " + key + ", value: " + value + ", class: " + (value == null ? "null" : value.getClass());
                Logger.d(TAG, line);
                bundleDump.append("\n").append(line);
            } catch (Exception ignored) {
            }
        }

        int accountId = Settings.get()
                .accounts()
                .getCurrent();

        if (accountId == ISettings.IAccountsSettings.INVALID_ID) {
            return;
        }

        final IPushRegistrationResolver registrationResolver = Injection.providePushRegistrationResolver();

      /*  if (!registrationResolver.canReceivePushNotification()) {
            Logger.d(TAG, "Invalid push registration on VK");
            return;
        }*/

        switch (collapseKey) {
            case CollapseKey.MSG:
                //TODO: add notifications here for background
                fireNewMessage(accountId, GCMMessage.genFromBundle(extras));
                break;
            case CollapseKey.WALL_POST:
                WallPostGCMMessage.fromBundle(extras).nofify(context, accountId);
                break;
            case CollapseKey.REPLY:
                ReplyGCMMessage.fromBundle(extras).notify(context, accountId);
                break;
            case CollapseKey.COMMENT:
                CommentGCMMessage.fromBundle(extras).notify(context, accountId);
                break;
            case CollapseKey.WALL_PUBLISH:
                WallPublishGCMMessage.fromBundle(extras).notify(context, accountId);
                break;
            case CollapseKey.FRIEND:
                FriendGCMMessage.fromBundle(extras).notify(context, accountId);
                break;
            case CollapseKey.FRIEND_ACCEPTED:
                FriendAcceptedGCMMessage.fromBundle(extras).notify(context, accountId);
                break;
            case CollapseKey.GROUP_INVITE:
                GroupInviteGCMMessage.fromBundle(extras).notify(context, accountId);
                break;
            case CollapseKey.BIRTHDAY:
                BirtdayGcmMessage.fromBundle(extras).notify(context, accountId);
                break;

            case CollapseKey.NEW_POST:
                new NewPostPushMessage(accountId, extras).notifyIfNeed(context);
                break;

            case CollapseKey.LIKE:
                new LikeGcmMessage(accountId, extras).notifyIfNeed(context);
                break;

            //case CollapseKey.BIRTHDAY:
            //    // TODO: 02.12.2016
            //    break;

            default:
                PersistentLogger.logThrowable("Push issues", new Exception("Unespected Push event, collapse_key: " + collapseKey + ", dump: " + bundleDump));
                break;
        }
    }

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from   SenderID of the sender.
     * @param extras Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */


    private void fireNewMessage(int accountId, final @NonNull GCMMessage dto) {
        try {
            Processors.realtimeMessages().process(accountId, dto.getMessageId(), true);
        } catch (QueueContainsException ignored) {
        }

        if (dto.getBadge() >= 0) {
            Stores.getInstance()
                    .dialogs()
                    .setUnreadDialogsCount(accountId, dto.getBadge());
        }
    }
}