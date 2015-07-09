package com.saphion.stencilweather.climacons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.widget.ImageView;

import com.saphion.stencilweather.R;

public class FogMoonIV extends ImageView {

	int height, width;
	Bitmap cloud, drop, sun, cloud_color;
	int angle = 0;
	Path path1, path2, path3;
	Paint mPaint = new Paint();
	PathMeasure pm1, pm2, pm3;
	Paint mPaint1, mPaint2, mPaint3, mPaint4;
	float curr1 = 0, curr2 = 40, curr3 = 20;
	float len = 0;
	float[] tan = new float[2];
	float[] pts = new float[10];

	public FogMoonIV(Context context) {
		super(context);
	}

	public FogMoonIV(Context context, int height, int width, int color) {
		super(context);
		this.height = height;
		this.width = width;
		sun = BitmapFactory.decodeResource(getResources(), R.drawable.moon);
		cloud = BitmapFactory.decodeResource(getResources(),
				R.drawable.cloud_fog_black);
		cloud_color = BitmapFactory.decodeResource(getResources(),
				R.drawable.cloud_fog_black_cut);
		drop = BitmapFactory
				.decodeResource(getResources(), R.drawable.fog_line);
		int mheight = (int) (width * 0.056);
		drop = Bitmap.createScaledBitmap(drop, (int) (width), mheight, true);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint1 = new Paint();
		mPaint1.setAntiAlias(true);
		mPaint2 = new Paint();
		mPaint2.setAntiAlias(true);
		mPaint3 = new Paint();
		mPaint3.setAntiAlias(true);

		mPaint4 = new Paint();
		mPaint4.setAntiAlias(true);

		ColorFilter filter = new LightingColorFilter(color, 0);
		mPaint4.setColorFilter(filter);

		cloud = Bitmap.createScaledBitmap(cloud, (int) (width), (int) (height),
				true);
		cloud_color = Bitmap.createScaledBitmap(cloud_color, (int) (width), (int) (height),
				true);

		path1 = new Path();
		path2 = new Path();
		path3 = new Path();

		path1.moveTo(width / 2, (float) (height / 1.7));
		path2.moveTo(width / 2, (float) (height / 1.468));
		path3.moveTo(width / 2, (float) (height / 1.3));

		path1.lineTo((float) (width * 0.43), (float) (height / 1.7));
		path2.lineTo((float) (width * 0.43), (float) (height / 1.468));
		path3.lineTo((float) (width * 0.43), (float) (height / 1.3));

		path1.lineTo((float) (width * 0.63), (float) (height / 1.7));
		path1.lineTo((float) (width * 0.5), (float) (height / 1.7));
		path2.lineTo((float) (width * 0.63), (float) (height / 1.468));
		path2.lineTo((float) (width * 0.5), (float) (height / 1.468));
		path3.lineTo((float) (width * 0.63), (float) (height / 1.3));
		path3.lineTo((float) (width * 0.5), (float) (height / 1.3));

		mPaint.setPathEffect(new DashPathEffect(new float[] { 10, 10 }, 0));
		pm1 = new PathMeasure(path1, false);
		pm2 = new PathMeasure(path2, false);
		pm3 = new PathMeasure(path3, false);
		len = pm1.getLength() / 100;
		
		mPaint1.setAlpha(60);
		mPaint2.setAlpha(150);
		mPaint3.setAlpha(40);

		sun = Bitmap.createScaledBitmap(sun, (int) (width * 0.35),
				(int) (height * 0.35), true);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawBitmap(sun, width - (float) (sun.getWidth() * 1.3),
				(float) (sun.getHeight() * 0.35), mPaint);

        canvas.drawBitmap(cloud_color, (width - cloud.getWidth()) / 2,
                (height - cloud.getHeight()) / 2, mPaint4);

        canvas.drawBitmap(cloud, (width - cloud.getWidth()) / 2,
				(height - cloud.getHeight()) / 2, mPaint);


		inc1 = setinc(mPaint1.getAlpha(), inc1);
		inc2 = (setinc(mPaint2.getAlpha(), inc2));
		inc3 = (setinc(mPaint3.getAlpha(), inc3));

		mPaint1.setAlpha(setval(mPaint1.getAlpha(), inc1));
		mPaint2.setAlpha(setval(mPaint2.getAlpha(), inc2));
		mPaint3.setAlpha(setval(mPaint3.getAlpha(), inc3));

		pm1.getPosTan((float) (len * curr1), pts, tan);
		canvas.drawBitmap(drop, pts[0] - drop.getWidth() / 2,
				pts[1] - drop.getHeight() / 2, mPaint1);

		pm2.getPosTan((float) (len * curr2), pts, tan);
		canvas.drawBitmap(drop, pts[0] - drop.getWidth() / 2,
				pts[1] - drop.getHeight() / 2, mPaint2);

		pm3.getPosTan((float) (len * curr3), pts, tan);
		canvas.drawBitmap(drop, pts[0] - drop.getWidth() / 2,
				pts[1] - drop.getHeight() / 2, mPaint3);

		curr1 += 0.2;
		curr2 += 0.2;
		curr3 += 0.2;

		curr1 = check(curr1);
		curr2 = check(curr2);
		curr3 = check(curr3);

		mHandler.postDelayed(runnable, 20);

	}

	boolean inc1 = true, inc2 = true, inc3 = true;

	boolean setinc(int val, boolean b) {
		if (val == 80)
			return true;

		if (val == 250)
			return false;
		return b;
	}

	private int setval(int curr, boolean inc) {
		int val = 0;
		if (inc)
			val = curr + 1;
		else
			val = curr - 1;

		return val;
	}

	float check(float i) {
		if (i > 99.7 && i < 100)
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
