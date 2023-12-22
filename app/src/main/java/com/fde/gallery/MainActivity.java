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
package com.fde.gallery;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.fde.gallery.adapter.SectionsPagerAdapter;
import com.fde.gallery.base.BaseActivity;
import com.fde.gallery.ui.fragment.PictureListFragment;
import com.fde.gallery.ui.fragment.VideoListFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends BaseActivity {
    VideoListFragment videoFragment;
    PictureListFragment pictureFragment;
    ViewPager viewPager;
    TabLayout tabLayout;
    SectionsPagerAdapter sectionsPagerAdapter;
    Context context;

    public static final String[] permissions = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initView();
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(permissions, 1);
        }
    }

    private void initView() {
        videoFragment = new VideoListFragment();
        pictureFragment = new PictureListFragment();
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        sectionsPagerAdapter = new SectionsPagerAdapter(pictureFragment, context, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}