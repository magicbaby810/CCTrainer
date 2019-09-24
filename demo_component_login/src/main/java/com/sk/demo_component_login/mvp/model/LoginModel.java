package com.sk.demo_component_login.mvp.model;


import com.sk.demo_common_base.bean.Response;
import com.sk.demo_common_base.http.Http;
import com.sk.demo_component_login.LoginApiService;
import com.sk.demo_component_login.bean.User;
import com.sk.demo_component_login.mvp.contract.LoginContract;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginModel implements LoginContract.Model {
    @Override
    public Observable<Response<User>> login(String code) {

        return Http.getApiService(LoginApiService.class).login(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
