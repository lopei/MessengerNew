package com.anotap.messenger.domain.impl;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.Constants;
import com.anotap.messenger.api.interfaces.INetworker;
import com.anotap.messenger.api.model.VKApiOwner;
import com.anotap.messenger.domain.ILikesInteractor;
import com.anotap.messenger.domain.mappers.Dto2Model;
import com.anotap.messenger.model.Owner;
import com.anotap.messenger.util.Utils;
import io.reactivex.Single;

/**
 * Created by admin on 03.10.2017.
 * phoenix
 */
public class LikesInteractor implements ILikesInteractor {

    private final INetworker networker;

    public LikesInteractor(INetworker networker) {
        this.networker = networker;
    }

    @Override
    public Single<List<Owner>> getLikes(int accountId, String type, int ownerId, int itemId, String filter, int count, int offset) {
        return networker.vkDefault(accountId)
                .likes()
                .getList(type, ownerId, itemId, null, filter, null, offset, count, null, Constants.MAIN_OWNER_FIELDS)
                .map(response -> {
                    List<VKApiOwner> dtos = Utils.listEmptyIfNull(response.owners);
                    List<Owner> owners = new ArrayList<>(dtos.size());

                    for(VKApiOwner dto : dtos){
                        owners.add(Dto2Model.transformOwner(dto));
                    }

                    return owners;
                });
    }
}