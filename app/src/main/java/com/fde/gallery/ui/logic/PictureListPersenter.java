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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fde.gallery.R;
import com.fde.gallery.adapter.PictureListAdapter;
import com.fde.gallery.base.BaseFragment;
import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.common.Constant;
import com.fde.gallery.event.ViewEvent;
import com.fde.gallery.ui.activity.PicturePreviewActivity;
import com.fde.gallery.utils.DeviceUtils;
import com.fde.gallery.utils.FileUtils;
import com.fde.gallery.utils.LogTools;
import com.fde.gallery.utils.SPUtils;
import com.fde.gallery.view.CustomScrollBarView;
import com.fde.gallery.view.RecyclerScrollBinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PictureListPersenter implements ViewEvent, View.OnClickListener {
    Context context;
    View view;



    PictureListAdapter pictureListAdapter;
    RecyclerView recyclerView;
    List<Multimedia> list;
    List<Multimedia> delList;

    View layoutBottomBtn;
    TextView txtShare;
    TextView txtDelete;
    TextView txtAllSelected;
    boolean isAllSelected;
    boolean isShowBottomBtn = false;

    int numberOfColumns = 3;


    BaseFragment baseFragment;

    GridLayoutManager gridLayoutManager;

    public PictureListPersenter(BaseFragment baseFragment, View view) {
        this.baseFragment = baseFragment;
        this.view = view;
        context = baseFragment.requireActivity();
    }

    public boolean initView() {
        numberOfColumns = DeviceUtils.getShowCount(baseFragment.getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        layoutBottomBtn = (View) view.findViewById(R.id.layoutBottom);
        txtShare = (TextView) view.findViewById(R.id.txtShare);
        txtDelete = (TextView) view.findViewById(R.id.txtDelete);
        txtAllSelected = (TextView) view.findViewById(R.id.txtAllSelected);
        CustomScrollBarView bar = (CustomScrollBarView)view.findViewById(R.id.scrollBar);
        txtShare.setOnClickListener(this);
        txtDelete.setOnClickListener(this);
        txtAllSelected.setOnClickListener(this);
        gridLayoutManager = new GridLayoutManager(context, 3);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        listenWindowResize();
//        recyclerView.addItemDecoration(new SpacesItemDecoration(2));
        list = new ArrayList<>();
        pictureListAdapter = new PictureListAdapter(context, list, numberOfColumns, this);
        pictureListAdapter.setHasStableIds(true);
        recyclerView.setAdapter(pictureListAdapter);

        RecyclerScrollBinder.bind(recyclerView, bar);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            private long touchDownTime = 0;

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    touchDownTime = System.currentTimeMillis();
                } else if (e.getAction() == MotionEvent.ACTION_UP) {
                    long duration = System.currentTimeMillis() - touchDownTime;
                    if (duration >= ViewConfiguration.getLongPressTimeout()) {
                        View childView = rv.findChildViewUnder(e.getX(), e.getY());
                        if (childView != null) {
                            int position = rv.getChildAdapterPosition(childView);
                            onRightEvent(position, 0);
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });


        recyclerView.setOnContextClickListener(new View.OnContextClickListener() {

            @Override
            public boolean onContextClick(View v) {
                onRightEvent(0,0);
                return false;
            }
        });

        return true;
    }

    private void listenWindowResize() {
        View root = baseFragment.getActivity().getWindow().getDecorView();
        root.addOnLayoutChangeListener(
                (v, l, t, r, b, ol, ot, orr, ob) -> {
                    int newW = r - l;
                    int oldW = orr - ol;
                    if (newW != oldW) {
                        onWindowWidthChanged(newW);
                    }
                });
    }

    private void onWindowWidthChanged(int widthPx) {
        int minItemPx = DeviceUtils.dpToPx(context, 100);
        int span = Math.max(1, widthPx / minItemPx);

        gridLayoutManager.setSpanCount(span);

        int itemSize = widthPx / span;
        pictureListAdapter.updateItemSize(itemSize);
    }

    /***
     * get all picture
     * @param context
     */
    public void getAllImages(Context context) {
        if (list != null) {
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
    public void onRightEvent(int pos, int groupPos) {
        isShowBottomBtn = !isShowBottomBtn;
        showOrHideBottomBtn(isShowBottomBtn);
    }

    public void showOrHideBottomBtn(boolean isShow) {
        isShowBottomBtn = isShow;
        try {
            layoutBottomBtn.setVisibility(isShowBottomBtn ? View.VISIBLE : View.GONE);
            for (int i = 0; i < list.size(); i++) {
                Multimedia picture = list.get(i);
                picture.setShowCheckbox(isShowBottomBtn);
                list.set(i, picture);
            }
            pictureListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(isShow){
            DeviceUtils.setBottomMargin(recyclerView, DeviceUtils.dpToPx(context, 0));
        }else {
            DeviceUtils.setBottomMargin(recyclerView, DeviceUtils.dpToPx(context, 50));
        }
    }

    public boolean hideBottomBtn() {
        if(layoutBottomBtn.getVisibility() == View.GONE){
            return false;
        }
        if(layoutBottomBtn !=null){
            showOrHideBottomBtn(false);
        }
        return true;
    }

    @Override
    public void onSelectEvent(int pos, int groupPos, boolean isSelect) {
        try {
            Multimedia picture = list.get(pos);
            picture.setSelected(isSelect);
            list.set(pos, picture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onJumpEvent(Multimedia picture) {
        SPUtils.putUserInfo(context, "curPicPath", picture.getPath());
        Intent intent = new Intent();
        intent.putExtra("picture_data", picture);
        intent.setClass(context, PicturePreviewActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        baseFragment.getActivity().startActivity(intent);
//        baseFragment.getActivity().startActivityFromFragment(baseFragment, intent, Constant.REQUEST_DELETE_PHOTO);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtShare:
                try {
                    ArrayList<Uri> imageUris = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isSelected()) {
                            imageUris.add(FileProvider.getUriForFile(context, Constant.PKG_PROVIDER, new File(list.get(i).getPath())));
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
                if (delList == null || delList.size() < 1) {
                    baseFragment.showShortToast(context.getString(R.string.can_not_choose_empty));
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context,com.fde.imageeditlibrary.R.style.RoundedAlertDialog);
                builder.setTitle(R.string.is_delete);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {

                        list.clear();
                        list.addAll(tempList);
                        deleteImage();
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.show();

                break;

            case R.id.txtAllSelected:
                isAllSelected = !isAllSelected;
                for (int i = 0; i < list.size(); i++) {
                    Multimedia picture = list.get(i);
                    picture.setSelected(isAllSelected);
                    list.set(i, picture);
                }
                pictureListAdapter.notifyDataSetChanged();
                txtAllSelected.setText(isAllSelected ? context.getString(R.string.deselect_all) : context.getString(R.string.select_all));
                Drawable drawableTop = isAllSelected ? context.getDrawable(R.mipmap.icon_select_none) : context.getDrawable(R.mipmap.icon_select_all);
                drawableTop.setBounds(0, 0, drawableTop.getIntrinsicWidth(), drawableTop.getIntrinsicHeight());
                txtAllSelected.setCompoundDrawables(null, drawableTop, null, null);
                break;
        }
    }

    @SuppressLint("NewApi")
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
