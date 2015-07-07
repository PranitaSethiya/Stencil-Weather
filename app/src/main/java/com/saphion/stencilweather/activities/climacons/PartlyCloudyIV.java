package com.saphion.stencilweather.activities.climacons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.widget.ImageView;

import com.saphion.stencilweather.R;

public class PartlyCloudyIV extends ImageView {

	int height, width;
	Bitmap cloud, sun;
	int angle = 0;
	Paint mPaint = new Paint();

	public PartlyCloudyIV(Context context) {
		super(context);
	}

	public PartlyCloudyIV(Context context, int height, int width) {
		super(context);
		this.height = height;
		this.width = width;
		sun = BitmapFactory.decodeResource(getResources(), R.drawable.sun);
		cloud = BitmapFactory.decodeResource(getResources(), R.drawable.cloud_black);
		mPaint = new Paint(); 
		mPaint.setAntiAlias(true); 

		cloud = Bitmap.createScaledBitmap(cloud, (int) (width), (int) (height),
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
