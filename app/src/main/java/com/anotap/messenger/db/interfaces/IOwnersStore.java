package com.anotap.messenger.db.interfaces;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.anotap.messenger.db.model.BanAction;
import com.anotap.messenger.db.model.UserPatch;
import com.anotap.messenger.db.model.entity.CommunityEntity;
import com.anotap.messenger.db.model.entity.FriendListEntity;
import com.anotap.messenger.db.model.entity.UserDetailsEntity;
import com.anotap.messenger.db.model.entity.UserEntity;
import com.anotap.messenger.model.Manager;
import com.anotap.messenger.util.Optional;
import com.anotap.messenger.util.Pair;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface IOwnersStore extends IStore {

    Single<Map<Integer, FriendListEntity>> findFriendsListsByIds(int accountId, int userId, Collection<Integer> ids);

    @CheckResult
    Maybe<String> getLocalizedUserActivity(int accountId, int userId);

    Single<Optional<UserEntity>> findUserDboById(int accountId, int ownerId);

    Single<Optional<CommunityEntity>> findCommunityDboById(int accountId, int ownerId);

    Single<Optional<UserEntity>> findUserByDomain(int accoutnId, String domain);

    Single<Optional<CommunityEntity>> findCommunityByDomain(int accountId, String domain);

    Single<List<UserEntity>> findUserDbosByIds(int accountId, List<Integer> ids);

    Single<List<CommunityEntity>> findCommunityDbosByIds(int accountId, List<Integer> ids);

    Completable storeUserDbos(int accountId, List<UserEntity> users);

    Completable storeCommunityDbos(int accountId, List<CommunityEntity> communityEntities);

    @CheckResult
    Single<Collection<Integer>> getMissingUserIds(int accountId, @NonNull Collection<Integer> ids);

    @CheckResult
    Single<Collection<Integer>> getMissingCommunityIds(int accountId, @NonNull Collection<Integer> ids);

    Completable fireBanAction(BanAction action);

    Observable<BanAction> observeBanActions();

    Completable fireManagementChangeAction(Pair<Integer, Manager> manager);

    Observable<Pair<Integer, Manager>> observeManagementChanges();

    Single<Optional<UserDetailsEntity>> getUserDetails(int accountId, int userId);

    Completable storeUserDetails(int accountId, int userId, UserDetailsEntity dbo);

    Completable updateUser(int accountId, int userId, UserPatch patch);
}