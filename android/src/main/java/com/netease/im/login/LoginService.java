package com.netease.im.login;

import android.content.Context;

import com.netease.im.team.TeamListService;
import com.netease.im.uikit.LoginSyncDataStatusObserver;
import com.netease.im.uikit.cache.DataCacheManager;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

/**
 * Created by dowin on 2017/4/28.
 */

public class LoginService {


    final static String TAG = "LoginService";
    // 自己的用户帐号
    private String account;
    private AbortableFuture<LoginInfo> loginInfoFuture;

    private LoginService() {

    }

    static class InstanceHolder {
        final static LoginService instance = new LoginService();
    }

    public static LoginService getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * 设置当前登录用户的帐号
     *
     * @param account 帐号
     */
    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return null;
    }

    public LoginInfo getLoginInfo(Context context) {
        LoginInfo info = new LoginInfo(null, null);
        return info;
    }

    void initLogin(LoginInfo loginInfo) {
        DataCacheManager.buildDataCacheAsync();
    }

    public void login(LoginInfo loginInfo, final RequestCallback<LoginInfo> callback) {
        loginInfoFuture = NIMClient.getService(AuthService.class).login(loginInfo);
        loginInfoFuture.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                account = loginInfo.getAccount();
                initLogin(loginInfo);
                callback.onSuccess(loginInfo);

                registerObserver(true);
                queryRecentContacts();
                loginInfoFuture = null;
            }


            @Override
            public void onFailed(int code) {
                callback.onFailed(code);
                registerObserver(false);
                loginInfoFuture = null;
            }

            @Override
            public void onException(Throwable exception) {
                callback.onException(exception);
                registerObserver(false);
                loginInfoFuture = null;
            }
        });

    }


    private void queryRecentContacts() {
        recentContactObserver.queryRecentContacts();
    }

    volatile boolean hasRegister;

    RecentContactObserver recentContactObserver = new RecentContactObserver();
//    SysMessageObserver sysMessageObserver = new SysMessageObserver();

    synchronized void registerObserver(boolean register) {
        if (hasRegister && register) {
            return;
        }
        hasRegister = register;

        recentContactObserver.registerRecentContactObserver(register);
//        sysMessageObserver.registerSystemObserver(register);
    }

    public boolean deleteRecentContact(String recentContactId) {
       return recentContactObserver.deleteRecentContact(recentContactId);
    }

    public void logout() {

        NIMClient.getService(AuthService.class).logout();//退出服务

        registerObserver(false);//取消登录注册
        TeamListService.getInstance().clear();//群清理
        // 清理缓存&注销监听&清除状态
        DataCacheManager.clearDataCache();
        account = null;
        LoginSyncDataStatusObserver.getInstance().reset();
    }


}
