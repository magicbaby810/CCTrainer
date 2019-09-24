package com.sk.demo_common_base.utils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


/**
 * 用于管理单个presenter的RxBus的事件和Rxjava相关代码的生命周期处理
 */
public class RxManager {
    public RxBus mRxBus = RxBus.getDefault();
    //管理rxbus订阅
    private Map<String, Observable<?>> mObservables = new HashMap<>();
    /*管理Observables 和 Subscribers订阅*/
    private CompositeDisposable mCompositeSubscription = new CompositeDisposable();

    /**
     * 单纯的Observables 和 Subscribers管理
     *
     * @param m
     */
    public void add(Disposable... m) {
        /*订阅管理*/
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeDisposable();
        }
        if (null == m) {
            return;
        }
        for (Disposable d : m) {
            if (null != d && !d.isDisposed()) {
                mCompositeSubscription.add(d);
            }
        }
    }

    /**
     * 单个Observable 取消订阅
     *
     * @param m
     */
    public void remove(Disposable... m) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeDisposable();
        }
        if (null == m) {
            return;
        }
        for (Disposable d : m) {
            if (null != d && !d.isDisposed()) {
                mCompositeSubscription.remove(d);
            }
        }
    }

    /**
     * 单个presenter生命周期结束，取消订阅和所有rxbus观察
     */
    public void clear() {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeDisposable();
        }
        if (!mCompositeSubscription.isDisposed()) {
            mCompositeSubscription.clear();// 取消所有订阅
        }
        for (Map.Entry<String, Observable<?>> entry : mObservables.entrySet()) {
            mRxBus.unRegister(entry.getValue().subscribe());// 移除rxbus观察
        }
    }
}
