package net.glochat.dev.fragment;




import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.glochat.dev.R;

import net.glochat.dev.activity.ChatActivity;
import net.glochat.dev.activity.ContactActivity;
import net.glochat.dev.activity.FriendActivity;
import net.glochat.dev.base.BaseFragment;
import net.glochat.dev.bean.DataCreate;
import net.glochat.dev.models.Conversation;
import net.glochat.dev.models.Users;
import net.glochat.dev.utils.Constants;



import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChatFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    private FirebaseUser firebaseUser;
    private DatabaseReference mConvDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mMessageDatabase;

    private String currentUserID;



    public ChatFragment() {
        // Required empty public constructor
    }


    @OnClick(R.id.fab)
    void setFloatingActionButton(){

        startActivity(new Intent(getActivity(), FriendActivity.class));

    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserID = firebaseUser.getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("chats").child(currentUserID);
        mConvDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.USERS);
        mUsersDatabase.keepSynced(true);

        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.MESSAGES).child(currentUserID);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void onViewCreated() {



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //ChatPageAdapter adapter = new ChatPageAdapter(getActivity(), DataCreate.datas);
       // recyclerView.setAdapter(adapter);

    }

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_chat;
    }

    @Override
    public void onStart() {
        super.onStart();


        Query query = mConvDatabase; //.orderByChild("time_stamp");

        SnapshotParser<Conversation> parser = snapshot -> snapshot.getValue(Conversation.class);

        FirebaseRecyclerOptions<Conversation> options =
                new FirebaseRecyclerOptions.Builder<Conversation>()
                        .setQuery(query, parser)
                        .build();


        FirebaseRecyclerAdapter<Conversation, ConversationViewHolder> friendsRecycleAdapter =
                new FirebaseRecyclerAdapter<Conversation, ConversationViewHolder>(options) {


            @NonNull
            @Override
            public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.data_item_chat, parent, false);

                return new ConversationViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ConversationViewHolder holder, int position, @NonNull Conversation model) {
                final String userId = getRef(position).getKey();
                Query lastMessageQuery = mMessageDatabase.child(userId).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String data = dataSnapshot.child("message").getValue().toString();
                        holder.lastMessage.setText(data);

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

                mUsersDatabase.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Users user = dataSnapshot.getValue(Users.class);
                        final String userName = user.getName();
                        String userThumb = user.getPhotoUrl();

                        if(dataSnapshot.hasChild("online")){

                            String userOnline = dataSnapshot.child("online").getValue().toString();

                        }
                        holder.friendName.setText(userName);
                        loadProfile(userThumb, holder);

                        holder.itemView.setOnClickListener(v -> {

                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                            chatIntent.putExtra("user_id", userId);
                            chatIntent.putExtra("user_name", userName);
                            startActivity(chatIntent);
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }


            private void loadProfile(String bitmap, ConversationViewHolder holder) {

                Glide.with(ChatFragment.this).load(bitmap)
                        .into(holder.friendProfilePic);
            }

        };
        recyclerView.setAdapter(friendsRecycleAdapter);
    }



    public static class ConversationViewHolder extends RecyclerView.ViewHolder{

        ImageView friendProfilePic;
        TextView friendName;
        TextView lastMessage;
        TextView messageTime;


        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);

            friendProfilePic = itemView.findViewById(R.id.user_img);
            friendName = itemView.findViewById(R.id.user_name);
            lastMessage = itemView.findViewById(R.id.last_message);
            messageTime = itemView.findViewById(R.id.message_time);
        }

    }

}