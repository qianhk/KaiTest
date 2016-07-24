package com.njnu.kai.test;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import com.njnu.kai.test.support.DisplayUtils;
import com.njnu.kai.test.view.GlobalMenuThumbImageView;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-7-10
 */
public class GlobalMenuDialog extends Dialog {

    private Context mContext;
    private GlobalMenuThumbImageView mIndicatorImageView;

    /**
     * @param context context
     */
    public GlobalMenuDialog(Context context) {
        super(context);
        mContext = context;
        setCancelable(true);
        setup(context);
    }

    /**
     * @param context context
     */
    public GlobalMenuDialog(Context context, int themeId) {
        super(context, themeId);
        mContext = context;
        setCancelable(true);
        setup(context);
    }

    private void setup(Context context) {
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.popups_global_menu_panel);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = DisplayUtils.getWidthPixels();
//        window.setWindowAnimations(R.style.Dialog_Window_Anim);
        window.setAttributes(params);

        mIndicatorImageView = (GlobalMenuThumbImageView)findViewById(R.id.thumbImageView);
        mIndicatorImageView.setThumbDrawable(mContext.getResources().getDrawable(R.drawable.img_menu_indicator_thumb));

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mIndicatorImageView.setThumbOffset(1.0f * progress / 100.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_MENU:
                dismiss();
                break;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void setBackgroundColor(int color) {
        getWindow().setBackgroundDrawable(new ColorDrawable(color));
    }
}
