package net.glochat.dev.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import net.glochat.dev.R
import net.glochat.dev.base.BaseActivity
import net.glochat.dev.models.Users
import net.glochat.dev.utils.SharedPref
import java.util.*

class CreateProfileActivity : BaseActivity() {
    private var firebaseUser: FirebaseUser? = null
    private var firebaseDatabase: DatabaseReference? = null
    private var storageReference: StorageReference? = null
    private var uri: Uri? = null
    private var providerUri: Uri? = null

    var profileImage: CircleImageView? = null

    var profile_pic: ImageButton? = null

    var fullName: TextInputEditText? = null

    var username: TextInputEditText? = null

    var button: RelativeLayout? = null

    var progressBar: ProgressBar? = null

    var proceedText: TextView? = null

    var phoneNumField: TextInputEditText? = null

    override fun onCreate() {

        button = findViewById(R.id.proceed_button)
        profile_pic = findViewById(R.id.profile_pic)
        profileImage = findViewById(R.id.activity_create_profile_image)
        fullName = findViewById(R.id.full_name)
        username = findViewById(R.id.username)
        progressBar = findViewById(R.id.progress_bar)
        proceedText = findViewById(R.id.proceed_text)
        phoneNumField = findViewById(R.id.activity_create_profile_phone_number)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseDatabase = FirebaseDatabase.getInstance().reference.child("users")
        val displayName = firebaseUser!!.displayName
        providerUri = firebaseUser!!.photoUrl
        if (providerUri != null) loadProfilePic(providerUri)
        if (displayName != null) {
            fullName!!.setText(displayName)
            fullName!!.isEnabled = false
            fullName!!.alpha = 0.5f
        } else {
            fullName!!.isEnabled = true
            fullName!!.alpha = 1f
        }

        if (firebaseUser!!.phoneNumber != null) {
            val number = if (firebaseUser!!.phoneNumber!!.contains("+234"))
                "0${firebaseUser!!.phoneNumber!!.split("+234")[1]}"
            else
                firebaseUser!!.phoneNumber

            phoneNumField?.setText(number)
            phoneNumField!!.isEnabled = false
            phoneNumField!!.alpha = 0.5f
        } else {
            phoneNumField!!.isEnabled = true
            phoneNumField!!.alpha = 1f
        }

        profile_pic?.setOnClickListener {
            Dexter.withActivity(this)
                    .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            if (report.areAllPermissionsGranted()) {
                                showImagePickerOptions()
                            }
                            if (report.isAnyPermissionPermanentlyDenied) {
                                showSettingsDialog()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {}
                    }).check()
        }

