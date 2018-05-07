package com.anotap.messenger.push.message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.MainActivity;
import com.anotap.messenger.api.util.VKStringUtils;
import com.anotap.messenger.link.VkLinkParser;
import com.anotap.messenger.link.types.AbsLink;
import com.anotap.messenger.link.types.WallPostLink;
import com.anotap.messenger.longpoll.AppNotificationChannels;
import com.anotap.messenger.longpoll.NotificationHelper;
import com.anotap.messenger.model.Owner;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.push.NotificationScheduler;
import com.anotap.messenger.push.NotificationUtils;
import com.anotap.messenger.push.OwnerInfo;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.PersistentLogger;
import com.anotap.messenger.util.Utils;

import static com.anotap.messenger.push.NotificationUtils.configOtherPushNotification;

public class GCMMessage {

    public int peer_id;
    public String text;
    public String collapse_key;
    public int message_id;
    private int badge;
    public String from;
    public String type;
    public String _genSrv;
    public long google_sent_time;

    public static GCMMessage genFromBundle(Bundle bundle) {
        GCMMessage message = new GCMMessage();
        message.collapse_key = bundle.getString("collapse_key");
        message.peer_id = Integer.parseInt(bundle.getString("uid"));
        message.text = VKStringUtils.unescape(bundle.getString("text"));
        message.message_id = Integer.parseInt(bundle.getString("msg_id"));
        message.badge = NotificationUtils.optInt(bundle, "badge", -1);
        message.from = bundle.getString("from");
        message.type = bundle.getString("type");
        message._genSrv = bundle.getString("_genSrv");
        message.google_sent_time = bundle.getLong("google.sent_time", 0L);
        return message;
    }

    public int getBadge() {
        return badge;
    }

    public int getPeerId() {
        return peer_id;
    }

    public int getMessageId() {
        return message_id;
    }

    public String getText() {
        return text;
    }

    public long getGoogleSentTime() {
        return google_sent_time;
    }
}