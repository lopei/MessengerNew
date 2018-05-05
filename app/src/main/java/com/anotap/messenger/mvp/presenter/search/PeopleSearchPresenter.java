package com.anotap.messenger.mvp.presenter.search;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import com.anotap.messenger.domain.IOwnersInteractor;
import com.anotap.messenger.domain.InteractorFactory;
import com.anotap.messenger.fragment.search.criteria.PeopleSearchCriteria;
import com.anotap.messenger.fragment.search.nextfrom.IntNextFrom;
import com.anotap.messenger.model.User;
import com.anotap.messenger.mvp.view.search.IPeopleSearchView;
import com.anotap.messenger.util.Pair;
import io.reactivex.Single;

/**
 * Created by admin on 03.10.2017.
 * phoenix
 */
public class PeopleSearchPresenter extends AbsSearchPresenter<IPeopleSearchView, PeopleSearchCriteria, User, IntNextFrom> {

    private final IOwnersInteractor ownersInteractor;

    public PeopleSearchPresenter(int accountId, @Nullable PeopleSearchCriteria criteria, @Nullable Bundle savedInstanceState) {
        super(accountId, criteria, savedInstanceState);
        this.ownersInteractor = InteractorFactory.createOwnerInteractor();
    }

    @Override
    IntNextFrom getInitialNextFrom() {
        return new IntNextFrom(0);
    }

    @Override
    boolean isAtLast(IntNextFrom startFrom) {
        return startFrom.getOffset() == 0;
    }

    @Override
    Single<Pair<List<User>, IntNextFrom>> doSearch(int accountId, PeopleSearchCriteria criteria, IntNextFrom startFrom) {
        final int offset = startFrom.getOffset();
        final int nextOffset = offset + 50;

        return ownersInteractor.searchPeoples(accountId, criteria, 50, offset)
                .map(users -> Pair.create(users, new IntNextFrom(nextOffset)));
    }

    @Override
    PeopleSearchCriteria instantiateEmptyCriteria() {
        return new PeopleSearchCriteria("");
    }

    @Override
    boolean canSearch(PeopleSearchCriteria criteria) {
        return true;
    }

    @Override
    protected String tag() {
        return PeopleSearchPresenter.class.getSimpleName();
    }

    public void fireUserClick(User user) {
        getView().openUserWall(getAccountId(), user);
    }
}