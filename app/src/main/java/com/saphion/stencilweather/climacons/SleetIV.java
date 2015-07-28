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
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.widget.ImageView;

import com.saphion.stencilweather.R;

public class SleetIV extends ImageView {

	int height, width;
	Bitmap cloud, drop;
	int angle = 0;
	Path path1, path2, path3;
	Paint mPaint = new Paint();
	PathMeasure pm1, pm2, pm3;
	Paint mPaint1, mPaint2, mPaint3;
	float curr1 = 0, curr2 = 50, curr3 = 25;
	float len = 0;
	float[] tan = new float[2];
	float[] pts = new float[10];

	public SleetIV(Context context) {
		super(context);
	}

	public SleetIV(Context context, int height, int width, int tint) {
		super(context);
		this.height = height;
		this.width = width;
		// sun = BitmapFactory.decodeResource(getResources(), R.drawable.sun);
		cloud = BitmapFactory.decodeResource(getResources(),
				R.drawable.cloud_open);
		drop = BitmapFactory
				.decodeResource(getResources(), R.drawable.drop_long);
		int mheight = (int) (width * 0.3375);
		drop = Bitmap.createScaledBitmap(drop, (int) (width * 0.075), mheight,
				true);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint1 = new Paint();
		mPaint1.setAntiAlias(true);
		mPaint2 = new Paint();
		mPaint2.setAntiAlias(true);
		mPaint3 = new Paint();
		mPaint3.setAntiAlias(true);

		ColorFilter tintColor = new LightingColorFilter(tint, 0);
		mPaint.setColorFilter(tintColor);
		mPaint1.setColorFilter(tintColor);
		mPaint2.setColorFilter(tintColor);
		mPaint3.setColorFilter(tintColor);

		cloud = Bitmap.createScaledBitmap(cloud, (int) (width), (int) (height),
				true);

		path1 = new Path();
		path2 = new Path();
		path3 = new Path();

		path1.moveTo(width / 3, (float) (height / 1.5));
		path2.moveTo((float) (width / 2), (float) (height / 1.5));
		path3.moveTo((float) (width * 0.66), (float) (height / 1.5));

		path1.lineTo(width / 3, height);
		path2.lineTo((float) (width / 2), height);
		path3.lineTo((float) (width * 0.66), height);

		mPaint.setPathEffect(new DashPathEffect(new float[] { 10, 10 }, 0));
		pm1 = new PathMeasure(path1, false);
		pm2 = new PathMeasure(path2, false);
		pm3 = new PathMeasure(path3, false);
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
		mPaint2.setAlpha(setval(curr2));
		mPaint3.setAlpha(setval(curr3));

		pm1.getPosTan((float) (len * curr1++), pts, tan);
		canvas.drawBitmap(drop, pts[0] - drop.getWidth() / 2,
				pts[1] - drop.getHeight() / 2, mPaint1);

		pm2.getPosTan((float) (len * curr2++), pts, tan);
		canvas.drawBitmap(drop, pts[0] - drop.getWidth() / 2,
				pts[1] - drop.getHeight() / 2, mPaint2);

		pm3.getPosTan((float) (len * curr3++), pts, tan);
		canvas.drawBitmap(drop, pts[0] - drop.getWidth() / 2,
				pts[1] - drop.getHeight() / 2, mPaint3);

		canvas.drawBitmap(cloud, (width - cloud.getWidth()) / 2,
				(height - cloud.getHeight()) / 2, mPaint);

		curr1 = check(curr1);
		curr2 = check(curr2);
		curr3 = check(curr3);

		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Style.STROKE);

		mPaint.setStrokeWidth((float) (height * 0.0015));
		canvas.drawPath(path1, mPaint1);
		canvas.drawPath(path2, mPaint2);
		canvas.drawPath(path3, mPaint3);

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
		if (i == 100)
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
