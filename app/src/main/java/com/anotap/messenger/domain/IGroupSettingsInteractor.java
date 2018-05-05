package com.anotap.messenger.domain;

import java.util.List;

import com.anotap.messenger.fragment.search.nextfrom.IntNextFrom;
import com.anotap.messenger.model.Banned;
import com.anotap.messenger.model.GroupSettings;
import com.anotap.messenger.model.Manager;
import com.anotap.messenger.model.User;
import com.anotap.messenger.util.Pair;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Ruslan Kolbasa on 15.06.2017.
 * phoenix
 */
public interface IGroupSettingsInteractor {

    Single<GroupSettings> getGroupSettings(int accountId, int groupId);

    Completable banUser(int accountId, int groupId, int userId, Long endDateUnixtime, int reason, String comment, boolean showCommentToUser);

    Completable editManager(int accountId, int groupId, User user, String role, boolean asContact, String position, String email, String phone);

    Completable unbanUser(int accountId, int groupId, int userId);

    Single<Pair<List<Banned>, IntNextFrom>> getBanned(int accountId, int groupId, IntNextFrom startFrom, int count);

    Single<List<Manager>> getManagers(int accountId, int groupId);
}