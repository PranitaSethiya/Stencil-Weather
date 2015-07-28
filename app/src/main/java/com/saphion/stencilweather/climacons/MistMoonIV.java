package com.saphion.stencilweather.climacons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.widget.ImageView;

import com.saphion.stencilweather.R;

public class MistMoonIV extends ImageView {

	int height, width;
	Bitmap cloud, drop, sun, cloud_color;
	int angle = 0;
	Paint mPaint = new Paint();
	Path cPath;
	PathMeasure pm;
	Paint mPaint1 = new Paint();
	Paint mPaint2 = new Paint();
	float curr1 = 0;
	float len;
	float[] tan = new float[2];
	float[] pts = new float[10];

	public MistMoonIV(Context context) {
		super(context);
	}

	public MistMoonIV(Context context, int height, int width, int color, int tint) {
		super(context);
		this.height = height;
		this.width = width;
		sun = BitmapFactory.decodeResource(getResources(), R.drawable.moon);
		cloud = BitmapFactory.decodeResource(getResources(),
				R.drawable.cloud_mist);
		cloud_color = BitmapFactory.decodeResource(getResources(),
				R.drawable.cloud_mist_cut);
		drop = BitmapFactory.decodeResource(getResources(),
				R.drawable.drop_small);
		int mheight = (int) (width * 0.3038);
		drop = Bitmap.createScaledBitmap(drop, (int) (width * 0.25), mheight,
				true);

		mPaint.setAntiAlias(true);
		mPaint1.setAntiAlias(true);
		mPaint2.setAntiAlias(true);

		ColorFilter tintColor = new LightingColorFilter(tint, 0);
		mPaint.setColorFilter(tintColor);
		mPaint1.setColorFilter(tintColor);

		ColorFilter filter = new LightingColorFilter(color, 0);
		mPaint2.setColorFilter(filter);

		cloud = Bitmap.createScaledBitmap(cloud, (int) (width), (int) (height),
				true);
		cloud_color = Bitmap.createScaledBitmap(cloud_color, (int) (width), (int) (height),
				true);

		sun = Bitmap.createScaledBitmap(sun, (int) (width * 0.35),
				(int) (height * 0.35), true);

		cPath = new Path();
		cPath.moveTo((float) (width / 2), (float) (height / 1.5));
		cPath.lineTo((float) (width / 2), height);
		pm = new PathMeasure(cPath, false);
		len = pm.getLength() / 100;

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawBitmap(sun, width - (float) (sun.getWidth() * 1.3),
				(float) (sun.getHeight() * 0.35), mPaint);

		mPaint1.setAlpha(setval(curr1));

		pm.getPosTan((float) (len * curr1), pts, tan);

        canvas.drawBitmap(cloud_color, (width - cloud.getWidth()) / 2,
                (height - cloud.getHeight()) / 2, mPaint2);
        canvas.drawBitmap(cloud, (width - cloud.getWidth()) / 2,
				(height - cloud.getHeight()) / 2, mPaint);


		canvas.drawBitmap(drop, pts[0] - drop.getWidth() / 2,
				pts[1] - drop.getHeight() / 2, mPaint1);

		curr1++;
		curr1 = check(curr1);

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
