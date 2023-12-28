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
import android.app.AlertDialog;
import android.app.RecoverableSecurityException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Picture;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fde.gallery.R;
import com.fde.gallery.base.BaseActivity;
import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.ui.logic.PicturePreviewPersenter;
import com.fde.gallery.utils.FileUtils;
import com.fde.gallery.utils.LogTools;
import com.fde.gallery.utils.StringUtils;
import com.github.chrisbanes.photoview.PhotoView;

public class PicturePreviewActivity extends BaseActivity {
    Multimedia picture;

    PhotoView imageView;
    ImageView imgDetails;
    PicturePreviewPersenter picturePreviewPersenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
//        View view = getLayoutInflater().inflate(R.layout.activity_picture_preview,null);
//        picturePreviewPersenter = new PicturePreviewPersenter(this,view);
        initView();
    }


    public boolean initView() {
        picture = (Multimedia) getIntent().getSerializableExtra("picture_data");
        LogTools.i("picture " + picture.toString());

        imageView = (PhotoView) findViewById(R.id.imageView);
        imgDetails = (ImageView) findViewById(R.id.imgDetails);
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(800, 800);

        Glide.with(context) // replace 'this' with your context
                .load(picture.getPath())
                .apply(options)
                .into(imageView);

        imageView.setOnContextClickListener(new View.OnContextClickListener() {
            @Override
            public boolean onContextClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.is_delete);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            // 你的删除或修改文件的代码
                            FileUtils.deleteImage(context, picture.getPath());
                            finish();
                        } catch (RecoverableSecurityException e) {
                            requestConfirmDialog(e);
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
                return false;
            }
        });

        imgDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.details);
                builder.setMessage("title:  " + picture.getTitle() + "\n"
                        + "width:  " + picture.getWidth() + "\n"
                        + "height:  " + picture.getHeight() + "\n"
                        + "date:   " + StringUtils.conversionTime(1000 * picture.getDateTaken()) + "\n"
                        + "path:  " + picture.getPath() + "\n"
                );
                builder.show();
            }
        });
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogTools.i("onActivityResult requestCode: " + requestCode + " ,resultCode: " + resultCode);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                FileUtils.deleteImage(context, picture.getPath());
                finish();
            } else {
            }
        }
    }
}