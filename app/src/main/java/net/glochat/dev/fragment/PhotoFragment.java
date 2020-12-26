package net.glochat.dev.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.glochat.dev.R;
import net.glochat.dev.activity.CameraActivity;
import net.glochat.dev.adapter.NewsFeedAdapter;
import net.glochat.dev.adapter.UsersStatusAdapter;
import net.glochat.dev.base.BaseFragment;
import net.glochat.dev.models.PicPostModel;
import net.glochat.dev.models.Users;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class PhotoFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public PhotoFragment() {
        // Required empty public constructor
    }


    public static PhotoFragment newInstance() {
        return new PhotoFragment();
    }

    private ArrayList<PicPostModel> list;
    private ArrayList<Users> usersList;
    private NewsFeedAdapter adapter;
    private UsersStatusAdapter usersStatusAdapter;
    private boolean isLoading = false;
    private int pastVisibleItems, visibleItemCounts, totalItemsCount, previousTotal = 0;
    private int viewThreashold = 10;
    private LinearLayoutManager linearLayoutManager, linearLayoutManagerStatus;
    private int pageNumber = 1;
    private int itemCount = 10;
    @BindView(R.id.photo_fragment_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.photo_fragment_recycler_view_status)
    RecyclerView recyclerViewStatus;
    @BindView(R.id.photo_fragment_swipe_refresh_status)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.photo_fragment_no_post_layout)
    LinearLayout noPostLayout;
    @BindView(R.id.photo_fragment_post_btn_post)
    RelativeLayout noPostPostBtn;

    @Override
    protected void onViewCreated() {
        //list = FetchAllPosts.picPost;
        list = new ArrayList<>();
        usersList = new ArrayList<>();

        noPostLayout.setVisibility(View.VISIBLE);

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new NewsFeedAdapter(list, getContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //recycler view for status
        linearLayoutManagerStatus = new LinearLayoutManager(getContext());
        linearLayoutManagerStatus.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewStatus.setLayoutManager(linearLayoutManagerStatus);
        recyclerViewStatus.setHasFixedSize(true);
        usersStatusAdapter = new UsersStatusAdapter(requireContext(), usersList);
        recyclerViewStatus.setAdapter(usersStatusAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCounts = linearLayoutManager.getChildCount();
                totalItemsCount = linearLayoutManager.getItemCount();

                pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                if (dy > 0) {

                    if (isLoading) {
                        if (totalItemsCount > previousTotal) {
                            isLoading = false;
                            previousTotal = totalItemsCount;
                        }
                    }

                    if (!isLoading && (totalItemsCount - visibleItemCounts) <= (pastVisibleItems + viewThreashold)) {
                        pageNumber++;
                        isLoading = true;
                    }
                }
            }
        });

        fetchUserStatus();

        noPostPostBtn.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openActivity();
            } else
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        });
    }

    private void fetchUserStatus() {
        swipeRefreshLayout.setRefreshing(true);
        usersList.clear();
        ArrayList<Users> fUsers = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot snaps : snapshot.getChildren()) {
                        Users user = snaps.getValue(Users.class);
                        if (user != null) {
                            if (!user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                fUsers.add(user);
                        }
                    }
                    usersList.add(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(Users.class));
                    usersList.addAll(fUsers);
                    usersStatusAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_user_photo;
    }

    @OnClick(R.id.camera_fab)
    void openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openActivity();
        } else
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
    }

    private void openActivity() {
        startActivity(new Intent(requireContext(), CameraActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            else {
                openActivity();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PicPostModel post) {
        Toast.makeText(requireContext(), "posted", Toast.LENGTH_LONG).show();
        noPostLayout.setVisibility(View.GONE);
        list.add(post);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh() {
        fetchUserStatus();
    }
}