package com.anotap.messenger.mvp.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.List;

import com.anotap.messenger.R;
import com.anotap.messenger.api.model.VKApiCommunity;
import com.anotap.messenger.domain.IGroupSettingsInteractor;
import com.anotap.messenger.domain.InteractorFactory;
import com.anotap.messenger.model.ContactInfo;
import com.anotap.messenger.model.Manager;
import com.anotap.messenger.model.User;
import com.anotap.messenger.mvp.presenter.base.AccountDependencyPresenter;
import com.anotap.messenger.mvp.view.ICommunityManagerEditView;
import com.anotap.messenger.util.RxUtils;
import com.anotap.messenger.util.Utils;
import com.anotap.mvp.reflect.OnGuiCreated;

import static com.anotap.messenger.util.Objects.nonNull;

/**
 * Created by Ruslan Kolbasa on 21.06.2017.
 * phoenix
 */
public class CommunityManagerEditPresenter extends AccountDependencyPresenter<ICommunityManagerEditView> {

    private static final String TAG = CommunityManagerEditPresenter.class.getSimpleName();

    private final List<User> users;

    private final int groupId;

    private int currentUserIndex;
    private int adminLevel;
    private boolean showAsContact;

    private String position;
    private String email;
    private String phone;

    private boolean adding;

    private final IGroupSettingsInteractor interactor;

    private final boolean creator;

    public CommunityManagerEditPresenter(int accountId, int groupId, Manager manager, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);

        User user = manager.getUser();
        this.users = Collections.singletonList(user);
        this.groupId = groupId;
        this.creator = "creator".equalsIgnoreCase(manager.getRole());

        if (!creator) {
            this.adminLevel = convertRoleToAdminLevel(manager.getRole());
        }

        this.showAsContact = manager.isDisplayAsContact();
        this.interactor = InteractorFactory.createGroupSettingsInteractor();
        this.adding = false;

