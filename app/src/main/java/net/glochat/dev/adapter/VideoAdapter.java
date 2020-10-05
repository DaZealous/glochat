package net.glochat.dev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;


import com.bumptech.glide.Glide;

import net.glochat.dev.R;
import net.glochat.dev.base.BaseAdapter;
import net.glochat.dev.base.BaseViewHolder;
import net.glochat.dev.bean.VideoBe;
import net.glochat.dev.view.ControllerView;
import net.glochat.dev.view.LikeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class VideoAdapter extends BaseAdapter<VideoBe, VideoAdapter.VideoViewHolder> {

    public VideoAdapter(Context context, List<VideoBe> datas) {
        super(context, datas);
    }

    @Override
    protected void onBindData(VideoViewHolder holder, VideoBe videoBean, int position) {

        loadBitmap(videoBean.getCoverRes(), holder.ivCover);
        //holder.ivCover.setBackgroundResource(videoBean.getCoverRes());
        holder.controllerView.setVideoData(videoBean);

        holder.likeView.setOnLikeListener(() -> {
            if (!videoBean.isLiked()) {
                holder.controllerView.like();
            }

        });

    }
    private void loadBitmap(int res, ImageView imageView) {
        Glide.with(context).load(res)
                .into(imageView);
    }


    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    public static class VideoViewHolder extends BaseViewHolder {
        @BindView(R.id.likeview)
        LikeView likeView;
        @BindView(R.id.controller)
        ControllerView controllerView;
        @BindView(R.id.cover_photo)
        ImageView ivCover;


        public VideoViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
