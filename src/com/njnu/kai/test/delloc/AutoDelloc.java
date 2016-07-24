package com.njnu.kai.test.delloc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.njnu.kai.test.support.LogUtils;
import com.njnu.kai.test.support.SDKVersionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-11-28
 */
final public class AutoDelloc {

    private static final String TAG = "AutoDelloc";

    private static final ArrayList<String> INNER_CLASS_PREFIX_LIST;

    private static HashMap<Object, Integer> sRetainMap = new HashMap<Object, Integer>();
    private static ArrayList<Object> sObjToBeReleased = new ArrayList<Object>();

    static {
        INNER_CLASS_PREFIX_LIST = new ArrayList<String>();
        INNER_CLASS_PREFIX_LIST.add("android.");
        INNER_CLASS_PREFIX_LIST.add("java.");
        INNER_CLASS_PREFIX_LIST.add("org.");
        INNER_CLASS_PREFIX_LIST.add("javax.");
        INNER_CLASS_PREFIX_LIST.add("com.android.");
        INNER_CLASS_PREFIX_LIST.add("junit.");
        INNER_CLASS_PREFIX_LIST.add("dalvik.");
    }

    private static void innerAutoDelloc(Object object) {
        if (sRetainMap.containsKey(object)) {
            sObjToBeReleased.add(object);
        } else {
            Class<?> aClass = object.getClass();
            try {
                while (!isInnerClass(aClass.getName())) {
                    innerSingleDelloc(object, aClass);
                    aClass = aClass.getSuperclass();
                }
            } catch (IllegalAccessException e) {
                LogUtils.e(TAG, "IllegalAccessException innerAutoDelloc obj=%s e=%s", object.getClass().getSimpleName(), e.toString());
            } catch (Exception e) {
                LogUtils.e(TAG, "Exception innerAutoDelloc obj=%s e=%s", object.getClass().getSimpleName(), e.toString());
            }
        }
    }

    private static void innerSingleDelloc(Object object, Class<?> clazz) throws IllegalAccessException {
        Field[] declaredFields = clazz.getDeclaredFields();
        if (declaredFields != null) {
            for (Field field : declaredFields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    Object obj = field.get(object);
                    if (obj != null) {
                        if (obj instanceof View) {
                            dellocView((View)obj);
                            field.set(object, null);
                        } else if (obj instanceof BaseAdapter) {
                            innerAutoDelloc(obj);
                            field.set(object, null);
                        } else if (obj instanceof Handler) {
                            ((Handler)obj).removeCallbacksAndMessages(null);
                            field.set(object, null);
                        } else if (!(obj instanceof Number || obj instanceof Boolean || obj instanceof Character || obj instanceof Enum)) {
                            field.set(object, null);
                        }
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void dellocView(View view) {
        view.setOnLongClickListener(null);
        view.setOnFocusChangeListener(null);
        if (view instanceof AdapterView) {
            AdapterView adapterView = (AdapterView)view;
            adapterView.setOnItemClickListener(null);
            adapterView.setOnItemLongClickListener(null);
            adapterView.setOnItemSelectedListener(null);
            int childCount = adapterView.getChildCount();
//            LogUtils.i(TAG, "innerAutoDelloc listView subViewCount=%d", childCount);
            for (int idx = 0; idx < childCount; ++idx) {
                View childView = adapterView.getChildAt(idx);
//                LogUtils.e(TAG, "innerAutoDelloc subViewTag=%s keyTag=%s", childView.getTag(), childView.getTag(R.id.btn_add));
                dellocView(childView);
            }
            adapterView.setAdapter(null);
        } else {
            view.setOnClickListener(null);
        }
        clearViewTag(view);
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView)view;
            imageView.setImageDrawable(null);
        }

        if (view.getBackground() != null) {
            if (!SDKVersionUtils.hasIceCreamSandwich()) {
                view.getBackground().setCallback(null);
            }
            if (SDKVersionUtils.hasJellyBean()) {
                view.setBackground(null);
            } else {
                view.setBackgroundDrawable(null);
            }
        }
    }

    private static void clearViewTag(View view) {
        view.setTag(null);
        try {
            if (SDKVersionUtils.hasHoneycomb()) {
                Field mKeyedTagsField = View.class.getDeclaredField("mKeyedTags");
                mKeyedTagsField.setAccessible(true);
                mKeyedTagsField.set(view, null);
            } else {
                Field mTagsField = View.class.getDeclaredField("sTags");
                mTagsField.setAccessible(true);
                Object obj = mTagsField.get(view);
                if (obj instanceof WeakHashMap) {
                    WeakHashMap weakHashMap = (WeakHashMap)obj;
                    obj = weakHashMap.get(view);
                    if (obj instanceof SparseArray) {
                        SparseArray sparseArray = (SparseArray)obj;
                        sparseArray.clear();
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "Exception clearViewTag e=%s", e.toString());
        }
    }

    /**
     * @param dialog dialog
     */
    public static void autoDelloc(Dialog dialog) {
        innerAutoDelloc(dialog);
    }

    /**
     * @param fragment fragment
     */
    public static void autoDelloc(Fragment fragment) {
        innerAutoDelloc(fragment);
    }

    /**
     * @param activity activity
     */
    public static void autoDelloc(Activity activity) {
        innerAutoDelloc(activity);
    }

    private static boolean isInnerClass(String className) {
        for (String classPrefix : INNER_CLASS_PREFIX_LIST) {
            if (className.startsWith(classPrefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param object 暂时保留的某object
     */
    public static void retain(Object object) {
        if (object instanceof Activity || object instanceof Fragment || object instanceof Dialog) {
            Integer integer = sRetainMap.get(object);
            int retainCount = 1;
            if (integer != null) {
                retainCount += integer;
            }
            sRetainMap.put(object, retainCount);
        }
    }

    /**
     * @param object 暂时保留的某object
     */
    public static void release(Object object) {
        if (object instanceof Activity || object instanceof Fragment || object instanceof Dialog) {
            Integer integer = sRetainMap.get(object);
            int retainCount = 0;
            if (integer != null) {
                retainCount = integer - 1;
            }
            if (retainCount <= 0) {
                sRetainMap.remove(object);
                int idx = sObjToBeReleased.indexOf(object);
                if (idx >= 0) {
                    sObjToBeReleased.remove(idx);
                    innerAutoDelloc(object);
                }
            } else {
                sRetainMap.put(object, retainCount);
            }
        }
    }
}
