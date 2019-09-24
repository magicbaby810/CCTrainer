package com.sk.demo_common_base.http;



import com.sk.demo_common_base.Constant;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 类名称：请求前拦截器,这个拦截器会在okhttp请求之前拦截并做处理
 * @author sk
 */
public class RequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        //请求定制：添加请求头
        Request.Builder requestBuilder = original
                .newBuilder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        //如果是post的情况下,请求体定制：统一添加参数,此处演示的是get请求,因此不做处理 wyman 2017年11月22日
        if (original.body() instanceof FormBody) {
            FormBody.Builder newFormBody = new FormBody.Builder();
            FormBody oidFormBody = (FormBody) original.body();
            for (int i = 0; i < oidFormBody.size(); i++) {
                newFormBody.addEncoded(oidFormBody.encodedName(i), oidFormBody.encodedValue(i));
            }
            //当post请求的情况下在此处追加统一参数

            requestBuilder.method(original.method(), newFormBody.build());
        }
        List<String> headers = original.headers("url_name");


        HttpUrl newUrl = null;
        if (null != headers && headers.size() > 0) {
            String urlStr = headers.get(0);
            if ("user".equals(urlStr)) {
                newUrl = HttpUrl.parse(ApiConstants.URL_BASE);
            } else if ("pay".equals(urlStr)) {
                newUrl = HttpUrl.parse(ApiConstants.URL_BASE);
            } else {
                newUrl = HttpUrl.parse(ApiConstants.URL_BASE);
            }

            HttpUrl oldUrl = original.url();
            HttpUrl newFullUrl = oldUrl
                    .newBuilder()
                    .scheme(newUrl.scheme())
                    .host(newUrl.host())
                    .port(newUrl.port())
                    .build();


            return chain.proceed(requestBuilder.url(newFullUrl).build());
        } else {
            return chain.proceed(original);
        }

    }
}
