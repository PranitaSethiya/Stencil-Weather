package com.saphion.stencilweather.adapters;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saphion.stencilweather.R;
import com.saphion.stencilweather.activities.MainActivity;

import java.util.List;

public class ShareItemViewAdapter extends RecyclerView.Adapter<ShareItemViewAdapter.SimpleViewHolder> {


    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView icon;
        View container;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.shareItemTitle);
            icon = (ImageView) itemView.findViewById(R.id.shareItemIcon);
            container = itemView.findViewById(R.id.containerShareLinks);
        }

    }

    private List<Drawable> packageIcons;
    private List<String> appNames;
    private List<String> packageNames;
    private MainActivity mainActivity;
    private List<String> classNames;


    public ShareItemViewAdapter(MainActivity mainActivity, List<Drawable> packageIcons, List<String> appNames, List<String> packageNames, List<String> classNames) {
        this.packageIcons = packageIcons;
        this.appNames = appNames;
        this.packageNames = packageNames;
        this.mainActivity = mainActivity;
        this.classNames = classNames;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        viewHolder.icon.setImageDrawable(packageIcons.get(position));
        viewHolder.name.setText(appNames.get(position));

        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Stencil Weather", "Share Item clicked");
                mainActivity.share(packageNames.get(position), classNames.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return appNames.size();
    }

}