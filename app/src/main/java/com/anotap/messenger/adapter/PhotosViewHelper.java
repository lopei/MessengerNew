package com.anotap.messenger.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.Constants;
import com.anotap.messenger.R;
import com.anotap.messenger.api.PicassoInstance;
import com.anotap.messenger.model.Document;
import com.anotap.messenger.model.Photo;
import com.anotap.messenger.model.PhotoSize;
import com.anotap.messenger.model.Video;
import com.anotap.messenger.settings.CurrentTheme;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.AppTextUtils;
import com.anotap.messenger.view.mozaik.MozaikLayout;

import static com.anotap.messenger.util.Utils.nonEmpty;

public class PhotosViewHelper {

    private Context context;
    private AttachmentsViewBinder.OnAttachmentsActionCallback attachmentsActionCallback;
    private int mIconColorActive;

    @PhotoSize
    private final int mPhotoPreviewSize;

    PhotosViewHelper(Context context, @NonNull AttachmentsViewBinder.OnAttachmentsActionCallback attachmentsActionCallback) {
        this.context = context;
        this.attachmentsActionCallback = attachmentsActionCallback;
        this.mIconColorActive = CurrentTheme.getIconColorActive(context);
        this.mPhotoPreviewSize = Settings.get().main().getPrefPreviewImageSize();
    }

    private static class Holder {

        final ImageView vgPhoto;
        final ImageView ivPlay;
        final TextView tvTitle;

        Holder(View itemView) {
            vgPhoto = itemView.findViewById(R.id.item_video_image);
            ivPlay = itemView.findViewById(R.id.item_video_play);
            tvTitle = itemView.findViewById(R.id.item_video_title);
        }
    }

    public void displayPhotos(final List<PostImage> photos, final ViewGroup container) {
        container.setVisibility(photos.size() == 0 ? View.GONE : View.VISIBLE);

        if (photos.size() == 0) {
            return;
        }

        int i = photos.size() - container.getChildCount();

        for (int j = 0; j < i; j++) {
            View root = LayoutInflater.from(context).inflate(R.layout.item_video, container, false);
            Holder holder = new Holder(root);
            root.setTag(holder);
            holder.ivPlay.getBackground().setColorFilter(mIconColorActive, PorterDuff.Mode.MULTIPLY);
            container.addView(root);
        }

        if (container instanceof MozaikLayout) {
            ((MozaikLayout) container).setPhotos(photos);
        }

        for (int g = 0; g < container.getChildCount(); g++) {
            View tmpV = container.getChildAt(g);
            Holder holder = (Holder) tmpV.getTag();

            if (g < photos.size()) {
                final PostImage image = photos.get(g);

                holder.ivPlay.setVisibility(image.getType() == PostImage.TYPE_IMAGE ? View.GONE : View.VISIBLE);
                holder.tvTitle.setVisibility(image.getType() == PostImage.TYPE_IMAGE ? View.GONE : View.VISIBLE);

                final int position = g;

                holder.vgPhoto.setOnClickListener(v -> {
                    switch (image.getType()) {
                        case PostImage.TYPE_IMAGE:
                            openImages(photos, position);
                            break;
                        case PostImage.TYPE_VIDEO:
                            Video video = (Video) image.getAttachment();
                            attachmentsActionCallback.onVideoPlay(video);
                            break;
                        case PostImage.TYPE_GIF:
                            Document document = (Document) image.getAttachment();
                            attachmentsActionCallback.onDocPreviewOpen(document);
                            break;
                    }
                });

                final String url = image.getPreviewUrl(mPhotoPreviewSize);

                switch (image.getType()) {
                    case PostImage.TYPE_VIDEO:
                        Video video = (Video) image.getAttachment();
                        holder.tvTitle.setText(AppTextUtils.getDurationString(video.getDuration()));
                        break;
                    case PostImage.TYPE_GIF:
                        Document document = (Document) image.getAttachment();
                        holder.tvTitle.setText(context.getString(R.string.gif, AppTextUtils.getSizeString(document.getSize())));
                        break;
                }

                if (nonEmpty(url)) {
                    PicassoInstance.with()
                            .load(url)
                            .placeholder(R.drawable.background_gray)
                            .tag(Constants.PICASSO_TAG)
                            .into(holder.vgPhoto);

                    tmpV.setVisibility(View.VISIBLE);
                } else {
                    tmpV.setVisibility(View.GONE);
                }
            } else {
                tmpV.setVisibility(View.GONE);
            }
        }
    }

    private void openImages(List<PostImage> photos, int index) {
        ArrayList<Photo> models = new ArrayList<>();

        for (PostImage postImage : photos) {
            if (postImage.getType() == PostImage.TYPE_IMAGE) {
                models.add((Photo) postImage.getAttachment());
            }
        }

        attachmentsActionCallback.onPhotosOpen(models, index);
    }
}