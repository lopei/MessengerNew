package com.anotap.messenger.domain;

import java.util.Collection;
import java.util.List;

import com.anotap.messenger.fragment.search.criteria.PeopleSearchCriteria;
import com.anotap.messenger.model.Community;
import com.anotap.messenger.model.CommunityDetails;
import com.anotap.messenger.model.IOwnersBundle;
import com.anotap.messenger.model.Owner;
import com.anotap.messenger.model.User;
import com.anotap.messenger.model.UserDetails;
import com.anotap.messenger.util.Pair;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by admin on 03.02.2017.
 * phoenix
 */
public interface IOwnersInteractor {

    int MODE_ANY = 1;

    int MODE_NET = 2;

    int MODE_CACHE = 3;

    Single<List<Owner>> findBaseOwnersDataAsList(int accountId, Collection<Integer> ids, int mode);

    Single<IOwnersBundle> findBaseOwnersDataAsBundle(int accountId, Collection<Integer> ids, int mode);

    Single<IOwnersBundle> findBaseOwnersDataAsBundle(int accountId, Collection<Integer> ids, int mode, Collection<? extends Owner> alreadyExists);

    Single<Owner> getBaseOwnerInfo(int accountId, int ownerId, int mode);

    Single<Pair<User, UserDetails>> getFullUserInfo(int accountId, int userId, int mode);

    Single<Pair<Community, CommunityDetails>> getFullCommunityInfo(int accountId, int comminityId, int mode);

    Completable cacheActualOwnersData(int accountId, Collection<Integer> ids);

    Single<List<Owner>> getCommunitiesWhereAdmin(int accountId, boolean admin, boolean editor, boolean moderator);

    Single<List<User>> searchPeoples(int accountId, PeopleSearchCriteria criteria, int count, int offset);
}