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

/**
 * Created by sachin on 6/8/15.
 */
public class SplashView extends View {

    private float height;
    private float width;
    private Paint mPaint;
    private Bitmap sunFlames;
    int angle = 0;


    private RectF smileRect;
    private RectF groundRect;

    //sun dimens
    private float sunYCenter;
    private float sunXCenter;
    private float sunEyeDistance;
    private Paint mPaintSun;
    private Paint mPaintSmile;
    int sunDimensions;


    public SplashView(Context context) {
        super(context);
    }

    public SplashView(Context context, float height, float width, int tint, int background) {
        super(context);
        this.height = height;
        this.width = width;

        int alpha = 160;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        ColorFilter tintColor = new LightingColorFilter(tint, 0);
        mPaint.setColorFilter(tintColor);
        mPaint.setColor(tint);
        mPaint.setAlpha(alpha);

        //ground points
        float groundStartX = (float) (width * 0.20);
        float groundStartY = (float) (height * 0.79);
        float groundEndX = (float) (width * 0.80);
        float groundEndY = (float) (height * 0.80);

        groundRect = new RectF();
        groundRect.set(groundStartX, groundStartY, groundEndX, groundEndY);

        //sun
        sunDimensions = (int) (width * 0.30);

//        sunFlames = BitmapFactory.decodeResource(getResources(), R.drawable.sun_anim_flames);
//        sunFlames = Bitmap.createScaledBitmap(sunFlames, sunDimensions, sunDimensions,
//                true);

        sunFlames = flames(sunDimensions, sunDimensions, tint);

        sunYCenter = height / 3;
        sunXCenter = width / 3;
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
        smileRect.set(sunXCenter - sunEyeDistance / 2,
                (float) (sunYCenter * 1.04),
                sunXCenter + sunEyeDistance / 2,
                (float) (sunYCenter * 1.12));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        time = time == 40 ? 0 : time + 1;

        canvas.save();

        canvas.rotate(angle, sunXCenter, sunYCenter);

        canvas.drawBitmap(sunFlames, sunXCenter - sunDimensions / 2,
                sunYCenter - sunDimensions / 2, mPaint);

        canvas.restore();

        if (time != 0) {
            mPaintSun.setStyle(Paint.Style.FILL);
            canvas.drawCircle(sunXCenter - sunEyeDistance / 2, sunYCenter, sunDimensions / 40, mPaintSun);
            canvas.drawCircle(sunXCenter + sunEyeDistance / 2, sunYCenter, sunDimensions / 40, mPaintSun);
        }

        mPaintSun.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(sunXCenter, sunYCenter, sunDimensions / 3, mPaintSun);

        canvas.drawRoundRect(groundRect, 10, 10, mPaint);


        canvas.drawArc(smileRect,
                10,
                160,
                false,
                mPaintSmile);


        if (angle++ > 360)
            angle = 0;

        mHandler.postDelayed(runnable, 20);
    }

    Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            invalidate();
        }
    };

    int time = 0;


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
}
