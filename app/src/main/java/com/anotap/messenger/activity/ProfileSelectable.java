package com.anotap.messenger.activity;

import com.anotap.messenger.model.SelectProfileCriteria;
import com.anotap.messenger.model.User;

public interface ProfileSelectable {

    void select(User user);

    SelectProfileCriteria getAcceptableCriteria();
}