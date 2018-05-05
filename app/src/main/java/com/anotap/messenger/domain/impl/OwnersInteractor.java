package com.anotap.messenger.domain.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.anotap.messenger.api.interfaces.INetworker;
import com.anotap.messenger.api.model.Items;
import com.anotap.messenger.api.model.VKApiUser;
import com.anotap.messenger.db.column.GroupColumns;
import com.anotap.messenger.db.column.UserColumns;
import com.anotap.messenger.db.interfaces.IOwnersStore;
import com.anotap.messenger.db.model.entity.CareerEntity;
import com.anotap.messenger.db.model.entity.CommunityEntity;
import com.anotap.messenger.db.model.entity.UserDetailsEntity;
import com.anotap.messenger.db.model.entity.UserEntity;
import com.anotap.messenger.domain.IOwnersInteractor;
import com.anotap.messenger.domain.mappers.Dto2Entity;
import com.anotap.messenger.domain.mappers.Dto2Model;
import com.anotap.messenger.domain.mappers.Entity2Model;
import com.anotap.messenger.exception.NotFoundException;
import com.anotap.messenger.fragment.search.criteria.PeopleSearchCriteria;
import com.anotap.messenger.fragment.search.options.SpinnerOption;
import com.anotap.messenger.model.Community;
import com.anotap.messenger.model.CommunityDetails;
import com.anotap.messenger.model.IOwnersBundle;
import com.anotap.messenger.model.Owner;
import com.anotap.messenger.model.SparseArrayOwnersBundle;
import com.anotap.messenger.model.User;
import com.anotap.messenger.model.UserDetails;
import com.anotap.messenger.util.Optional;
import com.anotap.messenger.util.Pair;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;

import static com.anotap.messenger.api.model.VKApiCommunity.ACTIVITY;
import static com.anotap.messenger.api.model.VKApiCommunity.BAN_INFO;
import static com.anotap.messenger.api.model.VKApiCommunity.BLACKLISTED;
import static com.anotap.messenger.api.model.VKApiCommunity.CAN_CTARE_TOPIC;
import static com.anotap.messenger.api.model.VKApiCommunity.CAN_POST;
import static com.anotap.messenger.api.model.VKApiCommunity.CAN_SEE_ALL_POSTS;
import static com.anotap.messenger.api.model.VKApiCommunity.CAN_UPLOAD_DOC;
import static com.anotap.messenger.api.model.VKApiCommunity.CAN_UPLOAD_VIDEO;
import static com.anotap.messenger.api.model.VKApiCommunity.CITY;
import static com.anotap.messenger.api.model.VKApiCommunity.CONTACTS;
import static com.anotap.messenger.api.model.VKApiCommunity.COUNTERS;
import static com.anotap.messenger.api.model.VKApiCommunity.COUNTRY;
import static com.anotap.messenger.api.model.VKApiCommunity.DESCRIPTION;
import static com.anotap.messenger.api.model.VKApiCommunity.FINISH_DATE;
import static com.anotap.messenger.api.model.VKApiCommunity.FIXED_POST;
import static com.anotap.messenger.api.model.VKApiCommunity.IS_FAVORITE;
import static com.anotap.messenger.api.model.VKApiCommunity.LINKS;
import static com.anotap.messenger.api.model.VKApiCommunity.MAIN_ALBUM_ID;
import static com.anotap.messenger.api.model.VKApiCommunity.MEMBERS_COUNT;
import static com.anotap.messenger.api.model.VKApiCommunity.PLACE;
import static com.anotap.messenger.api.model.VKApiCommunity.SITE;
import static com.anotap.messenger.api.model.VKApiCommunity.START_DATE;
import static com.anotap.messenger.api.model.VKApiCommunity.STATUS;
import static com.anotap.messenger.api.model.VKApiCommunity.VERIFIED;
import static com.anotap.messenger.api.model.VKApiCommunity.WIKI_PAGE;
import static com.anotap.messenger.util.Objects.nonNull;
import static com.anotap.messenger.util.Utils.join;
import static com.anotap.messenger.util.Utils.listEmptyIfNull;
import static com.anotap.messenger.util.Utils.nonEmpty;
import static com.anotap.messenger.util.Utils.stringJoin;
import static java.util.Collections.singletonList;

/**
 * Created by ruslan.kolbasa on 01.02.2017.
 * phoenix
 */
