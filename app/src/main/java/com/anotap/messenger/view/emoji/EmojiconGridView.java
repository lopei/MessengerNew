package com.anotap.messenger.view.emoji;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import java.util.Arrays;

import com.anotap.messenger.R;
import com.anotap.messenger.view.emoji.section.Emojicon;
import com.anotap.messenger.view.emoji.section.People;

public class EmojiconGridView {

    public View rootView;
    EmojiconsPopup mEmojiconPopup;
    Emojicon[] mData;

    public EmojiconGridView(Context context, Emojicon[] emojicons, EmojiconsPopup emojiconPopup) {
        LayoutInflater inflater = LayoutInflater.from(context);

        mEmojiconPopup = emojiconPopup;
        rootView = inflater.inflate(R.layout.emojicon_grid, null);
        GridView gridView = rootView.findViewById(R.id.Emoji_GridView);

        if (emojicons == null) {
            mData = People.DATA;
        } else {
            mData = Arrays.asList(emojicons).toArray(new Emojicon[emojicons.length]);
        }

        EmojiAdapter mAdapter = new EmojiAdapter(rootView.getContext(), mData);
        mAdapter.setEmojiClickListener(emojicon -> {
            if (mEmojiconPopup.getOnEmojiconClickedListener() != null) {
                mEmojiconPopup.getOnEmojiconClickedListener().onEmojiconClicked(emojicon);
            }
        });

        gridView.setAdapter(mAdapter);
    }

    public interface OnEmojiconClickedListener {
        void onEmojiconClicked(Emojicon emojicon);
    }
}