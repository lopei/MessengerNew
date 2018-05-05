package com.anotap.messenger.domain.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Constants;
import com.anotap.messenger.api.interfaces.INetworker;
import com.anotap.messenger.api.model.GroupSettingsDto;
import com.anotap.messenger.api.model.Items;
import com.anotap.messenger.api.model.VKApiCommunity;
import com.anotap.messenger.api.model.VKApiUser;
import com.anotap.messenger.db.interfaces.IOwnersStore;
import com.anotap.messenger.db.model.BanAction;
import com.anotap.messenger.domain.IGroupSettingsInteractor;
import com.anotap.messenger.domain.IOwnersInteractor;
import com.anotap.messenger.domain.mappers.Dto2Model;
import com.anotap.messenger.exception.NotFoundException;
import com.anotap.messenger.fragment.search.nextfrom.IntNextFrom;
import com.anotap.messenger.model.Banned;
import com.anotap.messenger.model.ContactInfo;
import com.anotap.messenger.model.Day;
import com.anotap.messenger.model.GroupSettings;
import com.anotap.messenger.model.IdOption;
import com.anotap.messenger.model.Manager;
import com.anotap.messenger.model.User;
import com.anotap.messenger.util.Pair;
import com.anotap.messenger.util.Utils;
import com.anotap.messenger.util.VKOwnIds;
import io.reactivex.Completable;
import io.reactivex.Single;

import static com.anotap.messenger.util.Objects.nonNull;
import static com.anotap.messenger.util.Utils.findById;
import static com.anotap.messenger.util.Utils.isEmpty;
import static com.anotap.messenger.util.Utils.listEmptyIfNull;

/**
 * Created by Ruslan Kolbasa on 15.06.2017.
 * phoenix
 */
public class GroupSettingsInteractor implements IGroupSettingsInteractor {

    private final INetworker networker;

    private final IOwnersStore repository;

    private final IOwnersInteractor ownersInteractor;

    public GroupSettingsInteractor(INetworker networker, IOwnersStore repository) {
        this.networker = networker;
        this.repository = repository;
        this.ownersInteractor = new OwnersInteractor(networker, repository);
    }

    @Override
    public Single<GroupSettings> getGroupSettings(int accountId, int groupId) {
        return networker.vkDefault(accountId)
                .groups()
                .getSettings(groupId)
                .flatMap(dto -> Single.just(createFromDto(dto)));
    }

    @Override
    public Completable banUser(int accountId, int groupId, int userId, Long endDateUnixtime, int reason, String comment, boolean showCommentToUser) {
        return networker.vkDefault(accountId)
                .groups()
                .banUser(groupId, userId, endDateUnixtime, reason, comment, showCommentToUser)
                .andThen(repository.fireBanAction(new BanAction(groupId, userId, true)));
    }

    @Override
    public Completable editManager(int accountId, int groupId, final User user, String role, boolean asContact, String position, String email, String phone) {
        final String targetRole = "creator".equalsIgnoreCase(role) ? "administrator" : role;

        return networker.vkDefault(accountId)
                .groups()
                .editManager(groupId, user.getId(), targetRole, asContact, position, email, phone)
                .andThen(Single
                        .fromCallable(() -> {
                            ContactInfo info = new ContactInfo(user.getId())
                                    .setDescriprion(position)
                                    .setPhone(phone)
                                    .setEmail(email);

                            return new Manager(user, role)
                                    .setContactInfo(info)
                                    .setDisplayAsContact(asContact);
                        })
                        .flatMapCompletable(manager -> repository.fireManagementChangeAction(Pair.create(groupId, manager))));
    }

    @Override
    public Completable unbanUser(int accountId, int groupId, int userId) {
        return networker.vkDefault(accountId)
                .groups()
                .unbanUser(groupId, userId)
                .andThen(repository.fireBanAction(new BanAction(groupId, userId, false)));
    }

