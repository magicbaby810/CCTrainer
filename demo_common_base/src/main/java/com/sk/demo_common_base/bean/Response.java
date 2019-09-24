package com.sk.demo_common_base.bean;


public class Response<T> {
    // 0成功
    public int code;
    public String msg;
    public T data;

    public boolean isTokenInvalid () {
        return code == 110;
    }
    public boolean isSuccess() {
        return code == 0;
    }

}