public class OwnersInteractor implements IOwnersInteractor {

    private static final String FIELDS_GROUPS_ALL = stringJoin(",", IS_FAVORITE,
            MAIN_ALBUM_ID, CAN_UPLOAD_DOC, CAN_CTARE_TOPIC, CAN_UPLOAD_VIDEO, BAN_INFO,
            CITY, COUNTRY, PLACE, DESCRIPTION, WIKI_PAGE, MEMBERS_COUNT, COUNTERS, START_DATE,
            FINISH_DATE, CAN_POST, CAN_SEE_ALL_POSTS, STATUS, CONTACTS, LINKS, FIXED_POST,
            VERIFIED, BLACKLISTED, SITE, ACTIVITY, "member_status", "can_message", "cover");

    private final INetworker networker;
    private final IOwnersStore cache;

    public OwnersInteractor(INetworker networker, IOwnersStore ownersRepository) {
        this.networker = networker;
        this.cache = ownersRepository;
    }

    private Single<Optional<UserDetails>> getCachedDetails(int accountId, int userId) {
        return cache.getUserDetails(accountId, userId)
                .flatMap(optional -> {
                    if (optional.isEmpty()) {
                        return Single.just(Optional.<UserDetails>empty());
                    }

                    UserDetailsEntity entity = optional.get();
                    Set<Integer> requiredIds = new HashSet<>(1);

                    if (nonEmpty(entity.getCareers())) {
                        for (CareerEntity career : entity.getCareers()) {
                            if (career.getGroupId() != 0) {
                                requiredIds.add(-career.getGroupId());
                            }
                        }
                    }

                    if (nonEmpty(entity.getRelatives())) {
                        for (UserDetailsEntity.RelativeEntity e : entity.getRelatives()) {
                            if (e.getId() > 0) {
                                requiredIds.add(e.getId());
                            }
                        }
                    }

                    if (entity.getRelationPartnerId() != 0) {
                        requiredIds.add(entity.getRelationPartnerId());
                    }

                    return findBaseOwnersDataAsBundle(accountId, requiredIds, MODE_ANY)
                            .map(bundle -> Optional.wrap(Entity2Model.buildUserDetailsFromDbo(entity, bundle)));
                });
    }

    private Single<Pair<User, UserDetails>> getCachedFullData(int accountId, int userId) {
        return cache.findUserDboById(accountId, userId)
                .zipWith(getCachedDetails(accountId, userId), (userEntityOptional, userDetailsOptional) -> {
                    User user = userEntityOptional.isEmpty() ? null : Entity2Model.buildUserFromDbo(userEntityOptional.get());
                    return Pair.create(user, userDetailsOptional.get());
                });
    }

    @Override
    public Single<Pair<User, UserDetails>> getFullUserInfo(int accountId, int userId, int mode) {
        switch (mode) {
            case MODE_CACHE:
                return getCachedFullData(accountId, userId);
            case MODE_NET:
                return networker.vkDefault(accountId)
                        .users()
                        .getUserWallInfo(userId, VKApiUser.ALL_FIELDS, null)
                        .flatMap(user -> {
                            UserEntity userEntity = Dto2Entity.buildUserDbo(user);
                            UserDetailsEntity detailsEntity = Dto2Entity.buildUserDetailsDbo(user);
                            return cache.storeUserDbos(accountId, singletonList(userEntity))
                                    .andThen(cache.storeUserDetails(accountId, userId, detailsEntity))
                                    .andThen(getCachedFullData(accountId, userId));
                        });
        }

        throw new UnsupportedOperationException("Unsupported mode: " + mode);
    }

    @Override
    public Single<Pair<Community, CommunityDetails>> getFullCommunityInfo(int accountId, int comminityId, int mode) {
        switch (mode) {
            case MODE_CACHE:
            case MODE_NET:
                return networker.vkDefault(accountId)
                        .groups()
                        .getWallInfo(String.valueOf(comminityId), FIELDS_GROUPS_ALL)
                        .flatMap(dto -> {
                            Community community = Dto2Model.transformCommunity(dto);
                            CommunityDetails details = Dto2Model.transformCommunityDetails(dto);
                            return Single.just(Pair.create(community, details));
                        });
        }

        return Single.error(new Exception("Not yet implemented"));
    }

