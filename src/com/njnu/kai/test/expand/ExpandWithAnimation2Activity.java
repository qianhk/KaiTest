package com.njnu.kai.test.expand;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.njnu.kai.test.R;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15/11/7
 */
public class ExpandWithAnimation2Activity extends Activity {

    private ExpandableLinearLayout mExpandView;
    private LinearLayout mLinearLayout;
    private TextView mTextView;
    private ImageView mImageView;

    private TextView mTvItemText1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_with_animation2);
        initExpandView();
    }

    public void initExpandView() {
        mLinearLayout = (LinearLayout) findViewById(R.id.layout_title);
        mTextView = (TextView) findViewById(R.id.textview_title);
        mImageView = (ImageView) findViewById(R.id.imageview_state);
        mExpandView = (ExpandableLinearLayout) findViewById(R.id.expandView);
        mLinearLayout.setClickable(true);
        mTvItemText1 = (TextView) findViewById(R.id.tv_item_text1);
        mLinearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mExpandView.isExpand()) {
                    mExpandView.collapse();
                } else {
                    mExpandView.expand();
                }
                updateExpanViewStatus();
            }
        });
        updateExpanViewStatus();

        mExpandView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTvItemText1.setText("在这里，你可以用\"pull\"命令把\"origin\"分支上的修改拉下来并且和你的修改合并； 结果看起来就像一个新的\"合并的提交\"(merge commit): ... (这些补丁放到\".git/rebase\"目录另外，我们在使用git pull命令的时候，可以使用--rebase参数，即git pull --rebase,这里 表示 把你的本地当前分支里的每个提交(commit)取消掉，并且把它们临时");
            }
        }, 3000);
    }

    private void updateExpanViewStatus() {
        if (mExpandView.isExpand()) {
            mTextView.setText("点击向上收叠");
            mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_drawer));
        } else {
            mTextView.setText("点击向下展开");
            mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_album));
        }
    }

}
