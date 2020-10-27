package net.glochat.dev.activity;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.glochat.dev.R;
import net.glochat.dev.adapter.UsersListAdapter;
import net.glochat.dev.base.BaseActivity;
import net.glochat.dev.models.Users;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class FriendActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.activity_friend_prgress_bar)
    ProgressBar progressBar;

    private String mCurrentUserId;
    private DatabaseReference mUsersDatabase;

    private List<Users> userList = new ArrayList<>();

    private UsersListAdapter adapter;

    @Override
    protected void onCreate() {

        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        //mUsersDatabase.keepSynced(true);

        adapter = new UsersListAdapter(this, userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        loadUsers();
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);

        userList.clear();

        mUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snaps : snapshot.getChildren()) {
                   Users users = snaps.getValue(Users.class);
                    if (users != null) {
                        if (!users.getUid().equals(mCurrentUserId))
                            userList.add(users);
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_friend;
    }

    /*@OnClick(R.id.view_users_button)
    void setViewAllUsersButton(){
        startActivity(new Intent(this, ContactActivity.class));
    }
*/
}