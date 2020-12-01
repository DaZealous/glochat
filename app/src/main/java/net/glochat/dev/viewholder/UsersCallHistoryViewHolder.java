package net.glochat.dev.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.glochat.dev.R;
import net.glochat.dev.models.Users;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersCallHistoryViewHolder extends RecyclerView.ViewHolder {

    CircleImageView imgProfile;
    TextView textFullName;

    public UsersCallHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        imgProfile = itemView.findViewById(R.id.users_video_history_img_profile);
        textFullName = itemView.findViewById(R.id.users_video_history_full_name);
    }

    public void initView(Context context, Users user) {

        Glide.with(context.getApplicationContext()).load(user.getPhotoUrl()).placeholder(R.drawable.profile_placeholder).into(imgProfile);

        textFullName.setText(user.getName());

    }

}
