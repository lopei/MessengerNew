package com.anotap.messenger.domain;

import com.anotap.messenger.model.SectionCounters;
import io.reactivex.Observable;

/**
 * Created by Ruslan Kolbasa on 30.06.2017.
 * phoenix
 */
public interface ICountersInteractor {
    Observable<SectionCounters> getCounters(int accountId);
}