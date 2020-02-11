package com.meizi.dummy.http;

import okhttp3.MediaType;

/**
 * @Classname HttpConstants
 * @Description TODO
 * @Date 2020/2/3 11:24
 * @Created by jion
 */
public class HttpConstants {
    public final static MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public final static String BASE_URL = "http://192.168.43.181:8086";
    // user login
    public final static String USER_LOGIN_URL = BASE_URL + "/auth/login";

    // create a account
    public final static String USER_CREATE_URL = BASE_URL + "/user/create";
}
