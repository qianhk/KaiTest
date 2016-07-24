package com.njnu.kai.test.next;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.njnu.kai.test.support.LogUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-2-28
 */
public class Next1Fragment extends Fragment {

    private static final String TAG = "Next1Fragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = new LinearLayout(inflater.getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        viewGroup.setLayoutParams(layoutParams);
        TextView textView = new TextView(inflater.getContext());
        textView.setText(getClass().getSimpleName());
        viewGroup.addView(textView);
        return viewGroup;
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(TAG, "lookTag onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d(TAG, "lookTag onPause");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.d(TAG, "lookTag setUserVisibleHint visible=%b", isVisibleToUser);
    }
}
