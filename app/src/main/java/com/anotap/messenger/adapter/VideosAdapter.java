package com.anotap.messenger.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.anotap.messenger.Constants;
import com.anotap.messenger.R;
import com.anotap.messenger.api.PicassoInstance;
import com.anotap.messenger.model.Video;
import com.anotap.messenger.util.AppTextUtils;
import com.anotap.messenger.util.Objects;
import com.anotap.messenger.util.Utils;
import com.anotap.messenger.view.VideoServiceIcons;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.Holder> {

    private Context context;
    private List<Video> data;
    private VideoOnClickListener videoOnClickListener;

    public VideosAdapter(@NonNull Context context, @NonNull List<Video> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_fave_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final Video video = data.get(position);

        holder.viewsCount.setText(context.getString(R.string.view_count, video.getViews()));
        holder.title.setText(video.getTitle());
        holder.videoLenght.setText(AppTextUtils.getDurationString(video.getDuration()));

        String photoUrl = video.getMaxResolutionPhoto();

        if (Utils.nonEmpty(photoUrl)) {
            PicassoInstance.with()
                    .load(photoUrl)
                    .tag(Constants.PICASSO_TAG)
                    .into(holder.image);
        }

        Integer serviceIcon = VideoServiceIcons.getIconByType(video.getPlatform());
        if(Objects.nonNull(serviceIcon)){
            holder.videoService.setVisibility(View.VISIBLE);
            holder.videoService.setImageResource(serviceIcon);
        } else {
            holder.videoService.setVisibility(View.GONE);
        }

        holder.card.setOnClickListener(v -> {
            if(videoOnClickListener != null){
                videoOnClickListener.onVideoClick(position, video);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setVideoOnClickListener(VideoOnClickListener videoOnClickListener) {
        this.videoOnClickListener = videoOnClickListener;
    }

    public void setData(List<Video> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public interface VideoOnClickListener {
        void onVideoClick(int position, Video video);
    }

    public class Holder extends RecyclerView.ViewHolder {

        View card;
        ImageView image;
        TextView videoLenght;
        ImageView videoService;
        TextView title;
        TextView viewsCount;

        public Holder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_view);
            image = itemView.findViewById(R.id.video_image);
            videoLenght = itemView.findViewById(R.id.video_lenght);
            videoService = itemView.findViewById(R.id.video_service);
            title = itemView.findViewById(R.id.title);
            viewsCount = itemView.findViewById(R.id.view_count);
        }
    }
}