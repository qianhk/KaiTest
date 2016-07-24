package com.njnu.kai.test.delloc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.njnu.kai.test.R;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-11-27
 */
public class DellocActivity extends FragmentActivity {

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            switch (viewId) {
                case R.id.btn_one:
                    methodOne();
                    break;
                case R.id.btn_Two:
                    methodTwo();
                    break;
                case R.id.btn_Three:
                    methodThree();
                    break;
                default:
                    break;
            }
        }
    };

    private void addFragment(Class<?> clazz) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_entry, Fragment.instantiate(this, clazz.getName(), null))
                .addToBackStack(null)
                .commitAllowingStateLoss();

    }

    private void methodOne() {
        addFragment(MediaListFragment.class);
    }

    private void methodTwo() {
        addFragment(DraggableMediaListFragment.class);
    }

    private void methodThree() {
        startActivity(new Intent(this, Delloc2Activity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_delloc);

        findViewById(R.id.btn_one).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_Two).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_Three).setOnClickListener(mOnClickListener);
    }
}
