package com.saphion.stencilweather.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saphion.stencilweather.R;
import com.saphion.stencilweather.activities.MainActivity;
import com.saphion.stencilweather.modules.WLocation;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.SimpleViewHolder> {

    private final Activity mActivity;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView listIndicator;
        ImageView myLocation;
        View container;
        View progressBar;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tvLocationName);
            listIndicator = (ImageView)itemView.findViewById(R.id.ivListIndicator);
            myLocation = (ImageView)itemView.findViewById(R.id.ivMyLocation);
            container = itemView.findViewById(R.id.locationItemContainer);
            progressBar = itemView.findViewById(R.id.pbMyLocation);
        }

    }

    private Context mContext;
    private List<WLocation> mDataset;

    public List<WLocation> getArrayList(){
        return this.mDataset;
    }

    public RecyclerViewAdapter(Context context, List<WLocation> objects, Activity mActivity) {
        this.mContext = context;
        this.mDataset = objects;
        this.mActivity = mActivity;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {

        WLocation item = mDataset.get(position);

        viewHolder.myLocation.setVisibility(View.VISIBLE);

        viewHolder.myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == 0){
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                    viewHolder.myLocation.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            viewHolder.myLocation.setVisibility(View.VISIBLE);
                        }
                    }, 2000);
                } else {

                    WLocation wLocation = mDataset.remove(position);
                    wLocation.delete();

                    try {
                        notifyDataSetChanged();
                    }catch (Exception ignored){}

                    ((MainActivity)mActivity).changePosition(position, wLocation);

                }
            }
        });

        if(position == 0){
            viewHolder.myLocation.setImageResource(R.drawable.curloc);
        } else {
            viewHolder.myLocation.setImageResource(R.drawable.ic_delete);
            viewHolder.myLocation.setColorFilter(mContext.getResources().getColor(R.color.main_button_red_normal));
        }

        if(position == mDataset.size() - 1){
            viewHolder.listIndicator.setImageResource(R.drawable.end);
        } else {
            viewHolder.listIndicator.setImageResource(R.drawable.middle);
        }

        viewHolder.name.setText(item.getName());
        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mActivity).setNavPosition(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void add(WLocation location) {
        mDataset.add(location);
        notifyDataSetChanged();
    }

    public void add(int index, WLocation wLocation){
        mDataset.add(index, wLocation);
        notifyItemInserted(index);
    }

    public void remove(WLocation wLocation) {
        int index = mDataset.indexOf(wLocation);
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

}