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
package com.fde.gallery.base;

import android.app.RecoverableSecurityException;
import android.content.Context;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    protected  Context context ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void requestConfirmDialog(RecoverableSecurityException e) {
        try {
            startIntentSenderForResult(
                    e.getUserAction().getActionIntent().getIntentSender()
                    , 1, null, 0, 0, 0, null);
        } catch (IntentSender.SendIntentException ex) {
            ex.printStackTrace();
        }
    }
}
