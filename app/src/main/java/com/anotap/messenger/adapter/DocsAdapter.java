package com.anotap.messenger.adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.anotap.messenger.settings.CurrentTheme;
import com.anotap.messenger.util.AppTextUtils;

import static com.anotap.messenger.util.Objects.nonNull;

/**
 * Created by admin on 25.12.2016.
 * phoenix
 */
public class DocsAdapter extends RecyclerBindableAdapter<Document, DocsAdapter.DocViewHolder> {

    public DocsAdapter(List<Document> data) {
        super(data);
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

        String targetExt = "." + item.getExt().toUpperCase();

        holder.tvExt.setText(targetExt);
        holder.tvTitle.setText(item.getTitle());
        holder.tvSize.setText(AppTextUtils.getSizeString((int) item.getSize()));

        String previewUrl = item.getPreviewWithSize(PhotoSize.M, false);
        boolean withImage = !TextUtils.isEmpty(previewUrl);

        holder.ivImage.setVisibility(withImage ? View.VISIBLE : View.GONE);
        holder.ivImage.setBackgroundColor(Color.TRANSPARENT);

        if (withImage) {
            PicassoInstance.with()
                    .load(previewUrl)
                    .tag(Constants.PICASSO_TAG)
                    .into(holder.ivImage);
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
        return R.layout.item_document_big;
    }

    static class DocViewHolder extends RecyclerView.ViewHolder {

        TextView tvExt;
        ImageView ivImage;
        TextView tvTitle;
        TextView tvSize;

        private DocViewHolder(View root) {
            super(root);
            tvExt = root.findViewById(R.id.item_document_big_ext);
            ivImage = root.findViewById(R.id.item_document_big_image);
            tvTitle = root.findViewById(R.id.item_document_big_title);
            tvSize = root.findViewById(R.id.item_document_big_size);

            tvExt.getBackground().setColorFilter(CurrentTheme.getIconColorActive(root.getContext()),
                    PorterDuff.Mode.MULTIPLY);
        }
    }
}