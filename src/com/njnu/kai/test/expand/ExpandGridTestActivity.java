package com.njnu.kai.test.expand;

import android.os.Bundle;
import android.view.View;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.BaseActivity;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-9-25
 */
public class ExpandGridTestActivity extends BaseActivity {

    private ExpandSimpleGridView mExpandSimpleGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expand_grid_main_layout);
        mExpandSimpleGridView = (ExpandSimpleGridView) findViewById(R.id.layout_expand_grid_content_layout);
        mExpandSimpleGridView.setNumColumns(3);
        mExpandSimpleGridView.setMaxShowCount(3);
        findViewById(R.id.tv_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mExpandSimpleGridView.isExpanded()) {
                    mExpandSimpleGridView.collapse();
                } else {
                    mExpandSimpleGridView.expand();
                }
            }
        });

    }
}
