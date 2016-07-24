package com.njnu.kai.test.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.njnu.kai.test.support.CallerUtils;
import com.njnu.kai.test.support.LogUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-9-2
 */
public class AIDLTestService extends Service {

    private static final String TAG = "AIDLTestService";
    private ForActivity callback;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private final ForService.Stub mBinder = new ForService.Stub() {
        @Override
        public void invokCallBack(int type) throws RemoteException {
            callback.performAction();
            LogUtils.e(TAG, "lookaidl invokCallBack " + CallerUtils.getAllCaller());
        }

        @Override
        public void registerTestCall(ForActivity cb) throws RemoteException {
            callback = cb;
        }

    };
}
