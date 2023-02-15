package com.jediwus.learningapplication.util;

import android.util.Log;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpHelper {

    private static final String TAG = "HttpHelper";

    /**
     * 发送普通Request请求，获得String类型的数据
     *
     * @param url String
     * @return String
     */
    public static String requestResult(String url){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 在这里处理响应数据
                return Objects.requireNonNull(response.body()).string();
            } else {
                // 处理请求失败的情况
                Log.d(TAG, "requestResult: 请求String类型的数据出错！");
            }
        } catch (IOException e) {
            // 处理网络异常的情况
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 发送普通Request请求，获得byte[]类型的数据
     *
     * @param url String
     * @return byte[]
     * @throws IOException e
     */
    public static byte[] requestBytes(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 在这里处理响应数据
                return Objects.requireNonNull(response.body()).bytes();
            } else {
                // 处理请求失败的情况
                Log.d(TAG, "requestBytes: 请求byte[]类型的数据出错！");
            }
        } catch (IOException e) {
            // 处理网络异常的情况
            e.printStackTrace();
        }
        return new byte[0];
    }

}
