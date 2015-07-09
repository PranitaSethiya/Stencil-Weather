package com.saphion.stencilweather.activities.climacons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.os.Handler;
import android.widget.ImageView;

import com.saphion.stencilweather.R;

public class PartlyCloudyIV extends ImageView {

	int height, width;
	Bitmap cloud, sun, cloud_color;
	int angle = 0;
	Paint mPaint = new Paint();
	Paint mPaint2 = new Paint();

	public PartlyCloudyIV(Context context) {
		super(context);
	}

	public PartlyCloudyIV(Context context, int height, int width, int color) {
		super(context);
		this.height = height;
		this.width = width;
		sun = BitmapFactory.decodeResource(getResources(), R.drawable.sun);
		cloud = BitmapFactory.decodeResource(getResources(), R.drawable.cloud_black);
		cloud_color = BitmapFactory.decodeResource(getResources(), R.drawable.cloud_black_cut);
		mPaint = new Paint();
		mPaint.setAntiAlias(true); 
        mPaint2 = new Paint();
		mPaint2.setAntiAlias(true);
        ColorFilter filter = new LightingColorFilter(color, 0);
		mPaint2.setColorFilter(filter);

		cloud = Bitmap.createScaledBitmap(cloud, (int) (width), (int) (height),
				true);

		cloud_color = Bitmap.createScaledBitmap(cloud_color, (int) (width), (int) (height),
				true);
		
		sun = Bitmap.createScaledBitmap(sun, (int) (width * 0.65),
				(int) (height * 0.65), true);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.save();
		canvas.rotate(angle,
				width - (float) (sun.getWidth()) + sun.getWidth() / 2,
				(float) sun.getHeight() / 2);

		canvas.drawBitmap(sun, width - (float) (sun.getWidth()),
				0, mPaint);
		canvas.restore();
        canvas.drawBitmap(cloud_color, (width - cloud.getWidth()) / 2,
                (height - cloud.getHeight()) / 2, mPaint2);
        canvas.drawBitmap(cloud, (width - cloud.getWidth()) / 2,
				(height - cloud.getHeight()) / 2, mPaint);


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

}
