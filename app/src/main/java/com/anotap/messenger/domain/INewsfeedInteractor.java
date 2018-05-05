package com.anotap.messenger.domain;

import java.util.List;

import com.anotap.messenger.model.NewsfeedComment;
import com.anotap.messenger.util.Pair;
import io.reactivex.Single;

/**
 * Created by admin on 08.05.2017.
 * phoenix
 */
public interface INewsfeedInteractor {

    Single<Pair<List<NewsfeedComment>, String>> getNewsfeedComments(int accountId, int count, String startFrom, String filter);

}
