package com.anotap.messenger.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.EventListener;
import java.util.List;

import com.anotap.messenger.Constants;
import com.anotap.messenger.R;
import com.anotap.messenger.adapter.base.RecyclerBindableAdapter;
import com.anotap.messenger.api.PicassoInstance;
import com.anotap.messenger.model.Document;
import com.anotap.messenger.model.PhotoSize;

import static com.anotap.messenger.util.Objects.nonNull;
import static com.anotap.messenger.util.Utils.nonEmpty;

/**
 * Created by admin on 25.12.2016.
 * phoenix
 */
public class DocsAsImagesAdapter extends RecyclerBindableAdapter<Document, DocsAsImagesAdapter.DocViewHolder> {

    public DocsAsImagesAdapter(List<Document> data) {
        super(data);
    }

    public void setData(List<Document> data) {
        super.setItems(data);
    }

    private ActionListener mActionListner;

    public void setActionListner(ActionListener listner) {
        this.mActionListner = listner;
    }

    public interface ActionListener extends EventListener {
        void onDocClick(int index, @NonNull Document doc);
        boolean onDocLongClick(int index, @NonNull Document doc);
    }

    @Override
    protected void onBindItemViewHolder(DocViewHolder holder, int position, int type) {
        Document item = getItem(position);

        holder.title.setText(item.getTitle());

        String previewUrl = item.getPreviewWithSize(PhotoSize.Q, false);
        boolean withImage = nonEmpty(previewUrl);

        if (withImage) {
            PicassoInstance.with()
                    .load(previewUrl)
                    .tag(Constants.PICASSO_TAG)
                    .into(holder.image);
        } else {
            PicassoInstance.with()
                    .cancelRequest(holder.image);
        }

        holder.itemView.setOnClickListener(v -> {
            if(nonNull(mActionListner)){
                mActionListner.onDocClick(holder.getAdapterPosition(), item);
            }
        });

        holder.itemView.setOnLongClickListener(v -> nonNull(mActionListner)
                && mActionListner.onDocLongClick(holder.getAdapterPosition(), item));
    }

    @Override
    protected DocViewHolder viewHolder(View view, int type) {
        return new DocViewHolder(view);
    }

    @Override
    protected int layoutId(int type) {
        return R.layout.item_doc_as_image;
    }

    static class DocViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;

        DocViewHolder(View root) {
            super(root);
            image = root.findViewById(R.id.image);
            title = root.findViewById(R.id.title);
        }
    }
}