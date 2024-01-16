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
package com.fde.gallery.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fde.gallery.MainActivity;
import com.fde.gallery.R;
import com.fde.gallery.base.BaseActivity;
import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.common.Constant;
import com.fde.gallery.ui.logic.PicturePreviewPersenter;
import com.fde.gallery.utils.FileUtils;
import com.fde.gallery.utils.LogTools;
import com.fde.gallery.utils.SPUtils;
import com.fde.gallery.utils.StringUtils;
import com.github.chrisbanes.photoview.PhotoView;
import com.yalantis.ucrop.UCrop;

import java.util.List;

public class PicturePreviewActivity extends BaseActivity implements View.OnClickListener {
    Multimedia picture;
    LinearLayout layoutBottomBtn;
    PhotoView imageView;
    ImageView imgDetails;
    ImageView imgLeft;
    ImageView imgRight;

    TextView txtEdit;

    TextView txtDelete;

    TextView txtMore;
    PicturePreviewPersenter picturePreviewPersenter;

    boolean isShowBottomBtn = true;

    PopupWindow popupWindow;
    View bottomSheetView;
    TextView txtDetails;
    TextView txtSetWallpage;
    TextView txtSetWallpageLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogTools.i("------PicturePreviewActivity-----onCreate------");
        setContentView(R.layout.activity_picture_preview);
//        View view = getLayoutInflater().inflate(R.layout.activity_picture_preview,null);
        picture = (Multimedia) getIntent().getSerializableExtra("picture_data");
        List<Multimedia> tempList = FileUtils.getAllImages(context);

        if(picture == null){
            Uri imageUri = getIntent().getData();
            DocumentFile documentFile = DocumentFile.fromSingleUri(context, imageUri);
            String realPath = StringUtils.ToString(documentFile.getUri());
            if("".equals(realPath)){
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }else {
                LogTools.i("realPath   "+realPath);
                Multimedia m = findPicture(tempList,realPath);
                if(m ==null){
                    picture = new Multimedia();
                    picture.setId(-1);
                    picture.setPath(realPath);
                }
                picturePreviewPersenter = new PicturePreviewPersenter(this, picture);
                initView();
            }
        }else {
            String path = SPUtils.getUserInfo(context,"curPicPath");
            Multimedia m = findPicture(tempList,path);
            picture = m;
            picturePreviewPersenter = new PicturePreviewPersenter(this, picture);
            initView();
        }

        popupWindow = new PopupWindow(this);
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        popupWindow.setContentView(bottomSheetView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        txtDetails = bottomSheetView. findViewById(R.id.txtDetails);
        txtSetWallpage = bottomSheetView.findViewById(R.id.txtSetWallpage);
        txtSetWallpageLock = bottomSheetView.findViewById(R.id.txtSetWallpageLock);
        txtDetails.setOnClickListener(this);
        txtSetWallpage.setOnClickListener(this);
        txtSetWallpageLock.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogTools.i("------PicturePreviewActivity-----onDestroy------");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public boolean initView() {
        imageView = (PhotoView) findViewById(R.id.imageView);
        imgDetails = (ImageView) findViewById(R.id.imgDetails);
        imgLeft = (ImageView) findViewById(R.id.imgLeft);
        imgRight = (ImageView) findViewById(R.id.imgRight);
        txtDelete = (TextView) findViewById(R.id.txtDelete);
        txtMore = (TextView) findViewById(R.id.txtMore);
        txtEdit = (TextView) findViewById(R.id.txtEdit);
        layoutBottomBtn = (LinearLayout) findViewById(R.id.layoutBottomBtn);
        txtMore.setOnClickListener(this);
        txtDelete.setOnClickListener(this);
        txtEdit.setOnClickListener(this);
        imgLeft.setOnClickListener(this);
        imgRight.setOnClickListener(this);

        showPic(picture);

//        imageView.setOnContextClickListener(new View.OnContextClickListener() {
//            @Override
//            public boolean onContextClick(View view) {
//                isShowBottomBtn = !isShowBottomBtn;
//                layoutBottomBtn.setVisibility(isShowBottomBtn ? View.VISIBLE : View.GONE);
//                return false;
//            }
//        });
//
//        imageView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                isShowBottomBtn = !isShowBottomBtn;
//                layoutBottomBtn.setVisibility(isShowBottomBtn ? View.VISIBLE : View.GONE);
//                return false;
//            }
//        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(picture.getId() > 0){
                    isShowBottomBtn = !isShowBottomBtn;
                    layoutBottomBtn.setVisibility(isShowBottomBtn ? View.VISIBLE : View.GONE);
                }
            }
        });

