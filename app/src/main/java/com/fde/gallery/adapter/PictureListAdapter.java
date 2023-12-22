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
import com.fde.gallery.R;
import com.fde.gallery.bean.Picture;
import com.fde.gallery.ui.activity.PicturePreviewActivity;
import com.fde.gallery.utils.LogTools;

import java.io.File;
import java.util.List;

public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.PictureListViewHolder> {
    List<Picture> list;
    Context context;

    public PictureListAdapter(Context context, List<Picture> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public PictureListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_picture_list, parent, false);
        PictureListViewHolder holder = new PictureListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PictureListViewHolder holder, int position) {
//        holder.imageView.setImageURI(Uri.parse(list.get(position).getPath()));
        Glide.with(context)
                .load(Uri.fromFile(new File(list.get(position).getPath())))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Picture picture = list.get(position);
                LogTools.i("picture"+picture.toString());
                Intent intent = new Intent();
                intent.putExtra("picture_data",picture);
                intent.setClass(context, PicturePreviewActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class PictureListViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootView;
        ImageView imageView;
        public PictureListViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = (LinearLayout) itemView.findViewById(R.id.rootView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

}