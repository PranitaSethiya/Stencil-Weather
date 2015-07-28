package com.saphion.stencilweather.climacons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
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

public class HazeMoonIV extends ImageView {

	int height, width;
	Bitmap cloud, drop, sun;
	int angle = 0;
	Path path1, path2, path3, path4, path5;
	Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	PathMeasure pm1, pm2, pm3, pm4, pm5;
	Paint mPaint1, mPaint2, mPaint3, mPaint4, mPaint5;
	float curr1 = 0, curr2 = 70, curr3 = 20, curr4 = 20, curr5 = 70;
	float len = 0;
	float[] tan = new float[2];
	float[] pts = new float[10];
	Options options = new Options();

	public HazeMoonIV(Context context) {
		super(context);
	}

	public HazeMoonIV(Context context, int height, int width, int tint) {
		super(context);
		this.height = height;
		this.width = width;
		
//		options.inScaled = false;

		sun = BitmapFactory.decodeResource(getResources(), R.drawable.moon, options);
		drop = BitmapFactory
				.decodeResource(getResources(), R.drawable.fog_line, options);
		int mheight = (int) (width * 0.05);
		drop = Bitmap.createScaledBitmap(drop, (int) (width * 0.5), mheight,
				true);
		mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
		mPaint.setAntiAlias(true);
		mPaint1 = new Paint(Paint.FILTER_BITMAP_FLAG);
		mPaint1.setAntiAlias(true);
		mPaint2 = new Paint(Paint.FILTER_BITMAP_FLAG);
		mPaint2.setAntiAlias(true);
		mPaint3 = new Paint(Paint.FILTER_BITMAP_FLAG);
		mPaint3.setAntiAlias(true);
		mPaint4 = new Paint(Paint.FILTER_BITMAP_FLAG);
		mPaint4.setAntiAlias(true);
		mPaint5 = new Paint(Paint.FILTER_BITMAP_FLAG);
		mPaint5.setAntiAlias(true);

		ColorFilter tintColor = new LightingColorFilter(tint, 0);
		mPaint.setColorFilter(tintColor);
		mPaint1.setColorFilter(tintColor);
		mPaint2.setColorFilter(tintColor);
		mPaint3.setColorFilter(tintColor);
		mPaint4.setColorFilter(tintColor);
		mPaint5.setColorFilter(tintColor);

		cloud = Bitmap.createBitmap(width, height, Config.ARGB_8888);

		sun = Bitmap.createScaledBitmap(sun, (int) (width * 0.45),
				(int) (height * 0.45), true);

		Canvas can = new Canvas(cloud);
		can.drawBitmap(sun, cloud.getWidth() / 2 - sun.getWidth() / 2,
				cloud.getHeight() / 2 - sun.getHeight() / 2, mPaint);

		path1 = new Path();
		path2 = new Path();
		path3 = new Path();
		path4 = new Path();
		path5 = new Path();
		// -0.08
		float h1 = (float) (height * 0.415);
		float h2 = (float) (height * 0.327);
		float h3 = (float) (height * 0.508);
		float h4 = (float) (height * 0.6012);
		float h5 = (float) (height * 0.689);
		float w1 = width / 2;
		float w2 = (float) (width * 0.33);
		float w3 = (float) (width * 0.73);
		float w4 = (float) (width * 0.5);
		path4.moveTo(w1, h1);// 0.495
		path5.moveTo(w1, h2);// 0.407
		path1.moveTo(w1, h3);// 0.588
		path2.moveTo(w1, h4);// 0.68119
		path3.moveTo(w1, h5);// 0.769

		path4.lineTo(w2, h1);
		path5.lineTo(w2, h2);
		path1.lineTo(w2, h3);
		path2.lineTo(w2, h4);
		path3.lineTo(w2, h5);

		path4.lineTo(w3, h1);
		path4.lineTo(w4, h1);
		path5.lineTo(w3, h2);
		path5.lineTo(w4, h2);
		path1.lineTo(w3, h3);
		path1.lineTo(w4, h3);
		path2.lineTo(w3, h4);
		path2.lineTo(w4, h4);
		path3.lineTo(w3, h5);
		path3.lineTo(w4, h5);

		mPaint.setPathEffect(new DashPathEffect(new float[] { 10, 10 }, 0));
		pm1 = new PathMeasure(path1, false);
		pm2 = new PathMeasure(path2, false);
		pm3 = new PathMeasure(path3, false);
		pm4 = new PathMeasure(path4, false);
		pm5 = new PathMeasure(path5, false);
		len = pm1.getLength() / 100;

		mPaint1.setAlpha(60);
		mPaint2.setAlpha(150);
		mPaint3.setAlpha(40);
		mPaint4.setAlpha(150);
		mPaint5.setAlpha(40);
		mPaint.setAlpha(180);

		// sun = Bitmap.createScaledBitmap(sun, (int) (width * 0.65),
		// (int) (height * 0.65), true);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawBitmap(cloud, (width - cloud.getWidth()) / 2,
				(height - cloud.getHeight()) / 2, mPaint);

		inc1 = setinc(mPaint1.getAlpha(), inc1);
		inc2 = (setinc(mPaint2.getAlpha(), inc2));
		inc3 = (setinc(mPaint3.getAlpha(), inc3));
		inc4 = (setinc(mPaint4.getAlpha(), inc4));
		inc5 = (setinc(mPaint5.getAlpha(), inc5));

		mPaint1.setAlpha(setval(mPaint1.getAlpha(), inc1));
		mPaint2.setAlpha(setval(mPaint2.getAlpha(), inc2));
		mPaint3.setAlpha(setval(mPaint3.getAlpha(), inc3));
		mPaint4.setAlpha(setval(mPaint4.getAlpha(), inc4));
		mPaint5.setAlpha(setval(mPaint5.getAlpha(), inc5));

		pm1.getPosTan((float) (len * curr1), pts, tan);
		canvas.drawBitmap(drop, pts[0] - drop.getWidth() / 2,
				pts[1] - drop.getHeight() / 2, mPaint1);

		pm2.getPosTan((float) (len * curr2), pts, tan);
		canvas.drawBitmap(drop, pts[0] - drop.getWidth() / 2,
				pts[1] - drop.getHeight() / 2, mPaint2);

		pm3.getPosTan((float) (len * curr3), pts, tan);
		canvas.drawBitmap(drop, pts[0] - drop.getWidth() / 2,
				pts[1] - drop.getHeight() / 2, mPaint3);

		pm4.getPosTan((float) (len * curr4), pts, tan);
		canvas.drawBitmap(drop, pts[0] - drop.getWidth() / 2,
				pts[1] - drop.getHeight() / 2, mPaint4);

		pm5.getPosTan((float) (len * curr5), pts, tan);
		canvas.drawBitmap(drop, pts[0] - drop.getWidth() / 2,
				pts[1] - drop.getHeight() / 2, mPaint5);

		curr1 += 0.2;
		curr2 += 0.2;
		curr3 += 0.2;
		curr4 += 0.2;
		curr5 += 0.2;

		curr1 = check(curr1);
		curr2 = check(curr2);
		curr3 = check(curr3);
		curr4 = check(curr4);
		curr5 = check(curr5);

		mHandler.postDelayed(runnable, 20);

	}

	boolean inc1 = true, inc2 = true, inc3 = true, inc4 = true, inc5 = true;

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
