package com.saphion.stencilweather.climacons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.widget.ImageView;

import com.saphion.stencilweather.R;

public class SnowIV extends ImageView {

	int height, width;
	Bitmap cloud, drop;
	int angle = 0;
	Path path1;
	Paint mPaint = new Paint();
	PathMeasure pm1;
	Paint mPaint1;
	float curr1 = 0;
	float len = 0;
	float[] tan = new float[2];
	float[] pts = new float[10];

	public SnowIV(Context context) {
		super(context);
	}

	public SnowIV(Context context, int height, int width, int tint) {
		super(context);
		this.height = height;
		this.width = width;
		// sun = BitmapFactory.decodeResource(getResources(), R.drawable.sun);
		cloud = BitmapFactory.decodeResource(getResources(),
				R.drawable.cloud_open);
		drop = BitmapFactory
				.decodeResource(getResources(), R.drawable.snow);
		int nWidth = (int)(width * 0.2);
		drop = Bitmap.createScaledBitmap(drop, nWidth, nWidth,
				true);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint1 = new Paint();
		mPaint1.setAntiAlias(true);

		ColorFilter tintColor = new LightingColorFilter(tint, 0);
		mPaint.setColorFilter(tintColor);
		mPaint1.setColorFilter(tintColor);

		cloud = Bitmap.createScaledBitmap(cloud, (int) (width), (int) (height),
				true);

		path1 = new Path();

		path1.moveTo((float) (width * 0.66), height / 2);
		path1.lineTo((float) (width / 2.08), (float) (height / 1.7));
		path1.lineTo((float) (width / 3), (float) (height / 1.3));
		path1.lineTo((float) (width / 1.4), height);

		mPaint.setPathEffect(new DashPathEffect(new float[] { 10, 10 }, 0));
		pm1 = new PathMeasure(path1, false);
		len = pm1.getLength() / 100;

		// sun = Bitmap.createScaledBitmap(sun, (int) (width * 0.65),
		// (int) (height * 0.65), true);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		/*
		 * canvas.save(); canvas.rotate(angle, width - (float) (sun.getWidth())
		 * + sun.getWidth() / 2, (float) sun.getHeight() / 2);
		 * 
		 * canvas.drawBitmap(sun, width - (float) (sun.getWidth()), 0, mPaint);
		 * canvas.restore();
		 */

		mPaint1.setAlpha(setval(curr1));

		pm1.getPosTan((float) (len * curr1), pts, tan);
		canvas.drawBitmap(drop, pts[0] - drop.getWidth() / 2,
				pts[1] - drop.getHeight() / 2, mPaint1);

		canvas.drawBitmap(cloud, (width - cloud.getWidth()) / 2,
				(height - cloud.getHeight()) / 2, mPaint);
		
		curr1 += 0.4;

		curr1 = check(curr1);

		mPaint.setColor(Color.WHITE);
	//	mPaint.setStyle(Style.STROKE);

	//	mPaint.setStrokeWidth((float) (height * 0.0015));
	//	canvas.drawPath(path1, mPaint1);

		if (angle++ > 360)
			angle = 0;

		mHandler.postDelayed(runnable, 20);

	}

	private int setval(float curr) {
		int val = 0;
		if (curr <= 50) {
			val = (int) (curr * 5.1);
		} else {
			val = (int) ((100 - curr) * 5.1);
		}
		return val;
	}

	float check(float i) {
		if (i > 99.6 && i < 100.2)
			return 0;
		return i;
	}

	Handler mHandler = new Handler();
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			invalidate();
		}
	};

}
