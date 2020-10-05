package net.glochat.dev.activity;

import android.content.Intent;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

import net.glochat.dev.R;
import net.glochat.dev.base.BaseActivity;



public class SplashScreen extends BaseActivity {


    private static final String TAG = "splash_screen_activity";

    @Override
    protected void onCreate() {
        new Handler().postDelayed(this::gotoPhoneAuthActivity, 3000L);
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_splash_screen;
    }

    private void gotoPhoneAuthActivity() {
        Intent intent;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null)
            intent = new Intent(this, MainActivity.class);
        else
            intent = new Intent(this, OnboardScreen.class);

        startActivity(intent);
        finish();
    }

}