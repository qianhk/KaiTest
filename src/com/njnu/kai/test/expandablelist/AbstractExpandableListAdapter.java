package com.njnu.kai.test.expandablelist;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.BitSet;

/**
 * Wraps a ListAdapter to give it expandable list view functionality.
 * The main_action_expandable_list thing it does is add a listener to the getToggleButton
 * which expands the getExpandableView for each list item.
 *
 * @author tjerkw.wolterink
 * @version 1.0.0
 */
public abstract class AbstractExpandableListAdapter extends WrapperListAdapterImpl {

    private static final int DEFAULT_ANIMATION_DURATION = 200;
	/**
	 * Reference to the last expanded list item.
	 * Since lists are recycled this might be null if
	 * though there is an expanded list item
	 */
	private View mLastOpen = null;
	/**
	 * The position of the last expanded list item.
	 * If -1 there is no list item expanded.
	 * Otherwise it points to the position of the last expanded list item
	 */
	private int mLastOpenPosition = -1;
	
	/**
	 * Default Animation duration
	 * Set animation duration with @see setAnimationDuration
	 */
	private int mAnimationDuration = DEFAULT_ANIMATION_DURATION;
	
	/**
	 * A list of positions of all list items that are expanded.
	 * Normally only one is expanded. But a mode to expand
	 * multiple will be added soon.
	 *
	 * If an item onj position x is open, its bit is set
	 */
	private BitSet mOpenItems = new BitSet();
	/**
	 * We remember, for each collapsable view its height.
	 * So we dont need to recalculate.
	 * The height is calculated just before the view is drawn.
	 */
	private final SparseIntArray mViewHeights = new SparseIntArray(10);

	/**
	* Will point to the ListView
	*/
	private ViewGroup mParent;

    /**
     *
     * @param wrapped ListAdapter
     */
	public AbstractExpandableListAdapter(ListAdapter wrapped) {
		super(wrapped);
	}

	private OnItemExpandCollapseListener mExpandCollapseListener;

	/**
	 * Sets a listener which gets call on item expand or collapse
	 * 
	 * @param listener
	 *            the listener which will be called when an item is expanded or
	 *            collapsed
	 */
	public void setItemExpandCollapseListener(
			OnItemExpandCollapseListener listener) {
        mExpandCollapseListener = listener;
	}

    /**
     *
     */
	public void removeItemExpandCollapseListener() {
        mExpandCollapseListener = null;
	}

	/**
	 * Interface for callback to be invoked whenever an item is expanded or
	 * collapsed in the list view.
	 */
	public interface OnItemExpandCollapseListener {
		/**
		 * Called when an item is expanded.
		 * 
		 * @param itemView
		 *            the view of the list item
		 * @param position
		 *            the position in the list view
		 */
		public void onExpand(View itemView, int position);

		/**
		 * Called when an item is collapsed.
		 * 
		 * @param itemView
		 *            the view of the list item
		 * @param position
		 *            the position in the list view
		 */
		public void onCollapse(View itemView, int position);

	}

