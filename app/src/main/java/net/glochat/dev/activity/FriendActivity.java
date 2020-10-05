package net.glochat.dev.activity;



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.glochat.dev.R;
import net.glochat.dev.base.BaseActivity;
import net.glochat.dev.models.Friends;
import net.glochat.dev.models.Users;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class FriendActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private String mCurrentUserId;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mUsersDatabase;

    private ChildEventListener mFriendListener;
    private List<Users> userList = new ArrayList<>();

    @Override
    protected void onCreate() {

        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("friends").child(mCurrentUserId);
        mFriendDatabase.keepSynced(true);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                System.out.println("God help me");
                System.out.println(snapshot.getValue());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        mFriendDatabase.addChildEventListener(childEventListener);
        mFriendListener = childEventListener;


        //---USERS DATA
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onStart() {
        super.onStart();

        //---FETCHING DATABASE FROM mFriendDatabase USING Friends.class AND ADDING TO RECYCLERVIEW----



        Query query = mFriendDatabase;

        SnapshotParser<Friends> parser = snapshot -> {

            Friends value = snapshot.getValue(Friends.class);

            System.out.println(value.getUid());
            if (value != null)
                return value;
            return new Friends();
        };


        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();


        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecycleAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                options
        ) {
            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.data_item_friend, parent, false);

                return new FriendsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull Friends model) {
                System.out.println("Please work");
                holder.setDate(model.getUid());
                final String list_user_id = getRef(position).getKey();
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //---IT WORKS WHENEVER CHILD OF mMessageDatabase IS CHANGED---

                        Users users = dataSnapshot.getValue(Users.class);

                        if(dataSnapshot.hasChild("online")){
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setOnline(userOnline);

                        }
                        holder.setName(users.getName());
                        holder.setUserImage(users.getPhotoUrl());

                        //--ALERT DIALOG FOR OPEN PROFILE OR SEND MESSAGE----

                        holder.mView.setOnClickListener(v -> {
                            CharSequence[] options = new CharSequence[]{"Open Profile" , "Send Message"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);
                            builder.setTitle("Select Options");
                            builder.setItems(options, (dialog, which) -> {

                                if(which == 0){
                                    Intent intent=new Intent(FriendActivity.this, ProfileActivity.class);
                                    intent.putExtra("user_id", list_user_id);
                                    startActivity(intent);
                                }

                                if(which == 1){
                                    Intent intent = new Intent(FriendActivity.this, ChatActivity.class);
                                    intent.putExtra("user_id",list_user_id);
                                    intent.putExtra("user_name", users.getName());
                                    startActivity(intent);
                                }

                            });
                            builder.show();

                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

            }

        };
        recyclerView.setAdapter(friendsRecycleAdapter);
    }



    @Override
    protected int setLayoutView() {
        return R.layout.activity_friend;
    }

    @OnClick(R.id.view_users_button)
    void setViewAllUsersButton(){
        startActivity(new Intent(this, ContactActivity.class));
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }
        public void setDate(String date){

            TextView lastMessage = mView.findViewById(R.id.last_message);
            lastMessage.setText(date);

        }
        public void setName(String name){

            TextView userNameView = mView.findViewById(R.id.name);
            userNameView.setText(name);

        }
        public void setUserImage(String userThumbImage){
            ImageView userImageview=  mView.findViewById(R.id.user_img);
            Glide.with(mView).load(userThumbImage).into(userImageview);

        }
        public void setOnline(String isOnline){
            ImageView online = mView.findViewById(R.id.userSingleOnlineIcon);
            if(isOnline.equals("true")){
                online.setVisibility(View.VISIBLE);
            }
            else{
                online.setVisibility(View.INVISIBLE);
            }
        }
    }

}