package net.glochat.dev.utils;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


import net.glochat.dev.models.Users;

public class FirebaseHelper {


    private static final String USERS = "users";
    private static DatabaseReference databaseReference;
    private static FirebaseUser firebaseUser;
    private static FirebaseAuth firebaseAuth;
    private static Users userBean;

    private static void writeNewUser() {
        userBean = new Users();
        if (firebaseUser.getPhotoUrl() != null)
            //userBean.setProfileUrl(firebaseUser.getPhotoUrl().toString());
        userBean.setName(firebaseUser.getDisplayName());
        getDatabaseReference().child(USERS).child(firebaseUser.getUid()).setValue(userBean).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("E don work");
            }
        });

    }

    public static Users getUserBean() {
        return userBean;
    }

    public static DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public static void setDatabaseReference(DatabaseReference databaseReference) {
        FirebaseHelper.databaseReference = databaseReference;
    }

    public static FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    private static void setFirebaseUser(FirebaseUser firebaseUser) {
        FirebaseHelper.firebaseUser = firebaseUser;
        writeNewUser();
    }

    public static FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public static void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        FirebaseHelper.firebaseAuth = firebaseAuth;
        setFirebaseUser(firebaseAuth.getCurrentUser());
    }
}