        button?.setOnClickListener {
            if (TextUtils.isEmpty(getFullName()))
                Toast.makeText(this, "Enter your full name", Toast.LENGTH_SHORT).show()
            else if (TextUtils.isEmpty(status)) Toast.makeText(this, "Enter your username", Toast.LENGTH_SHORT).show()
            else if (TextUtils.isEmpty(getPhone())) Toast.makeText(this, "Enter your phone number", Toast.LENGTH_SHORT).show()
            else if (getPhone().length < 11) Toast.makeText(this, "phone number must be 11 digits long", Toast.LENGTH_SHORT).show()
            else if (uri == null && providerUri == null) Toast.makeText(this, "Select a profile photo", Toast.LENGTH_SHORT).show()
            else {
                check()
            }
        }
    }

    /* @OnClick(R.id.proceed_button)
     fun setProvider() {
         if (TextUtils.isEmpty(getFullName()))
             Toast.makeText(this, "Enter your full name", Toast.LENGTH_SHORT).show()
         else if (TextUtils.isEmpty(status)) Toast.makeText(this, "Enter your username", Toast.LENGTH_SHORT).show()
         else if (TextUtils.isEmpty(getPhone())) Toast.makeText(this, "Enter your phone number", Toast.LENGTH_SHORT).show()
         else if (getPhone().length < 11) Toast.makeText(this, "phone number must be 11 digits long", Toast.LENGTH_SHORT).show()
         else if (uri == null && providerUri == null) Toast.makeText(this, "Select a profile photo", Toast.LENGTH_SHORT).show()
         else {
             check()
         }
     }*/

    private fun setUser(userBean: Users) {
        firebaseDatabase!!.child(firebaseUser!!.uid).setValue(userBean).addOnCompleteListener { task2: Task<Void?>? ->
            val token_id = FirebaseInstanceId.getInstance().token
            val addValue: MutableMap<String, Any> = HashMap()
            addValue["device_token"] = token_id!!
            addValue["online"] = "true"
            firebaseDatabase!!.child(firebaseUser!!.uid).updateChildren(addValue, DatabaseReference.CompletionListener { error: DatabaseError?, ref: DatabaseReference? ->
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                SharedPref.getInstance(this).addUser(userBean)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            })
        }
    }

    override fun setLayoutView(): Int {
        return R.layout.activity_create_profile
    }

    /*@OnClick(R.id.profile_pic)
    fun onProfileImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions()
                        }
                        if (report.isAnyPermissionPermanentlyDenied) {
                            showSettingsDialog()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {}
                }).check()
    }*/

    private fun showImagePickerOptions() {
        /*ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });*/
        CropImage.activity()
                .setAspectRatio(1, 1)
                .setAutoZoomEnabled(true)
                .setMinCropWindowSize(500, 500)
                .start(this)
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_permission_title))
                .setMessage(getString(R.string.dialog_permission_message))
                .setPositiveButton(getString(R.string.go_to_settings), dialogClickListener)
                .setNegativeButton(getString(android.R.string.cancel), dialogClickListener)
                .show()
    }

    var dialogClickListener = DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                dialog.cancel()
                openSettings()
            }
            DialogInterface.BUTTON_NEGATIVE -> dialog.cancel()
        }
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", this.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    private fun loadProfilePic(bitmap: Uri?) {
        Glide.with(this).load(bitmap).placeholder(R.drawable.profile_placeholder).into(profileImage!!)
    }

    private fun launchCameraIntent() {
        val intent = Intent(this, ImagePickerActivity::class.java)
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE)
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    private fun launchGalleryIntent() {
        val intent = Intent(this, ImagePickerActivity::class.java)
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE)
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {

                //uri = data.getParcelableExtra("path");

                try {
                    uri = CropImage.getActivityResult(data).uri
                    loadProfilePic(uri)
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }
        }
    }

    val status: String get() = username!!.text.toString()

    fun getFullName(): String {
        return fullName!!.text.toString()
    }

    fun getPhone(): String {
        return phoneNumField!!.text.toString()
    }

    companion object {
        const val PROVIDER = "provider"
        private val TAG = CreateProfileActivity::class.java.simpleName
        const val REQUEST_IMAGE = 100
    }

    private fun check() {

        startUpload()
        val query: Query = firebaseDatabase?.orderByChild("bio")?.equalTo(status)!!

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(this@CreateProfileActivity, "username is chosen", Toast.LENGTH_SHORT).show()
                    stopUpload()
                } else {
                    checkPhone()
                }

            }

        })
    }

    private fun stopUpload() {
        progressBar!!.visibility = View.GONE
        proceedText!!.visibility = View.VISIBLE
    }

    private fun startUpload() {
        progressBar!!.visibility = View.VISIBLE
        proceedText!!.visibility = View.GONE
    }

    private fun checkPhone() {
        val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
        val query: Query = firebaseDatabase?.orderByChild("phone")?.equalTo(getPhone())!!

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(this@CreateProfileActivity, "phone number is chosen", Toast.LENGTH_SHORT)
                            .show()
                    stopUpload()
                } else {
                    upload()
                }

            }

        })
    }

    private fun upload() {
        if (uri != null) {
            val userBean = Users()
            storageReference = FirebaseStorage.getInstance().reference.child("profile_images/" + uri!!.lastPathSegment)
            val uploadTask = storageReference!!.putFile(uri!!)
            uploadTask.addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->
                if (task.isSuccessful) {
                    task.result!!.storage.downloadUrl.addOnCompleteListener { task1: Task<Uri?> ->
                        if (task1.isSuccessful) {
                            val downloadUrl = task1.result
                            userBean.photoUrl = downloadUrl.toString()
                            userBean.name = getFullName()
                            userBean.uid = firebaseUser!!.uid
                            userBean.bio = status
                            userBean.followers = "0"
                            userBean.following = "0"
                            userBean.posts = "0"
                            userBean.phone = getPhone()
                            setUser(userBean)
                        } else {
                            println("failed")
                        }
                    }
                }
            }
        } else {
            progressBar!!.visibility = View.VISIBLE
            proceedText!!.visibility = View.GONE
            val userBean = Users()
            userBean.photoUrl = providerUri.toString()
            userBean.name = getFullName()
            userBean.uid = firebaseUser!!.uid
            userBean.bio = status
            userBean.followers = "0"
            userBean.following = "0"
            userBean.posts = "0"
            userBean.phone = getPhone()
            setUser(userBean)
        }
    }
}