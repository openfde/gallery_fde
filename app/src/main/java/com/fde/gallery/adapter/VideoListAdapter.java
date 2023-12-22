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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fde.gallery.R;
import com.fde.gallery.bean.Video;
import com.fde.gallery.ui.activity.VideoPlayActivity;
import com.fde.gallery.utils.LogTools;

import java.io.File;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoListViewHolder> {
    List<Video> list;
    Context context;

    public VideoListAdapter(Context context, List<Video> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_list, parent, false);
        VideoListViewHolder holder = new VideoListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoListViewHolder holder, int position) {
        Glide.with(context)
//                .load(Uri.fromFile(new File(list.get(position).getPath())))
                .load(list.get(position).getPath())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .apply(new RequestOptions().frame(1000))
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
//        Glide.with(context) // replace 'this' with your context
//                .load(list.get(position).getPath())
//                .apply(new RequestOptions().frame(1000)) // frame at 1 second into the video
//                .into(holder.imageView);


        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Video video = list.get(position);
                LogTools.i("video"+video.toString());
                Intent intent = new Intent();
                intent.putExtra("video_data",video);
                intent.setClass(context, VideoPlayActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VideoListViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        LinearLayout rootView;

        public VideoListViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = (LinearLayout)itemView.findViewById(R.id.rootView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

}
