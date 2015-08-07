package com.saphion.stencilweather.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.view.View;

import com.saphion.stencilweather.activities.SplashActivity;

import java.util.Random;

/**
 * Created by sachin on 6/8/15.
 */
public class SplashView extends View {

    private float sunState1;
    private float sunState2;
    private float sunState3;
    private float sunFinalState;
    private float height;
    private float width;
    private Paint mPaint;
    private Bitmap sunFlames;
    int angle = 0;
    SplashActivity mActivity;


    private RectF smileRect;
    private RectF groundRect;

    //sun dimens
    private float sunYCenter;
    private float sunXCenter;
    private float sunEyeDistance;
    private Paint mPaintSun;
    private Paint mPaintSmile;
    int sunDimensions;

    Paint clearPaint;


    public SplashView(Context context) {
        super(context);
    }

    public SplashView(SplashActivity mActivity, float height, float width, int tint, int background) {
        super(mActivity);
        this.height = height;
        this.width = width;
        this.mActivity = mActivity;

        int alpha = 160;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        ColorFilter tintColor = new LightingColorFilter(tint, 0);
        mPaint.setColorFilter(tintColor);
        mPaint.setColor(tint);
//        mPaint.setAlpha(alpha);

        //ground points
        float groundStartX = (float) (width * 0.05);
        float groundStartY = (float) (height * 0.79);
        float groundEndX = (float) (width * 0.95);
        float groundEndY = (float) (height * 0.80);

        groundRect = new RectF();
        groundRect.set(groundStartX, groundStartY, groundEndX, groundEndY);

        //sun
        sunDimensions = (int) (width * 0.30);

        sunFlames = flames(sunDimensions, sunDimensions, tint);


        //sun states
        sunState1 = groundEndY + sunDimensions / 10;
        sunState2 = groundEndY - sunDimensions / 8;
        sunState3 = groundEndY - sunDimensions / 6;
        sunFinalState = height / 3;

        sunYCenter = sunState1;
        sunXCenter = width / 2;
        sunEyeDistance = sunDimensions / 6;


        mPaintSun = new Paint();
        mPaintSun.setAntiAlias(true);
        mPaintSun.setColorFilter(tintColor);
        mPaintSun.setColor(tint);
        mPaintSun.setStyle(Paint.Style.STROKE);
        mPaintSun.setStrokeWidth((float) (width * 0.012));
        mPaintSun.setAlpha(alpha);

        mPaintSmile = new Paint();
        mPaintSmile.setAntiAlias(true);
        mPaintSmile.setColorFilter(tintColor);
        mPaintSmile.setColor(tint);
        mPaintSmile.setStrokeWidth((float) (width * 0.01));
        mPaintSmile.setStyle(Paint.Style.STROKE);
        mPaintSmile.setAlpha(alpha);
        mPaintSmile.setStrokeCap(Paint.Cap.ROUND);
        smileRect = new RectF();


        clearPaint = new Paint();
//        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        clearPaint.setColor(background);

        eyeState1 = sunXCenter - sunEyeDistance;
        eyeState2 = sunXCenter;
        eyeState3 = sunXCenter - sunEyeDistance / 2;

        currentEyeState = eyeState1;

        r = new Random();

    }

    float incYBy = 0;
    float timeSun = 0;
    float incXBy = 0;
    Random r;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        currentEyeState += incXBy;

        if (!eyeState2Complete && currentEyeState == eyeState1) {
            eyeHandler1.postDelayed(eyeRunnable1, 4000);
        }

        if (!eyeState2Complete && currentEyeState >= eyeState2 - 3) {
            eyeHandler2.postDelayed(eyeRunnable2, 1500);
            currentEyeState = eyeState2;
            incXBy = 0;
        }

        if (eyeState2Complete && currentEyeState >= eyeState3 - 3) {
            currentEyeState = eyeState3;
            eyeHandler1.removeCallbacks(eyeRunnable1);
            eyeHandler2.removeCallbacks(eyeRunnable2);
            incXBy = 0;
        }


        if (timeSun != 41)
            timeSun++;

