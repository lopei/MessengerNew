package com.anotap.messenger.mvp.view;

import java.util.List;

import com.anotap.messenger.model.DrawerCategory;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by Ruslan Kolbasa on 20.07.2017.
 * phoenix
 */
public interface IDrawerEditView extends IMvpView {
    void displayData(List<DrawerCategory> data);
    void goBackAndApplyChanges();
}