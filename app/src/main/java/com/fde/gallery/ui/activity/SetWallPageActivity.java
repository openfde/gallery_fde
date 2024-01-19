package com.fde.gallery.ui.activity;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fde.gallery.R;
import com.fde.gallery.adapter.PictureListAdapter;
import com.fde.gallery.adapter.SetWallPageAdapter;
import com.fde.gallery.base.BaseActivity;
import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.event.ViewEvent;
import com.fde.gallery.utils.FileUtils;
import com.fde.gallery.utils.LogTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SetWallPageActivity extends BaseActivity implements ViewEvent {
    ImageView imageView;
    ImageView imageOk;
    ImageView imageCancel;

    RecyclerView recyclerView;

    List<Multimedia> list;
    int numberOfColumns = 3;
    SetWallPageAdapter pictureListAdapter;
    int curPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_wall_page);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageOk = (ImageView) findViewById(R.id.imageOk);
        imageCancel = (ImageView) findViewById(R.id.imageCancel);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        list = new ArrayList<>();
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(context);
        gridLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

        recyclerView.setLayoutManager(gridLayoutManager);
        list = new ArrayList<>();
        pictureListAdapter = new SetWallPageAdapter(context, list, numberOfColumns, this);
        recyclerView.setAdapter(pictureListAdapter);

        getAllImages();

        imageCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imageOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Multimedia pic = list.get(curPos);
                Bitmap wallpaperBitmap = BitmapFactory.decodeFile(pic.getPath());
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                int w = wallpaperBitmap.getWidth();
                int h = wallpaperBitmap.getHeight();
                LogTools.i("w " + w + " h : " + h);
//                final int width = wallpaperManager.getDesiredMinimumWidth();
//                final int height = wallpaperManager.getDesiredMinimumHeight();
//                Bitmap wallpaper = Bitmap.createScaledBitmap(wallpaperBitmap, w, h, true);
//                LogTools.i("width " + width + " height : " + height);
                if (w != 0 && h != 0) {
                    if (w > h) {
                        wallpaperManager.suggestDesiredDimensions(w, h);
                    } else {
                        wallpaperManager.suggestDesiredDimensions(h, w);
                    }
                } else {
                    wallpaperManager.suggestDesiredDimensions(1280, 1706);
                }

                try {
                    wallpaperManager.setBitmap(wallpaperBitmap);
                    Toast.makeText(context,R.string.set_wallpage_success,Toast.LENGTH_SHORT).show();
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    /***
     * get all picture
     * @param context
     */
    public void getAllImages() {
        if (list != null) {
            list.clear();
        }
        list.addAll(FileUtils.getAllImages(context));
        if (pictureListAdapter == null) {
            LogTools.i("pictureListAdapter is null");
        } else {
            pictureListAdapter.notifyDataSetChanged();
        }
        Multimedia picture = list.get(0);
        setPic(picture);
    }

    public void setPic(Multimedia picture) {
        Glide.with(context)
                .load(Uri.fromFile(new File(picture.getPath())))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @Override
    public void onRightEvent(int pos, int groupPos) {

    }

    @Override
    public void onSelectEvent(int pos, int groupPos, boolean isSelect) {
        curPos = pos;
        Multimedia picture = list.get(pos);
        setPic(picture);
    }

    @Override
    public void onJumpEvent(Multimedia multimedia) {

    }
}