package net.glochat.dev.adapter;

import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;


import com.bumptech.glide.Glide;

import net.glochat.dev.R;
import net.glochat.dev.activity.PlayListActivity;
import net.glochat.dev.base.BaseAdapter;
import net.glochat.dev.base.BaseViewHolder;
import net.glochat.dev.bean.VideoBe;
import net.glochat.dev.view.CircleImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GridVideoAdapter extends BaseAdapter<VideoBe, GridVideoAdapter.GridVideoViewHolder> {

    public GridVideoAdapter(Context context, List<VideoBe> datas) {
        super(context, datas);
    }

    @Override
    protected void onBindData(GridVideoViewHolder holder, VideoBe videoBean, int position) {

        loadBitmap(videoBean.getUserBean().getHead(), holder.profilePic, false);
        loadBitmap(videoBean.getVideoRes(), holder.thumbnail, true);
        holder.userName.setText(videoBean.getUserBean().getNickName());
        holder.userComment.setText(videoBean.getContent());

        holder.itemView.setOnClickListener(v -> {
            PlayListActivity.initPos = position;
            context.startActivity(new Intent(context, PlayListActivity.class));
        });
    }



    private void loadBitmap(int bitmap, ImageView imageView, boolean cover) {
        if (cover)
            Glide.with(context)
                    .load(bitmap)
                    .thumbnail(Glide.with(context).load(bitmap))
                    .into(imageView);

        else
            Glide.with(context).load(bitmap)
                    .into(imageView);

    }

    @NonNull
    @Override
    public GridVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_data_item, parent, false);
        return new GridVideoViewHolder(view);
    }

    public static class GridVideoViewHolder extends BaseViewHolder {
        @BindView(R.id.thumbnail)
        ImageView thumbnail;
        @BindView(R.id.play_icon)
        ImageView play;
        @BindView(R.id.user_name)
        TextView userName;
        @BindView(R.id.user_comment)
        TextView userComment;
        @BindView(R.id.profile_pic)
        CircleImageView profilePic;


        public GridVideoViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
