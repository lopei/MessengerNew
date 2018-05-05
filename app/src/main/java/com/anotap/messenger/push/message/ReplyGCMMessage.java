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
import android.text.Spannable;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.MainActivity;
import com.anotap.messenger.link.LinkHelper;
import com.anotap.messenger.link.internal.OwnerLinkSpanFactory;
import com.anotap.messenger.longpoll.AppNotificationChannels;
import com.anotap.messenger.longpoll.NotificationHelper;
import com.anotap.messenger.model.Commented;
import com.anotap.messenger.model.Owner;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.push.NotificationScheduler;
import com.anotap.messenger.push.NotificationUtils;
import com.anotap.messenger.push.OwnerInfo;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.Logger;
import com.anotap.messenger.util.Utils;

import static com.anotap.messenger.push.NotificationUtils.configOtherPushNotification;

public class ReplyGCMMessage {

    private static final String TAG = ReplyGCMMessage.class.getSimpleName();

    //{from_id=175895893, reply_id=4908, first_name=Руслан, sex=2, from=376771982493,
    // text=[id25651989|Руслан], тест, type=reply, place=wall-72124992_4688, sandbox=0,
    // collapse_key=reply, last_name=Колбаса

    private int from_id;
    private int reply_id;
    //public String firstName;
    //private int sex;
    //public long from;
    private String text;
    private String place;
    //public String lastName;
    //private String type;

    public static ReplyGCMMessage fromBundle(@NonNull Bundle bundle){
        ReplyGCMMessage message = new ReplyGCMMessage();
        message.from_id = NotificationUtils.optInt(bundle, "from_id");
        message.reply_id = NotificationUtils.optInt(bundle, "reply_id");
        //message.sex = optInt(bundle, "sex");
        //message.firstName = bundle.getString("first_name");
        //message.lastName = bundle.getString("last_name");
        //message.from = optLong(bundle, "from");
        message.text = bundle.getString("text");
        //message.type = bundle.getString("type");
        message.place = bundle.getString("place");
        return message;
    }

    public void notify(final Context context, int accountId){
        if (!Settings.get()
                .notifications()
                .isReplyNotifEnabled()) {
            return;
        }

        Context app = context.getApplicationContext();
        OwnerInfo.getRx(app, accountId, from_id)
                .subscribeOn(NotificationScheduler.INSTANCE)
                .subscribe(ownerInfo -> notifyImpl(app, ownerInfo.getOwner(), ownerInfo.getAvatar()), throwable -> {/*ignore*/});
    }

    private void notifyImpl(Context context, @NonNull Owner owner, Bitmap bitmap){
        String url = "vk.com/" + place;
        Commented commented = LinkHelper.findCommentedFrom(url);

        if(commented == null){
            Logger.e(TAG, "Unknown place: " + place);
            return;
        }

        Spannable snannedText = OwnerLinkSpanFactory.withSpans(text, true, false, null);
        String targetText = snannedText == null ? null : snannedText.toString();

        final NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Utils.hasOreo()){
            nManager.createNotificationChannel(AppNotificationChannels.getCommentsChannel(context));
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AppNotificationChannels.COMMENTS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notify_statusbar)
                .setLargeIcon(bitmap)
                .setContentTitle(owner.getFullName())
                .setContentText(targetText)
                .setSubText(context.getString(R.string.in_reply_to_your_comment))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(targetText))
                .setAutoCancel(true);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        int aid = Settings.get()
                .accounts()
                .getCurrent();

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Extra.PLACE, PlaceFactory.getCommentsPlace(aid, commented, reply_id));
        intent.setAction(MainActivity.ACTION_OPEN_PLACE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(context, reply_id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();

        configOtherPushNotification(notification);
        nManager.notify(place, NotificationHelper.NOTIFICATION_REPLY_ID, notification);
    }
}
