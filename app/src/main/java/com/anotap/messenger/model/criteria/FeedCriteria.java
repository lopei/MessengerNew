package com.anotap.messenger.model.criteria;

import com.anotap.messenger.db.DatabaseIdRange;

/**
 * Created by ruslan.kolbasa on 08-Jun-16.
 * phoenix
 */

public class FeedCriteria extends Criteria {

    private final int accountId;

    private DatabaseIdRange range;

    public FeedCriteria(int accountId) {
        this.accountId = accountId;
    }

    public DatabaseIdRange getRange() {
        return range;
    }

    public FeedCriteria setRange(DatabaseIdRange range) {
        this.range = range;
        return this;
    }

    public int getAccountId() {
        return accountId;
    }
}
