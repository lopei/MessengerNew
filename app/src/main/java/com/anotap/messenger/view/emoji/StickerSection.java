package com.anotap.messenger.view.emoji;

import com.anotap.messenger.model.StickerSet;

public class StickerSection extends AbsSection {

    public StickerSet stickerSet;

    public StickerSection(StickerSet set) {
        super(TYPE_STICKER);
        this.stickerSet = set;
    }
}
