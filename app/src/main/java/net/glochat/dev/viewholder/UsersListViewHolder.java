package net.glochat.dev.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import net.glochat.dev.R;
import net.glochat.dev.activity.ChatActivity;
import net.glochat.dev.models.Users;
import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.users_list_layout_image_profile)
    CircleImageView imgProfile;
    @BindView(R.id.users_list_layout_full_name)
    TextView textFullName;
    @BindView(R.id.users_list_layout_username)
    TextView textUsername;
    @BindView(R.id.users_list_layout_profile_container)
    RelativeLayout layoutContainer;

    public UsersListViewHolder(@NonNull View itemView) {
        super(itemView);
        imgProfile = itemView.findViewById(R.id.users_list_layout_image_profile);
        textFullName = itemView.findViewById(R.id.users_list_layout_full_name);
        textUsername = itemView.findViewById(R.id.users_list_layout_username);
        layoutContainer = itemView.findViewById(R.id.users_list_layout_profile_container);
    }

    public void initView(Context context, Users users) {

        Glide.with(context.getApplicationContext()).load(users.getPhotoUrl()).placeholder(R.drawable.profile_placeholder).into(imgProfile);

        textFullName.setText(users.getName());
        textUsername.setText(users.getBio());

        layoutContainer.setOnClickListener(view -> context.startActivity(new Intent(context, ChatActivity.class).putExtra("user", users)));

    }

}
