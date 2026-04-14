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
import android.openfde.AppTaskControllerProxy;
import android.openfde.AppTaskStatusListener;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.bella.dlna.DLNARendererService;
import com.fde.baselib.view.CustomTitleBar;
import com.fde.gallery.adapter.SectionsPagerAdapter;
import com.fde.gallery.base.BaseActivity;
import com.fde.gallery.common.Constant;
import com.fde.gallery.ui.activity.PicturePreviewActivity;
import com.fde.gallery.ui.fragment.PictureListFragment;
import com.fde.gallery.ui.fragment.TimeLineListFragment;
import com.fde.gallery.ui.fragment.VideoListFragment;
import com.fde.gallery.utils.DeviceUtils;
import com.fde.gallery.utils.LogTools;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {
    VideoListFragment videoFragment;
    PictureListFragment pictureFragment;

    TimeLineListFragment timeLineFragment ;
    ViewPager viewPager;
    TabLayout tabLayout;

    CustomTitleBar customTitleBar;
    SectionsPagerAdapter sectionsPagerAdapter;
    Context context;

    private AppTaskControllerProxy appTaskController ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;


        appTaskController = AppTaskControllerProxy.create();
        appTaskController.initCustomCaption(new WeakReference<>(this),true, new AppTaskStatusListener() {
            @Override
            public void onStatusChanged(int windowingMode, boolean isSystemBarVisible) {
                customTitleBar.setButtonBackground(CustomTitleBar.Type.MAXIMIZE, windowingMode == 5 ? com.fde.baselib.R.drawable.icon_maximize : com.fde.baselib.R.drawable.icon_exitmaximize);
                customTitleBar.setButtonBackground(CustomTitleBar.Type.FULLSCREEN, isSystemBarVisible ? com.fde.baselib.R.drawable.icon_fullscreen : com.fde.baselib.R.drawable.icon_exitfullscreen);
            }
        });

        initView();

//        triggerSystemMediaScan();
    }

    public void triggerSystemMediaScan() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File externalDir = Environment.getExternalStorageDirectory();
        Uri contentUri = Uri.fromFile(externalDir);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    private void initView() {
        DLNARendererService.Companion.startService(context);

        videoFragment = new VideoListFragment();
        pictureFragment = new PictureListFragment();
        timeLineFragment = new TimeLineListFragment();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        sectionsPagerAdapter = new SectionsPagerAdapter(timeLineFragment, context, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
//        sectionsPagerAdapter.notifyDataSetChanged();
//        readImages();
        LogTools.i("getAppVersionCode: "+ DeviceUtils.getAppVersionCode(context));

        customTitleBar = (CustomTitleBar) findViewById(R.id.customTitleBar);
        customTitleBar.setTitle(getString(R.string.app_name));
        customTitleBar.setOnButtonClickListener(new CustomTitleBar.OnButtonClickListener() {
            @Override
            public void onLeftClick() {
                finish(); // 左上角返回
            }

            @Override
            public void onCloseClick() {
                appTaskController.closeTask();
            }

            @Override
            public void onFullscreenClick() {
                // 全屏逻辑
                appTaskController.enterOrExitFullscreen();
            }

            @Override
            public void onImportClick() {
                ListPopupWindow listPopupWindow = new ListPopupWindow(context);
                List<String> data = Arrays.asList(getString(R.string.open), getString(R.string.exit));
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        context,
                        android.R.layout.simple_list_item_1,
                        data
                );

                listPopupWindow.setAnchorView(customTitleBar.getButton(CustomTitleBar.Type.OPTION)); // 绑定按钮
                listPopupWindow.setAdapter(adapter);
                listPopupWindow.setWidth(150);

                listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
                    if (position == 0) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, Constant.REQUEST_SELECT_PHOTO);
                    } else {
                        appTaskController.closeTask();
                    }
                    listPopupWindow.dismiss();
                });
                listPopupWindow.show();
            }

            @Override
            public void onMinimizeClick() {
                appTaskController.minimize();
            }

            @Override
            public void onMaximizeClick() {
                appTaskController.maximizeOrNot();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DLNARendererService.Companion.stopService(context);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogTools.i("onActivityResult requestCode "+requestCode + " resultCode "+resultCode);
        if (requestCode == Constant.REQUEST_SELECT_PHOTO && resultCode == RESULT_OK) {
            data.setClass(context, PicturePreviewActivity.class);
            data.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(data);
        }
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