package com.njnu.kai.test.aidl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.CallerUtils;
import com.njnu.kai.test.support.LogUtils;
import com.njnu.kai.test.support.ToastUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-9-2
 */
public class AIDLTestActivity extends Activity {

    private static final String TAG = "AIDLTestActivity";
    private TextView mTvResult;

    private View mSendView;
    private Button mBtnAction;

    private boolean mConected;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int viewId = v.getId();
            if (viewId == R.id.btn_action) {
                v.setEnabled(false);
                if (mConected) {
                    unbindService(mConnection);
                    onDisconnectedServiceFlushView();
                } else {
                    Bundle args = new Bundle();
                    Intent intent = new Intent(AIDLTestActivity.this, AIDLTestService.class);
                    intent.putExtras(args);
                    final boolean bindService = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                    ToastUtils.showToast(AIDLTestActivity.this, "bindService " + bindService);
                }
            } else if (viewId == R.id.btn_send) {
                if (mService == null) {
                    ToastUtils.showToast(AIDLTestActivity.this, "未连接、无法发送命令");
                    return;
                }
                try {
                    mService.invokCallBack(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl_test);
        mTvResult = (TextView) findViewById(R.id.tv_result);
        mBtnAction = (Button) findViewById(R.id.btn_action);
        mBtnAction.setOnClickListener(mOnClickListener);
        mBtnAction.setEnabled(true);

        mSendView = findViewById(R.id.btn_send);
        mSendView.setOnClickListener(mOnClickListener);
        mSendView.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        if (mConected) {
            unbindService(mConnection);
        }
        super.onDestroy();
    }

    private ForActivity mCallback = new ForActivity.Stub() {

        public void performAction() throws RemoteException {
            mTvResult.setText("toast from service");
            ToastUtils.showToast(AIDLTestActivity.this, "this toast is called from service");
            LogUtils.e(TAG, "lookaidl performAction " + CallerUtils.getAllCaller());
        }

        @Override
        public void testAbc() throws RemoteException {

        }
    };

    private ForService mService;

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            try {
                mService = ForService.Stub.asInterface(service);
                mService.registerTestCall(mCallback);
                mTvResult.setText("connect service success");
            } catch (RemoteException e) {
                mTvResult.setText("connect service RemoteException");
            }
            mConected = true;
            mSendView.setEnabled(true);
            mBtnAction.setEnabled(true);
            mBtnAction.setText("已连接，断开吧");
        }

        public void onServiceDisconnected(ComponentName className) {
            onDisconnectedServiceFlushView();
        }
    };

    private void onDisconnectedServiceFlushView() {
        mTvResult.setText("disconnect service");
        mService = null;
        mConected = false;
        mSendView.setEnabled(false);
        mBtnAction.setEnabled(true);
        mBtnAction.setText("已断开，连接吧");
    }

}