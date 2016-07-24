package com.njnu.kai.test.okhttp;

import android.text.TextUtils;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 使用okhttp实现的
 *
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15/12/29
 */
public class HttpUtils {

    public static final String UTF8 = "UTF-8";

    public static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    public static final String HEADER_RANGE = "Range";
    public static final String HEADER_REFERER = "Referer";
    public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";

    private static final String TAG = "HttpUtils";


    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.4.1; Nexus 4 Build/KOT49E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.64 Mobile Safari/537.36";

    private static String sLastUrl;

    private static boolean sNeedReferer;

    public static void setNeedReferer(boolean needReferer) {
        sNeedReferer = needReferer;
    }

    private static final OkHttpClient sOkHttpClient = new OkHttpClient();

    static {
        sOkHttpClient.setConnectTimeout(16, TimeUnit.SECONDS);
        sOkHttpClient.setWriteTimeout(22, TimeUnit.SECONDS);
        sOkHttpClient.setReadTimeout(28, TimeUnit.SECONDS);
    }

    public static OkHttpClient getOkHttpClient() {
        return sOkHttpClient;
    }

    /**
     * 不会开启异步线程。
     *
     * @param requestBuilder builder
     * @return
     * @throws IOException
     */
    public static Response execute(Request.Builder requestBuilder) {
        try {
            addRefererHeader(requestBuilder);
            addRequestHeader(requestBuilder, null);
            Request request = requestBuilder.build();
            sLastUrl = request.urlString();
            return sOkHttpClient.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 开启异步线程访问网络
     *
     * @param requestBuilder   builder
     * @param responseCallback call back
     */
    public static void enqueue(Request.Builder requestBuilder, Callback responseCallback) {
        addRefererHeader(requestBuilder);
        addRequestHeader(requestBuilder, null);
        Request request = requestBuilder.build();
        sLastUrl = request.urlString();
        sOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     *
     * @param requestBuilder builder
     */
    public static void enqueue(Request.Builder requestBuilder) {
        enqueue(requestBuilder, new Callback() {
            @Override
            public void onResponse(Response arg0) {
            }

            @Override
            public void onFailure(Request arg0, IOException arg1) {
            }
        });
    }

    public static String getStringFromUrlSync(String url) {
        String responseString = null;
        Response response = execute(new Request.Builder().url(url));
        if (response != null && response.isSuccessful()) {
            try {
                responseString = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseString;
    }

    public static String getStringFromRequestSync(Request.Builder requestBuilder) {
        String responseString = null;
        Response response = execute(requestBuilder);
        if (response != null && response.isSuccessful()) {
            try {
                responseString = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseString;
    }

    private static void addRequestHeader(Request.Builder requestBuilder, HashMap<String, Object> header) {
        requestBuilder.addHeader(HEADER_USER_AGENT, USER_AGENT);
//        requestBuilder.addHeader(HEADER_ACCEPT_ENCODING, CONTENT_ENCODING_GZIP); //okhttp默认会添加这个,自己加了后反而不能解析gzip了
        if (header != null) {
            for (String key : header.keySet()) {
                requestBuilder.header(key, String.valueOf(header.get(key)));
            }
        }
    }

    private static void addRefererHeader(Request.Builder requestBuilder) {
        if (sNeedReferer && !TextUtils.isEmpty(sLastUrl)) {
            requestBuilder.addHeader(HEADER_REFERER, sLastUrl);
        }
    }

    /**
     * 经过试验发现okhttp有如下特性,可增强app联网的能力,但抓包调试可能带来困扰:
     * 1. 自动有Accept-Encoding: gzip,如果自己再加,反而让response返回的gzip数据无法自动解压
     * 2. 首次连接上某个域名后,会缓存这个域名的ip或者连接,从log看下次没有connect的过程直接write和read响应
     * 3. 设置系统代理后,okhttp会在连接失败后尝试不使用系统代理直接连接,连接成功后下次即使代理有效了,也不去使用代理了,改变系统代理的状态也没用,只能关掉重启app
     * 4. FormEncodingBuilder.addEncoded 仅对%起作用,其他字符还是会转义,如%25,不会再转成%2525
     *   ,且使用FormEncodingBuilder生成body后Content-Type自动是application/x-www-form-urlencoded
     * 5.
     */
}
