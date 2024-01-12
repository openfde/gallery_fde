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
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.fde.gallery.adapter.SectionsPagerAdapter;
import com.fde.gallery.base.BaseActivity;
import com.fde.gallery.ui.fragment.PictureListFragment;
import com.fde.gallery.ui.fragment.TimeLineListFragment;
import com.fde.gallery.ui.fragment.VideoListFragment;
import com.fde.gallery.utils.DeviceUtils;
import com.fde.gallery.utils.LogTools;
import com.google.android.material.tabs.TabLayout;

import java.io.File;

public class MainActivity extends BaseActivity {
    VideoListFragment videoFragment;
    PictureListFragment pictureFragment;

    TimeLineListFragment timeLineFragment ;
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
        videoFragment = new VideoListFragment();
        pictureFragment = new PictureListFragment();
        timeLineFragment = new TimeLineListFragment();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        sectionsPagerAdapter = new SectionsPagerAdapter(timeLineFragment, context, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
//        sectionsPagerAdapter.notifyDataSetChanged();
//        readImages();
        LogTools.i("getAppVersionCode: "+ DeviceUtils.getAppVersionCode(context));
    }

    private void readImages() {
        File file = new File("/mnt/sdcard/");
        MediaScannerConnection.scanFile(this,
                new String[] { file.getPath() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        // 文件已经被扫描完成，现在可以在MediaStore中查询到这个文件了
                        LogTools.i("readImages path "+path);
                    }
                });

        LogTools.i("readImages ");
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                LogTools.i(" --------moveToNext-----");
                // 获取图片的路径
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                LogTools.i("path "+path);
                // 使用路径来加载图片
                // ...
            }
            cursor.close();
        }
    }
}