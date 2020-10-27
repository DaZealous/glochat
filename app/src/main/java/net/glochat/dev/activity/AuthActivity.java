package net.glochat.dev.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import android.content.Intent;

import android.widget.Toast;


import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import net.glochat.dev.BuildConfig;
import net.glochat.dev.R;
import net.glochat.dev.base.BaseActivity;
import net.glochat.dev.models.Users;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import static net.glochat.dev.utils.Constants.PROVIDER_FACEBOOK;
import static net.glochat.dev.utils.Constants.PROVIDER_GOOGLE;
import static net.glochat.dev.utils.Constants.PROVIDER_PASSWORD;
import static net.glochat.dev.utils.Constants.PROVIDER_PHONE;

public class AuthActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = AuthActivity.class.getSimpleName();


    @Override
    protected void onCreate() {

        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_auth)
                /*   .setPhoneButtonId(R.id.phoneSignInButton)
                   .setGoogleButtonId(R.id.googleSignInButton)
                   .setFacebookButtonId(R.id.facebookSignInButton)*/
                .setEmailButtonId(R.id.emailSignInButton)
                .build();

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
                        .setAvailableProviders(Arrays.asList(
                              /*  new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                new AuthUI.IdpConfig.PhoneBuilder().build(),*/
                                new AuthUI.IdpConfig.EmailBuilder().build()))
                        .setAuthMethodPickerLayout(customLayout)
                        .setTheme(R.style.Theme_GloChat)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_base;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK && response != null) {
                String providerType = response.getProviderType();
                if (providerType != null) {
                    checkUser(response);
                }
            } else {

                if (response != null && response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response == null) {
                    Toast.makeText(this, "Sign In cancelled", Toast.LENGTH_SHORT).show();
                }


                Toast.makeText(this, "Sign in failed, please try again later", Toast.LENGTH_SHORT).show();


            }
            finish();
        }
    }


    private void createProfile(String providerType) {
        switch (providerType) {
            case PROVIDER_PHONE:
            case PROVIDER_PASSWORD:
                //checkUser(providerType);
                break;
            case PROVIDER_FACEBOOK:
            case PROVIDER_GOOGLE:
                saveUserBean();
                break;

        }
    }

    private void checkUser(IdpResponse response) {
        if (response.isNewUser()) {
            Intent intent = new Intent(AuthActivity.this, CreateProfileActivity.class);
            intent.putExtra(CreateProfileActivity.PROVIDER, PROVIDER_PASSWORD);
            startActivity(intent);
        } else {
            Toast.makeText(AuthActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AuthActivity.this, MainActivity.class));
        }

       /* FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(snapshot.hasChild(id)){
                    Toast.makeText(AuthActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                }else{
                    Intent intent = new Intent(AuthActivity.this, CreateProfileActivity.class);
                    intent.putExtra(CreateProfileActivity.PROVIDER, PROVIDER_PASSWORD);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    private void saveUserBean() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users");
        Users userBean = new Users();
        userBean.setName(currentUser.getDisplayName());
        userBean.setPhotoUrl(currentUser.getPhotoUrl().toString());
        userBean.setUid(currentUser.getUid());
        userBean.setBio("My name is " + currentUser.getDisplayName());
        userBean.setFollowers("0");
        userBean.setFollowing("0");
        userBean.setPosts("0");

        users.child(currentUser.getUid()).setValue(userBean).addOnCompleteListener(task -> {
            String token_id = FirebaseInstanceId.getInstance().getToken();
            Map addValue = new HashMap();
            addValue.put("device_token", token_id);
            addValue.put("online", "true");

            users.child(currentUser.getUid()).updateChildren(addValue, (error, ref) -> {
                startActivity(new Intent(AuthActivity.this, MainActivity.class));
                finish();
            });
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Registration failed please try again", Toast.LENGTH_SHORT).show());
    }


}