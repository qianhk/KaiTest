package com.njnu.kai.test.support;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-4-29
 */
public class BaseActivity extends Activity {

    public static final String KEY_TITLE = "key_title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String title = intent != null ? intent.getStringExtra(KEY_TITLE) : null;
        if (!StringUtils.isEmpty(title)) {
            setTitle(title);
        }
    }
}
