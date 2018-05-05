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
import com.anotap.messenger.model.Community;
import com.anotap.messenger.model.User;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.push.NotificationScheduler;
import com.anotap.messenger.push.NotificationUtils;
import com.anotap.messenger.push.OwnerInfo;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.Pair;
import com.anotap.messenger.util.Utils;
import io.reactivex.Single;

import static com.anotap.messenger.push.NotificationUtils.configOtherPushNotification;

public class GroupInviteGCMMessage {

    //collapseKey: group_invite, extras: Bundle[{from_id=175895893, from=376771982493, name=Pianoбой,
    // type=group_invite, group_id=1583008, sandbox=0, collapse_key=group_invite}]

    private int from_id;
    //public long from;
    //public String name;
    //public String type;
    private int group_id;

    public static GroupInviteGCMMessage fromBundle(@NonNull Bundle bundle) {
        GroupInviteGCMMessage message = new GroupInviteGCMMessage();
        message.from_id = NotificationUtils.optInt(bundle, "from_id");
        //message.name = bundle.getString("name");
        message.group_id = NotificationUtils.optInt(bundle, "group_id");
        //message.from = FriendGCMMessage.optLong(bundle, "from");
        //message.type = bundle.getString("type");
        return message;
    }

    public void notify(final Context context, final int accountId){
        if (!Settings.get()
                .notifications()
                .isGroupInvitedNotifEnabled()) {
            return;
        }

        Context app = context.getApplicationContext();
        Single<OwnerInfo> group = OwnerInfo.getRx(app, accountId, -Math.abs(group_id));
        Single<OwnerInfo> user = OwnerInfo.getRx(app, accountId, from_id);

        Single.zip(group, user, Pair::new)
                .subscribeOn(NotificationScheduler.INSTANCE)
                .subscribe(pair -> {
                    OwnerInfo userInfo = pair.getSecond();
                    OwnerInfo groupInfo = pair.getFirst();
                    notifyImpl(app, userInfo.getUser(), groupInfo.getAvatar(), groupInfo.getCommunity());
                }, throwable -> {/*ignore*/});
    }

    private void notifyImpl(Context context, @NonNull User user, Bitmap groupBitmap, @NonNull Community community){
        String contentText = context.getString(R.string.invites_you_to_join_community, user.getFullName());
        final NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Utils.hasOreo()){
            nManager.createNotificationChannel(AppNotificationChannels.getGroupInvitesChannel(context));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AppNotificationChannels.GROUP_INVITES_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notify_statusbar)
                .setLargeIcon(groupBitmap)
                .setContentTitle(community.getFullName())
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setAutoCancel(true);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        int aid = Settings.get()
                .accounts()
                .getCurrent();

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Extra.PLACE, PlaceFactory.getOwnerWallPlace(aid, community));
        intent.setAction(MainActivity.ACTION_OPEN_PLACE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(context, group_id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();

        configOtherPushNotification(notification);
        nManager.notify(String.valueOf(group_id), NotificationHelper.NOTIFICATION_GROUP_INVITE_ID, notification);
    }
}