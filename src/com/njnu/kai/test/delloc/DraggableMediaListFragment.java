package com.njnu.kai.test.delloc;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.ToastUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-11-27
 */
public class DraggableMediaListFragment extends MediaListFragment {

    private ImageView mIvFooter;

    @Override
    protected View onCreateFooterView(LayoutInflater inflater) {
        mIvFooter = new ImageView(inflater.getContext());
        mIvFooter.setImageResource(R.drawable.ic_mv);
        mIvFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(v.getContext(), "footer " + v.toString());
            }
        });
        return mIvFooter;
    }
}
