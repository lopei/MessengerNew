package com.anotap.messenger.plugins;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.exception.NotFoundException;
import com.anotap.messenger.model.Audio;
import com.anotap.messenger.util.Utils;
import io.reactivex.Single;

import static com.anotap.messenger.util.Objects.isNull;

/**
 * Created by admin on 2/3/2018.
 * Phoenix-for-VK
 */
public class AudioPluginConnector implements IAudioPluginConnector {

    private static final String AUTHORITY = "com.anotap.messenger.AudioProvider";

    private final Context app;

    public AudioPluginConnector(Context context) {
        this.app = context.getApplicationContext();
    }

    @Override
    public Single<List<Audio>> get(int ownerId, int offset) {
        return Single.create(emitter -> {
            Uri uri = new Uri.Builder()
                    .scheme("content")
                    .authority(AUTHORITY)
                    .path("audios")
                    .appendQueryParameter("owner_id", String.valueOf(ownerId))
                    .appendQueryParameter("offset", String.valueOf(offset))
                    .build();

            Cursor cursor = app.getContentResolver().query(uri, null, null, null, null);

            List<Audio> audios = new ArrayList<>(Utils.safeCountOf(cursor));

            if(cursor != null){
                while (cursor.moveToNext()){
                    if(emitter.isDisposed()){
                        break;
                    }

                    try {
                        checkAudioPluginError(cursor);
                    } catch (Exception e) {
                        cursor.close();
                        emitter.onError(e);
                        return;
                    }


                    int audioId = cursor.getInt(cursor.getColumnIndex("audio_id"));
                    int ownerId1 = cursor.getInt(cursor.getColumnIndex("owner_id"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    String artist = cursor.getString(cursor.getColumnIndex("artist"));
                    int duration = cursor.getInt(cursor.getColumnIndex("duration"));
                    String cover = cursor.getString(cursor.getColumnIndex("cover_url"));
                    String bigCover = cursor.getString(cursor.getColumnIndex("cover_url_big"));

                    Audio audio = new Audio()
                            .setArtist(artist)
                            .setDuration(duration)
                            .setId(audioId)
                            .setOwnerId(ownerId1)
                            .setTitle(title)
                            .setBigCover(bigCover)
                            .setCover(cover);

                    audios.add(audio);
                }

                cursor.close();
            }

            emitter.onSuccess(audios);
        });
    }

    private static void checkAudioPluginError(Cursor cursor) throws AudioPluginException {
        if(cursor.getPosition() == 0){
            int errorCodeIndex = cursor.getColumnIndex("error_code");

            if(errorCodeIndex >= 0){
                int code = cursor.getInt(errorCodeIndex);
                String message = cursor.getString(cursor.getColumnIndex("error_message"));
                throw new AudioPluginException(code, message);
            }
        }
    }

    @Override
    public Single<String> findAudioUrl(int audioId, int ownerId) {
        return Single.create(emitter -> {
            Uri uri = new Uri.Builder()
                    .scheme("content")
                    .authority(AUTHORITY)
                    .path("urls")
                    .appendQueryParameter("owner_id", String.valueOf(ownerId))
                    .appendQueryParameter("audio_id", String.valueOf(audioId))
                    .build();

            Cursor cursor = app.getContentResolver().query(uri, null, null, null, null);

            String url = null;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    url = cursor.getString(cursor.getColumnIndex("url"));
                }

                cursor.close();
            }

            if (isNull(url)) {
                emitter.onError(new NotFoundException());
            } else {
                emitter.onSuccess(url);
            }
        });
    }

    @Override
    public boolean isPluginAvailable() {
        Uri uri = new Uri.Builder()
                .scheme("content")
                .authority(AUTHORITY)
                .path("availability")
                .build();

        boolean available = false;

        try {
            Cursor cursor = app.getContentResolver().query(uri, null, null, null, null);
            if(cursor != null){
                if(cursor.moveToNext()){
                    available = cursor.getInt(cursor.getColumnIndex("available")) == 1;
                }
                cursor.close();
            }
        } catch (Exception ignored){

        }

        return available;
    }

    public static final class AudioPluginException extends Exception {

        final int code;

        AudioPluginException(int code, String message) {
            super(message);
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}