package com.njnu.kai.test.guideanimator;

import android.animation.*;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import com.njnu.kai.test.support.LogUtils;

import java.util.ArrayList;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-8-13
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GuideAnimatorShapeView extends View implements ValueAnimator.AnimatorUpdateListener{

    private Drawable mDrawable;

    public final ArrayList<ShapeHolder> balls = new ArrayList<ShapeHolder>();
    AnimatorSet animation = null;

    private static final int COLOR_BEGIN = 0x44FF8080;
    private static final int COLOR_END = 0x448080FF;

    public GuideAnimatorShapeView(Context context) {
        super(context);
        ValueAnimator colorAnim = ObjectAnimator.ofInt(this, "backgroundColor", COLOR_BEGIN, COLOR_END);
        colorAnim.setDuration(1000);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }

    public GuideAnimatorShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GuideAnimatorShapeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDrawableResourceId(int resId) {
        mDrawable = getResources().getDrawable(resId);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return false;
        }
        ShapeHolder newBall = addBall(event.getX(), event.getY());

        // Bouncing animation with squash and stretch
        float startY = newBall.getY();
        float endY = getHeight() - 50f;
        float h = (float)getHeight();
        float eventY = event.getY();
        int duration = (int)(2500 * ((h - eventY)/h));
        ValueAnimator bounceAnim = ObjectAnimator.ofFloat(newBall, "y", startY, endY);
        bounceAnim.addUpdateListener(this);
        bounceAnim.setDuration(duration);
        bounceAnim.setInterpolator(new AccelerateInterpolator());
        ValueAnimator squashAnim1 = ObjectAnimator.ofFloat(newBall, "x", newBall.getX(),
                newBall.getX() - 25f);
        squashAnim1.addUpdateListener(this);
        squashAnim1.setDuration(duration/4);
        squashAnim1.setRepeatCount(1);
        squashAnim1.setRepeatMode(ValueAnimator.REVERSE);
        squashAnim1.setInterpolator(new DecelerateInterpolator());
        ValueAnimator squashAnim2 = ObjectAnimator.ofFloat(newBall, "width", newBall.getWidth(),
                newBall.getWidth() + 50);
        squashAnim2.addUpdateListener(this);
        squashAnim2.setDuration(duration/4);
        squashAnim2.setRepeatCount(1);
        squashAnim2.setRepeatMode(ValueAnimator.REVERSE);
        squashAnim2.setInterpolator(new DecelerateInterpolator());
        ValueAnimator stretchAnim1 = ObjectAnimator.ofFloat(newBall, "y", endY,
                endY + 25f);
        stretchAnim1.addUpdateListener(this);
        stretchAnim1.setDuration(duration/4);
        stretchAnim1.setRepeatCount(1);
        stretchAnim1.setInterpolator(new DecelerateInterpolator());
        stretchAnim1.setRepeatMode(ValueAnimator.REVERSE);
        ValueAnimator stretchAnim2 = ObjectAnimator.ofFloat(newBall, "height",
                newBall.getHeight(), newBall.getHeight() - 25);
        stretchAnim2.addUpdateListener(this);
        stretchAnim2.setDuration(duration/4);
        stretchAnim2.setRepeatCount(1);
        stretchAnim2.setInterpolator(new DecelerateInterpolator());
        stretchAnim2.setRepeatMode(ValueAnimator.REVERSE);
        ValueAnimator bounceBackAnim = ObjectAnimator.ofFloat(newBall, "y", endY,
                startY);
        bounceBackAnim.addUpdateListener(this);
        bounceBackAnim.setDuration(duration);
        bounceBackAnim.setInterpolator(new DecelerateInterpolator());
        // Sequence the down/squash&stretch/up animations
        AnimatorSet bouncer = new AnimatorSet();
        bouncer.play(bounceAnim).before(squashAnim1);
        bouncer.play(squashAnim1).with(squashAnim2);
        bouncer.play(squashAnim1).with(stretchAnim1);
        bouncer.play(squashAnim1).with(stretchAnim2);
        bouncer.play(bounceBackAnim).after(stretchAnim2);

        // Fading animation - remove the ball when the animation is done
        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(newBall, "alpha", 1f, 0f);
        fadeAnim.addUpdateListener(this);
        fadeAnim.setDuration(250);
        fadeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Object target = ((ObjectAnimator)animation).getTarget();
                balls.remove(target);

            }
        });

        // Sequence the two animations to play one after the other
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(bouncer).before(fadeAnim);
        // Start the animation
        animatorSet.start();

        return true;
    }

    private ShapeHolder addBall(float x, float y) {
        OvalShape circle = new OvalShape();
        circle.resize(50f, 50f);
        ShapeDrawable drawable = new ShapeDrawable(circle);
        ShapeHolder shapeHolder = new ShapeHolder(drawable);
        shapeHolder.setX(x - 25f);
        shapeHolder.setY(y - 25f);
        int red = (int)(Math.random() * 255);
        int green = (int)(Math.random() * 255);
        int blue = (int)(Math.random() * 255);
        int color = 0xff000000 | red << 16 | green << 8 | blue;
        Paint paint = drawable.getPaint(); //new Paint(Paint.ANTI_ALIAS_FLAG);
        int darkColor = 0xff000000 | red/4 << 16 | green/4 << 8 | blue/4;
        RadialGradient gradient = new RadialGradient(37.5f, 12.5f,
                50f, color, darkColor, Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        shapeHolder.setPaint(paint);
        balls.add(shapeHolder);
        return shapeHolder;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < balls.size(); ++i) {
            ShapeHolder shapeHolder = balls.get(i);
            canvas.save();
            canvas.translate(shapeHolder.getX(), shapeHolder.getY());
            shapeHolder.getShape().draw(canvas);
            canvas.restore();
            LogUtils.d("TestGuide", "TestGuide onDraw Shape %d, x=%.0f y=%.0f", i, shapeHolder.getX(), shapeHolder.getY());
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }
}
