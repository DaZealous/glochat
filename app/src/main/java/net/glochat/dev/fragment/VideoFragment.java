package net.glochat.dev.fragment;


import android.os.CountDownTimer;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.glochat.dev.R;
import net.glochat.dev.adapter.GridVideoAdapter;
import net.glochat.dev.base.BaseFragment;
import net.glochat.dev.bean.DataCreate;

import butterknife.BindView;
import butterknife.OnClick;

public class VideoFragment extends BaseFragment {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.camera_fab)
    FloatingActionButton cameraButton;

    public VideoFragment(){}

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    protected void onViewCreated() {
        new DataCreate().initData();

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        GridVideoAdapter adapter = new GridVideoAdapter(getActivity(), DataCreate.datas);
        recyclerView.setAdapter(adapter);
        refreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        refreshLayout.setOnRefreshListener(() -> new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                refreshLayout.setRefreshing(false);
            }
        }.start());


    }

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_first;
    }

    @OnClick(R.id.camera_fab)
    void setCameraButton(){
       // startActivity(new Intent(getContext(), CameraActivity.class));
    }
}