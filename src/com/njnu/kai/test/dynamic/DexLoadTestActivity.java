package com.njnu.kai.test.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.BaseActivity;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-11-16
 */
public class DexLoadTestActivity extends BaseActivity {

    public static final String DEX_PATH = "/sdcard/DHost/plugin.apk";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int viewId = v.getId();
            if (viewId == R.id.btn_load_other_apk) {
                Intent intent = new Intent(DexLoadTestActivity.this, ProxyActivity.class);
                intent.putExtra(ProxyActivity.EXTRA_DEX_PATH, DEX_PATH);
                startActivity(intent);
            } else if (viewId == R.id.btn_load_other_apk_with_class) {
                Intent intent = new Intent(DexLoadTestActivity.this, ProxyActivity.class);
                intent.putExtra(ProxyActivity.EXTRA_DEX_PATH, DEX_PATH);
                intent.putExtra(ProxyActivity.EXTRA_CLASS, "com.njnu.kai.animator.guest.GuestActivity");
                startActivity(intent);
            } else if (viewId == R.id.btn_load_Dex) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dex_load_test);
        findViewById(R.id.btn_load_other_apk).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_load_other_apk_with_class).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_load_Dex).setOnClickListener(mOnClickListener);
    }
}
