package net.glochat.dev.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.androidkun.xtablayout.XTabLayout;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.glochat.dev.R;
import net.glochat.dev.fragment.UserPhotoFragment;
import net.glochat.dev.fragment.UserVideoFragment;
import net.glochat.dev.models.Users;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    public static final String USER_UID = "user_uid";



    @BindView(R.id.background_image)
    ImageView backgroundImage;
    @BindView(R.id.back_button)
    ImageView backButton;
    @BindView(R.id.user_profile_photo)
    CircleImageView userProfilePhoto;
    @BindView(R.id.user_title)
    TextView userTitle;
    @BindView(R.id.friend_request)
    TextView mProfileSendReqButton;
    @BindView(R.id.friend_request_cancel)
    TextView mProfileDeclineReqButton;
    @BindView(R.id.user_profile_name)
    TextView userProfileName;
    @BindView(R.id.user_bio)
    TextView userBio;
    @BindView(R.id.appbarlayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tablayout)
    XTabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;


    DatabaseReference mfriendReqReference;
    DatabaseReference mTargetDatabaseReference;
    DatabaseReference mFriendDatabase;
    DatabaseReference mNotificationReference;
    DatabaseReference mRootReference;
    DatabaseReference getmDatabaseReference;

    FirebaseUser mFirebaseUser;
    private String targetUserId;
    private String currentUserId;
    private String mCurrentState;
    private Users users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setTabLayout();
        setAppbarLayoutPercent();

        targetUserId = getIntent().getStringExtra(USER_UID);

        mProfileDeclineReqButton.setVisibility(View.INVISIBLE);
        mProfileDeclineReqButton.setEnabled(false);

        mfriendReqReference = FirebaseDatabase.getInstance().getReference().child("friend_request");
        mTargetDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(targetUserId);
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("friends");
        mNotificationReference = FirebaseDatabase.getInstance().getReference().child("notifications");
        mRootReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getmDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mFirebaseUser.getUid());

        if (mFirebaseUser != null)
            currentUserId = mFirebaseUser.getUid();

        mCurrentState = "not_friends";

        mTargetDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                users = dataSnapshot.getValue(Users.class);
                setUserInfo(users);

                mFriendDatabase.child(targetUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long len = dataSnapshot.getChildrenCount();
                      //  mprofileFriendCount.setText("TOTAL FRIENDS : " + len);


                        mfriendReqReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.hasChild(targetUserId)){

                                    String request_type = dataSnapshot.child(targetUserId).child("request_type").getValue().toString();

                                    if(request_type.equals("sent")){

                                        mCurrentState ="req_sent";
                                        mProfileSendReqButton.setText("Cancel Friend Request");
                                        mProfileDeclineReqButton.setVisibility(View.INVISIBLE);
                                        mProfileDeclineReqButton.setEnabled(false);

                                    }

                                    else if(request_type.equals("received")){
                                        mCurrentState ="req_received";
                                        mProfileSendReqButton.setText("Accept Friend Request");
                                        mProfileDeclineReqButton.setVisibility(View.VISIBLE);
                                        mProfileDeclineReqButton.setEnabled(true);
                                    }


                                }

                                //---USER IS FRIEND----
                                else{

                                    mFriendDatabase.child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            mProfileDeclineReqButton.setVisibility(View.INVISIBLE);
                                            mProfileDeclineReqButton.setEnabled(false);

                                            if(dataSnapshot.hasChild(currentUserId)){
                                                mCurrentState ="friends";
                                                mProfileSendReqButton.setText("Unfriend This Person");
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(ProfileActivity.this, "Error fetching Friend request data", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @OnClick(R.id.friend_request)
    void setmProfileSendReqButton(){


        if(getCurrentUserId().equals(getTargetUserId())){
            Toast.makeText(ProfileActivity.this, "Cannot send request to your own", Toast.LENGTH_SHORT).show();
            return ;
        }

        Log.e("m_current_state is : ", mCurrentState);
        mProfileSendReqButton.setEnabled(false);


        //-------NOT FRIEND STATE--------
        if(mCurrentState.equals("not_friends")){

            DatabaseReference newNotificationReference = mRootReference.child("notifications").child(getTargetUserId()).push();

            String newNotificationId = newNotificationReference.getKey();

            HashMap<String, String> notificationData = new HashMap<>();
            notificationData.put("from", mFirebaseUser.getUid());
            notificationData.put("type", "request");

            Map requestMap = new HashMap();
            requestMap.put("friend_request/"+mFirebaseUser.getUid()+ "/"+ getTargetUserId() + "/request_type","sent");
            requestMap.put("friend_request/"+ getTargetUserId()+"/"+mFirebaseUser.getUid()+"/request_type","received");
            requestMap.put("notifications/"+getTargetUserId()+"/"+newNotificationId,notificationData);

            //----FRIEND REQUEST IS SEND----
            mRootReference.updateChildren(requestMap, (databaseError, databaseReference) -> {
                if(databaseError==null){

                    Toast.makeText(ProfileActivity.this, "Friend Request sent successfully", Toast.LENGTH_SHORT).show();

                    mProfileSendReqButton.setEnabled(true);
                    mCurrentState= "req_sent";
                    mProfileSendReqButton.setText("Cancel Friend Request");

                }
                else{
                    mProfileSendReqButton.setEnabled(true);
                    Toast.makeText(ProfileActivity.this, "Some error in sending friend Request", Toast.LENGTH_SHORT).show();
                }

            });
        }

        //-------CANCEL--FRIEND--REQUEST-----

        if(mCurrentState.equals("req_sent")){

            Map valueMap=new HashMap();
            valueMap.put("friend_request/"+mFirebaseUser.getUid()+"/"+getTargetUserId(),null);
            valueMap.put("friend_request/"+getTargetUserId()+"/"+mFirebaseUser.getUid(),null);

            //----FRIEND REQUEST IS CANCELLED----
            mRootReference.updateChildren(valueMap, (databaseError, databaseReference) -> {
                if(databaseError == null){

                    mCurrentState = "not_friends";
                    mProfileSendReqButton.setText("Send Friend Request");
                    mProfileSendReqButton.setEnabled(true);
                    Toast.makeText(ProfileActivity.this, "Friend Request Cancelled Successfully...", Toast.LENGTH_SHORT).show();
                }
                else{

                    mProfileSendReqButton.setEnabled(true);
                    Toast.makeText(ProfileActivity.this, "Cannot cancel friend request...", Toast.LENGTH_SHORT).show();

                }
            });




        }

        //-------ACCEPT FRIEND REQUEST------

        if(mCurrentState.equals("req_received")){
            //-----GETTING DATE-----
            Date current_date=new Date(System.currentTimeMillis());

            //Log.e("----Date---",current_date.toString());
            String date[]=current_date.toString().split(" ");
            final String todays_date=(date[1] + " " + date[2] + "," + date[date.length-1] + " " + date[3]);

            Map friendMap = new HashMap();
            friendMap.put("friends/"+mFirebaseUser.getUid() + "/" + getTargetUserId() + "/date", todays_date);
            friendMap.put("friends/" + getTargetUserId() + "/" + mFirebaseUser.getUid() + "/date", todays_date);

            friendMap.put("friend_request/" + mFirebaseUser.getUid()+"/"+getTargetUserId(),null);
            friendMap.put("friend_request/"+getTargetUserId()+"/"+mFirebaseUser.getUid(),null);

            //-------BECAME FRIENDS------
            mRootReference.updateChildren(friendMap, (databaseError, databaseReference) -> {

                if(databaseError==null){

                    mProfileSendReqButton.setEnabled(true);
                    mCurrentState = "friends";
                    mProfileSendReqButton.setText("Unfriend this person");
                    mProfileDeclineReqButton.setEnabled(false);
                    mProfileDeclineReqButton.setVisibility(View.INVISIBLE);

                }
                else{
                    mProfileSendReqButton.setEnabled(true);
                    Toast.makeText(ProfileActivity.this, "Error is " +databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }


        //----UNFRIEND---THIS---PERSON----
        if(mCurrentState.equals("friends")){

            Map valueMap=new HashMap();
            valueMap.put("friends/"+mFirebaseUser.getUid()+"/"+getTargetUserId(),null);
            valueMap.put("friends/"+getTargetUserId()+"/"+mFirebaseUser.getUid(),null);

            //----UNFRIENDED THE PERSON---
            mRootReference.updateChildren(valueMap, (databaseError, databaseReference) -> {
                if(databaseError == null){
                    mCurrentState = "not_friends";
                    mProfileSendReqButton.setText("Send Friend Request");
                    mProfileSendReqButton.setEnabled(true);
                    Toast.makeText(ProfileActivity.this, "Successfully Unfriended...", Toast.LENGTH_SHORT).show();
                }
                else{
                    mProfileSendReqButton.setEnabled(true);
                    Toast.makeText(ProfileActivity.this, "Cannot Unfriend..Contact Kshitiz..", Toast.LENGTH_SHORT).show();

                }
            });


        }

    }


    @OnClick(R.id.friend_request_cancel)
    void setmProfileDeclineReqButton(){
        Map valueMap=new HashMap();
        valueMap.put("friend_request/"+mFirebaseUser.getUid()+"/"+getTargetUserId(),null);
        valueMap.put("friend_request/"+getTargetUserId()+"/"+mFirebaseUser.getUid(),null);

        mRootReference.updateChildren(valueMap, (databaseError, databaseReference) -> {
            if(databaseError == null){

                mCurrentState = "not_friends";
                mProfileSendReqButton.setText("Send Friend Request");
                mProfileSendReqButton.setEnabled(true);
                Toast.makeText(ProfileActivity.this, "Friend Request Declined Successfully...", Toast.LENGTH_SHORT).show();

                mProfileDeclineReqButton.setEnabled(false);
                mProfileDeclineReqButton.setVisibility(View.INVISIBLE);
            }
            else{

                mProfileSendReqButton.setEnabled(true);
                Toast.makeText(ProfileActivity.this, "Cannot decline friend request...", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public String getCurrentUserId() {
        return currentUserId;
    }


    public String getTargetUserId() {
        return targetUserId;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setUserInfo(Users userInfo) {

        userProfileName.setText(userInfo.getName());
        userTitle.setText(userInfo.getName());
        userBio.setText(userInfo.getBio());

        loadImage(userInfo.getPhotoUrl(), backgroundImage);
        loadImage(userInfo.getPhotoUrl(), userProfilePhoto);
        coordinatorLayoutBackTop();
    }

    private void loadImage(String bitmap, ImageView imageView) {

        Glide.with(this).load(bitmap).into(imageView);
    }


    private void setAppbarLayoutPercent() {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float percent = (Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange());

            if (percent > 0.8) {
                userTitle.setVisibility(View.VISIBLE);
                float alpha = 1 - (1 - percent) * 5;
                userTitle.setAlpha(alpha);

            }
            else {
                userTitle.setVisibility(View.GONE);

            }
        });
    }

    private void coordinatorLayoutBackTop() {
        CoordinatorLayout.Behavior behavior =
                ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).getBehavior();
        if (behavior instanceof AppBarLayout.Behavior) {
            AppBarLayout.Behavior appBarLayoutBehavior = (AppBarLayout.Behavior) behavior;
            int topAndBottomOffset = appBarLayoutBehavior.getTopAndBottomOffset();
            if (topAndBottomOffset != 0) {
                appBarLayoutBehavior.setTopAndBottomOffset(0);
            }
        }
    }


    public void transitionAnim(View view) {
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, getString(R.string.trans));
        Intent intent = new Intent(this, ShowImageActivity.class);
        intent.putExtra(ShowImageActivity.USER_PHOTO, users.getPhotoUrl());
        ActivityCompat.startActivity(this, intent, compat.toBundle());
    }


    private void setTabLayoutIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.user_video);
        tabLayout.getTabAt(1).setIcon(R.drawable.user_photo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTabLayoutIcons();

    }

    private void setTabLayout() {

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @OnClick(R.id.user_profile_photo)
    void setUserProfilePhoto() {
        transitionAnim(userProfilePhoto);
    }

    private static class PagerAdapter extends FragmentPagerAdapter {

        @StringRes
        private static final int[] TAB_TITLES = new int[]{R.string.videos, R.string.photos};
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
                    return new UserVideoFragment();
                case 1:
                    return new UserPhotoFragment();

            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            return context.getResources().getString(TAB_TITLES[position]);
        }
    }
}