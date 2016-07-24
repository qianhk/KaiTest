package com.njnu.kai.test.okhttp;

import android.os.Bundle;
import android.view.View;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.BaseActivity;
import com.njnu.kai.test.support.LogUtils;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.net.Proxy;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15/12/30
 */
public class OkHttpTestActivity extends BaseActivity implements View.OnClickListener {

    //http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0106/2275.html
    //MultipartBuilder 未看
    //缓存由于依赖Cache-Control,在api中一般用不到
    private static final String TAG = "OkHttpTestActivity";
    private final ScheduledExecutorService mExecutor = Executors.newScheduledThreadPool(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp_test);
//        HttpUtils.setNeedReferer(true);
        findViewById(R.id.btn_simple_get).setOnClickListener(this);
        findViewById(R.id.btn_post_json).setOnClickListener(this);
        findViewById(R.id.btn_post_form).setOnClickListener(this);
        findViewById(R.id.btn_cancel_request).setOnClickListener(this);
        findViewById(R.id.btn_auth).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        final Request.Builder builder = new Request.Builder();
        builder.url("http://wap.3g.cn");
        if (viewId == R.id.btn_simple_get) {
            TaskScheduler.execute(new Runnable() {
                @Override
                public void run() {
                    LogUtils.e(TAG, "lookOkHttp 结果:\n%s", HttpUtils.getStringFromUrlSync("http://baidu.com/?a=0&record=1&b=" + new Random().nextInt()));
                }
            });
        } else if (viewId == R.id.btn_post_json) {
            RequestBody body = RequestBody.create(HttpUtils.JSON_MEDIA_TYPE, "{\"code\":1,\"message\":\"ok\"}");
            builder.post(body);
            HttpUtils.enqueue(builder, mCallback);
        } else if (viewId == R.id.btn_post_form) {
            RequestBody body = new FormEncodingBuilder().add("platform", "android").add("Test测试", "\" ?&")
                    .add("name", "掌柜%a").addEncoded("encoded", "butNo编?& 码ha")
                    .add("trueEncoded", "%25").addEncoded("trueEncodeded", "%25掌?&0").build();
            builder.post(body);
            HttpUtils.enqueue(builder, mCallback);
        } else if (viewId == R.id.btn_cancel_request) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    onClickCancelRequest();
                }
            });
        } else if (viewId == R.id.btn_auth) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    onClickAuth();
                }
            });
        }
    }

    public void onClickAuth() {
        HttpUtils.getOkHttpClient().setAuthenticator(new Authenticator() {
            @Override
            public Request authenticate(Proxy proxy, Response response) {
                System.out.println("lookOkHttp Authenticating for response: " + response);
                System.out.println("lookOkHttp Challenges: " + response.challenges());
                String credential = Credentials.basic("jesse", "password1");
                return response.request().newBuilder()
                        .header("Authorization", credential)
                        .build();
            }


            @Override
            public Request authenticateProxy(Proxy proxy, Response response) {
                return null; // Null indicates no attempt to authenticate.
            }
        });

        Request request = new Request.Builder()
                .url("http://publicobject.com/secrets/hellosecret.txt")
                .build();

        try {
            Response response = HttpUtils.getOkHttpClient().newCall(request).execute();
//            if (!response.isSuccessful()) throw new IOException("lookOkHttp Unexpected code " + response);
            String string = response.body().string();
            System.out.println(String.format("lookOkHttp count=%d trim=%s body=%s", string.length(), string.trim(), string));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.printf("lookOkHttp has exception", e);
        }

    }

    private void onClickCancelRequest() {
        Request request = new Request.Builder()
                .url("http://httpbin.org/delay/2") // This URL is served with a 2 second delay.
                .build();

        final long startNanos = System.nanoTime();
        final Call call = HttpUtils.getOkHttpClient().newCall(request);

        // Schedule a job to cancel the call in 1 second.
        mExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.printf("lookOkHttp %.2f Canceling call.%n", (System.nanoTime() - startNanos) / 1e9f);
                call.cancel();
                System.out.printf("lookOkHttp %.2f Canceled call.%n", (System.nanoTime() - startNanos) / 1e9f);
            }
        }, 1, TimeUnit.SECONDS);

        try {
            System.out.printf("lookOkHttp %.2f Executing call.%n", (System.nanoTime() - startNanos) / 1e9f);
            Response response = call.execute();
            System.out.printf("lookOkHttp %.2f Call was expected to fail, but completed: %s%n",
                    (System.nanoTime() - startNanos) / 1e9f, response);
        } catch (IOException e) {
            System.out.printf("lookOkHttp %.2f Call failed as expected: %s%n",
                    (System.nanoTime() - startNanos) / 1e9f, e);
        }
    }

    private Callback mCallback = new Callback() {
        @Override
        public void onFailure(Request request, IOException e) {
            LogUtils.e(TAG, "lookOkHttp nFailure", e);
        }

        @Override
        public void onResponse(Response response) {
            String responseString = null;
            if (response != null && response.isSuccessful()) {
                try {
                    responseString = response.body().string();
                    Headers responseHeaders = response.headers();
                    for (int idx = 0; idx < responseHeaders.size(); idx++) {
                        LogUtils.e(TAG, "lookOkHttp header: %s: %s", responseHeaders.name(idx), responseHeaders.value(idx));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LogUtils.e(TAG, "lookOkHttp 结果:\n%s", responseString);
        }
    };
}
