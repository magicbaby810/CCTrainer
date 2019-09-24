package com.sk.demo_component_main.mvp.presenter;


import com.sk.demo_common_base.bean.Response;
import com.sk.demo_common_base.http.RxSubscriber;
import com.sk.demo_component_main.mvp.contract.MainContract;

import io.reactivex.disposables.Disposable;

/**
 * @author sk on 2019/4/3.
 */
public class MainPresenter extends MainContract.Presenter {

    @Override
    public void login(String code) {
        mModel.login(code)
                .subscribe(new RxSubscriber<Response>() {
                    @Override
                    public void onNext(Response response) {
                        if (response.isSuccess()) {
                            mView.returnSuccess(response.msg);
                        } else {
                            mView.returnError(response.msg);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mView.returnError(null);
                    }

                    @Override
                    public void onFinished() {
                        mView.returnFinish();
                    }

                    @Override
                    public void getDisposable(Disposable disposable) {
                        mRxManager.add(disposable);
                    }
                });
    }
}
