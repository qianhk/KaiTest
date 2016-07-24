package com.njnu.kai.test.aidl;

import com.njnu.kai.test.aidl.ForActivity;

/**
 * 
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-9-2
 */
interface ForService {

    void registerTestCall(ForActivity activity);

    void invokCallBack(int type);

}
