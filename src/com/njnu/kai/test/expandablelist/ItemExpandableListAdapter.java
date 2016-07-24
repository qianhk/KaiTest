package com.njnu.kai.test.expandablelist;

import android.view.View;
import android.widget.ListAdapter;

/**
 * ListAdapter that adds sliding functionality to a list.
 * Uses R.id.expandalbe_toggle_button and R.id.expandable id's if no
 * ids are given in the contructor.
 *
 * @author tjerkw.wolterink
 * @version 1.0.0
 */
public class ItemExpandableListAdapter extends AbstractExpandableListAdapter {
	private int mToggleButtonId;
	private int mExpandableViewId;

    /**
     *
     * @param wrapped ListAdapter
     * @param toggleButtonId button id
     * @param expandableViewId view id
     */
	public ItemExpandableListAdapter(ListAdapter wrapped, int toggleButtonId, int expandableViewId) {
		super(wrapped);
        if (toggleButtonId < 0) {
            throw new IllegalArgumentException("toggleButtonId can NOT be negative");
        }
        if (expandableViewId < 0) {
            throw new IllegalArgumentException("expandableViewId can NOT be negative");
        }
        this.mToggleButtonId = toggleButtonId;
        this.mExpandableViewId = expandableViewId;
    }

	@Override
	public View getExpandToggleButton(View parent) {
		return mToggleButtonId > 0 ? parent.findViewById(mToggleButtonId) : null;
	}

	@Override
	public View getExpandableView(View parent) {
		return mExpandableViewId > 0 ? parent.findViewById(mExpandableViewId) : null;
	}
}
