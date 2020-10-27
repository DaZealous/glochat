package net.glochat.dev.base;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    protected Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayoutView());
        unbinder = ButterKnife.bind(this);
        onCreate();
    }

    protected abstract void onCreate();

    protected abstract int setLayoutView();

    protected void setFullScreen() {
        ImmersionBar.with(this).init();
    }

    protected void setSystemBarColor(int color) {
        ImmersionBar.with(this).statusBarColor(color);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();
    }
}