package com.anotap.messenger.api.impl;

import android.annotation.SuppressLint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.anotap.messenger.api.IServiceProvider;
import com.anotap.messenger.api.IVkRetrofitProvider;
import com.anotap.messenger.api.RetrofitWrapper;
import com.anotap.messenger.api.TokenType;
import com.anotap.messenger.api.interfaces.IAccountApi;
import com.anotap.messenger.api.interfaces.IAccountApis;
import com.anotap.messenger.api.interfaces.IAudioApi;
import com.anotap.messenger.api.interfaces.IBoardApi;
import com.anotap.messenger.api.interfaces.ICommentsApi;
import com.anotap.messenger.api.interfaces.IDatabaseApi;
import com.anotap.messenger.api.interfaces.IDocsApi;
import com.anotap.messenger.api.interfaces.IFaveApi;
import com.anotap.messenger.api.interfaces.IFriendsApi;
import com.anotap.messenger.api.interfaces.IGroupsApi;
import com.anotap.messenger.api.interfaces.ILikesApi;
import com.anotap.messenger.api.interfaces.IMessagesApi;
import com.anotap.messenger.api.interfaces.INewsfeedApi;
import com.anotap.messenger.api.interfaces.INotificationsApi;
import com.anotap.messenger.api.interfaces.IOtherApi;
import com.anotap.messenger.api.interfaces.IPagesApi;
import com.anotap.messenger.api.interfaces.IPhotosApi;
import com.anotap.messenger.api.interfaces.IPollsApi;
import com.anotap.messenger.api.interfaces.IStatusApi;
import com.anotap.messenger.api.interfaces.IStoreApi;
import com.anotap.messenger.api.interfaces.IUsersApi;
import com.anotap.messenger.api.interfaces.IUtilsApi;
import com.anotap.messenger.api.interfaces.IVideoApi;
import com.anotap.messenger.api.interfaces.IWallApi;
import io.reactivex.Single;

import static com.anotap.messenger.util.Utils.intValueIn;

/**
 * Created by ruslan.kolbasa on 29.12.2016.
 * phoenix
 */
class VkApies implements IAccountApis {

    @SuppressLint("UseSparseArrays")
    private static volatile Map<Integer, VkApies> APIS = new HashMap<>(1);

    private final IMessagesApi messagesApi;
    private final IPhotosApi photosApi;
    private final IFriendsApi friendsApi;
    private final IDocsApi docsApi;
    private final IWallApi wallApi;
    private final INewsfeedApi newsfeedApi;
    private final ICommentsApi commentsApi;
    private final INotificationsApi notificationsApi;
    private final IVideoApi videoApi;
    private final IBoardApi boardApi;
    private final IUsersApi usersApi;
    private final IGroupsApi groupsApi;
    private final IAccountApi accountApi;
    private final IDatabaseApi databaseApi;
    private final IAudioApi audioApi;
    private final IStatusApi statusApi;
    private final ILikesApi likesApi;
    private final IPagesApi pagesApi;
    private final IStoreApi storeApi;
    private final IFaveApi faveApi;
    private final IPollsApi pollsApi;
    private final IUtilsApi utilsApi;
    private final IOtherApi otherApi;

