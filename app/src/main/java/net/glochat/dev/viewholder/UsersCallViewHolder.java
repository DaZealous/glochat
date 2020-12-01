package net.glochat.dev.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.glochat.dev.R;
import net.glochat.dev.models.Users;
import net.glochat.dev.view.VoiceVideoCallView;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersCallViewHolder extends RecyclerView.ViewHolder {

    CircleImageView imgProfile;
    TextView textFullName;
    ImageButton videoCall, audioCall;

    public UsersCallViewHolder(@NonNull View itemView) {
        super(itemView);

        imgProfile = itemView.findViewById(R.id.users_video_audio_img_profile);
        textFullName = itemView.findViewById(R.id.users_video_audio_full_name);
        videoCall = itemView.findViewById(R.id.users_video_audio_video_btn);
        audioCall = itemView.findViewById(R.id.users_video_audio_call_btn);

    }

    public void initView(Context context, Users user, VoiceVideoCallView view1) {

        Glide.with(context.getApplicationContext()).load(user.getPhotoUrl()).placeholder(R.drawable.profile_placeholder).into(imgProfile);

        textFullName.setText(user.getName());

        videoCall.setOnClickListener(view -> view1.doVideoCall(user.getBio()));
        audioCall.setOnClickListener(view -> view1.doVoiceCall(user.getBio()));

    }

}
