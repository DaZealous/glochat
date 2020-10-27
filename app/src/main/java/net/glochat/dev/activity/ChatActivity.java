package net.glochat.dev.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import net.glochat.dev.R;
import net.glochat.dev.adapter.ChatAdapter;
import net.glochat.dev.base.BaseActivity;
import net.glochat.dev.models.ChatDao;
import net.glochat.dev.models.Message;
import net.glochat.dev.models.Users;
import net.glochat.dev.utils.GetTimeAgo;
import net.glochat.dev.utils.KeyboardUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.activity_chat_user_img)
    ImageView circleImageView;
    @BindView(R.id.activity_chat_text_username)
    TextView textUsername;
    @BindView(R.id.activity_chat_text_time)
    TextView textTime;
    @BindView(R.id.activity_chat_img_attach)
    ImageButton imgAttach;
    @BindView(R.id.activity_chat_edit_text)
    EditText editText;
    @BindView(R.id.activity_chat_btn_send)
    ImageButton btnSend;
    @BindView(R.id.activity_chat_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.activity_chat_recycler_view)
    RecyclerView recyclerView;

    private DatabaseReference rootRef, mUserReference, mChatRef;
    private FirebaseAuth mAuth;

    private ValueEventListener onlineEvent, typingEvent;

    private String userID, otherUser, username, otherUsername;

    private final List<Message> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;

    //Solution for descending list on refresh
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    int pos = 0;
    public static final int TOTAL_ITEM_TO_LOAD = 15;
    private int mCurrentPage = 1;

    private static final int GALLERY_PICK = 1;
    StorageReference mImageStorage;
    private String imageValue;

    private Users user;
    private Animation slideRight, slideLeft;
    private String isOn = "";

    private Boolean isChat = false, isRecording = false;

    private ChatAdapter adapter;
    private List<ChatDao> list;
    private LinearLayoutManager mLinearLayout;

    @Override
    protected void onCreate() {

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        username = mAuth.getCurrentUser().getDisplayName();
        rootRef = FirebaseDatabase.getInstance().getReference();

        //set up message items
        swipeRefreshLayout.setOnRefreshListener(this);
        list = new ArrayList<>();
        adapter = new ChatAdapter(this, list);
        mLinearLayout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        //image attach slide animation setup
        slideImageAttach();

        if (getIntent().getSerializableExtra("user") != null) {
            user = (Users) getIntent().getSerializableExtra("user");
            otherUser = user.getUid();
            otherUsername = user.getName();
            Glide.with(this).load(user.getPhotoUrl()).placeholder(R.drawable.profile_placeholder).into(circleImageView);
            textUsername.setText(otherUsername);
            mUserReference = rootRef.child("users").child(otherUser);
            mChatRef = rootRef.child("Chat").child(userID).child(otherUser);
            rootRef.child("Chat").child(otherUser).child(userID).child("typing").onDisconnect().setValue("false");

            rootRef.child("Chat").child(userID).child(otherUser).child("seen").setValue(true);
            rootRef.child("Chat").child(userID).child(otherUser).child("type").setValue("user");
            rootRef.child("Chat").child(otherUser).child(userID).child("type").setValue("user");

            addOnlineEvent();
            addTypingEvent();
        }

        //Keyboard toggle
        KeyboardUtils.addKeyboardToggleListener(this, isVisible -> {
            if (isVisible) {
                imgAttach.startAnimation(slideRight);
                rootRef.child("Chat").child(otherUser).child(userID).child("typing").setValue("true");
            } else {
                imgAttach.startAnimation(slideLeft);
                rootRef.child("Chat").child(otherUser).child(userID).child("typing").setValue("false");
            }
        });

        //check if user has not sent any message yet
        rootRef.child("Chat").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(otherUser)) {

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("time_stamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + userID + "/" + otherUser, chatAddMap);
                    chatUserMap.put("Chat/" + otherUser + "/" + userID, chatAddMap);

                    rootRef.updateChildren(chatUserMap, (databaseError, databaseReference) -> {

                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //edit text listener
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    isChat = false;
                    btnSend.setImageResource(R.drawable.ic_keyboard_voice_white_24dp);
                } else {
                    isChat = true;
                    btnSend.setImageResource(R.drawable.ic_send_white_24dp);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //load messages
        loadMessages();
    }

    private void slideImageAttach() {
        slideLeft = AnimationUtils.loadAnimation(this, R.anim.card_slide_left2);
        slideRight = AnimationUtils.loadAnimation(this, R.anim.slide_right2);

        slideRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imgAttach.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        slideLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imgAttach.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_chat;
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeOnlineEvent();
        removeTypingEvent();
    }

    private void removeTypingEvent() {
        if (typingEvent != null && mChatRef != null)
            mChatRef.removeEventListener(typingEvent);
    }

    private void removeOnlineEvent() {
        if (onlineEvent != null && mUserReference != null)
            mUserReference.removeEventListener(onlineEvent);
    }

    private void addOnlineEvent() {

        onlineEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("online")) {
                    String online = snapshot.child("online").getValue(String.class);
                    if (online.equals("true")) {
                        isOn = "Online";
                        textTime.setText(isOn);
                        textTime.setVisibility(View.VISIBLE);
                    } else {
                        if (snapshot.hasChild("time_stamp")) {
                            if (snapshot.child("time_stamp").getValue(Long.class) != null) {
                                Long time_stamp = snapshot.child("time_stamp").getValue(Long.class);
                                isOn = "active : " + GetTimeAgo.getTimeAgo(time_stamp, ChatActivity.this);
                                textTime.setText(isOn);
                                textTime.setVisibility(View.VISIBLE);
                            } else {
                                textTime.setText("");
                                textTime.setVisibility(View.GONE);
                            }
                        } else {
                            textTime.setText("");
                            textTime.setVisibility(View.GONE);
                        }
                    }
                } else {
                    textTime.setText("");
                    textTime.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        mUserReference.addValueEventListener(onlineEvent);

    }

    private void addTypingEvent() {
        typingEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("typing")) {
                    if (snapshot.child("typing").getValue(String.class) != null) {
                        String typing = snapshot.child("typing").getValue(String.class);
                        if (typing.equals("true")) {
                            String isTyping = "typing...";
                            textTime.setText(isTyping);
                        } else {
                            textTime.setText(isOn);
                            removeOnlineEvent();
                            addOnlineEvent();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mChatRef.addValueEventListener(typingEvent);
    }

    @OnClick(R.id.activity_chat_back_btn)
    void goBack() {
        onBackPressed();
    }

    @OnClick(R.id.activity_chat_btn_send)
    void checkMessage() {
        if (isChat) {
            if (!TextUtils.isEmpty(editText.getText().toString())) {
                sendMessage(editText.getText().toString());
            }
        } else {
            if (isRecording) {
                try {
                    isRecording = false;
                    btnSend.setImageResource(R.drawable.ic_keyboard_voice_white_24dp);
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                startRecording();
            }
        }
    }

    private void sendMessage(String message) {
        if (!TextUtils.isEmpty(message)) {

            String current_user_ref = "messages/" + userID + "/" + otherUser;
            String chat_user_ref = "messages/" + otherUser + "/" + userID;

            DatabaseReference user_message_push = rootRef.child("messages")
                    .child(userID).child(otherUser).push();

            final String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("msg_body", message);
            messageMap.put("key", push_id);
            messageMap.put("seen", false);
            messageMap.put("isRead", false);
            messageMap.put("msg_type", "text");
            messageMap.put("time_stamp", ServerValue.TIMESTAMP);
            messageMap.put("from", userID);
            messageMap.put("to", otherUser);
            messageMap.put("msg_name", message);
            messageMap.put("fromUsername", username);
            messageMap.put("toUsername", otherUsername);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            editText.setText("");

            rootRef.child("Chat").child(userID).child(otherUser).child("seen").setValue(true);

            rootRef.child("Chat").child(userID).child(otherUser).child("time_stamp").setValue(ServerValue.TIMESTAMP);

            rootRef.child("Chat").child(otherUser).child(userID).child("seen").setValue(false);
            rootRef.child("Chat").child(otherUser).child(userID).child("time_stamp").setValue(ServerValue.TIMESTAMP);


            rootRef.updateChildren(messageUserMap, (databaseError, databaseReference) -> {

                if (databaseError == null) {

                    rootRef.child("messages").child(userID).child(otherUser).child(push_id).child("seen").setValue(true);
                    rootRef.child("messages").child(userID).child(otherUser).child(push_id).child("isRead").setValue(true);

                }

            });

        }
    }

    private void startRecording() {
        isRecording = true;
    }

    @Override
    public void onRefresh() {
        loadMoreMessages();
    }

    private void loadMessages() {
        swipeRefreshLayout.setRefreshing(true);
        DatabaseReference chatRef = rootRef.child("messages").child(userID).child(otherUser);
        Query messageQuery = chatRef.limitToLast(mCurrentPage * TOTAL_ITEM_TO_LOAD);
        list.clear();
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.hasChildren()) {
                    ChatDao message = dataSnapshot.getValue(ChatDao.class);

                    if (pos == 0 && message.getSeen())
                        pos = itemPos;

                    rootRef.child("messages").child(userID).child(otherUser).child(dataSnapshot.getKey()).child("seen").setValue(true);
                    rootRef.child("messages").child(userID).child(otherUser).child(dataSnapshot.getKey()).child("isRead").setValue(true);
                    itemPos++;

                    if (itemPos == 1) {

                        String messageKey = dataSnapshot.getKey();

                        mLastKey = messageKey;
                        mPrevKey = messageKey;

                    }

                    list.add(message);
                    adapter.notifyDataSetChanged();
                    if (recyclerView != null)
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
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
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadMoreMessages() {

        DatabaseReference messageRef = rootRef.child("messages").child(userID).child(otherUser);

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(15);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.hasChildren()) {
                    rootRef.child("messages").child(userID).child(otherUser).child(dataSnapshot.getKey()).child("seen").setValue(true);
                    rootRef.child("messages").child(userID).child(otherUser).child(dataSnapshot.getKey()).child("isRead").setValue(true);
                    ChatDao message = dataSnapshot.getValue(ChatDao.class);
                    String messageKey = dataSnapshot.getKey();

                    if (!mPrevKey.equals(messageKey)) {

                        list.add(itemPos++, message);

                    } else {

                        mPrevKey = mLastKey;

                    }


                    if (itemPos == 1) {

                        mLastKey = messageKey;

                    }

                    adapter.notifyDataSetChanged();

                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                    if (mLinearLayout != null)
                        mLinearLayout.scrollToPositionWithOffset(10, 0);

                } else {
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}