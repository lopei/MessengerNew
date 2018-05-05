package com.anotap.messenger.domain;

import java.util.Collection;
import java.util.List;

import com.anotap.messenger.model.Account;
import com.anotap.messenger.model.BannedPart;
import com.anotap.messenger.model.User;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by admin on 09.07.2017.
 * phoenix
 */
public interface IAccountsInteractor {
    Single<BannedPart> getBanned(int accountId, int count, int offset);
    Completable banUsers(int accountId, Collection<User> users);
    Completable unbanUser(int accountId, int userId);

    Completable changeStatus(int accountId, String status);

    Single<List<Account>> getAll();
}