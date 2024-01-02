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
package com.fde.gallery.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fde.gallery.R;
import com.fde.gallery.base.BaseFragment;
import com.fde.gallery.common.Constant;
import com.fde.gallery.ui.logic.PictureListPersenter;
import com.fde.gallery.utils.LogTools;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PictureListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictureListFragment extends BaseFragment {
    Context context;
    PictureListPersenter pictureListPersenter;

    public PictureListFragment() {
    }

    public static final String[] permissions = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"};

    public static PictureListFragment newInstance(String param1, String param2) {
        PictureListFragment fragment = new PictureListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        pictureListPersenter = new PictureListPersenter(this, view);
        pictureListPersenter.initView();

        if (Build.VERSION.SDK_INT >= 23) {
            int iGetPerm = context.checkSelfPermission(permissions[1]);
            if (iGetPerm == -1) {
                requestPermissions(permissions, 888);
            }
        }

        return view;
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            LogTools.i("----onFragmentVisibleChange--------");
            pictureListPersenter.getAllImages(context);
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogTools.i("onActivityResult requestCode: "+requestCode +" ,resultCode:  "+resultCode);
        if(requestCode == Constant.REQUEST_DELETE_PHOTO){
            pictureListPersenter.getAllImages(context);
        }else {
            pictureListPersenter.deleteImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        pictureListPersenter.getAllImages(context);
    }
}