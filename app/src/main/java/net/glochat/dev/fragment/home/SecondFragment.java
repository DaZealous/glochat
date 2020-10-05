package net.glochat.dev.fragment.home;




import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.glochat.dev.R;
import net.glochat.dev.activity.PlayListActivity;
import net.glochat.dev.adapter.VideoAdapter;
import net.glochat.dev.base.BaseFragment;
import net.glochat.dev.bean.CurUserBean;
import net.glochat.dev.bean.DataCreate;
import net.glochat.dev.bean.MainPageChangeEvent;
import net.glochat.dev.bean.PauseVideoEvent;
import net.glochat.dev.utils.OnVideoControllerListener;
import net.glochat.dev.utils.RxBus;
import net.glochat.dev.view.ControllerView;
import net.glochat.dev.view.FullScreenVideoView;
import net.glochat.dev.view.LikeView;
import net.glochat.dev.view.viewpagerlayoutmanager.OnViewPagerListener;
import net.glochat.dev.view.viewpagerlayoutmanager.ViewPagerLayoutManager;

import butterknife.BindView;
import rx.functions.Action1;

public class SecondFragment extends BaseFragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.refreshlayout)
    SwipeRefreshLayout refreshLayout;


    private VideoAdapter adapter;
    private ViewPagerLayoutManager viewPagerLayoutManager;
    private int curPlayPos = -1;
    private FullScreenVideoView videoView;
    private ImageView ivCurCover;


    public SecondFragment(){}

    public static SecondFragment newInstance() {
        return new SecondFragment();
    }


    @Override
    protected void onViewCreated() {
        adapter = new VideoAdapter(getActivity(), DataCreate.datas);
        recyclerView.setAdapter(adapter);

        videoView = new FullScreenVideoView(getActivity());

        setViewPagerLayoutManager();

        //setRefreshEvent();


       /* RxBus.getDefault().toObservable(PauseVideoEvent.class)
                .subscribe((Action1<PauseVideoEvent>) event -> {
                    if (event.isPlayOrPause()) {
                        videoView.start();
                    } else {
                        videoView.pause();
                    }
                });*/

    }

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_second;
    }

    private void setViewPagerLayoutManager() {
        viewPagerLayoutManager = new ViewPagerLayoutManager(getActivity());
        recyclerView.setLayoutManager(viewPagerLayoutManager);
        recyclerView.scrollToPosition(PlayListActivity.initPos);

        viewPagerLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {
                playCurVideo(PlayListActivity.initPos);
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                if (ivCurCover != null) {
                    ivCurCover.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                playCurVideo(position);
            }
        });
    }

    private void setRefreshEvent() {
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
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

    private void playCurVideo(int position) {
        if (position == curPlayPos) {
            return;
        }

        View itemView = viewPagerLayoutManager.findViewByPosition(position);
        if (itemView == null) {
            return;
        }

        ViewGroup rootView = itemView.findViewById(R.id.rl_container);
        LikeView likeView = rootView.findViewById(R.id.likeview);
        ControllerView controllerView = rootView.findViewById(R.id.controller);
        ImageView ivPlay = rootView.findViewById(R.id.play_icon);
        ImageView ivCover = rootView.findViewById(R.id.cover_photo);
        ivPlay.setAlpha(0.4f);


        likeView.setOnPlayPauseListener(() -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                ivPlay.setVisibility(View.VISIBLE);
            } else {
                videoView.start();
                ivPlay.setVisibility(View.GONE);
            }
        });


       likeShareEvent(controllerView);


        RxBus.getDefault().post(new CurUserBean(DataCreate.datas.get(position).getUserBean()));

        curPlayPos = position;


        detachParentView(rootView);

        autoPlayVideo(curPlayPos, ivCover);
    }


    private void detachParentView(ViewGroup rootView) {

        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent != null) {
            parent.removeView(videoView);
        }
        rootView.addView(videoView, 0);
    }


    private void autoPlayVideo(int position, ImageView ivCover) {
        String bgVideoPath = "android.resource://" + getActivity().getPackageName() + "/" + DataCreate.datas.get(position).getVideoRes();
        videoView.setVideoPath(bgVideoPath);
        videoView.start();
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);


            new CountDownTimer(200, 200) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    ivCover.setVisibility(View.GONE);
                    ivCurCover = ivCover;
                }
            }.start();
        });
    }

    private void likeShareEvent(ControllerView controllerView) {
        controllerView.setListener(new OnVideoControllerListener() {
            @Override
            public void onHeadClick() {
                RxBus.getDefault().post(new MainPageChangeEvent(1));
            }

            @Override
            public void onLikeClick() {

            }

            @Override
            public void onCommentClick() {
               // CommentDialog commentDialog = new CommentDialog();
             //   commentDialog.show(getChildFragmentManager(), "");
            }

            @Override
            public void onShareClick() {
               // new ShareDialog().show(getChildFragmentManager(), "");
            }
        });
    }


}