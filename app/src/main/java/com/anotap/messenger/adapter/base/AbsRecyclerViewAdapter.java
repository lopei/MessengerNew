package com.anotap.messenger.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.anotap.messenger.adapter.listener.OwnerClickListener;

import static com.anotap.messenger.util.Objects.nonNull;

/**
 * Created by Ruslan Kolbasa on 15.05.2017.
 * phoenix
 */
public abstract class AbsRecyclerViewAdapter<H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {

    protected OwnerClickListener ownerClickListener;

    public void setOwnerClickListener(OwnerClickListener ownerClickListener) {
        this.ownerClickListener = ownerClickListener;
    }

    protected void addOwnerAvatarClickHandling(View view, final int ownerId){
        view.setOnClickListener(v -> {
            if(nonNull(ownerClickListener)){
                ownerClickListener.onOwnerClick(ownerId);
            }
        });
    }
}