    @Override
    public Single<Pair<List<Banned>, IntNextFrom>> getBanned(int accountId, int groupId, IntNextFrom startFrom, int count) {
        final IntNextFrom nextFrom = new IntNextFrom(startFrom.getOffset() + count);

        return networker.vkDefault(accountId)
                .groups()
                .getBanned(groupId, startFrom.getOffset(), count, Constants.MAIN_OWNER_FIELDS, null)
                .map(Items::getItems)
                .flatMap(items -> {
                    VKOwnIds ids = new VKOwnIds();

                    for (VKApiUser u : items) {
                        ids.append(u.ban_info.admin_id);
                    }

                    //"ban_info": {
                    //    "admin_id": null,
                    //            "date": 0,
                    //            "reason": null,
                    //            "comment": "",
                    //            "comment_visible": 0,
                    //            "end_date": 0
                    //}
                    // UPD. В вебе написано Was removed from the blacklist.

                    return ownersInteractor.findBaseOwnersDataAsBundle(accountId, ids.getAll(), IOwnersInteractor.MODE_ANY)
                            .map(bundle -> {
                                List<Banned> infos = new ArrayList<>(items.size());

                                for (VKApiUser u : items) {

                                    User admin;
                                    VKApiUser.BanInfo banInfo = u.ban_info;

                                    if (banInfo.admin_id != 0) {
                                        admin = (User) bundle.getById(u.ban_info.admin_id);
                                    } else {
                                        // ignore this
                                        continue;
                                    }

                                    Banned.Info info = new Banned.Info()
                                            .setComment(banInfo.comment)
                                            .setCommentVisible(banInfo.comment_visible)
                                            .setDate(banInfo.date)
                                            .setEndDate(banInfo.end_date)
                                            .setReason(banInfo.reason);

                                    infos.add(new Banned(Dto2Model.transformUser(u), admin, info));
                                }

                                return Pair.create(infos, nextFrom);
                            });
                });
    }

    @Override
    public Single<List<Manager>> getManagers(int accountId, int groupId) {
        return networker.vkDefault(accountId)
                .groups()
                .getMembers(String.valueOf(groupId), null, null, null, Constants.MAIN_OWNER_FIELDS, "managers")
                .flatMap(items -> networker.vkDefault(accountId)
                        .groups()
                        .getById(Collections.singleton(groupId), null, null, "contacts")
                        .map(communities -> {
                            if(communities.isEmpty()){
                                throw new NotFoundException("Group with id " + groupId + " not found");
                            }

                            return listEmptyIfNull(communities.get(0).contacts);
                        })
                        .map(contacts -> {
                            List<VKApiUser> users = listEmptyIfNull(items.getItems());

                            List<Manager> managers = new ArrayList<>(users.size());
                            for (VKApiUser user : users) {
                                VKApiCommunity.Contact contact = findById(contacts, user.id);

                                Manager manager = new Manager(Dto2Model.transformUser(user), user.role);
                                if (nonNull(contact)) {
                                    manager.setDisplayAsContact(true).setContactInfo(transform(contact));
                                }

                                managers.add(manager);
                            }

                            return managers;
                        }));
    }

    private static ContactInfo transform(VKApiCommunity.Contact contact) {
        return new ContactInfo(contact.user_id)
                .setDescriprion(contact.desc)
                .setEmail(contact.email)
                .setPhone(contact.phone);
    }

    private IdOption createFromDto(GroupSettingsDto.PublicCategory category) {
        return new IdOption(category.id, category.name, createFromDtos(category.subtypes_list));
    }

    private List<IdOption> createFromDtos(List<GroupSettingsDto.PublicCategory> dtos) {
        if (isEmpty(dtos)) {
            return Collections.emptyList();
        }

        List<IdOption> categories = new ArrayList<>(dtos.size());
        for (GroupSettingsDto.PublicCategory dto : dtos) {
            categories.add(createFromDto(dto));
        }

        return categories;
    }

    private static Day parseDateCreated(String text) {
        if (isEmpty(text)) {
            return null;
        }

        String[] parts = text.split("\\.");

        return new Day(
                parseInt(parts, 0, 0),
                parseInt(parts, 1, 0),
                parseInt(parts, 2, 0)
        );
    }

    private static int parseInt(String[] parts, int index, int ifNotExists) {
        if (parts.length <= index) {
            return ifNotExists;
        }

        return Integer.parseInt(parts[index]);
    }

    private GroupSettings createFromDto(GroupSettingsDto dto) {
        List<IdOption> categories = createFromDtos(dto.public_category_list);

        IdOption category = null;
        IdOption subcategory = null;

        if (nonNull(dto.public_category)) {
            category = findById(categories, Integer.parseInt(dto.public_category));

            if (nonNull(dto.public_subcategory) && nonNull(category)) {
                subcategory = findById(category.getChilds(), Integer.parseInt(dto.public_subcategory));
            }
        }

        GroupSettings settings = new GroupSettings()
                .setTitle(dto.title)
                .setDescription(dto.description)
                .setAddress(dto.address)
                .setAvailableCategories(categories)
                .setCategory(category)
                .setSubcategory(subcategory)
                .setWebsite(dto.website)
                .setDateCreated(parseDateCreated(dto.public_date))
                .setFeedbackCommentsEnabled(dto.wall == 1)
                .setObsceneFilterEnabled(dto.obscene_filter)
                .setObsceneStopwordsEnabled(dto.obscene_stopwords)
                .setObsceneWords(Utils.join(dto.obscene_words, ",", orig -> orig));

        return settings;
    }
}