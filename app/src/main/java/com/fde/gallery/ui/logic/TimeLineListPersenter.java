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
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fde.gallery.R;
import com.fde.gallery.adapter.TimeLineListAdapter;
import com.fde.gallery.base.BaseFragment;
import com.fde.gallery.bean.MultGroup;
import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.common.Constant;
import com.fde.gallery.event.ViewEvent;
import com.fde.gallery.ui.activity.PicturePreviewActivity;
import com.fde.gallery.utils.DeviceUtils;
import com.fde.gallery.utils.FileUtils;
import com.fde.gallery.utils.LogTools;
import com.fde.gallery.utils.SPUtils;
import com.fde.gallery.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TimeLineListPersenter implements ViewEvent, View.OnClickListener {
    Context context;
    View view;
    RecyclerView recyclerView;
    TimeLineListPersenter timeLineListPersenter;
    TimeLineListAdapter timeLineListAdapter;
    List<Multimedia> list;

    List<MultGroup> listGroup;
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

    public boolean initView() {
        numberOfColumns = DeviceUtils.getShowCount(baseFragment.getActivity());
        list = new ArrayList<>();
        listGroup = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        layoutBottomBtn = (LinearLayout) view.findViewById(R.id.layoutBottomBtn);
        txtShare = (TextView) view.findViewById(R.id.txtShare);
        txtDelete = (TextView) view.findViewById(R.id.txtDelete);
        txtAllSelected = (TextView) view.findViewById(R.id.txtAllSelected);
        txtShare.setOnClickListener(this);
        txtDelete.setOnClickListener(this);
        txtAllSelected.setOnClickListener(this);
        timeLineListAdapter = new TimeLineListAdapter(context, listGroup, numberOfColumns, this);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(context);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(timeLineListAdapter);
        return true;
    }

    public void getAllMedia(Context context) {
        if (list != null) {
            list.clear();
            listGroup.clear();
        }
        list.addAll(FileUtils.getAllImages(context));
        list.addAll(FileUtils.getAllVideos(context));


        Map<String, List<Multimedia>> groupedMult = list.stream()
                .collect(Collectors.groupingBy(Multimedia::getDate));

        for (Map.Entry<String, List<Multimedia>> entry : groupedMult.entrySet()) {
            LogTools.i("Group by date: " + entry.getKey());
            MultGroup multGroup = new MultGroup();
            List<Multimedia> tempList = new ArrayList<>();
            for (Multimedia data : entry.getValue()) {
                tempList.add(data);
                LogTools.i("\t" + data.getTitle() + ", " + StringUtils.conversionTime(data.getDateTaken() * 1000));
            }

            Collections.sort(tempList, new Comparator<Multimedia>() {
                @Override
                public int compare(Multimedia o1, Multimedia o2) {
                    return Long.compare(o2.getDateTaken(), o1.getDateTaken());
                }
            });
            multGroup.setTitle(entry.getKey());
            multGroup.setList(tempList);
            listGroup.add(multGroup);
        }

        Collections.sort(listGroup, new Comparator<MultGroup>() {
            @Override
            public int compare(MultGroup o1, MultGroup o2) {
                return Long.compare(StringUtils.convertDateStringToLong(o2.getTitle(), "yyyy年MM月"), StringUtils.convertDateStringToLong(o1.getTitle(), "yyyy年MM月"));
            }
        });
        timeLineListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRightEvent(int pos, int groupPos) {
        LogTools.i("onRightEvent " + pos + " ,groupPos " + groupPos);
        isShowBottomBtn = !isShowBottomBtn;
        try {
            layoutBottomBtn.setVisibility(isShowBottomBtn ? View.VISIBLE : View.GONE);
            for (int i = 0; i < listGroup.size(); i++) {
                MultGroup multGroup = listGroup.get(i);
                List<Multimedia> tl = multGroup.getList();
                for (int j = 0; j < tl.size(); j++) {
                    Multimedia multimedia = tl.get(j);
                    multimedia.setShowCheckbox(isShowBottomBtn);
                    tl.set(j, multimedia);
                }
                multGroup.setList(tl);
                listGroup.set(i, multGroup);
            }
            timeLineListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSelectEvent(int pos, int groupPos, boolean isSelect) {
        LogTools.i("onSelectEvent " + pos + " ,groupPos " + groupPos);
        try {
            MultGroup multGroup = listGroup.get(groupPos);
            List<Multimedia> tempList = multGroup.getList();
            Multimedia multimedia = tempList.get(pos);
            multimedia.setSelected(isSelect);
            tempList.set(pos, multimedia);
            multGroup.setList(tempList);
            listGroup.set(groupPos, multGroup);
//            timeLineListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onJumpEvent(Multimedia picture) {
        SPUtils.putUserInfo(context,"curPicPath",picture.getPath());
        Intent intent = new Intent();
        intent.putExtra("picture_data", picture);
        intent.setClass(context, PicturePreviewActivity.class);
        baseFragment.getActivity().startActivityFromFragment(baseFragment, intent, Constant.REQUEST_DELETE_PHOTO);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtShare:
                try {
                    ArrayList<Uri> imageUris = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isSelected()) {
                            imageUris.add(FileProvider.getUriForFile(context, "com.fde.gallery.provider", new File(list.get(i).getPath())));
                        }
                    }
                    int size = imageUris.size();
                    if (size < 1) {
                        baseFragment.showShortToast(context.getString(R.string.can_not_choose_empty));
                        return;
                    } else if (size > 9) {
                        baseFragment.showShortToast(context.getString(R.string.can_not_choose_too_more));
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.setType("image/*"); //set MIME type
//                    intent.putExtra(Intent.EXTRA_STREAM, imageUris.get(0)); //
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                    baseFragment.getActivity().startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.txtDelete:
                delList = new ArrayList<>();
                for (int i = 0; i < listGroup.size(); i++) {
                    MultGroup multGroup = listGroup.get(i);
                    for (int j = 0; j < multGroup.getList().size(); j++) {
                        Multimedia multimedia = multGroup.getList().get(j);
                        if (multimedia.isSelected()) {
                            delList.add(multimedia);
                        }
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
                        deleteMultiMedia();
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
                break;

            case R.id.txtAllSelected:
                isAllSelected = !isAllSelected;

                for (int i = 0; i < listGroup.size(); i++) {
                    MultGroup multGroup = listGroup.get(i);
                    List<Multimedia> tl = multGroup.getList();
                    for (int j = 0; j < tl.size(); j++) {
                        Multimedia multimedia = tl.get(j);
                        multimedia.setSelected(isAllSelected);
                        tl.set(j, multimedia);
                    }
                    multGroup.setList(tl);
                    listGroup.set(i, multGroup);
                }

                timeLineListAdapter.notifyDataSetChanged();
                txtAllSelected.setText(isAllSelected ? context.getString(R.string.deselect_all) : context.getString(R.string.select_all));
                break;
        }
    }

    @SuppressLint("NewApi")
    public void deleteMultiMedia() {
        if (delList != null) {
            try {
                // 你的删除或修改文件的代码
                for (Multimedia multimedia : delList) {
                    if (multimedia.getMediaType() == Constant.MEDIA_PIC) {
                        FileUtils.deleteImage(context, multimedia.getPath());
                    } else {
                        FileUtils.deleteVideo(context, multimedia.getPath());
                    }
                }
                getAllMedia(context);
            } catch (RecoverableSecurityException e) {
                baseFragment.requestConfirmDialog(e);
            }
        }
    }
}
