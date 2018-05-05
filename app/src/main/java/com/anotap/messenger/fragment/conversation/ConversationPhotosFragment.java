package com.anotap.messenger.fragment.conversation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.adapter.fave.FavePhotosAdapter;
import com.anotap.messenger.model.Photo;
import com.anotap.messenger.model.TmpSource;
import com.anotap.messenger.mvp.presenter.history.ChatAttachmentPhotoPresenter;
import com.anotap.messenger.mvp.view.IChatAttachmentPhotosView;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.mvp.core.IPresenterFactory;

public class ConversationPhotosFragment extends AbsChatAttachmentsFragment<Photo, ChatAttachmentPhotoPresenter,
        IChatAttachmentPhotosView> implements FavePhotosAdapter.PhotoSelectionListener, IChatAttachmentPhotosView {

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        int columns = getContext().getResources().getInteger(R.integer.photos_column_count);
        return new GridLayoutManager(getActivity(), columns);
    }

    @Override
    public RecyclerView.Adapter createAdapter() {
        FavePhotosAdapter apiPhotoFavePhotosAdapter = new FavePhotosAdapter(getActivity(), Collections.emptyList());
        apiPhotoFavePhotosAdapter.setPhotoSelectionListener(this);
        return apiPhotoFavePhotosAdapter;
    }

    @Override
    public void onPhotoClicked(int position, Photo photo) {
        getPresenter().firePhotoClick(position, photo);
    }

    @Override
    public IPresenterFactory<ChatAttachmentPhotoPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int accountId = getArguments().getInt(Extra.ACCOUNT_ID);
            int peerId = getArguments().getInt(Extra.PEER_ID);
            return new ChatAttachmentPhotoPresenter(peerId, accountId, saveInstanceState);
        };
    }

    @Override
    protected String tag() {
        return ConversationPhotosFragment.class.getSimpleName();
    }

    @Override
    public void displayAttachments(List<Photo> data) {
        FavePhotosAdapter adapter = (FavePhotosAdapter) getAdapter();
        adapter.setData(data);
    }

    @Override
    public void goToTempPhotosGallery(int accountId, @NonNull TmpSource source, int index) {
        PlaceFactory.getTmpSourceGalleryPlace(accountId, source, index).tryOpenWith(getActivity());
    }
}