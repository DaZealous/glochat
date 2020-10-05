package net.glochat.dev.fragment;


import androidx.viewpager.widget.ViewPager;



import com.androidkun.xtablayout.XTabLayout;

import net.glochat.dev.R;
import net.glochat.dev.base.BaseFragment;
import net.glochat.dev.bean.PauseVideoEvent;
import net.glochat.dev.adapter.SectionsPagerAdapter;
import net.glochat.dev.utils.RxBus;

import butterknife.BindView;


public class HomeFragment extends BaseFragment {

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tab_title)
    XTabLayout tabTitle;
    public static int curPage = 1;


    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }



    @Override
    protected void onViewCreated() {

        viewPager.setAdapter(new SectionsPagerAdapter(getContext(), getChildFragmentManager()));
        viewPager.setSaveEnabled(false);
        tabTitle.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                curPage = position;

                if (position == 1) {

                    RxBus.getDefault().post(new PauseVideoEvent(true));
                } else {

                    RxBus.getDefault().post(new PauseVideoEvent(false));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_home;
    }


}