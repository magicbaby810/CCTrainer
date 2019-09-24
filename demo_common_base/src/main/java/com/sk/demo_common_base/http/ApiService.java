package com.sk.demo_common_base.http;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * 接口信息集合
 *
 * @FormUrlEncoded下如果不传参数，但是请求头有公共参数，需要增加一个默认参数进去
 * 默认 @Field("str") String str  实参传""即可
 *
 * 此处存放多个moudle共同调用的接口
 *
 * @author sk
 */
public interface ApiService {





}
