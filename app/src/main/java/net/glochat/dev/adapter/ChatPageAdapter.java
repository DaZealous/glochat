package net.glochat.dev.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import net.glochat.dev.R;

import net.glochat.dev.activity.ChatActivity;
import net.glochat.dev.base.BaseAdapter;
import net.glochat.dev.base.BaseViewHolder;
import net.glochat.dev.bean.VideoBe;
import net.glochat.dev.view.CircleImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatPageAdapter extends BaseAdapter<VideoBe, ChatPageAdapter.ChatPageAdapterViewHolder> {


    public ChatPageAdapter(Context context, List<VideoBe> datas) {
        super(context, datas);
    }

    @Override
    protected void onBindData(ChatPageAdapterViewHolder holder, VideoBe data, int position) {
        loadBitmap(data.getUserBean().getHead(), holder.userProfileImg);
        holder.userName.setText(data.getUserBean().getNickName());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("user_head", data.getUserBean().getHead());
            context.startActivity(intent);
        });


    }

    private void loadBitmap(int res, ImageView imageView) {
        Glide.with(context).load(res)
                .into(imageView);
    }
    @NonNull
    @Override
    public ChatPageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.data_item_chat, parent, false);
        return new ChatPageAdapterViewHolder(view);
    }

    public static class ChatPageAdapterViewHolder extends BaseViewHolder {
        @BindView(R.id.user_img)
        CircleImageView userProfileImg;
        @BindView(R.id.user_name)
        TextView userName;
        @BindView(R.id.last_message)
        TextView lastMessage;
        @BindView(R.id.message_time)
        TextView messageTime;

        public ChatPageAdapterViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