        if (nonNull(savedInstanceState)) {
            restoreState(savedInstanceState);
        } else {
            ContactInfo info = manager.getContactInfo();

            if (nonNull(info)) {
                position = info.getDescriprion();
                email = info.getEmail();
                phone = info.getPhone();
            }
        }
    }

    private boolean isCreator() {
        return creator;
    }

    public CommunityManagerEditPresenter(int accountId, int groupId, List<User> users, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);

        this.creator = false;
        this.users = users;
        this.groupId = groupId;
        this.adminLevel = VKApiCommunity.AdminLevel.MODERATOR;
        this.showAsContact = false;
        this.interactor = InteractorFactory.createGroupSettingsInteractor();
        this.adding = true;

        if (nonNull(savedInstanceState)) {
            restoreState(savedInstanceState);
        }
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        super.saveState(outState);
        outState.putInt("currentUserIndex", currentUserIndex);
        outState.putString("position", position);
        outState.putString("email", email);
        outState.putString("phone", phone);
    }

    private void restoreState(Bundle state) {
        currentUserIndex = state.getInt("currentUserIndex");
        position = state.getString("position");
        email = state.getString("email");
        phone = state.getString("phone");
    }

    private static int convertRoleToAdminLevel(String role) {
        if ("moderator".equalsIgnoreCase(role)) {
            return VKApiCommunity.AdminLevel.MODERATOR;
        } else if ("editor".equalsIgnoreCase(role)) {
            return VKApiCommunity.AdminLevel.EDITOR;
        } else if ("administrator".equalsIgnoreCase(role)) {
            return VKApiCommunity.AdminLevel.ADMIN;
        }

        throw new IllegalArgumentException("Invalid role");
    }

    private static String convertAdminLevelToRole(int adminLevel) {
        switch (adminLevel) {
            case VKApiCommunity.AdminLevel.MODERATOR:
                return "moderator";
            case VKApiCommunity.AdminLevel.EDITOR:
                return "editor";
            case VKApiCommunity.AdminLevel.ADMIN:
                return "administrator";
        }

        throw new IllegalArgumentException("Invalid adminLevel");
    }

    @NonNull
    private User getCurrentUser() {
        return users.get(currentUserIndex);
    }

    private boolean canDelete() {
        return !isCreator() && !adding;
    }

    @OnGuiCreated
    private void resolveRadioButtonsCheckState(){
        if(isGuiReady() && !isCreator()){
            switch (adminLevel) {
                case VKApiCommunity.AdminLevel.MODERATOR:
                    getView().checkModerator();
                    break;
                case VKApiCommunity.AdminLevel.EDITOR:
                    getView().checkEditor();
                    break;
                case VKApiCommunity.AdminLevel.ADMIN:
                    getView().checkAdmin();
                    break;
            }
        }
    }

    @OnGuiCreated
    private void resolveDeleteOptionVisiblity() {
        if (isGuiReady()) {
            getView().setDeleteOptionVisible(canDelete());
        }
    }

    @OnGuiCreated
    private void resolveRadioButtonsVisibility() {
        if (isGuiReady()) {
            getView().configRadioButtons(isCreator());
        }
    }

    private boolean savingNow;

    private void setSavingNow(boolean savingNow) {
        this.savingNow = savingNow;
        resolveProgressView();
    }

    @OnGuiCreated
    private void resolveProgressView() {
        if (isGuiReady()) {
            if (savingNow) {
                getView().displayProgressDialog(R.string.please_wait, R.string.saving, false);
            } else {
                getView().dismissProgressDialog();
            }
        }
    }

    private String getSelectedRole() {
        if (isCreator()) {
            return "creator";
        }

        return convertAdminLevelToRole(adminLevel);
    }

    public void fireButtonSaveClick() {
        final int accountId = super.getAccountId();
        final String role = getSelectedRole();
        final User user = getCurrentUser();

        setSavingNow(true);
        appendDisposable(interactor.editManager(accountId, groupId, user, role, showAsContact, position, email, phone)
                .compose(RxUtils.applyCompletableIOToMainSchedulers())
                .subscribe(this::onSavingComplete, throwable -> onSavingError(Utils.getCauseIfRuntime(throwable))));
    }

    public void fireDeleteClick() {
        if(isCreator()){
            return;
        }

        final int accountId = super.getAccountId();
        final User user = getCurrentUser();

        setSavingNow(true);
        appendDisposable(interactor.editManager(accountId, groupId, user, null, false, null, null, null)
                .compose(RxUtils.applyCompletableIOToMainSchedulers())
                .subscribe(this::onSavingComplete, throwable -> onSavingError(Utils.getCauseIfRuntime(throwable))));
    }

    private void onSavingComplete() {
        setSavingNow(false);
        safeShowToast(getView(), R.string.success, false);

        if (currentUserIndex == users.size() - 1) {
            callView(ICommunityManagerEditView::goBack);
        } else {
            // switch to next user
            currentUserIndex++;

            resolveUserInfoViews();

            adminLevel = VKApiCommunity.AdminLevel.MODERATOR;
            showAsContact = false;
            position = null;
            email = null;
            phone = null;

            resolveContactBlock();
            resolveRadioButtonsVisibility();
        }
    }

    @OnGuiCreated
    private void resolveContactBlock() {
        if (isGuiReady()) {
            getView().setShowAsContactCheched(showAsContact);
            getView().setContactInfoVisible(showAsContact);
            getView().displayPosition(position);
            getView().displayEmail(email);
            getView().displayPhone(phone);
        }
    }

    @OnGuiCreated
    private void resolveUserInfoViews() {
        if (isGuiReady()) {
            getView().displayUserInfo(getCurrentUser());
        }
    }

    private void onSavingError(Throwable throwable) {
        throwable.printStackTrace();
        setSavingNow(false);
        showError(getView(), throwable);
    }

    @Override
    protected String tag() {
        return TAG;
    }

    public void fireAvatarClick() {
        getView().showUserProfile(getAccountId(), getCurrentUser());
    }

    public void fireModeratorChecked() {
        this.adminLevel = VKApiCommunity.AdminLevel.MODERATOR;
    }

    public void fireEditorChecked() {
        this.adminLevel = VKApiCommunity.AdminLevel.EDITOR;
    }

    public void fireAdminChecked() {
        this.adminLevel = VKApiCommunity.AdminLevel.ADMIN;
    }

    public void fireShowAsContactChecked(boolean checked) {
        if (checked != showAsContact) {
            this.showAsContact = checked;
            getView().setContactInfoVisible(checked);
        }
    }

    public void firePositionEdit(CharSequence s) {
        this.position = s.toString();
    }

    public void fireEmailEdit(CharSequence s) {
        this.email = s.toString();
    }

    public void firePhoneEdit(CharSequence s) {
        this.phone = s.toString();
    }
}