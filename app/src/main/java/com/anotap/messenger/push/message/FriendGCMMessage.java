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
import com.anotap.messenger.longpoll.AppNotificationChannels;
import com.anotap.messenger.longpoll.NotificationHelper;
import com.anotap.messenger.model.User;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.push.NotificationScheduler;
import com.anotap.messenger.push.NotificationUtils;
import com.anotap.messenger.push.OwnerInfo;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.Utils;

import static com.anotap.messenger.push.NotificationUtils.configOtherPushNotification;

public class FriendGCMMessage {

    //collapseKey: friend, extras: Bundle[{first_name=Андрей, uid=320891480, from=376771982493,
    // type=friend, badge=1, common_count=0, sandbox=0, collapse_key=friend, last_name=Боталов}]

    //private String first_name;
    //private String last_name;
    private int uid;
    //private long from;
    //private String type;
    //private int badge;
    //private int common_count;

    public static FriendGCMMessage fromBundle(@NonNull Bundle bundle) {
        FriendGCMMessage message = new FriendGCMMessage();
        //message.first_name = bundle.getString("first_name");
        //message.last_name = bundle.getString("last_name");
        message.uid = NotificationUtils.optInt(bundle, "uid");
        //message.from = optLong(bundle, "from");
        //message.type = bundle.getString("type");
        //message.badge = optInt(bundle, "badge");
        //message.common_count = optInt(bundle, "common_count");
        return message;
    }

    public void notify(final Context context, int accountId) {
        if (!Settings.get()
                .notifications()
                .isNewFollowerNotifEnabled()) {
            return;
        }

        Context app = context.getApplicationContext();
        OwnerInfo.getRx(app, accountId, uid)
                .subscribeOn(NotificationScheduler.INSTANCE)
                .subscribe(ownerInfo -> notifyImpl(app, ownerInfo.getUser(), ownerInfo.getAvatar()), throwable -> {/*ignore*/});
    }

    private void notifyImpl(Context context, User user, Bitmap bitmap) {
        final NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Utils.hasOreo()){
            nManager.createNotificationChannel(AppNotificationChannels.getFriendRequestsChannel(context));
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AppNotificationChannels.FRIEND_REQUESTS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notify_statusbar)
                .setLargeIcon(bitmap)
                .setContentTitle(user.getFullName())
                .setContentText(context.getString(R.string.subscribed_to_your_updates))
                .setAutoCancel(true);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        int aid = Settings.get()
                .accounts()
                .getCurrent();

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Extra.PLACE, PlaceFactory.getOwnerWallPlace(aid, uid, user));
        intent.setAction(MainActivity.ACTION_OPEN_PLACE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(context, uid, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();

        configOtherPushNotification(notification);
        nManager.notify(String.valueOf(uid), NotificationHelper.NOTIFICATION_FRIEND_ID, notification);
    }
}