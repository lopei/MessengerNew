package com.anotap.messenger.push.message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.MainActivity;
import com.anotap.messenger.longpoll.AppNotificationChannels;
import com.anotap.messenger.longpoll.NotificationHelper;
import com.anotap.messenger.model.Commented;
import com.anotap.messenger.model.CommentedType;
import com.anotap.messenger.model.Photo;
import com.anotap.messenger.place.Place;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.push.NotificationScheduler;
import com.anotap.messenger.push.NotificationUtils;
import com.anotap.messenger.push.OwnerInfo;
import com.anotap.messenger.push.VkPlace;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.PersistentLogger;
import com.anotap.messenger.util.Utils;

import static com.anotap.messenger.push.NotificationUtils.configOtherPushNotification;
import static com.anotap.messenger.push.NotificationUtils.optInt;
import static com.anotap.messenger.util.Objects.isNull;
import static com.anotap.messenger.util.Utils.singletonArrayList;
import static com.anotap.messenger.util.Utils.stringEmptyIfNull;

/**
 * Created by ruslan.kolbasa on 10.10.2016.
 * phoenix
 */
public class LikeGcmMessage {

    private final int accountId;

    private String object;

    private int fromId;

    private String firstName;

    private String lastName;

    private int likesCount;

    private int replyId;

    public LikeGcmMessage(int accountId, Bundle bundle) {
        this.accountId = accountId;
        this.object = bundle.getString("object");
        this.fromId = NotificationUtils.optInt(bundle, "from_id");
        this.firstName = bundle.getString("first_name");
        this.lastName = bundle.getString("last_name");
        this.likesCount = NotificationUtils.optInt(bundle, "likes_count");
        this.replyId = optInt(bundle, "reply_id");
    }

    private void notifyImpl(Context context, OwnerInfo info) {
        VkPlace parsedPlace = VkPlace.parse(object);

        if (isNull(parsedPlace)) {
            PersistentLogger.logThrowable("Push issues", new Exception("LikeGcmMessage, UNKNOWN OBJECT: " + object));
            return;
        }

        String userName = (stringEmptyIfNull(firstName) + " " + stringEmptyIfNull(lastName)).trim();

        Place place = null;
        String contentText = null;

        if (parsedPlace instanceof VkPlace.Photo) {
            VkPlace.Photo photo = (VkPlace.Photo) parsedPlace;

            ArrayList<Photo> photos = singletonArrayList(
                    new Photo().setId(photo.getPhotoId()).setOwnerId(photo.getOwnerId())
            );

            place = PlaceFactory.getSimpleGalleryPlace(accountId, photos, 0, true);
            contentText = context.getString(R.string.push_user_liked_your_photo, userName);
        } else if (parsedPlace instanceof VkPlace.PhotoComment) {
            VkPlace.PhotoComment photoComment = (VkPlace.PhotoComment) parsedPlace;
            Commented commented = new Commented(photoComment.getPhotoId(), photoComment.getOwnerId(), CommentedType.PHOTO, null);
            place = PlaceFactory.getCommentsPlace(accountId, commented, replyId);
            contentText = context.getString(R.string.push_user_liked_your_comment_on_the_photo, userName);
        } else if (parsedPlace instanceof VkPlace.Video) {
            VkPlace.Video video = (VkPlace.Video) parsedPlace;
            place = PlaceFactory.getVideoPreviewPlace(accountId, video.getOwnerId(), video.getVideoId(), null);
            contentText = context.getString(R.string.push_user_liked_your_video, userName);
        } else if (parsedPlace instanceof VkPlace.VideoComment) {
            VkPlace.VideoComment videoComment = (VkPlace.VideoComment) parsedPlace;
            Commented commented = new Commented(videoComment.getVideoId(), videoComment.getOwnerId(), CommentedType.VIDEO, null);
            place = PlaceFactory.getCommentsPlace(accountId, commented, replyId);
            contentText = context.getString(R.string.push_user_liked_your_comment_on_the_video, userName);
        } else if (parsedPlace instanceof VkPlace.WallPost) {
            VkPlace.WallPost wallPost = (VkPlace.WallPost) parsedPlace;
            place = PlaceFactory.getPostPreviewPlace(accountId, wallPost.getPostId(), wallPost.getOwnerId(), null);
            contentText = context.getString(R.string.push_user_liked_your_post, userName);
        } else if (parsedPlace instanceof VkPlace.WallComment) {
            VkPlace.WallComment wallComment = (VkPlace.WallComment) parsedPlace;
            Commented commented = new Commented(wallComment.getPostId(), wallComment.getOwnerId(), CommentedType.POST, null);
            place = PlaceFactory.getCommentsPlace(accountId, commented, replyId);
            contentText = context.getString(R.string.push_user_liked_your_comment_on_the_post, userName);
        }

        if (isNull(place)) {
            PersistentLogger.logThrowable("Push issues", new Exception("LikeGcmMessage, UNKNOWN PLACE: " + object));
            return;
        }

        final NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Utils.hasOreo()){
            nManager.createNotificationChannel(AppNotificationChannels.getLikesChannel(context));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AppNotificationChannels.LIKES_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_statusbar_like)
                .setLargeIcon(info.getAvatar())
                .setContentTitle(context.getString(R.string.like_title))
                .setContentText(contentText)
                .setNumber(likesCount)
                .setAutoCancel(true);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Extra.PLACE, place);
        intent.setAction(MainActivity.ACTION_OPEN_PLACE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(context, fromId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(contentIntent);

        Notification notification = builder.build();

        configOtherPushNotification(notification);

        nManager.notify("like_" + object, NotificationHelper.NOTIFICATION_LIKE, notification);
    }

    public void notifyIfNeed(Context context) {
        if (!Settings.get()
                .notifications()
                .isLikeNotificationEnable()) {
            return;
        }

        OwnerInfo.getRx(context.getApplicationContext(), accountId, fromId)
                .subscribeOn(NotificationScheduler.INSTANCE)
                .subscribe(info -> notifyImpl(context, info), throwable -> {/*ignore*/});
    }
}