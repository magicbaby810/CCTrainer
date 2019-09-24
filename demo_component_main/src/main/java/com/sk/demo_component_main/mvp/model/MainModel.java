package com.sk.demo_component_main.mvp.model;


import com.sk.demo_common_base.bean.Response;
import com.sk.demo_common_base.http.Http;
import com.sk.demo_component_main.MainApiService;
import com.sk.demo_component_main.mvp.contract.MainContract;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainModel implements MainContract.Model {
    @Override
    public Observable<Response> login(String code) {

        return Http.getApiService(MainApiService.class).getData(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
