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
package com.fde.gallery.ui.logic;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fde.gallery.R;
import com.fde.gallery.adapter.VideoListAdapter;
import com.fde.gallery.bean.Video;
import com.fde.gallery.utils.DeviceUtils;
import com.fde.gallery.utils.LogTools;
import com.fde.gallery.utils.StringUtils;
import com.fde.gallery.view.AutoFitGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class VideoListPersenter {
    Context context;
    View view;

    VideoListAdapter videoListAdapter;
    RecyclerView recyclerView;
    List<Video> list;

    int numberOfColumns = 3;
    public VideoListPersenter(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public  boolean initView(){
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, numberOfColumns){
            @Override
            public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
                super.onMeasure(recycler, state, widthSpec, heightSpec);
                int sreenWidth = DeviceUtils.getSreenWidth(context);
//                LogTools.i("getSreenWidth: "+ DeviceUtils.getSreenWidth(context)  + "  , getSreenHight : "+DeviceUtils.getSreenHight(context));
//                int measuredWidth = recyclerView.getMeasuredWidth();
//                int measuredHeight = recyclerView.getMeasuredHeight();
//                int myMeasureHeight = 0;
//                int count = state.getItemCount();
//                for (int i = 0; i < count; i++) {
//                    View view = recycler.getViewForPosition(i);
//                    if (view != null) {
//                        if (myMeasureHeight < measuredHeight && i % numberOfColumns == 0) {
//                            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
//                            int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
//                                    getPaddingLeft() + getPaddingRight(), p.width);
//                            int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
//                                    getPaddingTop() + getPaddingBottom(), p.height);
//                            view.measure(childWidthSpec, childHeightSpec);
//                            myMeasureHeight += view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
//                        }
//                        recycler.recycleView(view);
//                    }
//                }
//                setMeasuredDimension(measuredWidth, Math.min(measuredHeight, myMeasureHeight));
                int width = StringUtils.ToInt(sreenWidth / numberOfColumns);
                setMeasuredDimension(width, width);
            }
        };
        recyclerView.setLayoutManager(gridLayoutManager);
        list = new ArrayList<>();
        videoListAdapter = new VideoListAdapter(context, list);
        recyclerView.setAdapter(videoListAdapter);
        return  true;
    }

    /**
     * get all video
     * @param context
     */
    public void getAllVideos(Context context) {
        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION,
        };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, // The content URI of the words table
                projection,   // The columns to return for each row
                null,         // Selection criteria
                null,         // Selection criteria
                null);        // The sort order for the returned rows

        if (cursor != null) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            int dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
            int widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH);
            int heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);


            while (cursor.moveToNext()) {
                Video video = new Video();
                video.setId(cursor.getLong(idColumn));
                video.setPath( cursor.getString(dataColumn));
                video.setSize(cursor.getLong(sizeColumn));
                video.setDuration(cursor.getInt(durationColumn));
                video.setTitle(cursor.getString(titleColumn));
                list.add(video);
                LogTools.i("video "+video.toString());
            }
            videoListAdapter.notifyDataSetChanged();
            cursor.close();
        }
    }
}
