package net.glochat.dev.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.glochat.dev.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowImageActivity extends AppCompatActivity {


    public static final String USER_PHOTO = "user_photo";
    @BindView(R.id.user_profile_photo)
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        ButterKnife.bind(this);

        String res = getIntent().getStringExtra(USER_PHOTO);
        Glide.with(this).load(res).into(imageView);
    }
}