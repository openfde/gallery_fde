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
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fde.gallery.R;
import com.fde.gallery.base.BaseActivity;
import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.common.Constant;
import com.fde.gallery.ui.activity.PictureResultActivity;
import com.fde.gallery.utils.FileUtils;
import com.fde.gallery.utils.LogTools;
import com.fde.gallery.utils.StringUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;

import java.io.File;

public class PicturePreviewPersenter implements   UCropFragmentCallback {
    BaseActivity baseActivity;
    Multimedia picture;
    Context context;

    public PicturePreviewPersenter(BaseActivity baseActivity, Multimedia picture) {
        this.baseActivity = baseActivity;
        context = baseActivity;
        this.picture = picture;
    }

    public void showDetailsDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.details);
        builder.setMessage("title:  " + picture.getTitle() + "\n"
                + "width:  " + picture.getWidth() + "\n"
                + "height:  " + picture.getHeight() + "\n"
                + "date:   " + StringUtils.conversionTime(1000 * picture.getDateTaken()) + "\n"
                + "path:  " + picture.getPath() + "\n"
        );
        builder.show();
    }

    public void showDelDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.is_delete);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    // 你的删除或修改文件的代码
                    FileUtils.deleteImage(context, picture.getPath());
                    baseActivity.setResult(Constant.REQUEST_DELETE_PHOTO);
                    baseActivity.finish();
                } catch (RecoverableSecurityException e) {
                    baseActivity.requestConfirmDialog(e);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }


    public void startCrop(@NonNull Uri uri) {
        String destinationFileName = "openfde.jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(baseActivity.getCacheDir(), destinationFileName)));
        uCrop = uCrop.useSourceImageAspectRatio();
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        uCrop = uCrop.withMaxResultSize(600, 600);
        uCrop = uCrop.withOptions(options);
        uCrop.start(baseActivity);


//        Uri sourceUri = Uri.fromFile(new File(picture.getPath()));
//        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), destinationFileName));
//        UCrop.of(sourceUri, destinationUri)
//                .withAspectRatio(16, 9)
//                .withMaxResultSize(10, 10)
//                .start(PicturePreviewActivity.this);

    }



    @Override
    public void loadingProgress(boolean showLoader) {

    }

    @Override
    public void onCropFinish(UCropFragment.UCropResult result) {
        LogTools.i("-------onCropFinish------");
        switch (result.mResultCode) {
            case -1:
                handleCropResult(result.mResultData);
                break;
            case UCrop.RESULT_ERROR:
                handleCropError(result.mResultData);
                break;
        }
    }

    public void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            PictureResultActivity.startWithUri(context, resultUri);
        } else {
            Toast.makeText(context, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            LogTools.e( "handleCropError: "+ cropError);
            Toast.makeText(context, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }

}
