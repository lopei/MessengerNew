/*
 * Copyright (C) 2012 Andrew Neal Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.anotap.messenger.player.ui;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import com.anotap.messenger.R;
import com.anotap.messenger.player.MusicPlaybackService;
import com.anotap.messenger.player.util.MusicUtils;
import com.anotap.messenger.settings.CurrentTheme;

public class ShuffleButton extends AppCompatImageButton implements OnClickListener {
    public ShuffleButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        MusicUtils.cycleShuffle();
        updateShuffleState();
    }

    public void updateShuffleState() {
        switch (MusicUtils.getShuffleMode()) {
            case MusicPlaybackService.SHUFFLE:
                setImageResource(R.drawable.shuffle);
                setColorFilter(CurrentTheme.getIconColorActive(getContext()));
                break;
            case MusicPlaybackService.SHUFFLE_NONE:
                setImageResource(R.drawable.shuffle_disabled);
                setColorFilter(CurrentTheme.getIconColorStatic(getContext()));
                break;
            default:
                break;
        }
    }

}
