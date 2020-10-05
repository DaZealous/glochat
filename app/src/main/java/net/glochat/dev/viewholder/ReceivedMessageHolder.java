package net.glochat.dev.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.glochat.dev.R;
import net.glochat.dev.models.Message;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeText;
    ImageView profileImage;

    View view;

    public ReceivedMessageHolder(View itemView) {
        super(itemView);
        view = itemView;
        messageText = itemView.findViewById(R.id.text_message_body);
        timeText = itemView.findViewById(R.id.text_message_time);
        profileImage = itemView.findViewById(R.id.image_message_profile);
    }

    public void bind(Message message, String uri, String time) {
        messageText.setText(message.getMessage());
        timeText.setText(time);
        Glide.with(view).load(uri).into(profileImage);
    }
}
