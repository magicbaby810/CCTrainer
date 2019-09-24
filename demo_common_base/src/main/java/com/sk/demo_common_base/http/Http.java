package com.sk.demo_common_base.http;

import android.os.Build;


import com.sk.demo_common_base.BuildConfig;
import com.sk.demo_common_base.Constant;
import com.sk.demo_common_base.UserApplication;
import com.sk.demo_common_base.utils.AppUtils;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
  *@author sk
  */
public class Http {

    private static Retrofit mRetrofit;

    private static Retrofit getRetrofit() {

        if (null == mRetrofit) {
            //RequestInterceptor
            RequestInterceptor requestInterceptor = new RequestInterceptor();
            //ResponseInterceptor
            ResponseInterceptor responseInterceptor = new ResponseInterceptor();

            // 通过当前的控制debug的全局常量控制是否打log
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            if (BuildConfig.DEBUG) {
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            } else {
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            }

            //OkHttpClient
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(requestInterceptor)
                    .addInterceptor(responseInterceptor)
                    .addInterceptor(createBasicParamsInterceptor())
                    .addInterceptor(httpLoggingInterceptor);


            OkHttpClient mOkHttpClient = builder.build();

            //Retrofit
            mRetrofit = new Retrofit.Builder()
                    .client(mOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(ApiConstants.URL_BASE)
                    .build();
        }

        return mRetrofit;
    }

    /**
     * 创建请求拦截器 用做上传公共参数和请求头
     *
     * @return
     */
    private static BasicParamsInterceptor createBasicParamsInterceptor() {
        return new BasicParamsInterceptor.Builder()
                .addParam(Constant.IMEI,AppUtils.getIMEI(UserApplication.getInstance()))
                .addParam(Constant.APPVERSION, AppUtils.getVersionName(UserApplication.getInstance())
                                                + ";"
                                                + AppUtils.getVersionCode(UserApplication.getInstance()))
                .addParam(Constant.PHONEMODEL, Build.MODEL)
                .addParam(Constant.TIMESTAMP, String.valueOf(System.currentTimeMillis()))
//                .addHeaderParam("Connection","false")
//                .addParam(Constant.TOKEN, String.valueOf(S.get(Constant.TOKEN, "")))
//                .addParam(Constant.DEVICEID, Constant.DEVICEID+"_"+S.get("deviceID",""))
                .build();
    }


    /**
     * 获取api实例
     */
    public static <T> T getApiService(Class<T> tClass) {
        return getRetrofit().create(tClass);
    }
}