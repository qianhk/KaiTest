package com.njnu.kai.test.delloc;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.ToastUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-11-27
 */
public class MediaListFragment extends AbsMediaListFragment {
    public TextView mTvTextView;
    private View mBtn1;
    private View mBtn2;
    private View mBtn3;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ToastUtils.showToast(v.getContext(), v.toString());
        }
    };

    @Override
    protected View onCreateHeaderView(LayoutInflater inflater) {
        View headerView = inflater.inflate(R.layout.header_medialist, null);
        mTvTextView = (TextView)headerView.findViewById(R.id.tv_title);
        mBtn1 = headerView.findViewById(R.id.btn_one);
        mBtn2 = headerView.findViewById(R.id.btn_Two);
        mBtn3 = headerView.findViewById(R.id.btn_Three);
        mBtn1.setOnClickListener(mOnClickListener);
        mBtn2.setOnClickListener(mOnClickListener);
        mBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(v.getContext(), "inner " + v.toString());
            }
        });
        mTvTextView.setText(getClass().getSimpleName());
        return headerView;
    }

    @Override
    protected View onCreateFooterView(LayoutInflater inflater) {
        return null;
    }
}
