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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.RecoverableSecurityException;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fde.gallery.R;
import com.fde.gallery.adapter.VideoListAdapter;
import com.fde.gallery.base.BaseFragment;
import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.event.ViewEvent;
import com.fde.gallery.utils.DeviceUtils;
import com.fde.gallery.utils.FileUtils;
import com.fde.gallery.utils.LogTools;

import java.util.ArrayList;
import java.util.List;

public class VideoListPersenter implements ViewEvent, View.OnClickListener {
    Context context;
    View view;
    VideoListAdapter videoListAdapter;
    LinearLayout layoutBottomBtn;
    RecyclerView recyclerView;
    TextView txtShare;
    TextView txtDelete;
    TextView txtAllSelected;
    List<Multimedia> list;
    List<Multimedia> delList;
    int numberOfColumns = 3;

    boolean isAllSelected;

    boolean isShowBottomBtn = false;

    BaseFragment baseFragment;

    public VideoListPersenter(BaseFragment baseFragment, View view) {
        this.baseFragment = baseFragment;
        this.view = view;
        context = baseFragment.getActivity();
    }

    public boolean initView() {
        numberOfColumns = DeviceUtils.getShowCount(baseFragment.getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        layoutBottomBtn = (LinearLayout) view.findViewById(R.id.layoutBottomBtn);
        txtShare = (TextView) view.findViewById(R.id.txtShare);
        txtDelete = (TextView) view.findViewById(R.id.txtDelete);
        txtAllSelected = (TextView) view.findViewById(R.id.txtAllSelected);
        txtShare.setOnClickListener(this);
        txtDelete.setOnClickListener(this);
        txtAllSelected.setOnClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
//        recyclerView.addItemDecoration(new SpacesItemDecoration(2));  // Here 16 is the space size
        list = new ArrayList<>();
        videoListAdapter = new VideoListAdapter(context, list, numberOfColumns, this);
        recyclerView.setAdapter(videoListAdapter);
        return true;
    }

    /**
     * get all video
     *
     * @param context
     */
    public void getAllVideos(Context context) {
        if (list != null) {
            list.clear();
        }
        list.addAll(FileUtils.getAllVideos(context));
        videoListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRightEvent(int pos, int groupPos) {
        isShowBottomBtn = !isShowBottomBtn;
        try {
            layoutBottomBtn.setVisibility(isShowBottomBtn ? View.VISIBLE : View.GONE);
            for (int i = 0; i < list.size(); i++) {
                Multimedia video = list.get(i);
                video.setShowCheckbox(isShowBottomBtn);
                list.set(i, video);
            }
            videoListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onJumpEvent(Multimedia multimedia) {

    }

    @Override
    public void onSelectEvent(int pos, int groupPos, boolean isSelect) {
        try {
            Multimedia video = list.get(pos);
            video.setSelected(isSelect);
            list.set(pos, video);
        } catch (Exception e) {
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
                    Multimedia video = list.get(i);
                    if (video.isSelected()) {
                        delList.add(video);
                    } else {
                        tempList.add(video);
                    }
                }
                if(delList ==null ||delList.size() <1){
                    baseFragment.showShortToast(context.getString(R.string.can_not_choose_empty));
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.is_delete);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {

                        list.clear();
                        list.addAll(tempList);
                        deleteVideo();
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.show();

                break;

            case R.id.txtAllSelected:
                isAllSelected = !isAllSelected;
                for (int i = 0; i < list.size(); i++) {
                    Multimedia video = list.get(i);
                    video.setSelected(isAllSelected);
                    list.set(i, video);
                }
                videoListAdapter.notifyDataSetChanged();
                txtAllSelected.setText(isAllSelected ? context.getString(R.string.deselect_all) : context.getString(R.string.select_all));
                break;
        }
    }

    @SuppressLint("NewApi")
    public void deleteVideo() {
        if (delList != null) {
            try {
                // 你的删除或修改文件的代码
                for (Multimedia video : delList) {
                    FileUtils.deleteVideo(context, video.getPath());
                }
                videoListAdapter.notifyDataSetChanged();
            } catch (RecoverableSecurityException e) {
                baseFragment.requestConfirmDialog(e);
            }
        }
    }
}
