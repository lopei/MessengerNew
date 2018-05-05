package com.anotap.messenger.mvp.presenter.history;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.R;
import com.anotap.messenger.api.Apis;
import com.anotap.messenger.api.model.VKApiAudio;
import com.anotap.messenger.api.model.response.AttachmentsHistoryResponse;
import com.anotap.messenger.domain.mappers.Dto2Model;
import com.anotap.messenger.model.Audio;
import com.anotap.messenger.mvp.view.IChatAttachmentAudiosView;
import com.anotap.messenger.util.Pair;
import com.anotap.mvp.reflect.OnGuiCreated;
import io.reactivex.Single;

import static com.anotap.messenger.util.Objects.nonNull;
import static com.anotap.messenger.util.Utils.safeCountOf;

/**
 * Created by admin on 29.03.2017.
 * phoenix
 */
public class ChatAttachmentAudioPresenter extends BaseChatAttachmentsPresenter<Audio, IChatAttachmentAudiosView> {

    public ChatAttachmentAudioPresenter(int peerId, int accountId, @Nullable Bundle savedInstanceState) {
        super(peerId, accountId, savedInstanceState);
    }

    @Override
    void onDataChanged() {
        super.onDataChanged();
        resolveToolbar();
    }

    @Override
    Single<Pair<String, List<Audio>>> requestAttachments(int peerId, String nextFrom) {
        return Apis.get().vkDefault(getAccountId())
                .messages()
                .getHistoryAttachments(peerId, "audio", nextFrom, 50, null)
                .map(response -> {
                    List<Audio> audios = new ArrayList<>(safeCountOf(response.items));

                    if (nonNull(response.items)) {
                        for (AttachmentsHistoryResponse.One one : response.items) {
                            if (nonNull(one) && nonNull(one.entry) && one.entry.attachment instanceof VKApiAudio) {
                                VKApiAudio dto = (VKApiAudio) one.entry.attachment;
                                audios.add(Dto2Model.transform(dto));
                            }
                        }
                    }

                    return Pair.create(response.next_from, audios);
                });
    }

    @SuppressWarnings("unused")
    public void fireAudioPlayClick(int position, Audio audio){
        super.fireAudioPlayClick(position, new ArrayList<>(data));
    }

    @OnGuiCreated
    private void resolveToolbar() {
        if (isGuiReady()) {
            getView().setToolbarTitle(getString(R.string.attachments_in_chat));
            getView().setToolbarSubtitle(getString(R.string.audios_count, safeCountOf(data)));
        }
    }

    @Override
    protected String tag() {
        return ChatAttachmentAudioPresenter.class.getSimpleName();
    }
}
