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

import android.app.AlertDialog;
import android.app.RecoverableSecurityException;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fde.gallery.R;
import com.fde.gallery.base.BaseActivity;
import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.utils.FileUtils;
import com.fde.gallery.utils.LogTools;
import com.github.chrisbanes.photoview.PhotoView;

public class PicturePreviewPersenter {
    BaseActivity baseActivity;
    View view;
    Context context;

    public PicturePreviewPersenter(BaseActivity baseActivity, View view) {
        this.baseActivity = baseActivity;
        this.view = view;
    }

}
