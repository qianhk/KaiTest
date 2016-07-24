package android.support.transitions.utils;

import android.annotation.TargetApi;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.transitions.hidden.Crossfade;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;

public class ViewGroupOverlayUtils {

    interface ViewGroupOverlayUtilsImpl {
        void addOverlay(ViewGroup sceneRoot, View overlayView, int screenX, int screenY);

        void removeOverlay(ViewGroup sceneRoot, View overlayView);

        void moveViewInOverlay(ViewGroup sceneRoot, View overlayView, int screenX, int screenY);

        void initializeOverlay(View sceneRoot);

        int[] getLocationOnScreenOfOverlayView(ViewGroup sceneRoot, View overlayView);

        void addCrossfadeOverlay(boolean useParentOverlay, View view, int fadeBehavior,
                                 BitmapDrawable startDrawable, BitmapDrawable endDrawable);

        void removeCrossfadeOverlay(boolean useParentOverlay, View view, int fadeBehavior,
                                    BitmapDrawable startDrawable, BitmapDrawable endDrawable);
    }

    static class BaseViewGroupOverlayUtilsImpl implements ViewGroupOverlayUtilsImpl {

        @Override
        public void addOverlay(ViewGroup sceneRoot, View overlayView, int screenX, int screenY) {
            ViewOverlayPreJellybean viewOverlay = ViewOverlayPreJellybean.getOverlay(sceneRoot);
            viewOverlay.addView(overlayView, screenX, screenY);
        }

        @Override
        public void removeOverlay(ViewGroup sceneRoot, View overlayView) {
            ViewOverlayPreJellybean viewOverlay = ViewOverlayPreJellybean.getOverlay(sceneRoot);
            viewOverlay.removeView(overlayView);
        }

        @Override
        public void moveViewInOverlay(ViewGroup sceneRoot, View overlayView, int screenX, int screenY) {
            ViewOverlayPreJellybean viewOverlay = ViewOverlayPreJellybean.getOverlay(sceneRoot);
            viewOverlay.moveView(overlayView, screenX, screenY);
        }

        @Override
        public void initializeOverlay(View sceneRoot) {
            ViewOverlayPreJellybean.getOverlay(sceneRoot);
        }

        @Override
        public int[] getLocationOnScreenOfOverlayView(ViewGroup sceneRoot, View overlayView) {
            int[] location = new int[2];
            overlayView.getLocationOnScreen(location);
            return location;
        }

        @Override
        public void addCrossfadeOverlay(boolean useParentOverlay, View view, int fadeBehavior,
                                        BitmapDrawable startDrawable, BitmapDrawable endDrawable) {
            //TODO ViewOverlay
        }

        @Override
        public void removeCrossfadeOverlay(boolean useParentOverlay, View view, int fadeBehavior,
                                           BitmapDrawable startDrawable, BitmapDrawable endDrawable) {
            //TODO ViewOverlay
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    static class JellyBeanMR2ViewGroupUtilsImpl implements ViewGroupOverlayUtilsImpl {
        @Override
        public void addOverlay(ViewGroup sceneRoot, View overlayView, int screenX, int screenY) {
            moveViewInOverlay(sceneRoot, overlayView, screenX, screenY);
            sceneRoot.getOverlay().add(overlayView);
        }

        @Override
        public void removeOverlay(ViewGroup sceneRoot, View overlayView) {
            sceneRoot.getOverlay().remove(overlayView);
        }

        @Override
        public void moveViewInOverlay(ViewGroup sceneRoot, View overlayView, int screenX, int screenY) {
            if (screenX != 0 && screenY != 0) {
                int[] loc = new int[2];
                sceneRoot.getLocationOnScreen(loc);
                overlayView.offsetLeftAndRight((screenX - loc[0]) - overlayView.getLeft());
                overlayView.offsetTopAndBottom((screenY - loc[1]) - overlayView.getTop());
            }
        }

        @Override
        public void initializeOverlay(View sceneRoot) {
            // do nothing
        }

        @Override
        public void addCrossfadeOverlay(boolean useParentOverlay, View view, int fadeBehavior,
                                        BitmapDrawable startDrawable, BitmapDrawable endDrawable) {
            ViewOverlay overlay = getViewOverlay(useParentOverlay, view);
            overlay.remove(startDrawable);
            if (fadeBehavior == Crossfade.FADE_BEHAVIOR_REVEAL) {
                overlay.remove(endDrawable);
            }
        }

        @Override
        public int[] getLocationOnScreenOfOverlayView(ViewGroup sceneRoot, View overlayView) {
            int[] location = new int[2];
            sceneRoot.getLocationOnScreen(location);
            location[0] += overlayView.getLeft();
            location[1] += overlayView.getTop();
            return location;
        }

        @Override
        public void removeCrossfadeOverlay(boolean useParentOverlay, View view, int fadeBehavior,
                                           BitmapDrawable startDrawable, BitmapDrawable endDrawable) {
            ViewOverlay overlay = getViewOverlay(useParentOverlay, view);
            if (fadeBehavior == Crossfade.FADE_BEHAVIOR_REVEAL) {
                overlay.add(endDrawable);
            }
            overlay.add(startDrawable);
        }

        private static ViewOverlay getViewOverlay(boolean useParentOverlay, View view) {
            return useParentOverlay ? ((ViewGroup) view.getParent()).getOverlay() : view.getOverlay();
        }

    }

    private static final ViewGroupOverlayUtilsImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            IMPL = new JellyBeanMR2ViewGroupUtilsImpl();
        } else {
            IMPL = new BaseViewGroupOverlayUtilsImpl();
        }
    }

    public static void addOverlay(ViewGroup sceneRoot, View overlayView, int screenX, int screenY) {
        if (overlayView != null) {
            IMPL.addOverlay(sceneRoot, overlayView, screenX, screenY);
        }
    }

    public static void initializeOverlay(View sceneRoot) {
        IMPL.initializeOverlay(sceneRoot);
    }

    public static void removeOverlay(ViewGroup sceneRoot, View overlayView) {
        if (overlayView != null) {
            IMPL.removeOverlay(sceneRoot, overlayView);
        }
    }

    public static void moveViewInOverlay(ViewGroup sceneRoot, View overlayView, int screenX, int screenY) {
        if (overlayView != null) {
            IMPL.moveViewInOverlay(sceneRoot, overlayView, screenX, screenY);
        }
    }

    public static int[] getLocationOnScreenOfOverlayView(ViewGroup sceneRoot, View overlayView) {
        if (overlayView != null) {
            return IMPL.getLocationOnScreenOfOverlayView(sceneRoot, overlayView);
        } else {
            return new int[2];
        }
    }

    public static void addCrossfadeOverlay(boolean useParentOverlay, View view, int fadeBehavior,
                             BitmapDrawable startDrawable, BitmapDrawable endDrawable) {
        IMPL.addCrossfadeOverlay(useParentOverlay, view, fadeBehavior, startDrawable, endDrawable);
    }

    public static void removeCrossfadeOverlay(boolean useParentOverlay, View view, int fadeBehavior,
                                BitmapDrawable startDrawable, BitmapDrawable endDrawable) {
        IMPL.removeCrossfadeOverlay(useParentOverlay, view, fadeBehavior, startDrawable, endDrawable);
    }
}
