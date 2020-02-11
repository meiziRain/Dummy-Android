package com.meizi.dummy.http;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @Classname HttpUtil
 * @Description TODO
 * @Date 2020/2/6 12:01
 * @Created by jion
 */
public class HttpUtil {

    public static void sendOkHttpRequest(String address, String jsonStr,okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(HttpConstants.MEDIA_TYPE_JSON, jsonStr);
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

}