        if (timeSun == 40) {
            incYBy = 10;
        }

        sunYCenter -= incYBy;

        if (sunYCenter <= sunState2 + 3 && sunYCenter >= sunState2 - 3) {
            if (!state1Complete) {
                incYBy = 0;
                sunHandler.postDelayed(new sunRunnable(0.2f), 150);
            }
            if (state1Complete && state3Complete) {
                sunHandler.postDelayed(new sunRunnable(12f), 2000);
            }
            state1Complete = true;
        }
        if (!state3Complete)
            if (sunYCenter <= sunState3 + 1 && sunYCenter >= sunState3 - 1) {
                incYBy = 0;
                sunHandler.postDelayed(new sunRunnable(-8f), 700);
                smileHandler.postDelayed(smileRunnable, 720);
            }

        if (sunYCenter <= sunFinalState + 10 && sunYCenter >= sunFinalState - 10) {
            incYBy = 0;
            mActivity.onAnimationComplete();
        }

        blink = blink == blinkAt ? 0 : blink + 1;

        smileRect.set(sunXCenter - sunEyeDistance / 2,
                sunYCenter + sunEyeDistance / 2,
                sunXCenter + sunEyeDistance / 2,
                sunYCenter + sunEyeDistance);

        canvas.save();

        canvas.rotate(angle, sunXCenter, sunYCenter);

        canvas.drawBitmap(sunFlames, sunXCenter - sunDimensions / 2,
                sunYCenter - sunDimensions / 2, mPaintSun);

        canvas.restore();

        if (blink != 0) {
            mPaintSun.setStyle(Paint.Style.FILL);
            canvas.drawCircle(currentEyeState, sunYCenter, sunDimensions / 40, mPaintSun);
            canvas.drawCircle(currentEyeState + sunEyeDistance, sunYCenter, sunDimensions / 40, mPaintSun);
        } else {
            blinkAt = (r.nextInt(60) + 40);
        }

        mPaintSun.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(sunXCenter, sunYCenter, sunDimensions / 3, mPaintSun);

        canvas.drawRoundRect(groundRect, 10, 10, mPaint);

        if(state3Complete)
            canvas.drawArc(smileRect,
                    10,
                    160,
                    false,
                    mPaintSmile);


        canvas.drawRect(0, groundRect.bottom, width, height, clearPaint);


        if (angle++ > 360)
            angle = 0;

        mHandler.postDelayed(runnable, 20);
    }

    //eyes
    float eyeState1;
    float eyeState2;
    float eyeState3;
    float currentEyeState;
    int blinkAt = 40;

    boolean state1Complete = false;
    boolean state3Complete = false;


    Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            invalidate();
        }
    };

    public class sunRunnable implements Runnable {

        private float yVal;

        public sunRunnable(float yVal) {

            this.yVal = yVal;
        }

        @Override
        public void run() {
            incYBy = yVal;
        }
    }

    int blink = 0;

    Handler sunHandler = new Handler();


    public Bitmap flames(int height, int width, int tint) {


        Bitmap circleBitmap = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(circleBitmap);

        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setColor(tint);

        RectF rectf = new RectF(width / 2 - width / 55, 0, width / 2 + width / 55, (float) (height * 0.1));

        for (int angle = 0; angle < 360; angle = angle + 45) {
            c.save();

            c.rotate(angle, width / 2, height / 2);

            c.drawRoundRect(rectf, 10, 10, mPaint);

            c.restore();
        }

        return circleBitmap;

    }

    Handler eyeHandler1 = new Handler();
    Runnable eyeRunnable1 = new Runnable() {
        @Override
        public void run() {
            incXBy = 5f;
        }
    };

    Handler eyeHandler2 = new Handler();
    Runnable eyeRunnable2 = new Runnable() {
        @Override
        public void run() {
            eyeState2Complete = true;
            incXBy = -5f;
        }
    };

    boolean eyeState2Complete = false;

    Handler smileHandler = new Handler();
    Runnable smileRunnable = new Runnable() {
        @Override
        public void run() {
            state3Complete = true;
        }
    };
}
