package com.fde.gallery.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fde.gallery.R;
import com.fde.gallery.bean.MultGroup;
import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.common.Constant;
import com.fde.gallery.event.ViewEvent;
import com.fde.gallery.ui.activity.PicturePreviewActivity;
import com.fde.gallery.ui.activity.VideoPlayActivity;
import com.fde.gallery.utils.LogTools;
import com.fde.gallery.utils.StringUtils;

import java.util.List;

public class TimeLineListAdapter extends RecyclerView.Adapter<TimeLineListAdapter.TimeLineListViewHolder> {
    Context context;
    List<MultGroup> list;
    int numberOfColumns;
    ViewEvent viewEvent;

    TimeLineAdapter timeLineAdapter;

    public TimeLineListAdapter(Context context, List<MultGroup> list, int numberOfColumns, ViewEvent viewEvent) {
        this.context = context;
        this.list = list;
        this.numberOfColumns = numberOfColumns;
        this.viewEvent = viewEvent;
    }

    @NonNull
    @Override
    public TimeLineListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_timeline_list, parent, false);
        TimeLineListAdapter.TimeLineListViewHolder holder = new TimeLineListAdapter.TimeLineListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineListViewHolder holder, int position) {
        MultGroup multGroup = list.get(position);
        holder.txtTitle.setText(multGroup.getTitle());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, numberOfColumns);
        timeLineAdapter = new TimeLineAdapter(context,multGroup.getList(),position,numberOfColumns,viewEvent);
        holder.recyclerView.setLayoutManager(gridLayoutManager);
        holder.recyclerView.setAdapter(timeLineAdapter);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class TimeLineListViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootView;
        RecyclerView recyclerView;
        TextView txtTitle;

        public TimeLineListViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = (LinearLayout) itemView.findViewById(R.id.rootView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        }
    }
}
