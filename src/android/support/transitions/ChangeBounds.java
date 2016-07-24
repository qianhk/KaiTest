/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.transitions;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.transitions.utils.*;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

/**
 * This transition captures the layout bounds of target views before and after
 * the scene change and animates those changes during the transition.
 * <p/>
 * <p>A ChangeBounds transition can be described in a resource file by using the
 * tag <code>changeBounds</code>, along with the other standard
 * attributes of {@link android.support.transitions.R.styleable#Transition}.</p>
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ChangeBounds extends Transition {

    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";
    private static final String PROPNAME_PARENT = "android:changeBounds:parent";
    private static final String PROPNAME_WINDOW_X = "android:changeBounds:windowX";
    private static final String PROPNAME_WINDOW_Y = "android:changeBounds:windowY";
    private static final String[] sTransitionProperties = {
            PROPNAME_BOUNDS,
            PROPNAME_PARENT,
            PROPNAME_WINDOW_X,
            PROPNAME_WINDOW_Y
    };

    int[] tempLocation = new int[2];
    boolean mResizeClip = false;
    boolean mReparent = false;
    private static final String LOG_TAG = "ChangeBounds";

    private static RectEvaluator sRectEvaluator;
    private static DrawableOriginProperty sDrawableOriginProperty;

    public ChangeBounds() {}

    public ChangeBounds(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    public void setResizeClip(boolean resizeClip) {
        mResizeClip = resizeClip;
    }

    /**
     * Setting this flag tells ChangeBounds to track the before/after parent
     * of every view using this transition. The flag is not enabled by
     * default because it requires the parent instances to be the same
     * in the two scenes or else all parents must use ids to allow
     * the transition to determine which parents are the same.
     *
     * @param reparent true if the transition should track the parent
     *                 container of target views and animate parent changes.
     * @deprecated Use {@link android.transition.ChangeTransform} to handle
     * android.support.transitions between different parents.
     */
    public void setReparent(boolean reparent) {
        mReparent = reparent;
    }

    private void captureValues(TransitionValues values) {
        View view = values.view;
        if (ViewUtils.isLaidOut(view, false) || view.getWidth() != 0 || view.getHeight() != 0) {
            values.values.put(PROPNAME_BOUNDS, new Rect(view.getLeft(), view.getTop(),
                    view.getRight(), view.getBottom()));
            values.values.put(PROPNAME_PARENT, values.view.getParent());
            if (mReparent) {
                values.view.getLocationInWindow(tempLocation);
                values.values.put(PROPNAME_WINDOW_X, tempLocation[0]);
                values.values.put(PROPNAME_WINDOW_Y, tempLocation[1]);
            }
        }
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private boolean parentMatches(View startParent, View endParent) {
        boolean parentMatches = true;
        if (mReparent) {
            TransitionValues endValues = getMatchedTransitionValues(startParent, true);
            if (endValues == null) {
                parentMatches = startParent == endParent;
            } else {
                parentMatches = endParent == endValues.view;
            }
        }
        return parentMatches;
    }

    @Override
    public Animator createAnimator(final ViewGroup sceneRoot, TransitionValues startValues,
                                   TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        if (sRectEvaluator == null) {
            sRectEvaluator = new RectEvaluator();
        }
        Map<String, Object> startParentVals = startValues.values;
        Map<String, Object> endParentVals = endValues.values;
        ViewGroup startParent = (ViewGroup) startParentVals.get(PROPNAME_PARENT);
        ViewGroup endParent = (ViewGroup) endParentVals.get(PROPNAME_PARENT);
        if (startParent == null || endParent == null) {
            return null;
        }
        final View view = endValues.view;
        if (parentMatches(startParent, endParent)) {
            Rect startBounds = (Rect) startValues.values.get(PROPNAME_BOUNDS);
            Rect endBounds = (Rect) endValues.values.get(PROPNAME_BOUNDS);
            int startLeft = startBounds.left;
            int endLeft = endBounds.left;
            int startTop = startBounds.top;
            int endTop = endBounds.top;
            int startRight = startBounds.right;
            int endRight = endBounds.right;
            int startBottom = startBounds.bottom;
            int endBottom = endBounds.bottom;
            int startWidth = startRight - startLeft;
            int startHeight = startBottom - startTop;
            int endWidth = endRight - endLeft;
            int endHeight = endBottom - endTop;
            int numChanges = 0;
            if ((startWidth != 0 && startHeight != 0) || (endWidth != 0 && endHeight != 0)) {
                if (startLeft != endLeft || startTop != endTop) ++numChanges;
                if (startRight != endRight || startBottom != endBottom) ++numChanges;
            }
            if (numChanges > 0) {
                if (!mResizeClip) {
                    Animator anim;
                    if (startWidth == endWidth && startHeight == endHeight &&
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        view.offsetLeftAndRight(startLeft - view.getLeft());
                        view.offsetTopAndBottom(startTop - view.getTop());
                        anim = AnimatorUtils.ofInt(this, view, new HorizontalOffsetProperty(),
                                new VerticalOffsetProperty(), 0, 0, endLeft - startLeft, endTop - startTop);
                    } else {
                        if (startLeft != endLeft) view.setLeft(startLeft);
                        if (startTop != endTop) view.setTop(startTop);
                        if (startRight != endRight) view.setRight(startRight);
                        if (startBottom != endBottom) view.setBottom(startBottom);
                        ObjectAnimator topLeftAnimator = AnimatorUtils.ofInt(this, view, "left", "top",
                                startLeft, startTop, endLeft, endTop);
                        ObjectAnimator bottomRightAnimator = AnimatorUtils.ofInt(this, view, "right", "bottom",
                                startRight, startBottom, endRight, endBottom);
                        anim = TransitionUtils.mergeAnimators(topLeftAnimator,
                                bottomRightAnimator);
                    }
                    if (view.getParent() instanceof ViewGroup) {
                        final ViewGroup parent = (ViewGroup) view.getParent();
                        ViewGroupUtils.suppressLayout(parent, true);
                        TransitionListener transitionListener = new TransitionListenerAdapter() {
                            boolean mCanceled = false;

                            @Override
                            public void onTransitionCancel(Transition transition) {
                                ViewGroupUtils.suppressLayout(parent, false);
                                mCanceled = true;
                            }

                            @Override
                            public void onTransitionEnd(Transition transition) {
                                if (!mCanceled) {
                                    ViewGroupUtils.suppressLayout(parent, false);
                                }
                            }

                            @Override
                            public void onTransitionPause(Transition transition) {
                                ViewGroupUtils.suppressLayout(parent, false);
                            }

                            @Override
                            public void onTransitionResume(Transition transition) {
                                ViewGroupUtils.suppressLayout(parent, true);
                            }
                        };
                        addListener(transitionListener);
                    }
                    return anim;
                } else {
                    if (startWidth != endWidth) view.setRight(endLeft +
                            Math.max(startWidth, endWidth));
                    if (startHeight != endHeight) view.setBottom(endTop +
                            Math.max(startHeight, endHeight));
                    // TODO: don't clobber TX/TY
                    if (startLeft != endLeft) view.setTranslationX(startLeft - endLeft);
                    if (startTop != endTop) view.setTranslationY(startTop - endTop);
                    // Animate location with translationX/Y and size with clip bounds
                    float transXDelta = endLeft - startLeft;
                    float transYDelta = endTop - startTop;
                    int widthDelta = endWidth - startWidth;
                    int heightDelta = endHeight - startHeight;
                    numChanges = 0;
                    if (transXDelta != 0) numChanges++;
                    if (transYDelta != 0) numChanges++;
                    if (widthDelta != 0 || heightDelta != 0) numChanges++;
                    ObjectAnimator translationAnimator = AnimatorUtils.ofFloat(this, view,
                            "translationX", "translationY", 0, 0, transXDelta, transYDelta);
                    ObjectAnimator clipAnimator = null;
                    if (widthDelta != 0 || heightDelta != 0) {
                        Rect tempStartBounds = new Rect(0, 0, startWidth, startHeight);
                        Rect tempEndBounds = new Rect(0, 0, endWidth, endHeight);
                        clipAnimator = ObjectAnimator.ofObject(view, "clipBounds", sRectEvaluator,
                                tempStartBounds, tempEndBounds);
                    }
                    Animator anim = TransitionUtils.mergeAnimators(translationAnimator,
                            clipAnimator);
                    if (view.getParent() instanceof ViewGroup) {
                        final ViewGroup parent = (ViewGroup) view.getParent();
                        ViewGroupUtils.suppressLayout(parent, true);
                        TransitionListener transitionListener = new TransitionListenerAdapter() {
                            boolean mCanceled = false;

                            @Override
                            public void onTransitionCancel(Transition transition) {
                                ViewGroupUtils.suppressLayout(parent, false);
                                mCanceled = true;
                            }

                            @Override
                            public void onTransitionEnd(Transition transition) {
                                if (!mCanceled) {
                                    ViewGroupUtils.suppressLayout(parent, false);
                                }
                            }

                            @Override
                            public void onTransitionPause(Transition transition) {
                                ViewGroupUtils.suppressLayout(parent, false);
                            }

                            @Override
                            public void onTransitionResume(Transition transition) {
                                ViewGroupUtils.suppressLayout(parent, true);
                            }
                        };
                        addListener(transitionListener);
                    }
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            ViewUtils.setClipBounds(view, null);
                        }
                    });
                    return anim;
                }
            }
        } else {
            int startX = (Integer) startValues.values.get(PROPNAME_WINDOW_X);
            int startY = (Integer) startValues.values.get(PROPNAME_WINDOW_Y);
            int endX = (Integer) endValues.values.get(PROPNAME_WINDOW_X);
            int endY = (Integer) endValues.values.get(PROPNAME_WINDOW_Y);
            // TODO: also handle size changes: check bounds and animate size changes
            if (startX != endX || startY != endY) {
                sceneRoot.getLocationInWindow(tempLocation);
                Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);
                final BitmapDrawable drawable = new BitmapDrawable(
                        sceneRoot.getContext().getResources(), bitmap);
                final float transitionAlpha = ViewUtils.getTransitionAlpha(view);
                ViewUtils.setTransitionAlpha(view, 0);
                ViewOverlayUtils.addOverlay(sceneRoot, drawable);
                ObjectAnimator anim;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (sDrawableOriginProperty == null) {
                        sDrawableOriginProperty = new DrawableOriginProperty();
                    }
                    Path topLeftPath = getPathMotion().getPath(startX - tempLocation[0],
                            startY - tempLocation[1], endX - tempLocation[0], endY - tempLocation[1]);
                    PropertyValuesHolder origin = AnimatorUtils.pvhOfObject(
                            sDrawableOriginProperty, null, topLeftPath);
                    anim = ObjectAnimator.ofPropertyValuesHolder(drawable, origin);
                } else {
                    Rect startBounds1 = new Rect(startX - tempLocation[0], startY - tempLocation[1],
                            startX - tempLocation[0] + view.getWidth(),
                            startY - tempLocation[1] + view.getHeight());
                    Rect endBounds1 = new Rect(endX - tempLocation[0], endY - tempLocation[1],
                            endX - tempLocation[0] + view.getWidth(),
                            endY - tempLocation[1] + view.getHeight());
                    anim = ObjectAnimator.ofObject(drawable, "bounds",
                            sRectEvaluator, startBounds1, endBounds1);
                }
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewOverlayUtils.removeOverlay(sceneRoot, drawable);
                        ViewUtils.setTransitionAlpha(view, transitionAlpha);
                    }
                });
                return anim;
            }
        }
        return null;
    }

    private abstract static class OffsetProperty extends IntProperty<View> {
        int mPreviousValue;

        public OffsetProperty(String name) {
            super(name);
        }

        @Override
        public void setValue(View view, int value) {
            int offset = value - mPreviousValue;
            offsetBy(view, offset);
            mPreviousValue = value;
        }

        @Override
        public Integer get(View object) {
            return null;
        }

        protected abstract void offsetBy(View view, int by);
    }

    private static class HorizontalOffsetProperty extends OffsetProperty {
        public HorizontalOffsetProperty() {
            super("offsetLeftAndRight");
        }

        @Override
        protected void offsetBy(View view, int by) {
            view.offsetLeftAndRight(by);
        }
    }

    private static class VerticalOffsetProperty extends OffsetProperty {
        public VerticalOffsetProperty() {
            super("offsetTopAndBottom");
        }

        @Override
        protected void offsetBy(View view, int by) {
            view.offsetTopAndBottom(by);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static class DrawableOriginProperty extends Property<Drawable, PointF> {
        private Rect mBounds = new Rect();

        private DrawableOriginProperty() {
            super(PointF.class, "boundsOrigin");
        }

        @Override
        public void set(Drawable object, PointF value) {
            object.copyBounds(mBounds);
            mBounds.offsetTo(Math.round(value.x), Math.round(value.y));
            object.setBounds(mBounds);
        }

        @Override
        public PointF get(Drawable object) {
            object.copyBounds(mBounds);
            return new PointF(mBounds.left, mBounds.top);
        }
    }

}
