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
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.fde.gallery.utils.FileUtils;
import com.fde.gallery.utils.LogTools;

import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoListViewHolder> {
    List<Multimedia> list;
    Context context;
    int numberOfColumns;
    ViewEvent viewEvent;
    int itemSizePx;
    static final int PAYLOAD_SIZE = 1;

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
    public void onBindViewHolder(@NonNull VideoListViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.contains(PAYLOAD_SIZE)) {
            holder.updateSize(itemSizePx);
            return;
        }
        onBindViewHolder(holder, position);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoListViewHolder holder,@SuppressLint("RecyclerView")  final int position) {
        Multimedia video = list.get(position);
        holder.bind(video, itemSizePx);

        holder.checkBox.setVisibility(video.isShowCheckbox() ? View.VISIBLE : View.GONE);
        holder.checkBox.setChecked(video.isSelected());

        GestureDetector detector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true; // ⚠️ 必须返回 true
                    }

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Multimedia video = list.get(position);
                        Intent intent = new Intent();
                        intent.putExtra("video_data", video);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, VideoPlayActivity.class);
                        context.startActivity(intent);
                        return true;
                    }
                });

        holder.rootView.setOnTouchListener((v, event) -> {
            return detector.onTouchEvent(event);
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

    @Override
    public long getItemId(int position) {
        return  list.get(position).getId();
    }

    public void updateItemSize(int sizePx) {
        if (itemSizePx != sizePx) {
            itemSizePx = sizePx;
            notifyItemRangeChanged(0, getItemCount(), PAYLOAD_SIZE);
        }
    }

    class VideoListViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        RelativeLayout rootView;
        CheckBox checkBox;
        String path;
        public VideoListViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = (RelativeLayout) itemView.findViewById(R.id.rootView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }

        void bind(Multimedia item, int sizePx) {
            updateSize(sizePx);




            if (!item.getPath().equals(path)) {
                path = item.getPath();
//                Glide.with(imageView)
//                        .load(item.getPath())
//                        .thumbnail(0.1f) // ⭐ 首帧快
//                        .dontAnimate()
//                        .centerCrop()
//                        .into(imageView);

                if(path.toLowerCase().endsWith(".flv")){
                    Glide.with(context).load(FileUtils.getFlvBitmap(path)).into(imageView);
                }else {
                    Glide.with(context)
//                .load(Uri.fromFile(new File(list.get(position).getPath())))
                            .load(item.getPath())
                            .error(R.mipmap.ic_launcher)
//                .apply(new RequestOptions().frame(1000))
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .frame(0)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop() // 裁剪图片以适应ImageView的大小
                            .dontTransform() // 禁用任何额外的转换
                            .dontAnimate()
                            .into(imageView);
                }
            }
        }

        void updateSize(int sizePx) {
            ViewGroup.LayoutParams lp = imageView.getLayoutParams();
            if (lp.width != sizePx) {
                lp.width = sizePx;
                lp.height = sizePx;
                imageView.setLayoutParams(lp);
            }
        }
    }

}