	private void notifyExpandCollapseListener(int type, View view, int position) {
		if (mExpandCollapseListener != null) {
			if (type == ExpandCollapseAnimation.EXPAND) {
                mExpandCollapseListener.onExpand(view, position);
			} else if (type == ExpandCollapseAnimation.COLLAPSE) {
                mExpandCollapseListener.onCollapse(view, position);
			}
		}

	}


	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		this.mParent = viewGroup;
		view = mWrapped.getView(position, view, viewGroup);
		enableFor(view, position);
		return view;
	}

	/**
	 * This method is used to get the Button view that should
	 * expand or collapse the Expandable View.
	 * <br/>
	 * Normally it will be implemented as:
	 * <pre>
	 * return parent.findViewById(R.id.expand_toggle_button)
	 * </pre>
	 *
	 * A listener will be attached to the button which will
	 * either expand or collapse the expandable view
	 *
	 * @see #getExpandableView(android.view.View)
	 * @param parent the list view item
	 * @ensure return!=null
	 * @return a child of parent which is a button
	 */
	public abstract View getExpandToggleButton(View parent);

	/**
	 * This method is used to get the view that will be hidden
	 * initially and expands or collapse when the ExpandToggleButton
	 * is pressed @see getExpandToggleButton
	 * <br/>
	 * Normally it will be implemented as:
	 * <pre>
	 * return parent.findViewById(R.id.expandable)
	 * </pre>
	 *
	 * @see #getExpandToggleButton(android.view.View)
	 * @param parent the list view item
	 * @ensure return!=null
	 * @return a child of parent which is a view (or often ViewGroup)
	 *  that can be collapsed and expanded
	 */
	public abstract View getExpandableView(View parent);

	/**
	 * Gets the duration of the collapse animation in ms.
	 * Default is 330ms. Override this method to change the default.
	 *
	 * @return the duration of the anim in ms
	 */
	public int getAnimationDuration() {
		return mAnimationDuration;
	}
	/**
	 * Set's the Animation duration for the Expandable animation
     *
     * throws IllegalArgumentException if parameter is less than zero
	 * 
	 * @param duration The duration as an integer in MS (duration > 0)
	 */
	public void setAnimationDuration(int duration) {
		if (duration < 0) {
			throw new IllegalArgumentException("Duration is less than zero");
		}

        mAnimationDuration = duration;
	}
	/**
	 * Check's if any position is currently Expanded
	 * To collapse the open item @see collapseLastOpen
	 * 
	 * @return boolean True if there is currently an item expanded, otherwise false
	 */
	public boolean isAnyItemExpanded() {
		return (mLastOpenPosition != -1);
	}

	private void enableFor(View parent, int position) {
		View more = getExpandToggleButton(parent);
		View itemToolbar = getExpandableView(parent);
        if (more == null || itemToolbar == null) {
            return;
        }
		itemToolbar.measure(parent.getWidth(), parent.getHeight());

		enableFor(more, itemToolbar, position);
		itemToolbar.requestLayout();
	}


	private void enableFor(final View button, final View toolbar, final int position) {
		if (toolbar == mLastOpen && position != mLastOpenPosition) {
			// lastOpen is recycled, so its reference is false
            mLastOpen = null;
		}
		if (position == mLastOpenPosition) {
			// re reference to the last view
			// so when can animate it when collapsed
            mLastOpen = toolbar;
		}
		int height = mViewHeights.get(position, -1);
		if (height == -1) {
            mViewHeights.put(position, toolbar.getMeasuredHeight());
			updateExpandable(toolbar, position);
		} else {
			updateExpandable(toolbar, position);
		}

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {

				Animation a = toolbar.getAnimation();

				if (a != null && a.hasStarted() && !a.hasEnded()) {

					a.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							view.performClick();
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});

				} else {

                    toolbar.setAnimation(null);

					int type = toolbar.getVisibility() == View.VISIBLE
							? ExpandCollapseAnimation.COLLAPSE
							: ExpandCollapseAnimation.EXPAND;

					// remember the state
					if (type == ExpandCollapseAnimation.EXPAND) {
                        mOpenItems.set(position, true);
					} else {
                        mOpenItems.set(position, false);
					}
					// check if we need to collapse a different view
					if (type == ExpandCollapseAnimation.EXPAND) {
						if (mLastOpenPosition != -1 && mLastOpenPosition != position) {
							if (mLastOpen != null) {
								animateView(mLastOpen, ExpandCollapseAnimation.COLLAPSE);
                                notifyExpandCollapseListener(
										ExpandCollapseAnimation.COLLAPSE,
                                        mLastOpen, mLastOpenPosition);
							}
                            mOpenItems.set(mLastOpenPosition, false);
						}
                        mLastOpen = toolbar;
                        mLastOpenPosition = position;
					} else if (mLastOpenPosition == position) {
                        mLastOpenPosition = -1;
					}
					animateView(toolbar, type);
                    notifyExpandCollapseListener(type, toolbar, position);
				}
			}
		});
	}

	private void updateExpandable(View target, int position) {

		final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)target.getLayoutParams();
		if (mOpenItems.get(position)) {
			target.setVisibility(View.VISIBLE);
			params.bottomMargin = 0;
		} else {
			target.setVisibility(View.GONE);
			params.bottomMargin = 0 - mViewHeights.get(position);
		}
	}

	/**
	 * Performs either COLLAPSE or EXPAND animation on the target view
	 * @param target the view to animate
	 * @param type the animation type, either ExpandCollapseAnimation.COLLAPSE
	 *			 or ExpandCollapseAnimation.EXPAND
	 */
	private void animateView(final View target, final int type) {
		Animation anim = new ExpandCollapseAnimation(
				target,
				type
		);
		anim.setDuration(getAnimationDuration());
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

            }

			@Override
			public void onAnimationRepeat(Animation animation) {

            }

			@Override
			public void onAnimationEnd(Animation animation) {
				if (type == ExpandCollapseAnimation.EXPAND) {
					if (mParent instanceof ListView) {
						ListView listView = (ListView) mParent;
						int movement = target.getBottom();

						Rect r = new Rect();
						boolean visible = target.getGlobalVisibleRect(r);
						Rect r2 = new Rect();
						listView.getGlobalVisibleRect(r2);
						
						if (!visible) {
							listView.smoothScrollBy(movement, getAnimationDuration());
						} else {
							if (r2.bottom == r.bottom) {
								listView.smoothScrollBy(movement, getAnimationDuration());
							}
						}
					}
				}

			}
		});
		target.startAnimation(anim);
	}


	/**
	 * Closes the current open item.
	 * If it is current visible it will be closed with an animation.
	 *
	 * @return true if an item was closed, false otherwise
	 */
	public boolean collapseLastOpen() {
		if (isAnyItemExpanded()) {
			// if visible animate it out
			if (mLastOpen != null) {
				animateView(mLastOpen, ExpandCollapseAnimation.COLLAPSE);
			}
            mOpenItems.set(mLastOpenPosition, false);
            mLastOpenPosition = -1;
			return true;
		}
		return false;
	}

    /**
     *
     * @param parcelable Parcelable
     * @return Parcelable
     */
	public Parcelable onSaveInstanceState(Parcelable parcelable) {

		SavedState ss = new SavedState(parcelable);
		ss.mLastOpenPosition = this.mLastOpenPosition;
		ss.mOpenItems = this.mOpenItems;
		return ss;
	}

    /**
     *
     * @param state SavedState
     */
	public void onRestoreInstanceState(SavedState state) {

		if (state != null) {
			this.mLastOpenPosition = state.mLastOpenPosition;
			this.mOpenItems = state.mOpenItems;
		}
	}

	/**
	 * Utility methods to read and write a bitset from and to a Parcel
	 */
	private static BitSet readBitSet(Parcel src) {
		BitSet set = new BitSet();
		if (src == null) {
			return set;
		}
		int cardinality = src.readInt();


		for (int i = 0; i < cardinality; i++) {
			set.set(src.readInt());
		}

		return set;
	}

	private static void writeBitSet(Parcel dest, BitSet set) {
		int nextSetBit = -1;

		if (dest == null || set == null) {
			return; // at least dont crash
		}

		dest.writeInt(set.cardinality());

		while ((nextSetBit = set.nextSetBit(nextSetBit + 1)) != -1) {
			dest.writeInt(nextSetBit);
		}
	}

	/**
	 * The actual state class
	 */
	static class SavedState extends View.BaseSavedState {
		private BitSet mOpenItems = null;
		private int mLastOpenPosition = -1;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
            mLastOpenPosition = in.readInt();
            mOpenItems = readBitSet(in);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(mLastOpenPosition);
			writeBitSet(out, mOpenItems);
		}

		//required field that makes Parcelables from a Parcel
		public static final Creator<SavedState> CREATOR =
		new Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}