    @Override
    public Completable cacheActualOwnersData(int accountId, Collection<Integer> ids) {
        Completable completable = Completable.complete();
        DividedIds dividedIds = new DividedIds(ids);

        if (nonEmpty(dividedIds.gids)) {
            completable = completable.andThen(networker.vkDefault(accountId)
                    .groups()
                    .getById(dividedIds.gids, null, null, GroupColumns.API_FIELDS)
                    .flatMapCompletable(communities -> cache.storeCommunityDbos(accountId, Dto2Entity.buildCommunityDbos(communities))));
        }

        if (nonEmpty(dividedIds.uids)) {
            completable = completable.andThen(networker.vkDefault(accountId)
                    .users()
                    .get(dividedIds.uids, null, UserColumns.API_FIELDS, null)
                    .flatMapCompletable(users -> cache.storeUserDbos(accountId, Dto2Entity.buildUserDbos(users))));
        }

        return completable;
    }

    @Override
    public Single<List<Owner>> getCommunitiesWhereAdmin(int accountId, boolean admin, boolean editor, boolean moderator) {
        List<String> roles = new ArrayList<>();

        if (admin) {
            roles.add("admin");
        }

        if (editor) {
            roles.add("editor");
        }

        if (moderator) {
            roles.add("moderator");
        }

        return networker.vkDefault(accountId)
                .groups()
                .get(accountId, true, join(roles, ",", orig -> orig), GroupColumns.API_FIELDS, null, 1000)
                .map(Items::getItems)
                .map(groups -> {
                    List<Owner> owners = new ArrayList<>(groups.size());
                    owners.addAll(Dto2Model.transformCommunities(groups));
                    return owners;
                });
    }

    @Override
    public Single<List<User>> searchPeoples(int accountId, PeopleSearchCriteria criteria, int count, int offset) {
        String q = criteria.getQuery();

        SpinnerOption sortOption = criteria.findOptionByKey(PeopleSearchCriteria.KEY_SORT);
        Integer sort = sortOption == null || sortOption.value == null ? null : sortOption.value.id;

        String fields = UserColumns.API_FIELDS;
        Integer city = criteria.extractDatabaseEntryValueId(PeopleSearchCriteria.KEY_CITY);
        Integer country = criteria.extractDatabaseEntryValueId(PeopleSearchCriteria.KEY_COUNTRY);
        String hometown = criteria.extractTextValueFromOption(PeopleSearchCriteria.KEY_HOMETOWN);
        Integer universityCountry = criteria.extractDatabaseEntryValueId(PeopleSearchCriteria.KEY_UNIVERSITY_COUNTRY);
        Integer university = criteria.extractDatabaseEntryValueId(PeopleSearchCriteria.KEY_UNIVERSITY);
        Integer universityYear = criteria.extractNumberValueFromOption(PeopleSearchCriteria.KEY_UNIVERSITY_YEAR);
        Integer universityFaculty = criteria.extractDatabaseEntryValueId(PeopleSearchCriteria.KEY_UNIVERSITY_FACULTY);
        Integer universityChair = criteria.extractDatabaseEntryValueId(PeopleSearchCriteria.KEY_UNIVERSITY_CHAIR);

        SpinnerOption sexOption = criteria.findOptionByKey(PeopleSearchCriteria.KEY_SEX);
        Integer sex = sexOption == null || sexOption.value == null ? null : sexOption.value.id;

        SpinnerOption statusOption = criteria.findOptionByKey(PeopleSearchCriteria.KEY_RELATIONSHIP);
        Integer status = statusOption == null || statusOption.value == null ? null : statusOption.value.id;

        Integer ageFrom = criteria.extractNumberValueFromOption(PeopleSearchCriteria.KEY_AGE_FROM);
        Integer ageTo = criteria.extractNumberValueFromOption(PeopleSearchCriteria.KEY_AGE_TO);
        Integer birthDay = criteria.extractNumberValueFromOption(PeopleSearchCriteria.KEY_BIRTHDAY_DAY);

        SpinnerOption birthMonthOption = criteria.findOptionByKey(PeopleSearchCriteria.KEY_BIRTHDAY_MONTH);
        Integer birthMonth = birthMonthOption == null || birthMonthOption.value == null ? null : birthMonthOption.value.id;

        Integer birthYear = criteria.extractNumberValueFromOption(PeopleSearchCriteria.KEY_BIRTHDAY_YEAR);
        Boolean online = criteria.extractBoleanValueFromOption(PeopleSearchCriteria.KEY_ONLINE_ONLY);
        Boolean hasPhoto = criteria.extractBoleanValueFromOption(PeopleSearchCriteria.KEY_WITH_PHOTO_ONLY);
        Integer schoolCountry = criteria.extractDatabaseEntryValueId(PeopleSearchCriteria.KEY_SCHOOL_COUNTRY);
        Integer schoolCity = criteria.extractDatabaseEntryValueId(PeopleSearchCriteria.KEY_SCHOOL_CITY);
        Integer schoolClass = criteria.extractDatabaseEntryValueId(PeopleSearchCriteria.KEY_SCHOOL_CLASS);
        Integer school = criteria.extractDatabaseEntryValueId(PeopleSearchCriteria.KEY_SCHOOL);
        Integer schoolYear = criteria.extractNumberValueFromOption(PeopleSearchCriteria.KEY_SCHOOL_YEAR);
        String religion = criteria.extractTextValueFromOption(PeopleSearchCriteria.KEY_RELIGION);
        String interests = criteria.extractTextValueFromOption(PeopleSearchCriteria.KEY_INTERESTS);
        String company = criteria.extractTextValueFromOption(PeopleSearchCriteria.KEY_COMPANY);
        String position = criteria.extractTextValueFromOption(PeopleSearchCriteria.KEY_POSITION);

        Integer groupId = criteria.getGroupId();

        SpinnerOption fromListOption = criteria.findOptionByKey(PeopleSearchCriteria.KEY_FROM_LIST);
        Integer fromList = fromListOption == null || fromListOption.value == null ? null : fromListOption.value.id;

        String targetFromList = null;
        if (fromList != null) {
            switch (fromList) {
                case PeopleSearchCriteria.FromList.FRIENDS:
                    targetFromList = "friends";
                    break;
                case PeopleSearchCriteria.FromList.SUBSCRIPTIONS:
                    targetFromList = "subscriptions";
                    break;
            }
        }

        return networker
                .vkDefault(accountId)
                .users()
                .search(q, sort, offset, count, fields, city, country, hometown, universityCountry,
                        university, universityYear, universityFaculty, universityChair, sex, status,
                        ageFrom, ageTo, birthDay, birthMonth, birthYear, online, hasPhoto, schoolCountry,
                        schoolCity, schoolClass, school, schoolYear, religion, interests, company,
                        position, groupId, targetFromList)
                .map(items -> {
                    List<VKApiUser> dtos = listEmptyIfNull(items.getItems());
                    return Dto2Model.transformUsers(dtos);
                });
    }

