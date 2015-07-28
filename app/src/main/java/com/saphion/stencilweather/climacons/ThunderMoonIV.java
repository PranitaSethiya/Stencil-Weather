package com.saphion.stencilweather.climacons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.os.Handler;
import android.widget.ImageView;

import com.saphion.stencilweather.R;

public class ThunderMoonIV extends ImageView {

	int height, width;
	Bitmap cloud, drop, sun, cloud_color;
	int angle = 0;
	Paint mPaint = new Paint();
	Paint mPaint1 = new Paint();
	Paint mPaint4 = new Paint();
	float curr1 = 0;

	public ThunderMoonIV(Context context) {
		super(context);
	}

	public ThunderMoonIV(Context context, int height, int width, int color, int tint) {
		super(context);
		this.height = height;
		this.width = width;
		sun = BitmapFactory.decodeResource(getResources(), R.drawable.moon);
		cloud = BitmapFactory.decodeResource(getResources(),
				R.drawable.cloud_open_black);
		cloud_color = BitmapFactory.decodeResource(getResources(),
				R.drawable.cloud_open_black_cut);
		drop = BitmapFactory.decodeResource(getResources(), R.drawable.bolt);
		int mheight = (int) (width * 0.6);
		drop = Bitmap.createScaledBitmap(drop, mheight, mheight, true);

		mPaint.setAntiAlias(true);
		mPaint1.setAntiAlias(true);
		mPaint4.setAntiAlias(true);

		ColorFilter tintColor = new LightingColorFilter(tint, 0);
		mPaint.setColorFilter(tintColor);
		mPaint1.setColorFilter(tintColor);

		ColorFilter filter = new LightingColorFilter(color, 0);
		mPaint4.setColorFilter(filter);

		cloud = Bitmap.createScaledBitmap(cloud, (int) (width), (int) (height),
				true);

		cloud_color = Bitmap.createScaledBitmap(cloud_color, (int) (width), (int) (height),
				true);

		sun = Bitmap.createScaledBitmap(sun, (int) (width * 0.35),
				(int) (height * 0.35), true);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawBitmap(sun, width - (float) (sun.getWidth() * 1.3),
				(float) (sun.getHeight() * 0.35), mPaint);

		mPaint1.setAlpha(setval(curr1));

        canvas.drawBitmap(cloud_color, (width - cloud.getWidth()) / 2,
                (height - cloud.getHeight()) / 2, mPaint4);

        canvas.drawBitmap(cloud, (width - cloud.getWidth()) / 2,
				(height - cloud.getHeight()) / 2, mPaint);


		canvas.drawBitmap(drop, (cloud.getWidth() - drop.getWidth()) / 2,
				(float) ((cloud.getHeight()) / 2), mPaint1);
		curr1++;
		curr1 = check(curr1);

		mHandler.postDelayed(runnable, 20);

	}

	private int setval(float curr) {
		int val = 0;
		if (curr <= 50) {
			val = (int) (curr * 10);
		} else if (curr > 100 && curr <= 130) {
			val = (int) ((100 - curr) * 10);
		} else if (curr > 130 && curr <= 150) {
			val = (int) ((100 - curr) * 26);
		} else {
			val = (int) ((100 - curr) * 5.1);
		}
		return val;
	}

	float check(float i) {
		if (i == 200)
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
