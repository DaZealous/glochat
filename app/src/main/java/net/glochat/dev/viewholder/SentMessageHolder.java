package net.glochat.dev.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.glochat.dev.R;
import net.glochat.dev.models.Message;

public class SentMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeText;

    public SentMessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        timeText = itemView.findViewById(R.id.text_message_time);
    }

    public void bind(Message message, String time) {
        messageText.setText(message.getMessage());
        timeText.setText(time);
    }
}
