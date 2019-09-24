package com.sk.demo_common_base.base;

import android.content.Context;
import android.util.Log;

import com.sk.demo_common_base.utils.RxManager;


/**
 * des:基类presenter
 * T:mView
 * E:mModel
 * @author sk
 */
public abstract class BasePresenter<T, E> {
    public Context mContext;
    public E mModel;
    public T mView;
    public RxManager mRxManager = new RxManager();

    public void setVM(T mView, E mModel) {
        this.mView = mView;
        this.mModel = mModel;
        this.onStart();
    }

    public void onStart() {
    }

    public void onDestroy() {
        Log.e("presenter", mContext + "-->>" + mRxManager);
        mRxManager.clear();
    }
}
