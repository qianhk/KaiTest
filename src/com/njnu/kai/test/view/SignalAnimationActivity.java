package com.njnu.kai.test.view;

import android.os.Bundle;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.BaseActivity;

public class SignalAnimationActivity extends BaseActivity {

    private SignalView mSignalView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_signal_animation);
        mSignalView = (SignalView)findViewById(R.id.signal_view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSignalView.setLoop(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSignalView.setLoop(true);
    }
}
