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

import android.content.Context;
import android.view.View;

import com.fde.gallery.base.BaseActivity;

public class VideoPlayPersenter {
    BaseActivity baseActivity;
    View view;
    Context context;

    public VideoPlayPersenter(BaseActivity baseActivity, View view) {
        this.baseActivity = baseActivity;
        this.view = view;
        context = baseActivity;
    }

    public boolean initView() {

        return  true;
    }
}