    @Override
    public Single<List<Owner>> findBaseOwnersDataAsList(int accountId, Collection<Integer> ids, int mode) {
        if (ids.isEmpty()) {
            return Single.just(Collections.emptyList());
        }

        final DividedIds dividedIds = new DividedIds(ids);

        return getUsers(accountId, dividedIds.uids, mode)
                .zipWith(getCommunities(accountId, dividedIds.gids, mode), (users, communities) -> {
                    List<Owner> owners = new ArrayList<>(users.size() + communities.size());
                    owners.addAll(users);
                    owners.addAll(communities);
                    return owners;
                });
    }

    private static final class DividedIds {

        final List<Integer> uids;
        final List<Integer> gids;

        DividedIds(Collection<Integer> ids) {
            this.uids = new LinkedList<>();
            this.gids = new LinkedList<>();

            for (int id : ids) {
                if (id > 0) {
                    uids.add(id);
                } else if (id < 0) {
                    gids.add(-id);
                } else {
                    throw new IllegalArgumentException("Zero owner id!!!");
                }
            }
        }
    }

    private static final BiFunction<List<User>, List<Community>, IOwnersBundle> TO_BUNDLE_FUNCTION = (users, communities) -> {
        SparseArrayOwnersBundle bundle = new SparseArrayOwnersBundle(users.size() + communities.size());
        bundle.putAll(users);
        bundle.putAll(communities);
        return bundle;
    };

    @Override
    public Single<IOwnersBundle> findBaseOwnersDataAsBundle(int accountId, Collection<Integer> ids, int mode) {
        if (ids.isEmpty()) {
            return Single.just(new SparseArrayOwnersBundle(0));
        }

        final DividedIds dividedIds = new DividedIds(ids);

        return getUsers(accountId, dividedIds.uids, mode)
                .zipWith(getCommunities(accountId, dividedIds.gids, mode), TO_BUNDLE_FUNCTION);
    }

