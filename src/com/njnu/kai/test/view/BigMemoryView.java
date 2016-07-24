package com.njnu.kai.test.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.njnu.kai.test.support.LogUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-12-19
 */
public class BigMemoryView extends TextView {

    private byte[] mBytes;
    public static final int MEMORY_SIZE = 25 * 1024 * 1024;

    public BigMemoryView(Context context) {
        super(context);
        initView();
    }

    public BigMemoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BigMemoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mBytes = new byte[MEMORY_SIZE];
        setText("MemorySize=" + MEMORY_SIZE);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        LogUtils.d("BigMemoryView", "finalize lookFinalize");
    }
}
