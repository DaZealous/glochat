package net.glochat.dev.activity;


import net.glochat.dev.R;
import net.glochat.dev.base.BaseActivity;
import net.glochat.dev.fragment.home.SecondFragment;

public class PlayListActivity extends BaseActivity {
    public static int initPos;


    @Override
    protected void onCreate() {
        getSupportFragmentManager().beginTransaction().add(R.id.framelayout, new SecondFragment()).commit();
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_play_list;
    }
}
