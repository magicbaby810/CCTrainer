package com.sk.demo_component_login;



import com.sk.demo_common_base.bean.Response;
import com.sk.demo_component_login.bean.User;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * 接口信息集合
 *
 * @FormUrlEncoded下如果不传参数，但是请求头有公共参数，需要增加一个默认参数进去
 * 默认 @Field("str") String str  实参传""即可
 */
public interface LoginApiService {



    /**
     * 登录信息
     * @return
     */
    @FormUrlEncoded
    @Headers({"url_name:order"})
    @POST("login/v1/getUserinfo")
    Observable<Response<User>> login(@Field("code") String code);




}
