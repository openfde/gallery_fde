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
package com.fde.imageeditlibrary.editimage.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.fde.imageeditlibrary.R;
import com.fde.imageeditlibrary.editimage.EditImageActivity;
import com.fde.imageeditlibrary.editimage.ModuleConfig;
import com.fde.imageeditlibrary.editimage.task.StickerTask;
import com.fde.imageeditlibrary.editimage.ui.ColorPicker;
import com.fde.imageeditlibrary.editimage.view.TextStickerView;


/**
 * add text
 *
 * @author openfde
 */
public class AddTextFragment extends BaseEditFragment implements TextWatcher {
    public static final int INDEX = ModuleConfig.INDEX_ADDTEXT;
    public static final String TAG = AddTextFragment.class.getName();

    private View mainView;
    private View backToMenu;// back to menu

    private EditText mInputText;//input view
    private ImageView mTextColorSelector;//color select
    private TextStickerView mTextStickerView;// text view
    private CheckBox mAutoNewLineCheck;

    private ColorPicker mColorPicker;

    private int mTextColor = Color.WHITE;
    private InputMethodManager imm;

    private SaveTextStickerTask mSaveTask;

    public static AddTextFragment newInstance() {
        AddTextFragment fragment = new AddTextFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mainView = inflater.inflate(R.layout.fragment_edit_image_add_text, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTextStickerView = (TextStickerView)getActivity().findViewById(R.id.text_sticker_panel);

        backToMenu = mainView.findViewById(R.id.back_to_main);
        mInputText = (EditText) mainView.findViewById(R.id.text_input);
        mTextColorSelector = (ImageView) mainView.findViewById(R.id.text_color);
        mAutoNewLineCheck = (CheckBox) mainView.findViewById(R.id.check_auto_newline);

        backToMenu.setOnClickListener(new BackToMenuClick());// backToMenu
        mColorPicker = new ColorPicker(getActivity(), 255, 0, 0);
        mTextColorSelector.setOnClickListener(new SelectColorBtnClick());
        mInputText.addTextChangedListener(this);
        mTextStickerView.setEditText(mInputText);

        //统一颜色设置
        mTextColorSelector.setBackgroundColor(mColorPicker.getColor());
        mTextStickerView.setTextColor(mColorPicker.getColor());
    }

    @Override
    public void afterTextChanged(Editable s) {
        //mTextStickerView change
        String text = s.toString().trim();
        mTextStickerView.setText(text);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * color select
     */
    private final class SelectColorBtnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mColorPicker.show();
            Button okColor = (Button) mColorPicker.findViewById(R.id.okColorButton);
            okColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeTextColor(mColorPicker.getColor());
                    mColorPicker.dismiss();
                }
            });
        }
    }//end inner class

    /**
     * modify text color
     *
     * @param newColor
     */
    private void changeTextColor(int newColor) {
        this.mTextColor = newColor;
        mTextColorSelector.setBackgroundColor(mTextColor);
        mTextStickerView.setTextColor(mTextColor);
    }

    public void hideInput() {
        if (getActivity() != null && getActivity().getCurrentFocus() != null && isInputMethodShow()) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public boolean isInputMethodShow() {
        return imm.isActive();
    }

    /**
     * back
     *
     * @author openfde
     */
    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            activity.setSaveBtnShow(false);
            backToMain();
        }
    }// end class

    /**
     * banckto menu
     */
    @Override
    public void backToMain() {
        hideInput();
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.bannerFlipper.showPrevious();
        mTextStickerView.setVisibility(View.GONE);
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_TEXT;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.bannerFlipper.showNext();
        mTextStickerView.setVisibility(View.VISIBLE);
        mInputText.clearFocus();
    }

    /**
     * save image
     */
    public void applyTextImage() {
        if (mSaveTask != null) {
            mSaveTask.cancel(true);
        }

        //启动任务
        mSaveTask = new SaveTextStickerTask(activity);
        mSaveTask.execute(activity.getMainBit());
    }

    /**
     * text merge
     *
     */
    private final class SaveTextStickerTask extends StickerTask {

        public SaveTextStickerTask(EditImageActivity activity) {
            super(activity);
        }

        @Override
        public void handleImage(Canvas canvas, Matrix m) {
            float[] f = new float[9];
            m.getValues(f);
            int dx = (int) f[Matrix.MTRANS_X];
            int dy = (int) f[Matrix.MTRANS_Y];
            float scale_x = f[Matrix.MSCALE_X];
            float scale_y = f[Matrix.MSCALE_Y];
            canvas.save();
            canvas.translate(dx, dy);
            canvas.scale(scale_x, scale_y);
            //System.out.println("scale = " + scale_x + "       " + scale_y + "     " + dx + "    " + dy);
            mTextStickerView.drawText(canvas, mTextStickerView.layout_x,
                    mTextStickerView.layout_y, mTextStickerView.mScale, mTextStickerView.mRotateAngle);
            canvas.restore();
        }

        @Override
        public void onPostResult(Bitmap result) {
            mTextStickerView.clearTextContent();
            mTextStickerView.resetView();

            activity.changeMainBitmap(result , true);
            activity.setSaveBtnShow(true);
            backToMain();
        }
    }//end inner class

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSaveTask != null && !mSaveTask.isCancelled()) {
            mSaveTask.cancel(true);
        }
    }
}// end class
