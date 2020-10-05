/*
 *
 * Copyright 2018 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package net.glochat.dev.interactors;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import net.glochat.dev.app.ApplicationHelper;
import net.glochat.dev.interactors.interfaces.OnCountChangedListener;
import net.glochat.dev.interactors.interfaces.OnDataChangedListener;
import net.glochat.dev.interactors.interfaces.OnObjectExistListener;
import net.glochat.dev.interactors.interfaces.OnRequestComplete;
import net.glochat.dev.models.Follower;
import net.glochat.dev.models.Following;
import net.glochat.dev.utils.DatabaseHelper;
import net.glochat.dev.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FollowInteractor {

    private static final String TAG = FollowInteractor.class.getSimpleName();
    private static FollowInteractor instance;

    private DatabaseHelper databaseHelper;
    private Context context;

    public static FollowInteractor getInstance(Context context) {
        if (instance == null) {
            instance = new FollowInteractor(context);
        }

        return instance;
    }

    private FollowInteractor(Context context) {
        this.context = context;
        databaseHelper = ApplicationHelper.getDatabaseHelper();
    }



    private DatabaseReference getFollowersRef(String userId) {
        return databaseHelper
                .getDatabaseReference()
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(userId)
                .child(DatabaseHelper.FOLLOWERS_DB_KEY);
    }

    private DatabaseReference getFollowingsRef(String userId) {
        return databaseHelper
                .getDatabaseReference()
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(userId)
                .child(DatabaseHelper.FOLLOWINGS_DB_KEY);
    }

    public void getFollowersList(String targetUserId, OnDataChangedListener<String> onDataChangedListener) {
        getFollowersRef(targetUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Follower follower = snapshot.getValue(Follower.class);

                    if (follower != null) {
                        String profileId = follower.getProfileId();
                        list.add(profileId);
                    }

                }
                onDataChangedListener.onListChanged(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LogUtil.logDebug(TAG, "getFollowersList, onCancelled");
            }
        });
    }

    public void getFollowingsList(String targetUserId, OnDataChangedListener<String> onDataChangedListener) {
        getFollowingsRef(targetUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Following following = snapshot.getValue(Following.class);

                    if (following != null) {
                        String profileId = following.getProfileId();
                        list.add(profileId);
                    }

                }
                onDataChangedListener.onListChanged(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LogUtil.logDebug(TAG, "getFollowingsList, onCancelled");
            }
        });
    }

    public ValueEventListener getFollowingsCount(String targetUserId, OnCountChangedListener onCountChangedListener) {
        return getFollowingsRef(targetUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onCountChangedListener.onCountChanged(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LogUtil.logDebug(TAG, "getFollowingsCount, onCancelled");
            }
        });
    }

    public ValueEventListener getFollowersCount(String targetUserId, OnCountChangedListener onCountChangedListener) {
        return getFollowersRef(targetUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onCountChangedListener.onCountChanged(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LogUtil.logDebug(TAG, "getFollowersCount, onCancelled");
            }
        });
    }

    public void isFollowingExist(String followerUserId, String followingUserId, final OnObjectExistListener onObjectExistListener) {
        DatabaseReference followingRef = databaseHelper
                .getDatabaseReference()
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(followerUserId)
                .child(DatabaseHelper.FOLLOWINGS_DB_KEY)
                .child(followingUserId);

        DatabaseReference followerRef = databaseHelper
                .getDatabaseReference()
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(followingUserId)
                .child(DatabaseHelper.FOLLOWERS_DB_KEY)
                .child(followerUserId);


        followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            onObjectExistListener.onDataChanged(dataSnapshot.exists());
                            LogUtil.logDebug(TAG, "isFollowerExist for" + followingUserId + ", success: " + dataSnapshot.exists());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    onObjectExistListener.onDataChanged(false);
                    LogUtil.logDebug(TAG, "isFollowingExist for" + followerUserId + ", success: " + dataSnapshot.exists());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LogUtil.logDebug(TAG, "isFollowingExist, onCancelled");
            }
        });
    }

    private Task<Void> addFollowing(String followerUserId, String followingUserId) {
        Following following = new Following(followingUserId);
        return databaseHelper
                .getDatabaseReference()
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(followerUserId)
                .child(DatabaseHelper.FOLLOWINGS_DB_KEY)
                .child(followingUserId)
                .setValue(following);
    }

    private Task<Void> addFollower(String followerUserId, String followingUserId) {
        Follower follower = new Follower(followerUserId);
        return databaseHelper
                .getDatabaseReference()
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(followingUserId)
                .child(DatabaseHelper.FOLLOWERS_DB_KEY)
                .child(followerUserId)
                .setValue(follower);
    }

    private Task<Void> removeFollowing(String followerUserId, String followingUserId) {
        return databaseHelper
                .getDatabaseReference()
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(followerUserId)
                .child(DatabaseHelper.FOLLOWINGS_DB_KEY)
                .child(followingUserId)
                .removeValue();
    }

    private Task<Void> removeFollower(String followerUserId, String followingUserId) {
        return databaseHelper
                .getDatabaseReference()
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(followingUserId)
                .child(DatabaseHelper.FOLLOWERS_DB_KEY)
                .child(followerUserId).removeValue();
    }

    public void unfollowUser(Activity activity, String followerUserId, String followingUserId, final OnRequestComplete onRequestComplete) {
        removeFollowing(followerUserId, followingUserId)
                .continueWithTask(task -> removeFollower(followerUserId, followingUserId))
                .addOnCompleteListener(activity, task -> {
                    onRequestComplete.onComplete(task.isSuccessful());
                    LogUtil.logDebug(TAG, "unfollowUser " + followingUserId + ", success: " + task.isSuccessful());
                });

    }

    public void followUser(Activity activity, String followerUserId, String followingUserId, final OnRequestComplete onRequestComplete) {
        addFollowing(followerUserId, followingUserId)
                .continueWithTask(task -> addFollower(followerUserId, followingUserId))
                .addOnCompleteListener(activity, task -> {
                    onRequestComplete.onComplete(task.isSuccessful());
                    LogUtil.logDebug(TAG, "followUser " + followingUserId + ", success: " + task.isSuccessful());
                });
    }
}
