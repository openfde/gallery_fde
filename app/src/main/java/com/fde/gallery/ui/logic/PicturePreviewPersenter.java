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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.RecoverableSecurityException;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fde.gallery.R;
import com.fde.gallery.base.BaseActivity;
import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.common.Constant;
import com.fde.gallery.ui.activity.PictureResultActivity;
import com.fde.gallery.utils.FileUtils;
import com.fde.gallery.utils.LogTools;
import com.fde.gallery.utils.SPUtils;
import com.fde.gallery.utils.StringUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PicturePreviewPersenter implements UCropFragmentCallback {
    BaseActivity baseActivity;
    Multimedia picture;
    Context context;

    List<Multimedia> list;

    int curPos = -1;

    public PicturePreviewPersenter(BaseActivity baseActivity, Multimedia picture) {
        this.baseActivity = baseActivity;
        context = baseActivity;
        this.picture = picture;
        getPicList();
    }

    public void getPicList() {
        try {
            list = FileUtils.getAllImages(context);
            curPos = -1;
            for (int i = 0; i < list.size(); i++) {
                Multimedia pic = list.get(i);
                if (pic.getId() == picture.getId()) {
                    curPos = i;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public Multimedia getNextPic() {
        try {
            int maxLen = list.size();
            if (curPos >= 0 && curPos < (maxLen - 1)) {
                curPos++;
                Multimedia m = list.get(curPos);
                SPUtils.putUserInfo(context, "curPicPath", m.getPath());
                return m;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Multimedia getPrePic() {
        try {
            if (curPos > 0) {
                curPos--;
                Multimedia m = list.get(curPos);
                SPUtils.putUserInfo(context, "curPicPath", m.getPath());
                return m;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showDetailsDlg() {
        try {
            Multimedia pic = list.get(curPos);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.details);
            builder.setMessage("title:  " + pic.getTitle() + "\n"
                    + "width:  " + pic.getWidth() + "\n"
                    + "height:  " + pic.getHeight() + "\n"
                    + "date:   " + StringUtils.conversionTime(1000 * pic.getDateTaken()) + "\n"
                    + "path:  " + pic.getPath() + "\n"
            );
            builder.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("NewApi")
    public void deleteImage(Context context) {
        try {
            Multimedia pic = list.get(curPos);
            // 你的删除或修改文件的代码
            FileUtils.deleteImage(context, pic.getPath());
            baseActivity.setResult(Constant.REQUEST_DELETE_PHOTO);
            baseActivity.finish();
        } catch (RecoverableSecurityException e) {
            baseActivity.requestConfirmDialog(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDelDlg() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.is_delete);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteImage(context);
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            builder.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void startCrop(Uri... uris) {
        try {
            Uri uri = null;
            if (uris != null && uris.length > 0) {
                uri = uris[0];
            } else {
                Multimedia pic = list.get(curPos);
                uri = Uri.fromFile(new File(pic.getPath()));
            }
            String destinationFileName = "openfde.jpg";

            UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(baseActivity.getCacheDir(), destinationFileName)));
            uCrop = uCrop.useSourceImageAspectRatio();
            UCrop.Options options = new UCrop.Options();
            options.setCompressionFormat(Bitmap.CompressFormat.PNG);
            uCrop = uCrop.withMaxResultSize(600, 600);
            uCrop = uCrop.withOptions(options);
            uCrop.start(baseActivity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


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
            LogTools.e("handleCropError: " + cropError);
            Toast.makeText(context, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }


    public void setWallpage(int type) {
        Multimedia pic = list.get(curPos);
        Bitmap wallpaperBitmap = BitmapFactory.decodeFile(pic.getPath());
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        wallpaperManager.suggestDesiredDimensions(1280, 1706); // 设置建议的壁纸尺寸

        try {
            if (1 == type) {
                wallpaperManager.setBitmap(wallpaperBitmap);
            } else if (2 == type) {
                wallpaperManager.setBitmap(wallpaperBitmap, null, true, WallpaperManager.FLAG_LOCK);
            } else {
                wallpaperManager.setBitmap(wallpaperBitmap);
                wallpaperManager.setBitmap(wallpaperBitmap, null, true, WallpaperManager.FLAG_LOCK);
            }
            // wallpaperManager.setResource(R.drawable.your_image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
