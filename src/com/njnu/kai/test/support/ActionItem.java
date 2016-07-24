package com.njnu.kai.test.support;


import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * 弹出式列表数据项
 * @version 2.0.0
 * @since 2012-6-18
 */
public class ActionItem {

	private int mId;
    private int mIconRes;
    private CharSequence mSubtitle;
    private CharSequence mTitle;
    private Drawable mIcon;
    private Object mTag;
    private int mCount;

    private Resources mResources = BaseApplication.getApp().getResources();

	/**
	 * Constructor
	 * @param id 菜单ID
	 * @param iconRes 图标资源ID
	 * @param titleRes 文字
	 */
	public ActionItem(int id, int iconRes, int titleRes) {
		mId = id;

        if (iconRes != 0) {
            mIconRes = iconRes;
            mIcon = mResources.getDrawable(iconRes);
        }

        if (titleRes != 0) {
            mTitle = mResources.getText(titleRes, "");
        }
    }

    /**
     * Constructor
     * @param id 菜单ID
     * @param icon 图标资源ID
     * @param titleRes 文字
     */
    public ActionItem(int id, Drawable icon, int titleRes) {
        mId = id;
        mIcon = icon;

        if (titleRes != 0) {
            mTitle = getResources().getText(titleRes);
        }
    }

    /**
     * Constructor
     * @param id 菜单ID
     * @param iconRes 图标资源ID
     * @param title 文字
     */
    public ActionItem(int id, int iconRes, CharSequence title) {
        mId = id;
        mTitle = title;

        if (iconRes != 0) {
            mIcon = getResources().getDrawable(iconRes);
        }
    }

    /**
     * Constructor
     * @param id 菜单ID
     * @param iconRes 图标资源ID
     * @param title 文字
     * @param subTitle 子标题
     */
    public ActionItem(int id, int iconRes, CharSequence title, CharSequence subTitle) {
        this(id, iconRes, title);
        mSubtitle = subTitle;
    }

    /**
     * Constructor
     * @param id 菜单ID
     * @param icon 图标资源ID
     * @param title 文字
     */
    public ActionItem(int id, Drawable icon, CharSequence title) {
        mId = id;
        mIcon = icon;
        mTitle = title;
    }

    /**
     * 获取标题文本
     * @return 获取标题文本
     */
    public CharSequence getTitle() {
        return mTitle;
    }

    /**
     * @param titleResId 标题的ID
     * @return ActionItem
     * @param <T> ActionItem子类
     */
    public <T extends ActionItem> T setTitle(int titleResId) {
        if (titleResId != 0) {
            mTitle = getResources().getText(titleResId);
        }
        return (T)this;
    }

    /**
     * 设置标题
     * @param title 标题文本
     * @return ActionItem
     * @param <T> ActionItem子类
     */
    public <T extends ActionItem> T setTitle(CharSequence title) {
        mTitle = title;
        return (T)this;
    }

	/**
	 * @return 该菜单项的ID
	 */
	public int getId() {
		return mId;
	}

	/**
	 * @param aId 菜单项的ID
     * @return ActionItem
     * @param <T> ActionItem子类
	 */
	public <T extends ActionItem> T setId(int aId) {
		mId = aId;
        return (T)this;
	}

    /**
     * 设置tag信息
     * @param data 数据
     * @return ActionItem
     * @param <T> ActionItem子类
     */
    public <T extends ActionItem> T setTag(Object data) {
        mTag = data;
        return (T)this;
    }

    /**
     * 获取tag
     * @return 信息
     */
    public Object getTag() {
        return mTag;
    }

	/**
	 * @param iconResId 图标的ID
     * @return ActionItem
     * @param <T> ActionItem子类
	 */
	public <T extends ActionItem> T setIcon(int iconResId) {
		if (iconResId != 0) {
            mIcon = getResources().getDrawable(iconResId);
        }
        return (T)this;
	}

    /**
     * 设置图标
     * @param drawable 图标
     * @return ActionItem
     * @param <T> ActionItem子类
     */
    public <T extends ActionItem> T setIcon(Drawable drawable) {
        mIcon = drawable;
        return (T)this;
    }

    /**
     * 获取图标
     * @return 获取图标
     */
    public Drawable getIcon() {
        return mIcon;
    }

    public int getSubItemCount() {
        return mCount;
    }

    /**
     * @param count 要设置的数量
     */
    public void setCount(int count) {
        mCount = count;
        if (mSubtitle == null) {
            mSubtitle = Integer.toString(count);
        }
    }

    /**
     * 设置子标题资源
     * @param subtitleResId 子标题资源
     * @return ActionItem
     * @param <T> ActionItem子类
     */
    public <T extends ActionItem> T setSubtitle(int subtitleResId) {
        if (subtitleResId != 0) {
            mSubtitle = getResources().getText(subtitleResId);
        }
        return (T)this;
    }

    /**
     * 设置子标题
     * @param subtitle 子标题
     * @return ActionItem
     * @param <T> ActionItem子类
     */
    public <T extends ActionItem> T setSubtitle(CharSequence subtitle) {
        mSubtitle = subtitle;
        return (T)this;
    }

    /**
     * 获取子标题
     * @return 子标题
     */
    public CharSequence getSubTitle() {
        return mSubtitle;
    }

    /**
     * 获取资源管理对象
     * @return 资源管理对象
     */
    public Resources getResources() {
        return mResources;
    }

    /**
     * @return 返回图标的resid
     */
    public int getIconRes() {
        return mIconRes;
    }

    @Override
    public String toString() {
        return mTitle.toString();
    }

    /**
     * 菜单点击监听
     * @author zhenhui.chen
     * @version 2.0.0
     * @since 2012-6-18
     */
    public static interface OnItemClickListener {
        /**
         * @param mi 点击的菜单项
         * @param position 位置
         */
        public void onItemClick(ActionItem mi, int position);
    }
}