    @Override
    public Single<IOwnersBundle> findBaseOwnersDataAsBundle(int accountId, Collection<Integer> ids, int mode, Collection<? extends Owner> alreadyExists) {
        if (ids.isEmpty()) {
            return Single.just(new SparseArrayOwnersBundle(0));
        }

        final IOwnersBundle b = new SparseArrayOwnersBundle(ids.size());
        if (nonNull(alreadyExists)) {
            b.putAll(alreadyExists);
        }

        return Single.just(b)
                .flatMap(bundle -> {
                    final Collection<Integer> missing = bundle.getMissing(ids);
                    if (missing.isEmpty()) {
                        return Single.just(bundle);
                    }

                    return findBaseOwnersDataAsList(accountId, missing, mode)
                            .map(owners -> {
                                bundle.putAll(owners);
                                return bundle;
                            });
                });
    }

    @Override
    public Single<Owner> getBaseOwnerInfo(int accountId, int ownerId, int mode) {
        if (ownerId == 0) {
            return Single.error(new IllegalArgumentException("Zero owner id!!!"));
        }

        if (ownerId > 0) {
            return getUsers(accountId, singletonList(ownerId), mode)
                    .map(users -> {
                        if (users.size() == 0) {
                            throw new NotFoundException();
                        }
                        return users.get(0);
                    });
        } else {
            return getCommunities(accountId, singletonList(-ownerId), mode)
                    .map(communities -> {
                        if (communities.size() == 0) {
                            throw new NotFoundException();
                        }
                        return communities.get(0);
                    });
        }
    }

    private Single<List<Community>> getCommunities(int accountId, List<Integer> gids, int mode) {
        if (gids.isEmpty()) {
            return Single.just(Collections.emptyList());
        }

        switch (mode) {
            case MODE_CACHE:
                return cache.findCommunityDbosByIds(accountId, gids)
                        .map(Entity2Model::buildCommunitiesFromDbos);
            case MODE_ANY:
                return cache.findCommunityDbosByIds(accountId, gids)
                        .flatMap(dbos -> {
                            if (dbos.size() == gids.size()) {
                                return Single.just(Entity2Model.buildCommunitiesFromDbos(dbos));
                            }

                            return getActualComminitiesAndStore(accountId, gids);
                        });
            case MODE_NET:
                return getActualComminitiesAndStore(accountId, gids);
        }

        throw new IllegalArgumentException("Invalid mode: " + mode);
    }

    private Single<List<User>> getActualUsersAndStore(int accountId, Collection<Integer> uids) {
        return networker.vkDefault(accountId)
                .users()
                .get(uids, null, UserColumns.API_FIELDS, null)
                .flatMap(dtos -> cache.storeUserDbos(accountId, Dto2Entity.buildUserDbos(dtos))
                        .andThen(Single.just(Dto2Model.transformUsers(dtos))));
    }

    private Single<List<Community>> getActualComminitiesAndStore(int accountId, List<Integer> gids) {
        return networker.vkDefault(accountId)
                .groups()
                .getById(gids, null, null, GroupColumns.API_FIELDS)
                .flatMap(dtos -> {
                    List<CommunityEntity> communityEntities = Dto2Entity.buildCommunityDbos(dtos);
                    List<Community> communities = Dto2Model.transformCommunities(dtos);
                    return cache.storeCommunityDbos(accountId, communityEntities)
                            .andThen(Single.just(communities));
                });
    }

    private Single<List<User>> getUsers(int accountId, List<Integer> uids, int mode) {
        if (uids.isEmpty()) {
            return Single.just(Collections.emptyList());
        }

        switch (mode) {
            case MODE_CACHE:
                return cache.findUserDbosByIds(accountId, uids)
                        .map(Entity2Model::buildUsersFromDbo);
            case MODE_ANY:
                return cache.findUserDbosByIds(accountId, uids)
                        .flatMap(dbos -> {
                            if (dbos.size() == uids.size()) {
                                return Single.just(Entity2Model.buildUsersFromDbo(dbos));
                            }

                            return getActualUsersAndStore(accountId, uids);
                        });
            case MODE_NET:
                return getActualUsersAndStore(accountId, uids);
        }

        throw new IllegalArgumentException("Invalid mode: " + mode);
    }
}