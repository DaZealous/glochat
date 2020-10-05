package net.glochat.dev.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.glochat.dev.R;
import net.glochat.dev.models.Message;
import net.glochat.dev.viewholder.ReceivedMessageHolder;
import net.glochat.dev.viewholder.SentMessageHolder;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Message> mMessageList;
    private FirebaseAuth mAuth;
    String imageUrl;
    DatabaseReference mDatabaseReference;


    public MessageListAdapter(Context context, List<Message> messageList, FirebaseAuth mAuth, String imageUrl) {
        mContext = context;
        mMessageList = messageList;
        this.mAuth = mAuth;
        this.imageUrl = imageUrl;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message =  mMessageList.get(position);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        if (message.getFrom().equals(mAuth.getCurrentUser().getUid())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }

    }


    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.data_item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.data_item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message =  mMessageList.get(position);
        long timeStamp = message.getTime();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        String[] cal = calendar.getTime().toString().split(" ");
        String time_of_message = cal[1] + "," + cal[2] + "  " + cal[3].substring(0, 5);
        Log.e("TIME IS : ", calendar.getTime().toString());


        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message, time_of_message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message, imageUrl, time_of_message);
        }
    }
}
