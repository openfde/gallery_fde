package com.fde.gallery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fde.gallery.R;
import com.fde.gallery.bean.Multimedia;
import com.fde.gallery.common.Constant;
import com.fde.gallery.event.ViewEvent;
import com.fde.gallery.ui.activity.PicturePreviewActivity;
import com.fde.gallery.ui.activity.VideoPlayActivity;
import com.fde.gallery.utils.LogTools;
import com.fde.gallery.utils.SPUtils;
import com.fde.gallery.utils.StringUtils;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.TimeLineListViewHolder> {
    Context context;
    List<Multimedia> list;

    int groupPos;
    int numberOfColumns;

    ViewEvent viewEvent;

    public TimeLineAdapter(Context context, List<Multimedia> list, int groupPos,int numberOfColumns, ViewEvent viewEvent) {
        this.context = context;
        this.list = list;
        this.groupPos = groupPos;
        this.numberOfColumns = numberOfColumns;
        this.viewEvent = viewEvent;
    }

    @NonNull
    @Override
    public TimeLineListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_timeline, parent, false);
        TimeLineAdapter.TimeLineListViewHolder holder = new TimeLineAdapter.TimeLineListViewHolder(view);
        return holder;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull TimeLineAdapter.TimeLineListViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.rootView.post(new Runnable() {
            @Override
            public void run() {
                if (holder.rootView.getParent() != null) {
                    int width = ((RecyclerView) holder.rootView.getParent()).getWidth();
                    if (width != 0) {
                        // 获取RecyclerView的宽度
                        // 计算item的宽度和高度
                        int size = width / numberOfColumns; // replace 3 with the number of columns
                        // 设置item的宽度和高度
                        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                        layoutParams.width = size;
                        layoutParams.height = size;
                        holder.itemView.setLayoutParams(layoutParams);
                    }
                }
            }
        });
    }


    @Override
    public void onBindViewHolder(@NonNull TimeLineListViewHolder holder,@SuppressLint("RecyclerView")  final  int position) {
        Multimedia multimedia = list.get(position);
        Glide.with(context)
//                .load(Uri.fromFile(new File(list.get(position).getPath())))
                .load(multimedia.getPath())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
//                .apply(new RequestOptions().frame(1000))
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop() // 裁剪图片以适应ImageView的大小
                .dontTransform() // 禁用任何额外的转换
                .dontAnimate()
                .into(holder.imageView);
//        Glide.with(context) // replace 'this' with your context
//                .load(list.get(position).getPath())
//                .apply(new RequestOptions().frame(1000)) // frame at 1 second into the video
//                .into(holder.imageView);
        holder.checkBox.setVisibility(multimedia.isShowCheckbox() ? View.VISIBLE : View.GONE);
        holder.checkBox.setChecked(multimedia.isSelected());
        holder.txtDate.setText(StringUtils.conversionTime(1000* multimedia.getDateTaken()));

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Multimedia multimedia = list.get(position);
                LogTools.i("multimedia" + multimedia.toString());
                Intent intent = new Intent();
                if (multimedia.getMediaType() == Constant.MEDIA_PIC) {
                    SPUtils.putUserInfo(context,"curPicPath",multimedia.getPath());
                    intent.putExtra("picture_data", multimedia);
                    intent.setClass(context, PicturePreviewActivity.class);
                } else {
                    intent.putExtra("video_data", multimedia);
                    intent.setClass(context, VideoPlayActivity.class);
                }
                context.startActivity(intent);
            }
        });

        holder.rootView.setOnContextClickListener(new View.OnContextClickListener() {
            @Override
            public boolean onContextClick(View view) {
//                holder.checkBox.setVisibility(View.VISIBLE);
                viewEvent.onRightEvent(position,groupPos);
                return false;
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                viewEvent.onSelectEvent(position,groupPos, b);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class TimeLineListViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        RelativeLayout rootView;

        TextView txtDate;
        CheckBox checkBox;

        public TimeLineListViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = (RelativeLayout) itemView.findViewById(R.id.rootView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            txtDate =(TextView) itemView.findViewById(R.id.txtDate);
        }
    }
}
