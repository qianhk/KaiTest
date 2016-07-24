package com.njnu.kai.test.expandablelist;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

/**
 * Implementation of a WrapperListAdapter interface
 * in which each method delegates to the wrapped adapter.
 *
 * Extend this class if you only want to change a
 * few methods of the wrapped adapter.
 *
 * The wrapped adapter is available to subclasses as the "wrapped" field.
 *
 * @author tjerk.wolterink
 * @version 7.6.0
 */
public abstract class WrapperListAdapterImpl extends BaseAdapter implements WrapperListAdapter {
    /**
     *
     */
	protected ListAdapter mWrapped;

    /**
     *
     * @param wrapped Adapter
     */
	public WrapperListAdapterImpl(ListAdapter wrapped) {
		this.mWrapped = wrapped;
	}

	@Override
	public ListAdapter getWrappedAdapter() {
		return mWrapped;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return mWrapped.areAllItemsEnabled();
	}

	@Override
	public boolean isEnabled(int i) {
		return mWrapped.isEnabled(i);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        mWrapped.registerDataSetObserver(dataSetObserver);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        mWrapped.unregisterDataSetObserver(dataSetObserver);
	}

	@Override
	public int getCount() {
		return mWrapped.getCount();
	}

	@Override
	public Object getItem(int i) {
		return mWrapped.getItem(i);
	}

	@Override
	public long getItemId(int i) {
		return mWrapped.getItemId(i);
	}

	@Override
	public boolean hasStableIds() {
		return mWrapped.hasStableIds();
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		return mWrapped.getView(position, view, viewGroup);
	}

	@Override
	public int getItemViewType(int i) {
		return mWrapped.getItemViewType(i);
	}

	@Override
	public int getViewTypeCount() {
		return mWrapped.getViewTypeCount();
	}

	@Override
	public boolean isEmpty() {
		return mWrapped.isEmpty();
	}
}
