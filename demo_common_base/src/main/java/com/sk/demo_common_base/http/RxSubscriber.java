package com.sk.demo_common_base.http;

import android.content.Context;
import android.os.Message;

import androidx.annotation.NonNull;


import com.sk.demo_common_base.UserApplication;
import com.sk.demo_common_base.utils.AppUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;


/**
 * @author sk
 * @desc
 * @data 2019/4/30
 */
public abstract class RxSubscriber<R> implements Observer<R> {
    /**
     * 建立链接的时候调用并生成Disposable对象,此处相当于1.x的onStart()方法我做了如下处理
     *
     * @param d 链接状态对象
     */
    public Context mContext;

    public RxSubscriber() {

    }

    public RxSubscriber(Context mContext, boolean showLoading) {
        this.mContext = mContext;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (!AppUtils.isNetAvailable(UserApplication.getInstance())) {
            if (d != null && !d.isDisposed()) {
                d.dispose();
            }
            Throwable throwable = new Throwable("请检查网络连接后重试!");
            onError(throwable);
        } else {
            getDisposable(d);
        }
    }

    /**
     * 出现异常的时候会走这里,我们统一放在 onFinished();处理
     */
    @Override
    public void onError(Throwable e) {
        onFinished();
        if (e instanceof HttpException || e instanceof ConnectException || e instanceof SocketTimeoutException || e instanceof TimeoutException) {
            onNetworkException(e);
        } else {
            onUnknownException(e);
        }
    }


    /**
     * 不管成功与失败,这里都会走一次,所以加onFinished();方法
     */
    @Override
    public void onComplete() {
        onFinished();
    }

    /**
     * 请求结束之后的回调,无论成功还是失败,此处一般无逻辑代码,经常用来写ProgressBar的dismiss
     */
    public abstract void onFinished();

    /**
     * 向子类暴露 Disposable
     */
    public abstract void getDisposable(Disposable disposable);

    private void onNetworkException(Throwable e) {
        e.printStackTrace();
    }

    private void onUnknownException(Throwable e) {
        e.printStackTrace();
    }
}
