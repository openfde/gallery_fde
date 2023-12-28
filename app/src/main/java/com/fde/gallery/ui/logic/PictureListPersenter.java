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
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fde.gallery.R;
import com.fde.gallery.adapter.PictureListAdapter;
import com.fde.gallery.base.BaseFragment;
import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.event.ViewEvent;
import com.fde.gallery.utils.FileUtils;
import com.fde.gallery.utils.LogTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PictureListPersenter implements ViewEvent, View.OnClickListener {
    Context context;
    View view;

    LinearLayout layoutBottomBtn;

    PictureListAdapter pictureListAdapter;
    RecyclerView recyclerView;
    List<Multimedia> list;
    List<Multimedia> delList;
    TextView txtShare;

    TextView txtDelete;

    TextView txtAllSelected;

    int numberOfColumns = 3;

    boolean isAllSelected;

    boolean isShowBottomBtn = false;

    BaseFragment baseFragment;

    public PictureListPersenter(BaseFragment baseFragment, View view) {
        this.baseFragment = baseFragment;
        this.view = view;
        context = baseFragment.getActivity();
    }

    public boolean initView() {
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
//        recyclerView.addItemDecoration(new SpacesItemDecoration(2));
        list = new ArrayList<>();
        pictureListAdapter = new PictureListAdapter(context, list, numberOfColumns, this);
        recyclerView.setAdapter(pictureListAdapter);
        return true;
    }

    /***
     * get all picture
     * @param context
     */
    public void getAllImages(Context context) {
            if(list !=null){
                list.clear();
            }
            list.addAll(FileUtils.getAllImages(context));
            if (pictureListAdapter == null) {
                LogTools.i("pictureListAdapter is null");
            } else {
                pictureListAdapter.notifyDataSetChanged();
            }
    }

    @Override
    public void onRightEvent(int pos) {
        isShowBottomBtn = !isShowBottomBtn;

        try {
            layoutBottomBtn.setVisibility(isShowBottomBtn ? View.VISIBLE : View.GONE);
            for (int i = 0; i < list.size(); i++) {
                Multimedia picture = list.get(i);
                picture.setShowCheckbox(isShowBottomBtn);
                list.set(i, picture);
            }
            pictureListAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSelectEvent(int pos, boolean isSelect) {
     try {
         Multimedia picture = list.get(pos);
         picture.setSelected(isSelect);
         list.set(pos, picture);
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
                    Multimedia picture = list.get(i);
                    if (picture.isSelected()) {
                        delList.add(picture);
                    } else {
                        tempList.add(picture);
                    }
                }
                list.clear();
                list.addAll(tempList);
                deleteImage();
                break;

            case R.id.txtAllSelected:
                isAllSelected = !isAllSelected;
                for (int i = 0; i < list.size(); i++) {
                    Multimedia picture = list.get(i);
                    picture.setSelected(isAllSelected);
                    list.set(i, picture);
                }
                pictureListAdapter.notifyDataSetChanged();
                txtAllSelected.setText(isAllSelected? context.getString(R.string.deselect_all) :context.getString(R.string.select_all) );
                break;
        }
    }

    public void deleteImage() {
        if (delList != null) {
            try {
                // 你的删除或修改文件的代码
                for (Multimedia picture : delList) {
                    FileUtils.deleteImage(context, picture.getPath());
                }
                pictureListAdapter.notifyDataSetChanged();
            } catch (RecoverableSecurityException e) {
                baseFragment.requestConfirmDialog(e);
            }
        }
    }
}
