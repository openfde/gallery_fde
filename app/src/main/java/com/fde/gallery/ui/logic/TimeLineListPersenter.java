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

import android.app.RecoverableSecurityException;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fde.gallery.R;
import com.fde.gallery.adapter.TimeLineListAdapter;
import com.fde.gallery.base.BaseFragment;
import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.common.Constant;
import com.fde.gallery.event.ViewEvent;
import com.fde.gallery.utils.FileUtils;
import com.fde.gallery.utils.LogTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TimeLineListPersenter implements ViewEvent, View.OnClickListener {
    Context context;
    View view;

    RecyclerView recyclerView ;
    TimeLineListPersenter timeLineListPersenter ;
    TimeLineListAdapter timeLineListAdapter ;
    List<Multimedia> list;
    List<Multimedia> delList;
    int numberOfColumns = 3;

    LinearLayout layoutBottomBtn;
    TextView txtShare;
    TextView txtDelete;
    TextView txtAllSelected;

    boolean isAllSelected;
    boolean isShowBottomBtn = false;

    BaseFragment baseFragment;
    public TimeLineListPersenter(BaseFragment baseFragment, View view) {
        this.baseFragment = baseFragment;
        this.view = view;
        context = baseFragment.getActivity();
    }

    public  boolean initView(){
        list = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        layoutBottomBtn = (LinearLayout) view.findViewById(R.id.layoutBottomBtn);
        txtShare = (TextView) view.findViewById(R.id.txtShare);
        txtDelete = (TextView) view.findViewById(R.id.txtDelete);
        txtAllSelected = (TextView) view.findViewById(R.id.txtAllSelected);
        txtShare.setOnClickListener(this);
        txtDelete.setOnClickListener(this);
        txtAllSelected.setOnClickListener(this);
        timeLineListAdapter = new TimeLineListAdapter(context,list,numberOfColumns,this);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(timeLineListAdapter);
        return  true;
    }

    public void getAllMedia(Context context) {
        if(list !=null){
            list.clear();
        }
        list.addAll(FileUtils.getAllImages(context));
        list.addAll(FileUtils.getAllVideos(context));
        Collections.sort(list, new Comparator<Multimedia>() {
            @Override
            public int compare(Multimedia o1, Multimedia o2) {
                return Long.compare(o1.getDateTaken(), o2.getDateTaken());
            }
        });
        LogTools.i("list size "+list.size());
        timeLineListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRightEvent(int pos) {
        isShowBottomBtn = !isShowBottomBtn;
        try {
            layoutBottomBtn.setVisibility(isShowBottomBtn ? View.VISIBLE : View.GONE);
            for (int i = 0; i < list.size(); i++) {
                Multimedia multimedia = list.get(i);
                multimedia.setShowCheckbox(isShowBottomBtn);
                list.set(i, multimedia);
            }
            timeLineListAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSelectEvent(int pos, boolean isSelect) {
        try {
            Multimedia multimedia = list.get(pos);
            multimedia.setSelected(isSelect);
            list.set(pos, multimedia);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtShare:
                LogTools.i("list " + list.toString());
                break;

            case R.id.txtDelete:
                List<Multimedia> tempList = new ArrayList<>();
                delList = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    Multimedia multimedia = list.get(i);
                    if (multimedia.isSelected()) {
                        delList.add(multimedia);
                    } else {
                        tempList.add(multimedia);
                    }
                }
                list.clear();
                list.addAll(tempList);
                deleteImage();
                break;

            case R.id.txtAllSelected:
                isAllSelected = !isAllSelected;
                for (int i = 0; i < list.size(); i++) {
                    Multimedia multimedia = list.get(i);
                    multimedia.setSelected(isAllSelected);
                    list.set(i, multimedia);
                }
                timeLineListAdapter.notifyDataSetChanged();
                txtAllSelected.setText(isAllSelected? context.getString(R.string.deselect_all) :context.getString(R.string.select_all) );
                break;
        }
    }

    public void deleteImage() {
        if (delList != null) {
            try {
                // 你的删除或修改文件的代码
                for (Multimedia multimedia : delList) {
                    if(multimedia.getMediaType() == Constant.MEDIA_PIC){
                        FileUtils.deleteImage(context, multimedia.getPath());
                    }else {
                        FileUtils.deleteVideo(context, multimedia.getPath());
                    }
                }
                timeLineListAdapter.notifyDataSetChanged();
            } catch (RecoverableSecurityException e) {
                baseFragment.requestConfirmDialog(e);
            }
        }
    }
}
