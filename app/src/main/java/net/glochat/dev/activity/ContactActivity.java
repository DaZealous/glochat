package net.glochat.dev.activity;


import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import net.glochat.dev.R;
import net.glochat.dev.base.BaseActivity;
import net.glochat.dev.models.Users;

import butterknife.BindView;

public class ContactActivity extends BaseActivity {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;



    private DatabaseReference mUserReference;
    private FirebaseRecyclerAdapter<Users, UserDataViewHolder> adapter;



    private String TAG = ContactActivity.class.getSimpleName();

    @Override
    protected void onCreate() {

        mUserReference = FirebaseDatabase.getInstance().getReference("users");

    }

    @Override
    protected void onStart() {
        super.onStart();


        Query query = mUserReference;

        SnapshotParser<Users> parser = snapshot -> {
            Users value = snapshot.getValue(Users.class);
            if (value != null)
                return value;
            return new Users();
        };
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, parser)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Users, UserDataViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserDataViewHolder holder, int position, @NonNull Users model) {
                holder.userNameTextView.setText(model.getName());
                holder.userStatusTextView.setText(model.getBio());
                loadProfile(model.getPhotoUrl(), holder);


                Intent intent = new Intent(ContactActivity.this, ProfileActivity.class);
                intent.putExtra(ProfileActivity.USER_UID, model.getUid());

                holder.itemView.setOnClickListener(view -> {
                    startActivity(intent);
                });
            }
            private void loadProfile(String bitmap, UserDataViewHolder holder) {

                Glide.with(holder.itemView).load(bitmap).into(holder.imageView);
            }


            @NonNull
            @Override
            public UserDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.data_item_contact, parent, false);

                return new UserDataViewHolder(view);
            }

        };

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();


    }



    @Override
    protected int setLayoutView() {
        return R.layout.activity_contact;
    }




    public static class UserDataViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView userNameTextView;
        TextView userStatusTextView;

        UserDataViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.user_img);
            userNameTextView = view.findViewById(R.id.user_name);
            userStatusTextView = view.findViewById(R.id.last_message);
        }


    }

}

