package com.sk.demo_component_login.mvp.contract;


import com.sk.demo_common_base.base.BaseModel;
import com.sk.demo_common_base.base.BasePresenter;
import com.sk.demo_common_base.base.BaseView;
import com.sk.demo_common_base.bean.Response;
import com.sk.demo_component_login.bean.User;

import io.reactivex.Observable;

public interface LoginContract {

    interface Model extends BaseModel {
        Observable<Response<User>> login(String code);
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
