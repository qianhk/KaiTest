package com.njnu.kai.test.next;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.BaseActivity;
import com.njnu.kai.test.support.StringUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-2-28
 */
public class HasNextActivity extends FragmentActivity {

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if (viewId == R.id.btn_page1) {
                getSupportFragmentManager().beginTransaction().add(R.id.layout_entry, new Next1Fragment()).addToBackStack(null).commitAllowingStateLoss();
            } else if (viewId == R.id.btn_page2) {
                getSupportFragmentManager().beginTransaction().add(R.id.layout_entry, new Next2Fragment()).addToBackStack(null).commitAllowingStateLoss();
            } else if (viewId == R.id.btn_return) {
                getSupportFragmentManager().popBackStack();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String title = intent != null ? intent.getStringExtra(BaseActivity.KEY_TITLE) : null;
        if (!StringUtils.isEmpty(title)) {
            setTitle(title);
        }
        setContentView(R.layout.activity_has_next);
        findViewById(R.id.btn_page1).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_page2).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_return).setOnClickListener(mOnClickListener);
    }

}