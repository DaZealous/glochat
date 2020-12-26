package net.glochat.dev.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.glochat.dev.R;
import net.glochat.dev.models.Users;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersStatusViewHolder extends RecyclerView.ViewHolder {

    CircleImageView imgProfile;
    TextView textFullName;
    ImageView imgAddMore;
    RelativeLayout parentLayout;

    public UsersStatusViewHolder(@NonNull View itemView) {
        super(itemView);

        imgProfile = itemView.findViewById(R.id.single_user_status_image_circle);
        textFullName = itemView.findViewById(R.id.single_user_status_text_user_name);
        imgAddMore = itemView.findViewById(R.id.single_user_status_image_add_more);
        parentLayout = itemView.findViewById(R.id.single_user_status_parent_layout);

    }

    public void initView(Context context, Users user, int position) {

        Glide.with(context.getApplicationContext()).load(user.getPhotoUrl()).placeholder(R.drawable.profile_placeholder).into(imgProfile);

        if (position == 0) {
            imgAddMore.setVisibility(View.VISIBLE);
            parentLayout.setBackground(null);
        }
        else {
            imgAddMore.setVisibility(View.GONE);
            parentLayout.setBackgroundResource(R.drawable.round_circle_status);
        }
        textFullName.setText(user.getBio());

    }

}
