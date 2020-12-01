package net.glochat.dev.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.glochat.dev.R;
import net.glochat.dev.adapter.UsersCallHistoryAdapter;
import net.glochat.dev.base.BaseFragment;
import net.glochat.dev.models.Users;
import net.glochat.dev.activity.CallVideoOrAudio;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CallFragment extends BaseFragment {

    @BindView(R.id.fragment_call_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_call_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.fragment_call_fab)
    FloatingActionButton fab;

    private String mCurrentUserId;
    private DatabaseReference mUsersDatabase;

    private List<Users> userList = new ArrayList<>();

    private UsersCallHistoryAdapter adapter;

    public CallFragment() {
        // Required empty public constructor
    }

    public static CallFragment newInstance() {
        return new CallFragment();
    }

    @Override
    protected void onViewCreated() {

        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        adapter = new UsersCallHistoryAdapter(requireContext(), userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        loadUsers();
    }

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_call;
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

    @OnClick(R.id.fragment_call_fab)
    void showCallActivity(){
        startActivity(new Intent(requireContext(), CallVideoOrAudio.class));
    }

}