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
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fde.gallery.R;
import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.event.ViewEvent;
import com.fde.gallery.utils.LogTools;

import java.util.List;

public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.PictureListViewHolder> {
    List<Multimedia> list;
    Context context;
    int numberOfColumns;
    ViewEvent viewEvent;
    int itemSizePx;
    static final int PAYLOAD_SIZE = 1;
    public PictureListAdapter(Context context, List<Multimedia> list, int numberOfColumns, ViewEvent viewEvent) {
        this.list = list;
        this.context = context;
        this.numberOfColumns = numberOfColumns;
        this.viewEvent = viewEvent;
    }

    @NonNull
    @Override
    public PictureListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_picture_list, parent, false);
        return new PictureListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PictureListViewHolder holder,
            int position,
            @NonNull List<Object> payloads) {

        if (!payloads.isEmpty() && payloads.contains(PAYLOAD_SIZE)) {
            holder.updateSize(itemSizePx);
            return;
        }
        onBindViewHolder(holder, position);
    }

    @Override
    public void onBindViewHolder(@NonNull PictureListViewHolder holder, @SuppressLint("RecyclerView")  final int position) {
//        holder.imageView.setImageURI(Uri.parse(list.get(position).getPath()));
        Multimedia picture = list.get(position);
        holder.bind(picture, itemSizePx);

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewEvent.onJumpEvent(picture);
            }
        });

        holder.checkBox.setVisibility(picture.isShowCheckbox() ?View.VISIBLE:View.INVISIBLE);
        holder.checkBox.setChecked(picture.isSelected());

        holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
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

        holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
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


    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    public void updateItemSize(int sizePx) {
        if (itemSizePx != sizePx) {
            itemSizePx = sizePx;
            notifyItemRangeChanged(0, getItemCount(), PAYLOAD_SIZE);
        }
    }

    class PictureListViewHolder extends RecyclerView.ViewHolder {
        FrameLayout rootView;
        ImageView imageView;
        CheckBox checkBox;
        String path;

        public PictureListViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = (FrameLayout) itemView.findViewById(R.id.rootView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }

        void bind(Multimedia item, int sizePx) {
            updateSize(sizePx);

            if (!item.getPath().equals(path)) {
                path = item.getPath();
                Glide.with(imageView)
                        .load(item.getPath())
                        .error(R.mipmap.ic_launcher)
                        .thumbnail(0.1f) // ⭐ 首帧快
                        .dontAnimate()
                        .centerCrop()
                        .into(imageView);
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