//        imageView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
//            @Override
//            public boolean onGenericMotion(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    float x = motionEvent.getX();
//                    float y = motionEvent.getY();
//                    int w = DeviceUtils.getSreenWidth(context);
//                    LogTools.i("onGenericMotion x " + x + " , y: " + y + " ,w " + w);
//                }
//                return false;
//            }
//        });

//        imageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
//                    float x = motionEvent.getX();
//                    float y = motionEvent.getY();
//                    int w = DeviceUtils.getSreenWidth(context);
//                    LogTools.i("onTouch x "+x +" , y: "+y + " ,w "+w);
//                }
//                return false;
//            }
//        });

        if(picture.getId() <= 0){
            isShowBottomBtn = false;
            imgLeft.setVisibility(View.GONE);
            imgRight.setVisibility(View.GONE);
            layoutBottomBtn.setVisibility(View.GONE);
        }else {
            imgLeft.setVisibility(View.VISIBLE);
            imgRight.setVisibility(View.VISIBLE);
            layoutBottomBtn.setVisibility(View.VISIBLE );
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogTools.i("onActivityResult requestCode: " + requestCode + " ,resultCode: " + resultCode);
        if (requestCode == Constant.REQUEST_PERMISSION_DELETE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                picturePreviewPersenter.deleteImage(context);
            }
        } else {
            if (resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    final Uri selectedUri = data.getData();
                    if (selectedUri != null) {
                        picturePreviewPersenter.startCrop(selectedUri);
                    } else {
                        Toast.makeText(context, R.string.toast_cannot_retrieve_selected_image, Toast.LENGTH_SHORT).show();
                    }
                } else if (requestCode == UCrop.REQUEST_CROP) {
                    picturePreviewPersenter.handleCropResult(data);
                }
            }
            if (resultCode == UCrop.RESULT_ERROR) {
                picturePreviewPersenter.handleCropError(data);
            }
        }
    }

    public void showPic(Multimedia multimedia) {
        if (multimedia != null &&  !"".equals(multimedia.getPath() )) {
            try {
                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(2800, 2800);

                Glide.with(context) // replace 'this' with your context
                        .load(multimedia.getPath())
                        .fitCenter()
                        .into(imageView);
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtMore:
                if(!popupWindow.isShowing()){
                    popupWindow.showAtLocation(bottomSheetView, Gravity.BOTTOM, 0, 0);
                }
                break;

            case R.id.txtDelete:
                picturePreviewPersenter.showDelDlg();
                break;


            case R.id.txtEdit:
//                MultiTransformation mation3 = new MultiTransformation(new CircleCrop());
//                RequestOptions options1 = new RequestOptions();
//                options1.override(200,200);
//                MultiTransformation mation1 = new MultiTransformation(new CenterCrop());
////                MultiTransformation mation3 = new MultiTransformation(new GranularRoundedCorners());
//                Glide.with(context) // replace 'this' with your context
//                        .load(picture.getPath())
////                        .apply(RequestOptions.bitmapTransform(mation1))
////                        .apply(RequestOptions.bitmapTransform(new MultiTransformation<Bitmap>(new GrayscaleTransformation())))
////                        .apply(options1)
//                        .into(imageView);


                picturePreviewPersenter.startCrop();
                break;

            case R.id.imgLeft:
                Multimedia prePic = picturePreviewPersenter.getPrePic();
                showPic(prePic);
                break;

            case R.id.imgRight:
                Multimedia nextPic = picturePreviewPersenter.getNextPic();
                showPic(nextPic);
                break;

            case R.id.txtDetails:
                picturePreviewPersenter.showDetailsDlg();
                popupWindow.dismiss();
                break;

            case R.id.txtSetWallpage:
                picturePreviewPersenter.setWallpage(1);
                popupWindow.dismiss();
                break;

            case R.id.txtSetWallpageLock:
                picturePreviewPersenter.setWallpage(2);
                popupWindow.dismiss();
                break;

            default:

                break;
        }
    }

    public Multimedia findPicture(List<Multimedia> listM, String path) {
        int curPos = -1 ;
        try {
            for (int i = 0; i < listM.size(); i++) {
                if (path.equals(listM.get(i).getPath())) {
                    curPos = i;
                }
            }
            return listM.get(curPos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}