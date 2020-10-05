package net.glochat.dev.view.viewpagerlayoutmanager;



public interface OnViewPagerListener {

    void onInitComplete();


    void onPageRelease(boolean isNext, int position);


    void onPageSelected(int position, boolean isBottom);

}
