package com.saphion.stencilweather.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saphion.stencilweather.R;
import com.saphion.stencilweather.activities.ForecastActivity;
import com.saphion.stencilweather.activities.MainActivity;
import com.saphion.stencilweather.modules.WLocation;

import java.util.List;

public class WeatherCardAdapter extends RecyclerView.Adapter<WeatherCardAdapter.SimpleViewHolder> {

    private final MainActivity mActivity;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView day;
        TextView date;
        TextView high;
        TextView low;
        TextView slash;
        TextView condition;
        ImageView icon;
        View container;

        public SimpleViewHolder(View itemView) {
            super(itemView);
//            day = (TextView) itemView.findViewById(R.id.cardDay);
//            date = (TextView) itemView.findViewById(R.id.cardDate);
//            condition = (TextView) itemView.findViewById(R.id.cardCondition);
//            high = (TextView) itemView.findViewById(R.id.cardHigh);
//            low = (TextView) itemView.findViewById(R.id.cardLow);
//            icon = (ImageView)itemView.findViewById(R.id.cardIcon);
            container = itemView.findViewById(R.id.card);
        }

    }

    private Context mContext;
    private List<String> mDataset;

    public List<String> getArrayList(){
        return this.mDataset;
    }

    public WeatherCardAdapter(Context context, List<String> objects, MainActivity mActivity) {
        this.mContext = context;
        this.mDataset = objects;
        this.mActivity = mActivity;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_card, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {

//        WLocation item = mDataset.get(position);
//
//        if(position == 0){
//            viewHolder.myLocation.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.myLocation.setVisibility(View.GONE);
//        }
//
//        if(position == mDataset.size() - 1){
//            viewHolder.listIndicator.setImageResource(R.drawable.end);
//        } else {
//            viewHolder.listIndicator.setImageResource(R.drawable.middle);
//        }
//
//        viewHolder.name.setText(item.getName());
//        viewHolder.container.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((MainActivity)mActivity).setViewPagerPosition(position);
//            }
//        });

        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ForecastActivity.class);
                intent.putExtra("subtitle", mActivity.getSelectedLocation().getName());
                intent.putExtra("locationID", mActivity.getSelectedLocation().getId());
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void add(Object item) {
        mDataset.add((String) item);
        notifyDataSetChanged();
    }

    public void add(int index, Object item){
        mDataset.add(index, (String) item);
        notifyItemInserted(index);
    }

    public void remove(Object item) {
        int index = mDataset.indexOf(item);
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

}