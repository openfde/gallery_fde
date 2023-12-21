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
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.fde.gallery.adapter.SectionsPagerAdapter;
import com.fde.gallery.base.BaseActivity;
import com.fde.gallery.ui.PictureFragment;
import com.fde.gallery.ui.VideoFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends BaseActivity {
    VideoFragment videoFragment;
    PictureFragment pictureFragment;
    ViewPager viewPager;
    TabLayout tabLayout;
    SectionsPagerAdapter sectionsPagerAdapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initView();

    }

    private void initView() {
        videoFragment = new VideoFragment();
        pictureFragment = new PictureFragment();
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        sectionsPagerAdapter = new SectionsPagerAdapter(pictureFragment, context, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}