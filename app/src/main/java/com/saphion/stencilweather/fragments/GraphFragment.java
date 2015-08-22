package com.saphion.stencilweather.fragments;



import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.saphion.stencilweather.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphFragment extends Fragment {


    private static final int HOURLY = 0;
    private static final int DAILY = 1;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GraphFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GraphFragment newInstance() {
        GraphFragment fragment = new GraphFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        LineChart chartTemperature = (LineChart) view.findViewById(R.id.chartTemperature);
        BarChart chartPrecipitation = (BarChart) view.findViewById(R.id.chartPrecipitation);

        initialiseTemperatureGraph(chartTemperature);
        initialisePrecipitationGraph(chartPrecipitation);

        return view;
    }

    private void initialisePrecipitationGraph(BarChart chart) {
        chart.setDescription("");
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setTextColor(Color.WHITE);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);
//        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5, false);
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
////        rightAxis.setTypeface(mTf);
//        rightAxis.setLabelCount(5, false);
//        rightAxis.setDrawGridLines(false);

        // set data
        chart.setData(generateDataBar());

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        chart.animateX(750);

        Legend legend = chart.getLegend();
//        legend.setTextColor(Color.WHITE);
        legend.setEnabled(false);

        chart.setTouchEnabled(false);
    }

    private void initialiseTemperatureGraph(LineChart chart) {
        chart.setDescription("");
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setTextColor(Color.WHITE);

        YAxis leftAxis = chart.getAxisLeft();
//        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5, false);
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
////        rightAxis.setTypeface(mTf);
//        rightAxis.setLabelCount(5, false);
//        rightAxis.setDrawGridLines(false);

        // set data
        chart.setData(generateDataLine());

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        chart.animateX(750);

        Legend legend = chart.getLegend();
        legend.setEnabled(false);

//        chart.setTouchEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
    }


    private LineData generateDataLine() {

        ArrayList<Entry> e1 = new ArrayList<Entry>();

        for (int i = 0; i < 12; i++) {
            e1.add(new Entry((int) (Math.random() * 65) + 40, i));
        }

        LineDataSet d1 = new LineDataSet(e1, "High");
        d1.setLineWidth(2.5f);
        d1.setCircleSize(3.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setColor(getResources().getColor(R.color.primary_red));
        d1.setCircleColor(getResources().getColor(R.color.primary_red));
        d1.setCircleColorHole(getResources().getColor(R.color.primary_red));

        d1.setDrawValues(false);

        ArrayList<Entry> e2 = new ArrayList<Entry>();

        for (int i = 0; i < 12; i++) {
            e2.add(new Entry(e1.get(i).getVal() - 30, i));
        }

        LineDataSet d2 = new LineDataSet(e2, "Low");
        d2.setLineWidth(2.5f);
        d2.setCircleSize(3.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setCircleColorHole(getResources().getColor(R.color.primary_blue));
        d2.setColor(getResources().getColor(R.color.primary_blue));
        d2.setCircleColor(getResources().getColor(R.color.primary_blue));
        d2.setDrawValues(false);

        ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
        sets.add(d1);
        sets.add(d2);

        LineData cd = new LineData(getXAxisLabels(), sets);
        return cd;
    }

    private BarData generateDataBar() {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry((int) (Math.random() * 70) + 30, i));
        }

        BarDataSet d = new BarDataSet(entries, null);
        d.setBarSpacePercent(20f);
//        d.setHighLightColor(Color.TRANSPARENT);
        d.setColor(getResources().getColor(R.color.primary_light_blue));
        d.setHighLightAlpha(0);
        d.setValueTextColor(Color.WHITE);

        BarData cd = new BarData(getXAxisLabels(), d);
        return cd;
    }

    private ArrayList<String> getXAxisLabels(/*int type, String[] timestamps, boolean is24hour*/) {

        ArrayList<String> m = new ArrayList<String>();

//        if(type == GraphFragment.HOURLY) {
//            int currentTime;
//            if(is24hour) {
//                currentTime = c.get(Calendar.HOUR_OF_DAY);
//                for(int i = 0 ; i < 11 ; i++){
//                    String temp = String.valueOf(currentTime + i);
//                    if(temp.length() == 1)
//                        temp = "0" + temp;
//                    m.add(temp);
//                }
//            } else {
//                currentTime = c.get(Calendar.HOUR);
//                c.
//                for()
//            }


            m.add("Jan");
            m.add("Feb");
            m.add("Mar");
            m.add("Apr");
            m.add("May");
            m.add("Jun");
            m.add("Jul");
            m.add("Aug");
            m.add("Sep");
            m.add("Okt");
            m.add("Nov");
            m.add("Dec");
//        } else {
//
//        }

        return m;
    }


}