    private VkApies(int accountId, boolean useCustomToken, String customAccessToken, IVkRetrofitProvider provider) {
        IServiceProvider retrofitProvider = new IServiceProvider() {
            @Override
            public <T> Single<T> provideService(int accountId, Class<T> serviceClass, int... tokenTypes) {
                return provideRetrofit(accountId, tokenTypes).map(retrofit -> retrofit.create(serviceClass));
            }

            Single<RetrofitWrapper> provideRetrofit(int aid, int... tokenPolicy) {
                if (useCustomToken) {
                    return provider.provideCustomRetrofit(aid, customAccessToken);
                }

                boolean isCommunity = aid < 0;

                if (isCommunity) {
                    if (intValueIn(TokenType.COMMUNITY, tokenPolicy)) {
                        return provider.provideNormalRetrofit(aid);
                    } else if (intValueIn(TokenType.SERVICE, tokenPolicy)) {
                        return provider.provideServiceRetrofit();
                    } else {
                        throw new UnsupportedOperationException("Unsupported account_id: " + aid + " with token_policy: " + Arrays.toString(tokenPolicy));
                    }
                } else {
                    if (intValueIn(TokenType.USER, tokenPolicy)) {
                        return provider.provideNormalRetrofit(aid);
                    } else if (intValueIn(TokenType.SERVICE, tokenPolicy)) {
                        return provider.provideServiceRetrofit();
                    } else {
                        throw new UnsupportedOperationException("Unsupported account_id: " + aid + " with token_policy: " + Arrays.toString(tokenPolicy));
                    }
                }
            }
        };

        this.accountApi = new AccountApi(accountId, retrofitProvider);
        this.audioApi = new AudioApi(accountId, retrofitProvider);
        this.boardApi = new BoardApi(accountId, retrofitProvider);
        this.commentsApi = new CommentsApi(accountId, retrofitProvider);
        this.databaseApi = new DatabaseApi(accountId, retrofitProvider);
        this.docsApi = new DocsApi(accountId, retrofitProvider);
        this.faveApi = new FaveApi(accountId, retrofitProvider);
        this.friendsApi = new FriendsApi(accountId, retrofitProvider);
        this.groupsApi = new GroupsApi(accountId, retrofitProvider);
        this.likesApi = new LikesApi(accountId, retrofitProvider);
        this.messagesApi = new MessagesApi(accountId, retrofitProvider);
        this.newsfeedApi = new NewsfeedApi(accountId, retrofitProvider);
        this.notificationsApi = new NotificationsApi(accountId, retrofitProvider);
        this.pagesApi = new PagesApi(accountId, retrofitProvider);
        this.photosApi = new PhotosApi(accountId, retrofitProvider);
        this.pollsApi = new PollsApi(accountId, retrofitProvider);
        this.statusApi = new StatusApi(accountId, retrofitProvider);
        this.storeApi = new StoreApi(accountId, retrofitProvider);
        this.usersApi = new UsersApi(accountId, retrofitProvider);
        this.utilsApi = new UtilsApi(accountId, retrofitProvider);
        this.videoApi = new VideoApi(accountId, retrofitProvider);
        this.wallApi = new WallApi(accountId, retrofitProvider);
        this.otherApi = new OtherApi(accountId, provider);
    }

    public static VkApies create(int accountId, String accessToken, IVkRetrofitProvider provider) {
        return new VkApies(accountId, true, accessToken, provider);
    }

    public static synchronized VkApies get(int accountId, IVkRetrofitProvider provider) {
        VkApies apies = APIS.get(accountId);
        if (apies == null) {
            apies = new VkApies(accountId, false, null, provider);
            APIS.put(accountId, apies);
        }

        return apies;
    }

    @Override
    public IMessagesApi messages() {
        return messagesApi;
    }

    @Override
    public IPhotosApi photos() {
        return photosApi;
    }

    @Override
    public IFriendsApi friends() {
        return friendsApi;
    }

    @Override
    public IWallApi wall() {
        return wallApi;
    }

    @Override
    public IDocsApi docs() {
        return docsApi;
    }

    @Override
    public INewsfeedApi newsfeed() {
        return newsfeedApi;
    }

    @Override
    public ICommentsApi comments() {
        return commentsApi;
    }

    @Override
    public INotificationsApi notifications() {
        return notificationsApi;
    }

    @Override
    public IVideoApi video() {
        return videoApi;
    }

    @Override
    public IBoardApi board() {
        return boardApi;
    }

    @Override
    public IUsersApi users() {
        return usersApi;
    }

    @Override
    public IGroupsApi groups() {
        return groupsApi;
    }

    @Override
    public IAccountApi account() {
        return accountApi;
    }

    @Override
    public IDatabaseApi database() {
        return databaseApi;
    }

    @Override
    public IAudioApi audio() {
        return audioApi;
    }

    @Override
    public IStatusApi status() {
        return statusApi;
    }

    @Override
    public ILikesApi likes() {
        return likesApi;
    }

    @Override
    public IPagesApi pages() {
        return pagesApi;
    }

    @Override
    public IStoreApi store() {
        return storeApi;
    }

    @Override
    public IFaveApi fave() {
        return faveApi;
    }

    @Override
    public IPollsApi polls() {
        return pollsApi;
    }

    @Override
    public IUtilsApi utils() {
        return utilsApi;
    }

    @Override
    public IOtherApi other() {
        return otherApi;
    }
}