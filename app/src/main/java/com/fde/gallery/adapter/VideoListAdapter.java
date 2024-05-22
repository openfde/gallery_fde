/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fde.gallery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fde.gallery.R;
import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.event.ViewEvent;
import com.fde.gallery.ui.activity.VideoPlayActivity;
import com.fde.gallery.utils.LogTools;

import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoListViewHolder> {
    List<Multimedia> list;
    Context context;
    int numberOfColumns;

    ViewEvent viewEvent;

    public VideoListAdapter(Context context, List<Multimedia> list, int numberOfColumns, ViewEvent viewEvent) {
        this.list = list;
        this.context = context;
        this.numberOfColumns = numberOfColumns;
        this.viewEvent = viewEvent;
    }

    @NonNull
    @Override
    public VideoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_list, parent, false);
        VideoListViewHolder holder = new VideoListViewHolder(view);
        return holder;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull VideoListViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.rootView.post(new Runnable() {
            @Override
            public void run() {
                if (holder.rootView.getParent() != null) {
                    int width = ((RecyclerView) holder.rootView.getParent()).getWidth();
                    if (width != 0) {
                        // get RecyclerView width
                        // calc RecyclerView width and height
                        int size = width / numberOfColumns; // replace 3 with the number of columns
                        // set item width and height
                        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                        layoutParams.width = size;
                        layoutParams.height = size;
                        holder.itemView.setLayoutParams(layoutParams);
                    }
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull VideoListViewHolder holder,@SuppressLint("RecyclerView")  final int position) {
        Multimedia video = list.get(position);
        Glide.with(context)
//                .load(Uri.fromFile(new File(list.get(position).getPath())))
                .load(video.getPath())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
//                .apply(new RequestOptions().frame(1000))
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop() // 裁剪图片以适应ImageView的大小
                .dontTransform() // 禁用任何额外的转换
                .dontAnimate()
                .into(holder.imageView);
//        Glide.with(context) // replace 'this' with your context
//                .load(list.get(position).getPath())
//                .apply(new RequestOptions().frame(1000)) // frame at 1 second into the video
//                .into(holder.imageView);
        holder.checkBox.setVisibility(video.isShowCheckbox() ? View.VISIBLE : View.GONE);
        holder.checkBox.setChecked(video.isSelected());



        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Multimedia video = list.get(position);
                LogTools.i("video" + video.toString());
                Intent intent = new Intent();
                intent.putExtra("video_data", video);
                intent.setClass(context, VideoPlayActivity.class);
                context.startActivity(intent);
            }
        });

        holder.rootView.setOnContextClickListener(new View.OnContextClickListener() {
            @Override
            public boolean onContextClick(View view) {
//                holder.checkBox.setVisibility(View.VISIBLE);
                viewEvent.onRightEvent(position,0);
                return false;
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                viewEvent.onSelectEvent(position,0, b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VideoListViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        RelativeLayout rootView;

        CheckBox checkBox;

        public VideoListViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = (RelativeLayout) itemView.findViewById(R.id.rootView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
    }

}
