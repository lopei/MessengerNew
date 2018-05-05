package com.anotap.messenger.model;

import android.os.Parcel;
import android.os.Parcelable;

import static com.anotap.messenger.util.Utils.stringEmptyIfNull;

/**
 * Created by admin on 22.11.2016.
 * phoenix
 */
public class Audio extends AbsModel implements Parcelable {

    private int id;

    private int ownerId;

    private String artist;

    private String title;

    private int duration;

    private String url;

    private int lyricsId;

    private int albumId;

    private int genre;

    private String accessKey;

    private boolean deleted;

    private String bigCover;

    private String cover;

    public Audio(){

    }

    protected Audio(Parcel in) {
        super(in);
        id = in.readInt();
        ownerId = in.readInt();
        artist = in.readString();
        title = in.readString();
        duration = in.readInt();
        url = in.readString();
        lyricsId = in.readInt();
        albumId = in.readInt();
        genre = in.readInt();
        accessKey = in.readString();
        deleted = in.readByte() != 0;
        bigCover = in.readString();
        cover = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(id);
        dest.writeInt(ownerId);
        dest.writeString(artist);
        dest.writeString(title);
        dest.writeInt(duration);
        dest.writeString(url);
        dest.writeInt(lyricsId);
        dest.writeInt(albumId);
        dest.writeInt(genre);
        dest.writeString(accessKey);
        dest.writeByte((byte) (deleted ? 1 : 0));
        dest.writeString(bigCover);
        dest.writeString(cover);
    }

    public static final Creator<Audio> CREATOR = new Creator<Audio>() {
        @Override
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        @Override
        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };

    public int getId() {
        return id;
    }

    public Audio setId(int id) {
        this.id = id;
        return this;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public Audio setOwnerId(int ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public String getArtist() {
        return artist;
    }

    public Audio setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Audio setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public Audio setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Audio setUrl(String url) {
        this.url = url;
        return this;
    }

    public int getLyricsId() {
        return lyricsId;
    }

    public Audio setLyricsId(int lyricsId) {
        this.lyricsId = lyricsId;
        return this;
    }

    public int getAlbumId() {
        return albumId;
    }

    public Audio setAlbumId(int albumId) {
        this.albumId = albumId;
        return this;
    }

    public int getGenre() {
        return genre;
    }

    public Audio setGenre(int genre) {
        this.genre = genre;
        return this;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public Audio setAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Audio setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public String getBigCover() {
        return bigCover;
    }

    public Audio setBigCover(String bigCover) {
        this.bigCover = bigCover;
        return this;
    }

    public String getCover() {
        return cover;
    }

    public Audio setCover(String cover) {
        this.cover = cover;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getArtistAndTitle(){
        return stringEmptyIfNull(artist) + " - " + stringEmptyIfNull(title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Audio audio = (Audio) o;
        return id == audio.id && ownerId == audio.ownerId;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + ownerId;
        return result;
    }
}