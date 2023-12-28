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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fde.gallery.R;
import com.fde.gallery.base.BaseFragment;
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

    boolean isInit = false;

    public PictureListFragment() {
    }

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
        return view;
    }


    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
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
        pictureListPersenter.deleteImage();
    }
}