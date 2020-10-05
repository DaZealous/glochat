package net.glochat.dev.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import net.glochat.dev.R;
import net.glochat.dev.base.BaseActivity;
import net.glochat.dev.models.Users;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateProfileActivity extends BaseActivity {

    public static final String PROVIDER = "provider";
    private static final String TAG = CreateProfileActivity.class.getSimpleName();
    private FirebaseUser firebaseUser;
    private DatabaseReference firebaseDatabase;
    private StorageReference storageReference;
    private Uri uri;

    public static final int REQUEST_IMAGE = 100;

    @BindView(R.id.profile_pic)
    ImageView profileImage;
    @BindView(R.id.full_name)
    TextInputEditText fullName;
    @BindView(R.id.status)
    TextInputEditText status;
    @BindView(R.id.proceed_button)
    RelativeLayout button;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.proceed_text)
    TextView proceedText;



    @Override
    protected void onCreate() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users");


        String displayName = firebaseUser.getDisplayName();
        if (displayName != null) {
            fullName.setText(displayName);
            status.setText(displayName.replace(" ", "."));
        }

    }

    @OnClick(R.id.proceed_button)
    void setProvider() {

        if (TextUtils.isEmpty(getFullName()))
            Toast.makeText(this, "Enter your full name", Toast.LENGTH_SHORT).show();

        else if (TextUtils.isEmpty(getStatus()))
            Toast.makeText(this, "Enter your username", Toast.LENGTH_SHORT).show();
        else if (uri == null)
            Toast.makeText(this, "Select a profile photo", Toast.LENGTH_SHORT).show();
        else {
            progressBar.setVisibility(View.VISIBLE);
            proceedText.setVisibility(View.GONE);
            Users userBean = new Users();
            storageReference = FirebaseStorage.getInstance().getReference().child("profile_images/"+ uri.getLastPathSegment());
            UploadTask uploadTask = storageReference.putFile(uri);

            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Uri downloadUrl = task1.getResult();

                            userBean.setPhotoUrl(downloadUrl.toString());
                            userBean.setName(getFullName());
                            userBean.setUid(firebaseUser.getUid());
                            userBean.setBio(getStatus());
                            userBean.setFollowers("0");
                            userBean.setFollowing("0");
                            userBean.setPosts("0");

                            firebaseDatabase.child(firebaseUser.getUid()).setValue(userBean).addOnCompleteListener(task2 -> {

                                String token_id= FirebaseInstanceId.getInstance().getToken();
                                Map addValue = new HashMap();
                                addValue.put("device_token", token_id);
                                addValue.put("online","true");

                                firebaseDatabase.child(firebaseUser.getUid()).updateChildren(addValue, (error, ref) -> {
                                    Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, MainActivity.class));
                                    finish();
                                });

                            });
                        } else {
                            System.out.println("failed");
                        }
                    });
                }
            });


        }
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_create_profile;
    }

    @OnClick(R.id.profile_pic)
    void onProfileImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }

                }).check();
    }


    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_permission_title))
                .setMessage(getString(R.string.dialog_permission_message))
                .setPositiveButton(getString(R.string.go_to_settings), dialogClickListener).setNegativeButton(getString(android.R.string.cancel), dialogClickListener).show();

    }
    DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                dialog.cancel();
                openSettings();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                dialog.cancel();
                break;
        }
    };

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void loadProfilePic(Uri bitmap) {

        Glide.with(this).load(bitmap)
                .into(profileImage);
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK && data != null) {

                uri = data.getParcelableExtra("path");
                loadProfilePic(uri);
                /*try {

                    final InputStream imageStream = this.getContentResolver().openInputStream(uri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    imageStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }

    public String getStatus() {
        return status.getText().toString();
    }

    public String getFullName() {
        return fullName.getText().toString();
    }

}