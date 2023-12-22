package com.fde.gallery.ui.logic;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fde.gallery.R;
import com.fde.gallery.adapter.PictureListAdapter;
import com.fde.gallery.bean.Picture;
import com.fde.gallery.view.AutoFitGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class PictureListPersenter {
    Context context;
    View view;

    PictureListAdapter pictureListAdapter;
    RecyclerView recyclerView;
    List<Picture> list;

    int numberOfColumns =  3 ;

    public PictureListPersenter(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public  boolean initView(){
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, numberOfColumns){
            @Override
            public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
                super.onMeasure(recycler, state, widthSpec, heightSpec);
                int measuredWidth = recyclerView.getMeasuredWidth();
                int measuredHeight = recyclerView.getMeasuredHeight();
                int myMeasureHeight = 0;
                int count = state.getItemCount();
                for (int i = 0; i < count; i++) {
                    View view = recycler.getViewForPosition(i);
                    if (view != null) {
                        if (myMeasureHeight < measuredHeight && i % numberOfColumns == 0) {
                            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
                            int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                                    getPaddingLeft() + getPaddingRight(), p.width);
                            int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                                    getPaddingTop() + getPaddingBottom(), p.height);
                            view.measure(childWidthSpec, childHeightSpec);
                            myMeasureHeight += view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
                        }
                        recycler.recycleView(view);
                    }
                }
//                setMeasuredDimension(measuredWidth, Math.min(measuredHeight, myMeasureHeight));
                setMeasuredDimension(measuredWidth, measuredWidth);
            }
        };
        recyclerView.setLayoutManager(gridLayoutManager);
        list = new ArrayList<>();
        pictureListAdapter = new PictureListAdapter(context, list);
        recyclerView.setAdapter(pictureListAdapter);
        return  true;
    }


    /***
     * get all picture
     * @param context
     */
    public void getAllImages(Context context) {
        // The columns we're interested in (id, data, date taken, title, width, height and size)
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.SIZE,
        };

        // Query the content provider
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // The content URI of the words table
                projection,   // The columns to return for each row
                null,         // Selection criteria
                null,         // Selection criteria
                null);        // The sort order for the returned rows

        if (cursor != null) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
            int widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
            int heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

            while (cursor.moveToNext()) {
                Picture picture = new Picture();
                picture.setId(cursor.getLong(idColumn));
                picture.setPath(cursor.getString(dataColumn));
                picture.setDateTaken(cursor.getLong(dateTakenColumn));
                picture.setTitle(cursor.getString(titleColumn));
                picture.setSize(cursor.getLong(sizeColumn));
                picture.setWidth(cursor.getInt(widthColumn));
                picture.setHeight(cursor.getInt(heightColumn));
                list.add(picture);
            }
            pictureListAdapter.notifyDataSetChanged();
            cursor.close();
        }
    }

}
