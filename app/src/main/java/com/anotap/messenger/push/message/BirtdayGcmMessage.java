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
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.MainActivity;
import com.anotap.messenger.domain.IOwnersInteractor;
import com.anotap.messenger.domain.InteractorFactory;
import com.anotap.messenger.longpoll.AppNotificationChannels;
import com.anotap.messenger.longpoll.NotificationHelper;
import com.anotap.messenger.model.Owner;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.push.NotificationScheduler;
import com.anotap.messenger.push.NotificationUtils;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.Objects;
import com.anotap.messenger.util.Utils;

import static com.anotap.messenger.push.NotificationUtils.configOtherPushNotification;
import static com.anotap.messenger.util.Utils.isEmpty;

public class BirtdayGcmMessage {

    //Bundle[{type=birthday, uids=20924995, _genSrv=605120, sandbox=0, collapse_key=birthday}]

    //key: google.sent_time, value: 1481879102336, class: class java.lang.Long
    //key: type, value: birthday, class: class java.lang.String
    //key: uids, value: 61354506,8056682, class: class java.lang.String
    //key: google.message_id, value: 0:1481879102338566%8c76e97a3fbd627d, class: class java.lang.String
    //key: no_sound, value: 1, class: class java.lang.String
    //key: _genSrv, value: 605119, class: class java.lang.String
    //key: sandbox, value: 0, class: class java.lang.String
    //key: collapse_key, value: birthday, class: class java.lang.String

    private ArrayList<Integer> uids;

    public void notify(Context context, int accountId) {
        if (isEmpty(uids)) {
            return;
        }

        if (!Settings.get()
                .notifications()
                .isBirtdayNotifEnabled()) {
            return;
        }

        InteractorFactory.createOwnerInteractor()
                .findBaseOwnersDataAsList(accountId, uids, IOwnersInteractor.MODE_ANY)
                .subscribeOn(NotificationScheduler.INSTANCE)
                .subscribe(owners -> onOwnersDataReceived(context, accountId, owners), t -> {});
    }

    private void onOwnersDataReceived(Context context, int accoutnId, List<Owner> owners){
        for(Owner owner : owners){
            NotificationUtils.loadRoundedImageRx(context, owner.get100photoOrSmaller(), R.drawable.ic_avatar_unknown)
                    .subscribeOn(NotificationScheduler.INSTANCE)
                    .subscribe(bitmap -> onUsersDataReceived(context, accoutnId, owner, bitmap), t -> {});
        }
    }

    private void onUsersDataReceived(Context context, int accountId, Owner owner, Bitmap bitmap) {
        int ownerId = owner.getOwnerId();

        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Objects.isNull(manager)) {
            return;
        }

        if (Utils.hasOreo()){
            manager.createNotificationChannel(AppNotificationChannels.getBirthdaysChannel(context));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AppNotificationChannels.BIRTHDAYS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_statusbar_birthday)
                .setLargeIcon(bitmap)
                .setContentTitle(context.getString(R.string.birthday))
                .setContentText(owner.getFullName())
                .setAutoCancel(true);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Extra.PLACE, PlaceFactory.getOwnerWallPlace(accountId, ownerId, owner));
        intent.setAction(MainActivity.ACTION_OPEN_PLACE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(context, ownerId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();

        configOtherPushNotification(notification);
        manager.notify(String.valueOf(ownerId), NotificationHelper.NOTIFICATION_BIRTHDAY, notification);
    }

    public static BirtdayGcmMessage fromBundle(@NonNull Bundle bundle) {
        BirtdayGcmMessage message = new BirtdayGcmMessage();

        String uidsString = bundle.getString("uids");
        String[] uidsStringArray = TextUtils.isEmpty(uidsString) ? null : uidsString.split(",");

        if (uidsStringArray != null) {
            message.uids = new ArrayList<>(uidsStringArray.length);
            for (String anUidsStringArray : uidsStringArray) {
                try {
                    message.uids.add(Integer.parseInt(anUidsStringArray));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return message;
    }
}