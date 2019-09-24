package com.sk.demo_component_main.mvp.contract;


import com.sk.demo_common_base.base.BaseModel;
import com.sk.demo_common_base.base.BasePresenter;
import com.sk.demo_common_base.base.BaseView;
import com.sk.demo_common_base.bean.Response;

import io.reactivex.Observable;

public interface MainContract {

    interface Model extends BaseModel {
        Observable<Response> login(String code);
     }

    interface View extends BaseView {

        void returnSuccess(String msg);

        void returnFinish();

        void returnError(String msg);

    }


    abstract class Presenter extends BasePresenter<View, Model> {


        public abstract void login(String code);

    }
}
