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
import com.saphion.stencilweather.activities.MapActivity;
import com.saphion.stencilweather.modules.WLocation;
import com.saphion.stencilweather.utilities.Utils;

import java.util.List;

public class MapListAdapter extends RecyclerView.Adapter<MapListAdapter.SimpleViewHolder> {

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
            myLocation.setClickable(false);
            listIndicator.setColorFilter(itemView.getResources().getColor(R.color.black_matt));
            int padding = Utils.dpToPx(10, itemView.getContext());
            listIndicator.setPadding(padding, padding, padding, padding);
        }

    }

    private Context mContext;
    private List<WLocation> mDataset;

    public MapListAdapter(List<WLocation> objects, Activity mActivity) {
        this.mContext = mActivity.getBaseContext();
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

        if(position == 0){
            viewHolder.myLocation.setImageResource(R.drawable.curloc);
        } else {
            viewHolder.myLocation.setImageResource(R.color.transparent);
        }

        viewHolder.listIndicator.setImageResource(R.drawable.ic_map_marker);

        viewHolder.name.setText(item.getName());

        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MapActivity) mActivity).performDialogItemClick(position);
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

}