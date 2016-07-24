package com.njnu.kai.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.njnu.kai.test.support.BaseActivity;
import com.njnu.kai.test.support.LogUtils;
import com.njnu.kai.test.support.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-12-22
 */
public class EntryActivity extends BaseActivity {

    private static final String TAG = "EntryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        printIntent(getIntent(), "onCreate");
        TextView textView = new TextView(this);
        setContentView(textView);
        textView.setText("EntryActivity test");

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        printIntent(getIntent(), "onNewIntent");
    }

    private void printIntent(Intent intent, String flag) {
        LogUtils.i(TAG, "printIntent %s flag=%s", intent.toString(), flag);
        final String dataString = intent.getDataString();
        if (!StringUtils.isEmpty(dataString)) {
            Pattern pattern = Pattern.compile("gid=(\\d+)&pid=(\\d+)");
            final Matcher matcher = pattern.matcher(dataString);
            if (matcher.find()) {
                final int groupCount = matcher.groupCount();
                if (groupCount == 2) {
                    String gId = matcher.group(1);
                    String pId = matcher.group(2);
                    LogUtils.i(TAG, "printIntent gId=%s pid=%s", gId, pId);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (TestApplication.isSplashHasFinished()) {
            startMainActivity();
        } else {
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    TestApplication.setSplashAlreadyFinished();
                    startMainActivity();
                }
            }, 600);
        }
    }

    private void startMainActivity() {
        startActivity(new Intent(EntryActivity.this, TestMainActivity.class));
//        finish();
    }
}
