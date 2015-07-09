package com.saphion.stencilweather.climacons;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class Main extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		setContentView(R.layout.main);
		super.onCreate(savedInstanceState);

        ScrollView sv = new ScrollView(Main.this);
        sv.setBackgroundColor(Color.BLACK);

		LinearLayout fl = new LinearLayout(Main.this);
        fl.setOrientation(LinearLayout.VERTICAL);

		LayoutParams lp = new LayoutParams((int) (getResources()
				.getDisplayMetrics().widthPixels * 0.45), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.45));


		HazeSunIV hsiv = new HazeSunIV(getBaseContext(), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35));
		hsiv.setLayoutParams(lp);
		fl.addView(hsiv);

		HazeMoonIV hmiv = new HazeMoonIV(getBaseContext(), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35));
		hmiv.setLayoutParams(lp);
		fl.addView(hmiv);

		FogSunIV fsiv = new FogSunIV(getBaseContext(), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), Color.BLACK);
		fsiv.setLayoutParams(lp);
		fl.addView(fsiv);

		FogMoonIV fmiv = new FogMoonIV(getBaseContext(), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), Color.BLACK);
		fmiv.setLayoutParams(lp);
		fl.addView(fmiv);

		MistSunIV msiv = new MistSunIV(getBaseContext(), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), Color.BLACK);
		msiv.setLayoutParams(lp);
		fl.addView(msiv);

		MistMoonIV mmiv = new MistMoonIV(getBaseContext(),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35), Color.BLACK);
		mmiv.setLayoutParams(lp);
		fl.addView(mmiv);

		ThunderSunIV tsiv = new ThunderSunIV(getBaseContext(),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35), Color.BLACK);
		tsiv.setLayoutParams(lp);
		fl.addView(tsiv);

		ThunderMoonIV tmiv = new ThunderMoonIV(getBaseContext(),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35), Color.BLACK);
		tmiv.setLayoutParams(lp);
		fl.addView(tmiv);

		ThunderIV tiv = new ThunderIV(getBaseContext(), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35));
		tiv.setLayoutParams(lp);
		fl.addView(tiv);

		HailIV hiv = new HailIV(getBaseContext(), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35));
		hiv.setLayoutParams(lp);
		fl.addView(hiv);

		PartlyCloudyIV pciv = new PartlyCloudyIV(getBaseContext(),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35), Color.BLACK);
		pciv.setLayoutParams(lp);
		fl.addView(pciv);

		PartlyCloudyIVn pcdiv = new PartlyCloudyIVn(getBaseContext(),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35), Color.BLACK);
		pcdiv.setLayoutParams(lp);
		fl.addView(pcdiv);

		IcyIV riv = new IcyIV(getBaseContext(), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35));
		riv.setLayoutParams(lp);
		fl.addView(riv);

		IcyMoonIV rniv = new IcyMoonIV(getBaseContext(), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), Color.BLACK);
		rniv.setLayoutParams(lp);
		fl.addView(rniv);

		IcySunIV rdiv = new IcySunIV(getBaseContext(), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), Color.BLACK);
		rdiv.setLayoutParams(lp);
		fl.addView(rdiv);

		RainIV riiv = new RainIV(getBaseContext(), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35));
		riiv.setLayoutParams(lp);
		fl.addView(riiv);

		RainMoonIV riniv = new RainMoonIV(getBaseContext(),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35), Color.BLACK);
		riniv.setLayoutParams(lp);
		fl.addView(riniv);

		RainSunIV ridiv = new RainSunIV(getBaseContext(), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), Color.BLACK);
		ridiv.setLayoutParams(lp);
		fl.addView(ridiv);

		SleetIV sliv = new SleetIV(getBaseContext(), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35));
		sliv.setLayoutParams(lp);
		fl.addView(sliv);

		SnowDayIV sdiv = new SnowDayIV(getBaseContext(), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), Color.BLACK);
		sdiv.setLayoutParams(lp);
		fl.addView(sdiv);

		SnowNightIV sniv = new SnowNightIV(getBaseContext(),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35), Color.BLACK);
		sniv.setLayoutParams(lp);
		fl.addView(sniv);

		SnowIV siv = new SnowIV(getBaseContext(), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35), (int) (getResources()
				.getDisplayMetrics().widthPixels * 0.35));
		siv.setLayoutParams(lp);
		fl.addView(siv);

		SunImageView suniv = new SunImageView(getBaseContext(),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35),
				(int) (getResources().getDisplayMetrics().widthPixels * 0.35));
		suniv.setLayoutParams(lp);
		fl.addView(suniv);

        sv.addView(fl);

		setContentView(sv);

	}

}
