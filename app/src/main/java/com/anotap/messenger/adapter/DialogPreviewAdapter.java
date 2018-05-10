package com.anotap.messenger.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anotap.messenger.model.Message;
import com.squareup.picasso.Transformation;

import java.util.EventListener;
import java.util.List;

import com.anotap.messenger.Constants;
import com.anotap.messenger.R;
import com.anotap.messenger.model.Chat;
import com.anotap.messenger.model.Community;
import com.anotap.messenger.model.User;
import com.anotap.messenger.settings.CurrentTheme;
import com.anotap.messenger.util.ViewUtils;

/**
 * Created by golde on 02.05.2017.
 * phoenix
 */
public class DialogPreviewAdapter extends RecyclerView.Adapter<DialogPreviewAdapter.DialogPreviewHolder> {

    private List<Object> mData;
    private Transformation mTransformation;

    private final ActionListener actionListener;

    public DialogPreviewAdapter(Context context, List<Object> items, ActionListener actionListener) {
        this.mData = items;
        this.mTransformation = CurrentTheme.createTransformationForAvatar(context);
        this.actionListener = actionListener;
    }

    @Override
    public DialogPreviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DialogPreviewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dialog_preview, parent, false));
    }

    @Override
    public void onBindViewHolder(DialogPreviewHolder holder, int position) {
        Object item = mData.get(position);

        if (item instanceof User) {
            bindUserDialog(holder, (User) item);
        }

        if (item instanceof Community) {
            bindGroupDialog(holder, (Community) item);
        }

        if (item instanceof Chat) {
            bindGroupChat(holder, (Chat) item);
        }

        if (item instanceof Message) {
            bindUserDialog(holder, (Message) item);
        }

        holder.itemView.setOnClickListener(v -> actionListener.onEntryClick(item));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void bindUserDialog(DialogPreviewHolder holder, User item) {
        holder.mTitle.setText(item.getFullName());
        ViewUtils.displayAvatar(holder.mAvatar, mTransformation, item.get100photoOrSmaller(), Constants.PICASSO_TAG);
    }

    private void bindUserDialog(DialogPreviewHolder holder, Message item) {
        holder.mTitle.setText(item.getSender().getFullName());
        ViewUtils.displayAvatar(holder.mAvatar, mTransformation, item.getSender().get100photoOrSmaller(), Constants.PICASSO_TAG);
    }

    private void bindGroupDialog(DialogPreviewHolder holder, Community item) {
        holder.mTitle.setText(item.getFullName());
        ViewUtils.displayAvatar(holder.mAvatar, mTransformation, item.get100photoOrSmaller(), Constants.PICASSO_TAG);
    }

    private void bindGroupChat(DialogPreviewHolder holder, Chat item){
        holder.mTitle.setText(item.getTitle());
        ViewUtils.displayAvatar(holder.mAvatar, mTransformation, item.get100orSmallerAvatar(), Constants.PICASSO_TAG, R.drawable.ic_group_chat);
    }

    public interface ActionListener extends EventListener {
        void onEntryClick(Object o);
    }

    public void setData(List<Object> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    class DialogPreviewHolder extends RecyclerView.ViewHolder {

        private ImageView mAvatar;
        private TextView mTitle;

        DialogPreviewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.dialog_title);
            mAvatar = itemView.findViewById(R.id.item_chat_avatar);
        }
    }
}