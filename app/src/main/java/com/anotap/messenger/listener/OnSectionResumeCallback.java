package com.anotap.messenger.listener;

import com.anotap.messenger.model.drawer.SectionDrawerItem;

public interface OnSectionResumeCallback {
    void onSectionResume(SectionDrawerItem sectionDrawerItem);
    void onChatResume(int accountId, int peerId, String title, String imgUrl);
    void onClearSelection();
}
