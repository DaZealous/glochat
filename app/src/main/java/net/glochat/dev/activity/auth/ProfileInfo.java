package net.glochat.dev.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;

import net.glochat.dev.R;
import net.glochat.dev.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class ProfileInfo extends BaseActivity {

    @BindView(R.id.full_name)
    TextInputEditText fullName;


    @Override
    protected void onCreate() {

    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_profile_info;
    }

    @OnClick(R.id.proceed_button)
    void setProceedButton(){

    }
}