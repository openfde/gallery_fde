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
package com.fde.gallery.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.fde.gallery.R;
import com.fde.gallery.base.BaseFragment;
import com.fde.gallery.ui.PictureFragment;
import com.fde.gallery.ui.TimeLineFragment;
import com.fde.gallery.ui.VideoFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_picture, R.string.tab_video, R.string.tab_time_line};
    private final Context context;
    BaseFragment baseFragment;

    public SectionsPagerAdapter(BaseFragment baseFragment, Context context, FragmentManager fm) {
        super(fm);
        this.baseFragment = baseFragment;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            PictureFragment pictureFragment = new PictureFragment();
            return pictureFragment;
        } else if (position == 1) {
            VideoFragment videoFragment = new VideoFragment();
            return videoFragment;
        } else {
            TimeLineFragment timeLineFragment = new TimeLineFragment();
            return timeLineFragment;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}
