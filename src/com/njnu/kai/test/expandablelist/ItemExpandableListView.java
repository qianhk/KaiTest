package com.njnu.kai.test.expandablelist;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import com.njnu.kai.test.draglistview.DraggableListView;

/**
 * Simple subclass of listview which does nothing more than wrap
 * any ListAdapter in a SlideExpandalbeListAdapter
 * @author tjerkw.wolterink
 * @version 1.0.0
 */
class ItemExpandableListView extends DraggableListView {
	private ItemExpandableListAdapter mAdapter;

	public ItemExpandableListView(Context context) {
		super(context, null);
	}

	public ItemExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ItemExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
	}

	/**
	 * Collapses the currently open view.
	 *
	 * @return true if a view was collapsed, false if there was no open view.
	 */
	public boolean collapse() {
        return mAdapter != null && mAdapter.collapseLastOpen();
	}

    /**
     *
     * @param adapter ListAdapter
     * @param toggleButtonId button id
     * @param expandableViewId view id
     */
    public void setAdapter(ListAdapter adapter, int toggleButtonId, int expandableViewId) {
        this.mAdapter = new ItemExpandableListAdapter(adapter, toggleButtonId, expandableViewId);
        super.setAdapter(this.mAdapter);
    }

    /**
	 * Registers a OnItemClickListener for this listview which will
	 * expand the item by default. Any other OnItemClickListener will be overriden.
	 *
	 * To undo call setOnItemClickListener(null)
	 *
	 * Important: This method call setOnItemClickListener, so the value will be reset
	 */
	public void enableExpandOnItemClick() {
		this.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				ItemExpandableListAdapter adapter = (ItemExpandableListAdapter)getAdapter();
				adapter.getExpandToggleButton(view).performClick();
			}
		});
	}


	@Override
	public Parcelable onSaveInstanceState() {
		return mAdapter.onSaveInstanceState(super.onSaveInstanceState());
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof AbstractExpandableListAdapter.SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}

		AbstractExpandableListAdapter.SavedState ss = (AbstractExpandableListAdapter.SavedState)state;
		super.onRestoreInstanceState(ss.getSuperState());

        mAdapter.onRestoreInstanceState(ss);
	}
}
