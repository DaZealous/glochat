package net.glochat.dev.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.glochat.dev.R;
import net.glochat.dev.activity.ChatActivity;
import net.glochat.dev.models.ChatDao;
import net.glochat.dev.models.Conv;
import net.glochat.dev.models.Users;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.viewHolder> {

    private List<String> list;
    private List<Conv> convs;
    private Context context;
    private Users users;

    public ChatHistoryAdapter(List<String> list, List<Conv> convs, Context context) {
        this.list = list;
        this.convs = convs;
        this.context = context;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatHistoryAdapter.viewHolder(LayoutInflater.from(context).inflate(R.layout.user_chat_history_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        String id = list.get(position);
        Conv conv = convs.get(position);
        holder.imgMsgType.setVisibility(View.GONE);
        holder.textTyping.setText("");
        holder.textTyping.setVisibility(View.GONE);
        holder.textMessage.setVisibility(View.VISIBLE);
        //  holder.textOnline.setVisibility(View.GONE);
        holder.textMessage.setText("");
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference().child("messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(id);
        Query messageQuery = chatRef.limitToLast(1);
        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(id);

        if (conv.type.equals("user"))
            messageQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if (dataSnapshot.exists()) {
                        ChatDao message = dataSnapshot.getValue(ChatDao.class);
                    /*if(conv.isSeen())
                        holder.textSeen.setVisibility(View.GONE);
                    else{
                        holder.textSeen.setText("new");
                        holder.textSeen.setVisibility(View.VISIBLE);
                    }*/
                        if (message.getMsg_type().equalsIgnoreCase("image")) {
                            holder.imgMsgType.setImageResource(R.drawable.ic_image_grey_24dp);
                            holder.imgMsgType.setVisibility(View.VISIBLE);
                            holder.textMessage.setText("image");
                        } else if (message.getMsg_type().equalsIgnoreCase("audio")) {
                            holder.imgMsgType.setImageResource(R.drawable.ic_keyboard_voice_drey_24dp);
                            holder.imgMsgType.setVisibility(View.VISIBLE);
                            holder.textMessage.setText("audio");
                        } else if (message.getMsg_type().equalsIgnoreCase("video")) {
                            holder.imgMsgType.setImageResource(R.drawable.ic_videocam_grey_24dp);
                            holder.imgMsgType.setVisibility(View.VISIBLE);
                            holder.textMessage.setText("video");
                        } else if (message.getMsg_type().equalsIgnoreCase("file")) {
                            holder.imgMsgType.setImageResource(R.drawable.ic_insert_drive_file_grey_24dp);
                            holder.imgMsgType.setVisibility(View.VISIBLE);
                            holder.textMessage.setText("document");
                        } else if (message.getMsg_type().equalsIgnoreCase("contact")) {
                            holder.imgMsgType.setImageResource(R.drawable.ic_perm_contact_calendar_grey_24dp);
                            holder.imgMsgType.setVisibility(View.VISIBLE);
                            holder.textMessage.setText(message.getMsg_name());
                        } else {
                            holder.imgMsgType.setVisibility(View.GONE);
                            holder.textMessage.setText(message.getMsg_body());
                            holder.textTime.setText(DateUtils.getRelativeTimeSpanString(message.getTime_stamp()));
                        }
                    }else {
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, list.size());
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        else {
            FirebaseDatabase.getInstance().getReference().child("messages").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(id)) {
                        mUsersDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    holder.imgMsgType.setVisibility(View.GONE);
                                    holder.textMessage.setText(dataSnapshot.child("admin").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ? "Group created" : "You were added");
                                    holder.textTime.setText(DateUtils.getRelativeTimeSpanString(dataSnapshot.child("created_at").getValue(Long.class)));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            FirebaseDatabase.getInstance().getReference().child("messages").child(id).limitToLast(1).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if (dataSnapshot.exists()) {
                        ChatDao message = dataSnapshot.getValue(ChatDao.class);
                       /* if (conv.isSeen())
                            holder.textSeen.setVisibility(View.GONE);
                        else {
                            holder.textSeen.setText("new");
                            holder.textSeen.setVisibility(View.VISIBLE);
                        }*/
                        if (message.getMsg_type().equalsIgnoreCase("image")) {
                            holder.imgMsgType.setImageResource(R.drawable.ic_image_grey_24dp);
                            holder.imgMsgType.setVisibility(View.VISIBLE);
                            holder.textMessage.setText("image");
                        } else if (message.getMsg_type().equalsIgnoreCase("audio")) {
                            holder.imgMsgType.setImageResource(R.drawable.ic_keyboard_voice_drey_24dp);
                            holder.imgMsgType.setVisibility(View.VISIBLE);
                            holder.textMessage.setText("audio");
                        } else if (message.getMsg_type().equalsIgnoreCase("video")) {
                            holder.imgMsgType.setImageResource(R.drawable.ic_videocam_grey_24dp);
                            holder.imgMsgType.setVisibility(View.VISIBLE);
                            holder.textMessage.setText("video");
                        } else if (message.getMsg_type().equalsIgnoreCase("file")) {
                            holder.imgMsgType.setImageResource(R.drawable.ic_insert_drive_file_grey_24dp);
                            holder.imgMsgType.setVisibility(View.VISIBLE);
                            holder.textMessage.setText("document");
                        } else if (message.getMsg_type().equalsIgnoreCase("contact")) {
                            holder.imgMsgType.setImageResource(R.drawable.ic_perm_contact_calendar_grey_24dp);
                            holder.imgMsgType.setVisibility(View.VISIBLE);
                            holder.textMessage.setText(message.getMsg_name());
                        } else {
                            holder.imgMsgType.setVisibility(View.GONE);
                            holder.textMessage.setText(message.getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ? "you : " + message.getMsg_body() : message.getFromUsername().split(" ")[0].toLowerCase() + " : " + message.getMsg_body());
                            holder.textTime.setText(DateUtils.getRelativeTimeSpanString(message.getTime_stamp()));
                        }
                    }else {
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, list.size());
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    users = dataSnapshot.getValue(Users.class);
                    holder.textUsername.setText(users.getName());
                    Glide.with(context).load(users.getPhotoUrl()).placeholder(conv.type.equals("user") ? R.drawable.profile_placeholder : R.drawable.ic_people_outline_grey_24dp).into(holder.circleImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(id).child("typing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String typing = dataSnapshot.getValue(String.class);
                if (!TextUtils.isEmpty(typing))
                    if (typing.equals("true")) {
                        holder.imgMsgType.setVisibility(View.GONE);
                        holder.textTyping.setText("typing...");
                        holder.textTyping.setVisibility(View.VISIBLE);
                        holder.textMessage.setVisibility(View.GONE);

                    } else {

                        holder.textTyping.setText("");
                        holder.textTyping.setVisibility(View.GONE);
                        holder.textMessage.setVisibility(View.VISIBLE);

                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*if(conv.type.equals("user"))
        FirebaseDatabase.getInstance().getReference().child("users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("online").getValue() != null) {
                    String online = dataSnapshot.child("online").getValue().toString();
                    if (!TextUtils.isEmpty(online))
                        if (online.equals("true"))
                            holder.textOnline.setVisibility(View.VISIBLE);
                        else
                            holder.textOnline.setVisibility(View.GONE);
                    else
                        holder.textOnline.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        holder.linearLayout.setOnClickListener(view ->
                holder.startChat(conv, users)
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        TextView textUsername, textMessage, textTime, textTyping;
        CircleImageView circleImageView;
        LinearLayout linearLayout;
        ImageView imgMsgType;

        private viewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.user_chat_history_text_username);
            textMessage = itemView.findViewById(R.id.user_chat_history_text_single_message);
            textTime = itemView.findViewById(R.id.user_chat_history_text_time);
            //textSeen = itemView.findViewById(R.id.user_chat_history_text_new_msg);
            circleImageView = itemView.findViewById(R.id.user_chat_history_image_profile);
            linearLayout = itemView.findViewById(R.id.user_chat_history_linear_layout);
            textTyping = itemView.findViewById(R.id.user_chat_history_text_typing);
            // textOnline = itemView.findViewById(R.id.user_chat_history_text_online);
            imgMsgType = itemView.findViewById(R.id.user_chat_history_img_msg_type);
        }

        private void startChat(Conv conv, Users user) {
            if (conv.type.equals("user"))
                if (users != null)
                    context.startActivity(new Intent(context, ChatActivity.class)
                            .putExtra("userID", user.getUid())
                            .putExtra("username", user.getName())
                            .putExtra("img_url", user.getPhotoUrl()));
                    //context.startActivity(new Intent(context, ChatActivity.class).putExtra("user", users));
        }
    }
}
