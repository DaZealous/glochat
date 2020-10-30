package net.glochat.dev.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.glochat.dev.R;

import net.glochat.dev.activity.FriendActivity;
import net.glochat.dev.adapter.ChatHistoryAdapter;
import net.glochat.dev.base.BaseFragment;
import net.glochat.dev.models.Conv;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;


public class ChatFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.activity_chat_history_recycler_view)
    RecyclerView mConvList;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.activity_chat_history_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fragments_chat_no_recent_chat_layout)
    LinearLayout noChatLayout;

    List<String> list;
    List<Conv> convs;

    ChatHistoryAdapter adapter;
    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;
    private String mCurrent_user_id;

    private FirebaseUser firebaseUser;


    public ChatFragment() {
        // Required empty public constructor
    }


    @OnClick(R.id.fab)
    void setFloatingActionButton() {

        startActivity(new Intent(getActivity(), FriendActivity.class));

    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrent_user_id = firebaseUser.getUid();
        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);

        mConvDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        mUsersDatabase.keepSynced(true);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void onViewCreated() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        list = new ArrayList<>();
        convs = new ArrayList<>();
        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);
        adapter = new ChatHistoryAdapter(list, convs, requireContext());
        mConvList.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        loadHistory();

        //ChatPageAdapter adapter = new ChatPageAdapter(getActivity(), DataCreate.datas);
        // recyclerView.setAdapter(adapter);

    }

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_chat;
    }

    protected void loadHistory() {
        swipeRefreshLayout.setRefreshing(true);
        noChatLayout.setVisibility(View.GONE);
        list.clear();
        convs.clear();
        adapter.notifyDataSetChanged();

        Query conversationQuery = mConvDatabase.orderByChild("time_stamp");

        conversationQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                if (dataSnapshot1.hasChildren()) {
                    list.clear();
                    convs.clear();
                    adapter.notifyDataSetChanged();
                    mMessageDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            list.clear();
                            convs.clear();
                            adapter.notifyDataSetChanged();
                            for (DataSnapshot snap : dataSnapshot1.getChildren()) {
                                if (snap.child("type").getValue(String.class) != null) {
                                    if (snap.child("type").getValue(String.class).equals("user")) {
                                        if (dataSnapshot2.child(Objects.requireNonNull(snap.getKey())).exists())
                                            if (snap.child("time_stamp").getValue(Long.class) != null) {
                                                list.add(snap.getKey());
                                                convs.add(snap.getValue(Conv.class));
                                            }
                                    } else {
                                        if (snap.child("time_stamp").getValue(Long.class) != null) {
                                            list.add(snap.getKey());
                                            convs.add(snap.getValue(Conv.class));
                                        }
                                    }
                                }
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            if (list.isEmpty())
                                noChatLayout.setVisibility(View.VISIBLE);
                            else {
                                noChatLayout.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    noChatLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onRefresh() {
        loadHistory();
    }
}