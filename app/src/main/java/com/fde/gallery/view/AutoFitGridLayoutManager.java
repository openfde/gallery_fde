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
package com.fde.gallery.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AutoFitGridLayoutManager extends GridLayoutManager {
    Context context ;
    int numberOfColumns ;
    RecyclerView recyclerView;

    public AutoFitGridLayoutManager(Context context, int numberOfColumns,RecyclerView recyclerView) {
        super(context, 1);
        this.context  = context;
        this.numberOfColumns = numberOfColumns;
        this.recyclerView = recyclerView ;
    }

//    @Override
//    public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
//        super.onMeasure(recycler, state, widthSpec, heightSpec);
//        int measuredWidth = recyclerView.getMeasuredWidth();
//        int measuredHeight = recyclerView.getMeasuredHeight();
//        int myMeasureHeight = 0;
//        int count = state.getItemCount();
//        for (int i = 0; i < count; i++) {
//            View view = recycler.getViewForPosition(i);
//            if (view != null) {
//                if (myMeasureHeight < measuredHeight && i % numberOfColumns == 0) {
//                    RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
//                    int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
//                            getPaddingLeft() + getPaddingRight(), p.width);
//                    int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
//                            getPaddingTop() + getPaddingBottom(), p.height);
//                    view.measure(childWidthSpec, childHeightSpec);
//                    myMeasureHeight += view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
//                }
//                recycler.recycleView(view);
//            }
//        }
//        setMeasuredDimension(measuredWidth, Math.min(measuredHeight, myMeasureHeight));
//    }
}
