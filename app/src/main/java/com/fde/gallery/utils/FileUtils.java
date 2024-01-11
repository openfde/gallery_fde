package com.fde.gallery.utils;

import android.annotation.SuppressLint;
import android.app.RecoverableSecurityException;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.common.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    /**
     * file delete
     */
    public static boolean delete(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            boolean isSuccess = file.delete();
            LogTools.i("isSuccess " + isSuccess);
            return isSuccess;
        }
        return false;
    }

    /**
     * delete pic
     *
     * @param context
     * @param imagePath
     * @throws RecoverableSecurityException
     */
    @SuppressLint("NewApi")
    public static void deleteImage(Context context, String imagePath) throws RecoverableSecurityException {
        // Use the MediaStore to find the ID of the image
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Images.Media.DATA + "=?";
        String[] selectionArgs = {imagePath};
        Cursor cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                if(id !=-1){
                    Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    LogTools.i("deleteImage " + imagePath + " , " + deleteUri.getPath());
                    context.getContentResolver().delete(deleteUri, null, null);
                }
            }
            cursor.close();
        }
    }


    /**
     * delete video
     *
     * @param context
     * @param imagePath
     * @throws RecoverableSecurityException
     */
    @SuppressLint("NewApi")
    public static void deleteVideo(Context context, String imagePath) throws RecoverableSecurityException {
        // Use the MediaStore to find the ID of the image
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Video.Media.DATA + "=?";
        String[] selectionArgs = {imagePath};
        Cursor cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                Uri deleteUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                LogTools.i("deleteVideo " + imagePath + " , " + deleteUri.getPath());
                context.getContentResolver().delete(deleteUri, null, null);
            }
            cursor.close();
        }
    }

    /***
     * get all picture
     * @param context
     */
    public static List<Multimedia> getAllImages(Context context) {
        List<Multimedia> list = new ArrayList<>();
        // Query the content provider
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // The content URI of the words table
                null,
                null,         // Selection criteria
                null,         // Selection criteria
                MediaStore.Images.Media.DATE_ADDED + " desc");        // The sort order for the returned rows

        if (cursor != null) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            if(idColumn != -1){
                int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                int dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
                int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                int widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
                int heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                int dateAddDateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);

                while (cursor.moveToNext()) {
                    Multimedia picture = new Multimedia();
                    picture.setId(cursor.getLong(idColumn));
                    picture.setPath(cursor.getString(dataColumn));
                    long date = cursor.getLong(dateTakenColumn);
                    picture.setDateTaken(date > 0 ? date : cursor.getLong(dateAddDateColumn));
                    picture.setTitle(cursor.getString(titleColumn));
                    picture.setSize(cursor.getLong(sizeColumn));
                    picture.setWidth(cursor.getInt(widthColumn));
                    picture.setHeight(cursor.getInt(heightColumn));
                    picture.setSelected(false);
                    picture.setShowCheckbox(false);
                    picture.setMediaType(Constant.MEDIA_PIC);
                    list.add(picture);
                }
            }
//            LogTools.i("picture list size " + list.toString());
            cursor.close();
        }
        return list;
    }


    /**
     * get all video
     *
     * @param context
     */
    public static List<Multimedia> getAllVideos(Context context) {
        List<Multimedia> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, // The content URI of the words table
                null,   // The columns to return for each row
                null,         // Selection criteria
                null,         // Selection criteria
                MediaStore.Images.Media.DATE_ADDED + " desc");        // The sort order for the returned rows

        if (cursor != null) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            int dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
            int widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH);
            int heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int dateAddDateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);

            while (cursor.moveToNext()) {
                Multimedia video = new Multimedia();
                video.setId(cursor.getLong(idColumn));
                video.setPath(cursor.getString(dataColumn));
                video.setSize(cursor.getLong(sizeColumn));
                video.setDuration(cursor.getInt(durationColumn));
                video.setTitle(cursor.getString(titleColumn));
                long date = cursor.getLong(dateTakenColumn);
                video.setDateTaken(date > 0 ? date : cursor.getLong(dateAddDateColumn));
                video.setMediaType(Constant.MEDIA_VIDEO);
                list.add(video);
            }
//            LogTools.i("video list size " + list.toString());
            cursor.close();
        }
        return list;
    }

    public static Multimedia getNextPicture(Context context) {
        String[] projection = new String[]{MediaStore.Images.Media._ID};

        // 按时间降序排序
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null,
                MediaStore.Images.Media.DATE_ADDED + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            int imageIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

            if(imageIdIndex != -1){
                long currentImageId = cursor.getLong(imageIdIndex);

                // 移动光标到下一张图片位置
                if (!cursor.isLast() || !cursor.moveToNext()) {
                    return null; // 没有更多图片
                } else {
                    long nextImageId = cursor.getLong(imageIdIndex);
                    Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, nextImageId);
                    Multimedia pic = new Multimedia();
                    pic.setPath(uri.getPath());
                    return pic;
                }
            }
            cursor.close();
        }

        return null; // 无法获取任何图片
    }
}
