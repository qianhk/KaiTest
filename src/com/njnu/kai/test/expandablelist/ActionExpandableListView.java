package com.njnu.kai.test.expandablelist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

/**
 * A more specific expandable listview in which the expandable area
 * consist of some buttons which are context actions for the item itself.
 *
 * It handles event binding for those buttons and allow for adding
 * a listener that will be invoked if one of those buttons are pressed.
 *
 * @author tjerkw.wolterink
 * @version 1.0.0
 */
public class ActionExpandableListView extends ItemExpandableListView {
	private OnActionClickListener mListener;
	private int[] mButtonIds = null;

    /**
     *
     * @param context Context
     */
	public ActionExpandableListView(Context context) {
		super(context);
	}

    /**
     *
     * @param context Context
     * @param attrs AttributeSet
     */
	public ActionExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
	}

    /**
     *
     * @param context Context
     * @param attrs AttributeSet
     * @param defStyle int
     */
	public ActionExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

    /**
     *
     * @param listener OnActionClickListener
     * @param buttonIds ids
     */
	public void setItemActionListener(OnActionClickListener listener, int ... buttonIds) {
		this.mListener = listener;
		this.mButtonIds = buttonIds;
	}

	/**
	 * Interface for callback to be invoked whenever an action is clicked in
	 * the expandle area of the list item.
	 */
	public interface OnActionClickListener {
		/**
		 * Called when an action item is clicked.
		 *
		 * @param itemView the view of the list item
		 * @param clickedView the view clicked
		 * @param position the position in the listview
		 */
		public void onClick(View itemView, View clickedView, int position);
	}

    /**
     * 重载父类setAdapter方法，不能调用此方法
     * @param adapter ListAdapter
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
//        throw new IllegalStateException("DO NOT use this method, please use setAdapter(ListAdapter adapter, "
//                + "int toggleButtonId, int expandableViewId) instead.");
        setAdapter(adapter, 0, 0);
    }

    /**
     * 最好调用此方法，可以自己设置右键按钮的id和菜单布局id
     * @param adapter ListAdapter
     * @param toggleButtonId 右键按钮的id
     * @param expandableViewId 菜单布局id
     */
    @Override
    public void setAdapter(ListAdapter adapter, int toggleButtonId, int expandableViewId) {
        super.setAdapter(new WrapperListAdapterImpl(adapter) {
            @Override
            public View getView(final int position, View view, ViewGroup viewGroup) {
                final View listView = mWrapped.getView(position, view, viewGroup);
                // add the action listeners
                if (mButtonIds != null && listView != null) {
                    for (int id : mButtonIds) {
                        View buttonView = listView.findViewById(id);
                        if (buttonView != null) {
                            buttonView.findViewById(id).setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (mListener != null) {
                                        mListener.onClick(listView, view, position);
                                    }
                                }
                            });
                        }
                    }
                }
                return listView;
            }
        }, toggleButtonId, expandableViewId);
    }
}
