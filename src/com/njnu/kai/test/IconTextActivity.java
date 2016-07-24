package com.njnu.kai.test;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.StateSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.njnu.kai.test.view.IconTextView;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-8-25
 */
public class IconTextActivity extends Activity {

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            IconTextView itv51 = (IconTextView)findViewById(R.id.itv_51);
            IconTextView itv52 = (IconTextView)findViewById(R.id.itv_52);
            IconTextView itv62 = (IconTextView)findViewById(R.id.itv_62);
            IconTextView itv63 = (IconTextView)findViewById(R.id.itv_63);
            IconTextView itv64 = (IconTextView)findViewById(R.id.itv_64);
            ImageView iv53 = (ImageView)findViewById(R.id.iv_53);
            if (viewId == R.id.itv_21) {
                itv51.startAnimation(AnimationUtils.loadAnimation(IconTextActivity.this, R.anim.unlimited_rotate));
                itv52.startAnimation(AnimationUtils.loadAnimation(IconTextActivity.this, R.anim.unlimited_rotate));
                itv62.startAnimation(AnimationUtils.loadAnimation(IconTextActivity.this, R.anim.unlimited_rotate));
                itv63.startAnimation(AnimationUtils.loadAnimation(IconTextActivity.this, R.anim.unlimited_rotate));
                itv64.startAnimation(AnimationUtils.loadAnimation(IconTextActivity.this, R.anim.unlimited_rotate));
                iv53.startAnimation(AnimationUtils.loadAnimation(IconTextActivity.this, R.anim.unlimited_rotate));
            } else {
                itv51.clearAnimation();
                itv52.clearAnimation();
                itv62.clearAnimation();
                itv63.clearAnimation();
                itv64.clearAnimation();
                iv53.clearAnimation();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_icon_text);

        ImageView iv = null;

        IconTextView itv = (IconTextView)findViewById(R.id.itv_11);
        itv.setText(R.string.icon_qian);

        itv = (IconTextView)findViewById(R.id.itv_12);
        itv.setText(R.string.icon_hong);

        itv = (IconTextView)findViewById(R.id.itv_13);
        itv.setImageResource(R.drawable.img_icon_qian);

        itv = (IconTextView)findViewById(R.id.itv_14);
        itv.setImageResource(R.drawable.img_icon_hong);



        itv = (IconTextView)findViewById(R.id.itv_21);
        itv.setText(R.string.icon_qian);
        itv.setOnClickListener(mOnClickListener);

        itv = (IconTextView)findViewById(R.id.itv_22);
        itv.setImageResource(R.drawable.img_icon_hong_empty, R.drawable.img_icon_hong);
        itv.setOnClickListener(mOnClickListener);

        itv = (IconTextView)findViewById(R.id.itv_23);
        itv.setImageResource(R.drawable.xml_icon_chicken);

        itv = (IconTextView)findViewById(R.id.itv_24);
        itv.setImageResource(R.drawable.xml_icon_chicken, R.drawable.xml_icon_dot);
        itv.setCheckable(true);



        itv = (IconTextView)findViewById(R.id.itv_31);
        itv.setText(R.string.icon_qian);

        itv = (IconTextView)findViewById(R.id.itv_32);
        itv.setText(R.string.icon_hong_empty);
        itv.setTextColor(Color.CYAN);

        itv = (IconTextView)findViewById(R.id.itv_33);
        itv.setImageResource(R.drawable.img_icon_qian);

        itv = (IconTextView)findViewById(R.id.itv_34);
        itv.setImageResource(R.drawable.img_icon_hong);




        itv = (IconTextView)findViewById(R.id.itv_41);

        itv = (IconTextView)findViewById(R.id.itv_42);
        itv.setText(R.string.icon_hong_empty, R.string.icon_hong);
        itv.setCheckable(true);
        Resources resources = getResources();
        itv.setTextColor(resources.getColorStateList(R.color.xml_color_green), resources.getColorStateList(R.color.xml_color_magenta));

        itv = (IconTextView)findViewById(R.id.itv_51);
//        itv.mLog = true;
        itv.setClickable(true);

        itv = (IconTextView)findViewById(R.id.itv_52);
        itv.setImageResource(R.drawable.img_flush);

        iv = (ImageView)findViewById(R.id.iv_53);

        itv = (IconTextView)findViewById(R.id.itv_54);
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{{android.R.attr.state_pressed}, {android.R.attr.state_selected}, StateSet.WILD_CARD},
                new int[]{Color.GREEN, Color.MAGENTA, Color.CYAN});
        itv.setTextColor(colorStateList);

//        StateListDrawable stateListDrawable = new StateListDrawable();
//        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, getResources().getDrawable(R.drawable.image_guide_duck_1));
//        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, getResources().getDrawable(R.drawable.image_guide_duck_1));
//        stateListDrawable.addState(StateSet.WILD_CARD, getResources().getDrawable(R.drawable.image_guide_duck_2));
//        itv.setImageDrawable(stateListDrawable);

        TextView tv = (TextView)findViewById(R.id.tv_test_text);
        colorStateList = new ColorStateList(
                new int[][]{{android.R.attr.state_pressed}, {android.R.attr.state_focused}, {0}},
                new int[]{Color.RED, Color.GREEN, Color.BLUE});
        tv.setTextColor(colorStateList);

        itv = (IconTextView)findViewById(R.id.itv_61);
        itv.mLog = true;

        itv = (IconTextView)findViewById(R.id.itv_62);
        itv.mLog = true;

        itv = (IconTextView)findViewById(R.id.itv_63);
        itv.mLog = true;
    }
}
