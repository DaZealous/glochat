package net.glochat.dev.activity;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import net.glochat.dev.R;
import net.glochat.dev.bean.MainPageChangeEvent;
import net.glochat.dev.bean.PauseVideoEvent;
import net.glochat.dev.fragment.ChatFragment;
import net.glochat.dev.fragment.PhotoFragment;
import net.glochat.dev.fragment.CallFragment;
import net.glochat.dev.fragment.VideoFragment;
import net.glochat.dev.models.Users;
import net.glochat.dev.utils.RxBus;
import net.glochat.dev.view.CircleImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    CircleImageView profilePic;

    private FirebaseUser firebaseUser;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("");

        View view = LayoutInflater.from(this).inflate(R.layout.custom_toolbar_home, null);
        profilePic = view.findViewById(R.id.profile_pic);

        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(view, layout);

        //toolbar.addView(view);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseReference.keepSynced(true);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        try {

            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            mDatabaseReference.child(firebaseUser.getUid()).child("time_stamp").onDisconnect().setValue(ServerValue.TIMESTAMP);
            mDatabaseReference.child(firebaseUser.getUid()).child("online").onDisconnect().setValue("false");

            mDatabaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users value = snapshot.getValue(Users.class);
                    Glide.with(getApplicationContext()).load(value.getPhotoUrl()).placeholder(R.drawable.profile_placeholder).into(profilePic);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            RxBus.getDefault().toObservable(MainPageChangeEvent.class)
                    .subscribe((Action1<MainPageChangeEvent>) event -> {
                        if (viewPager != null) {
                            viewPager.setCurrentItem(event.getPage());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    RxBus.getDefault().post(new PauseVideoEvent(true));
                }
                else if (position == 1) {
                    RxBus.getDefault().post(new PauseVideoEvent(false));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseReference.child(firebaseUser.getUid()).child("online").setValue("true");
    }
    @Override
    protected void onResume() {
        super.onResume();
        setTabLayoutIcons();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setTint(getColor(R.color.white));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;

    }*/

    private void setTabLayoutIcons(){
        tabLayout.getTabAt(0).setIcon(R.drawable.home_video_camera_white);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_chat);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_photo);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_call);
    }

    private static class PagerAdapter extends FragmentPagerAdapter {

        @StringRes
        private static final int[] TAB_TITLES = new int[]{R.string.video, R.string.chat, R.string.photo, R.string.call};
        private Context context;

        public PagerAdapter(@NonNull FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new VideoFragment();
                case 1:
                    return new ChatFragment();
                case 2:
                    return new PhotoFragment();
                case 3:
                    return new CallFragment();

            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            return  context.getResources().getString(TAB_TITLES[position]);
        }
    }

}