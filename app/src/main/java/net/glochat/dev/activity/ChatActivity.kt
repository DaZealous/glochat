package net.glochat.dev.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import de.hdodenhof.circleimageview.CircleImageView
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText
import net.glochat.dev.ChangeChatBack
import net.glochat.dev.adapter.ChatAdapter
import net.glochat.dev.view.ChatsView
import org.apache.commons.io.FileUtils
import net.glochat.dev.R
import net.glochat.dev.adapter.MyAdapter
import net.glochat.dev.models.ChatDao
import net.glochat.dev.models.ContactModel
import net.glochat.dev.utils.ChatDatabase
import net.glochat.dev.utils.FileUtility
import net.glochat.dev.utils.GetTimeAgo
import net.glochat.dev.utils.KeyboardUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

private lateinit var recyclerView: RecyclerView
private lateinit var btnSend: ImageButton
private lateinit var imgPickImage: ImageButton
private lateinit var imgAttach: ImageButton
private lateinit var backBtn: ImageButton
private lateinit var audioPick: ImageButton
private lateinit var btnCameraPick: ImageButton
private lateinit var btnImgCameraPick: ImageButton
private lateinit var btnVideoPick: ImageButton
private lateinit var btnDocumentPick: ImageButton
private lateinit var btnContactPick: ImageButton
private lateinit var popUpMenuBtn: ImageButton
private lateinit var chooseEmoji: ImageButton
private lateinit var editText: EmojiconEditText
private lateinit var adapter: ChatAdapter
private lateinit var list: ArrayList<ChatDao>
private lateinit var userID: String
private lateinit var otherUser: String
private lateinit var username: String
private lateinit var otherUsername: String
private lateinit var imgUrl: String
private lateinit var swipeRefreshLayout: SwipeRefreshLayout
private lateinit var parentLayout: RelativeLayout
private lateinit var imageView: ImageView
private lateinit var chatBackImg: ImageView
private var lastTime: Long = 0L
private const val TOTAL_ITEMS_TO_LOAD = 15
private const val DC_RESULT = 1
private const val AUDIO_RESULT = 2
private const val VIDEO_RESULT = 3
private const val DOC_RESULT = 4
private const val CONTACT_RESULT = 5
private lateinit var rootRef: DatabaseReference
private var mCurrentPage = 1
private var itemPos = 0
private var pos = 0
private var mLastKey = ""
private var mPrevKey = ""
private lateinit var mImageStorage: StorageReference
private lateinit var mLinearLayout: LinearLayoutManager
private lateinit var textUsername: TextView
private lateinit var textTimeStamp: TextView
private lateinit var emojiIcons: EmojIconActions

//private lateinit var textUserBlocked: TextView
private lateinit var chooseFileLayout: LinearLayout
private var isChooseFileVisible = false
private lateinit var slideLeft: Animation
private lateinit var slideRight: Animation
private lateinit var userImage: CircleImageView
private var isChat = false
private var isRecording = false
private lateinit var bar: ProgressBar
private lateinit var mediaRecorder: MediaRecorder
private lateinit var outputFile: String
private lateinit var online: String
private lateinit var rootFile: File
private lateinit var dir: String
private lateinit var fileName: String
private var isBlock = false
private var isUserBlock: Boolean = false
private val REQUEST_CODE = 24
private lateinit var usernameLayout: LinearLayout

class ChatActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, ChatsView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_chat)

            askForMultiplePermissions()
            username = FirebaseAuth.getInstance().currentUser?.displayName!!
            userID = FirebaseAuth.getInstance().currentUser?.uid!!

            otherUser = intent.getStringExtra("userID")!!
            otherUsername = intent.getStringExtra("username")!!
            imgUrl = intent.getStringExtra("img_url")!!

            recyclerView = findViewById(R.id.activity_chat_recycler_view)
            btnSend = findViewById(R.id.activity_chat_btn_send)
            imgPickImage = findViewById(R.id.activity_chat_image_file_pick)
            audioPick = findViewById(R.id.activity_chat_audio_file_pick)
            imgAttach = findViewById(R.id.activity_chat_img_attach)
            editText = findViewById(R.id.activity_chat_edit_text)
            swipeRefreshLayout = findViewById(R.id.activity_chat_swipe_refresh)
            textUsername = findViewById(R.id.activity_chat_text_username)
            textTimeStamp = findViewById(R.id.activity_chat_text_time)
            chooseEmoji = findViewById(R.id.activity_chat_choose_emoji)
            chooseFileLayout =
                    findViewById(R.id.activity_chat_card_choose_file_layout)
            userImage =
                    findViewById(R.id.activity_chat_user_img)
            bar = findViewById(R.id.activity_chat_progress_file_upload)
            backBtn = findViewById(R.id.activity_chat_back_btn)
            btnCameraPick = findViewById(R.id.activity_chat_image_camera_pick)
            btnImgCameraPick = findViewById(R.id.activity_chat_img_camera)
            btnVideoPick = findViewById(R.id.activity_chat_video_file_pick)
            btnDocumentPick = findViewById(R.id.activity_chat_document_file_pick)
            btnContactPick = findViewById(R.id.activity_chat_contact_file_pick)
            chatBackImg = findViewById(R.id.activity_chat_img_back)
            popUpMenuBtn = findViewById(R.id.activity_chat_menu_options)
            //   textUserBlocked = findViewById(R.id.activity_chat_text_user_blocked)
            usernameLayout = findViewById(R.id.activity_chat_username_layout)
            parentLayout = findViewById(R.id.activity_chat_parent_layout)

           emojiIcons = EmojIconActions(this, parentLayout, editText, chooseEmoji)
            emojiIcons.ShowEmojIcon()
            emojiIcons.setIconsIds(R.drawable.ic_baseline_keyboard_grey_24, R.drawable.ic_emoji_mood_grey_24)

            /*chooseEmoji.setOnClickListener {
                *//*val emojiPopup = EmojiPopup.Builder.fromRootView(parentLayout).build(editText)
                if (emojiPopup.isShowing) {
                    chooseEmoji.setImageResource(R.drawable.ic_emoji_mood_grey_24)
                    emojiPopup.dismiss()
                } else {
                    chooseEmoji.setImageResource(R.drawable.ic_baseline_keyboard_grey_24)
                    emojiPopup.toggle();
                }*//*
            }*/

            //Place a call

            /* activity_chat_menu_call.setOnClickListener {
                 callButtonClicked()
             }

             //place video call
             activity_chat_menu_video.setOnClickListener {
                 callVideoButton()
             }*/

            //rootFile = Environment.getExternalStorageDirectory()


            /*   if (new ChatDatabase(this).findChatImage("1") == null)
            chatBackImg.setImageResource(R.drawable.chat_back_img);
        else
            chatBackImg.setImageBitmap(BitmapFactory.decodeByteArray(new ChatDatabase(this).findChatImage("1"),
                    0, new ChatDatabase(this).findChatImage("1").length));*/

            // rootFile = Environment.getExternalStorageDirectory();
            rootFile =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            Glide.with(this).load(imgUrl).placeholder(R.drawable.profile_placeholder)
                    .into(userImage)
            textUsername.text = otherUsername
            textTimeStamp.text = ""
            imageView = ImageView(this)
            mLinearLayout = LinearLayoutManager(this)
            swipeRefreshLayout.setOnRefreshListener(this)
            list = ArrayList()
            adapter = ChatAdapter(this, list, this)
            recyclerView.layoutManager = mLinearLayout
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter
            rootRef = FirebaseDatabase.getInstance().reference
            mImageStorage = FirebaseStorage.getInstance().reference
            slideLeft =
                    AnimationUtils.loadAnimation(this, R.anim.card_slide_left2)
            slideRight =
                    AnimationUtils.loadAnimation(this, R.anim.slide_right2)

            //outputFile = Environment.getExternalStorageDirectory().absolutePath

            outputFile = rootFile.absolutePath

            online = ""
            rootRef.child("Chat").child(userID).child(otherUser).child("seen").setValue(true)
            rootRef.child("Chat").child(userID).child(otherUser).child("type").setValue("user")
            rootRef.child("Chat").child(otherUser).child(userID).child("type").setValue("user")

            rootRef.child("Users").child(otherUser)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.child("online").value != null) {
                                online = dataSnapshot.child("online").value.toString()
                                if (!TextUtils.isEmpty(online)) {
                                    if (online == "true") {
                                        textTimeStamp.text = "Online"
                                        textTimeStamp.visibility = View.VISIBLE
                                    } else {
                                        if (dataSnapshot.hasChild("time_stamp")) {
                                            lastTime = dataSnapshot.child("time_stamp").getValue(Long::class.java)!!
                                            val lastSeenTime = "active : " + GetTimeAgo.getTimeAgo(
                                                    lastTime,
                                                    applicationContext
                                            )
                                            textTimeStamp.text = lastSeenTime
                                            textTimeStamp.visibility = View.VISIBLE
                                        }
                                    }
                                } else {
                                    textTimeStamp.text = ""
                                    textTimeStamp.visibility = View.GONE
                                }
                            } else {
                                textTimeStamp.text = ""
                                textTimeStamp.visibility = View.GONE
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })

            slideRight.setAnimationListener(object :
                    Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    imgAttach.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            slideLeft.setAnimationListener(object :
                    Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    imgAttach.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            loadMessages()

            rootRef.child("Chat").child(userID).child(otherUser)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val typing =
                                    dataSnapshot.child("typing").getValue(
                                            String::class.java
                                    )
                            isBlock = if (dataSnapshot.child("is_block")
                                            .exists()
                            ) dataSnapshot.child("is_block").getValue(
                                    Boolean::class.java
                            )!! else false
                            if (isBlock) {
                                editText.isEnabled = false
                                editText.alpha = 0.5f
                                imgAttach.isEnabled = false
                                btnSend.isEnabled = false
                                btnSend.alpha = 0.5f
                                /* textUserBlocked.text = "You have been blocked by this user"
                                 textUserBlocked.visibility = View.VISIBLE*/
                            } else {
                                editText.isEnabled = true
                                editText.alpha = 1f
                                imgAttach.isEnabled = true
                                btnSend.isEnabled = true
                                btnSend.alpha = 1f
                                // textUserBlocked.visibility = View.INVISIBLE
                            }
                            if (!TextUtils.isEmpty(typing)) if (typing == "true") {
                                textTimeStamp.text = "typing..."
                            } else {
                                if (!TextUtils.isEmpty(online))
                                    textTimeStamp.text =
                                            (if (online == "true") "Online" else "active : " + GetTimeAgo.getTimeAgo(
                                                    lastTime,
                                                    applicationContext
                                            )) else textTimeStamp.text = ""
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })


            rootRef.child("Chat").child(otherUser).child(userID)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            isUserBlock = if (dataSnapshot.child("is_block")
                                            .exists()
                            ) dataSnapshot.child("is_block").getValue(
                                    Boolean::class.java
                            )!! else false

                            /*if (isUserBlock) {
                            editText.setEnabled(false);
                            editText.setAlpha(0.5f);
                            imgAttach.setEnabled(false);
                            btnSend.setEnabled(false);
                            btnSend.setAlpha(0.5f);
                            textUserBlocked.setText("You block this user");
                            textUserBlocked.setVisibility(View.VISIBLE);
                        } else {
                            editText.setEnabled(true);
                            editText.setAlpha(1f);
                            imgAttach.setEnabled(true);
                            btnSend.setEnabled(true);
                            btnSend.setAlpha(1f);
                            textUserBlocked.setVisibility(View.INVISIBLE);
                        }*/
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
            rootRef.child("Chat").child(userID).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.hasChild(otherUser)) {
                        val chatAddMap: MutableMap<String, Any> = HashMap()
                        chatAddMap["seen"] = false
                        chatAddMap["time_stamp"] = ServerValue.TIMESTAMP
                        val chatUserMap: MutableMap<String, Any> = HashMap()
                        chatUserMap["Chat/$userID/$otherUser"] = chatAddMap
                        chatUserMap["Chat/$otherUser/$userID"] = chatAddMap
                        rootRef.updateChildren(chatUserMap,
                                DatabaseReference.CompletionListener { databaseError: DatabaseError?, databaseReference: DatabaseReference? -> }
                        )
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }

        try {
            /*KeyboardUtils.addKeyboardToggleListener(this) {
                if (it) {
                    imgAttach.startAnimation(slideRight)
                    rootRef.child("Chat").child(otherUser).child(userID).child("typing")
                            .setValue("true")
                } else {
                    imgAttach.startAnimation(slideLeft)
                    rootRef.child("Chat").child(otherUser).child(userID).child("typing")
                            .setValue("false")
                }
            }*/

            emojiIcons.setKeyboardListener(object : EmojIconActions.KeyboardListener{
                override fun onKeyboardClose() {
                    imgAttach.startAnimation(slideLeft)
                    rootRef.child("Chat").child(otherUser).child(userID).child("typing")
                            .setValue("false")
                }

                override fun onKeyboardOpen() {
                    imgAttach.startAnimation(slideRight)
                    rootRef.child("Chat").child(otherUser).child(userID).child("typing")
                            .setValue("true")
                }

            })
            btnSend.setOnClickListener { view: View? ->
                if (isChat) {
                    if (!TextUtils.isEmpty(editText.text.toString())) {
                        sendMessage(editText.text.toString())
                        //Toast.makeText(this, editText.text.toString(), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (isRecording) {
                        try {
                            isRecording = false
                            mediaRecorder.stop()
                            mediaRecorder.release()
                            btnSend.setImageResource(R.drawable.ic_keyboard_voice_white_24dp)
                            sendAudio(Uri.fromFile(File(dir)))
                        } catch (e: java.lang.Exception) {
                            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        startRecording()
                    }
                }
            }
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                        charSequence: CharSequence,
                        i: Int,
                        i1: Int,
                        i2: Int
                ) {
                }

                override fun onTextChanged(
                        charSequence: CharSequence,
                        i: Int,
                        i1: Int,
                        i2: Int
                ) {
                    if (TextUtils.isEmpty(editText.text.toString())) {
                        isChat = false
                        btnSend.setImageResource(R.drawable.ic_keyboard_voice_white_24dp)
                    } else {
                        isChat = true
                        btnSend.setImageResource(R.drawable.ic_send_white_24dp)
                    }
                }

                override fun afterTextChanged(editable: Editable) {}
            })

            imgAttach.setOnClickListener { view: View? ->
                if (isChooseFileVisible) {
                    chooseFileLayout.visibility = View.GONE
                    isChooseFileVisible = false
                } else {
                    chooseFileLayout.visibility = View.VISIBLE
                    isChooseFileVisible = true
                }
            }
            backBtn.setOnClickListener { view: View? -> onBackPressed() }

            //open user profile
            /* usernameLayout.setOnClickListener {
                 startActivity(
                     Intent(this, ProfileStyle2Activity::class.java).putExtra(
                         "id",
                         otherUser
                     )
                 )
             }*/

            imgPickImage.setOnClickListener { view: View? ->
                chooseFileLayout.visibility = View.GONE
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(
                        Intent.createChooser(intent, "SELECT IMAGE"), DC_RESULT
                )
            }
            audioPick.setOnClickListener { view: View? ->
                chooseFileLayout.visibility = View.GONE
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "audio/*"
                startActivityForResult(intent, AUDIO_RESULT)
            }
            btnCameraPick.setOnClickListener { view: View? ->
                chooseFileLayout.visibility = View.GONE
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, DC_RESULT)
            }

            btnImgCameraPick.setOnClickListener { view: View? ->
                chooseFileLayout.visibility = View.GONE
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, DC_RESULT)
            }

            btnVideoPick.setOnClickListener { view: View? ->
                chooseFileLayout.visibility = View.GONE
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "video/*"
                startActivityForResult(intent, VIDEO_RESULT)
            }
            btnDocumentPick.setOnClickListener { view: View? ->
                chooseFileLayout.visibility = View.GONE
                //val intent = Intent()
                //intent.action = Intent.ACTION_OPEN_DOCUMENT
                //intent.addCategory(Intent.CATEGORY_OPENABLE)
                //intent.type = "*/*"
                /*val mimes = arrayOf(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "applicaton/html",
                    "application/txt"
                )*/
                /*intent.putExtra(Intent.EXTRA_MIME_TYPES, mimes)
                startActivityForResult(intent, DOC_RESULT)*/

                // Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                //intent.addCategory(Intent.CATEGORY_OPENABLE);
                //intent.setType("*/*");
                //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //String[] mimes = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "applicaton/html", "application/txt"};
                //intent.putExtra(Intent.EXTRA_MIME_TYPES, mimes);
                //startActivityForResult(intent, DOC_RESULT);
                val intent = Intent(this@ChatActivity, FilePickerActivity::class.java)
                intent.putExtra(
                        FilePickerActivity.CONFIGS, Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(false)
                        .setShowAudios(false)
                        .setShowFiles(true)
                        .setShowVideos(false)
                        .enableImageCapture(false)
                        .setMaxSelection(1)
                        .setSkipZeroSizeFiles(true)
                        .build()
                )
                startActivityForResult(intent, DOC_RESULT)
            }
            btnContactPick.setOnClickListener { view: View? ->
                chooseFileLayout.visibility = View.GONE
                val contactsList = getContacts()
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.contacts_list_view)
                dialog.setCancelable(true)
                dialog.setCanceledOnTouchOutside(true)
                dialog.setTitle("Select Contact")
                val listView =
                        dialog.findViewById<ListView>(R.id.contacts_list_view)
                val adapter = MyAdapter(this, contactsList)
                listView.adapter = adapter
                listView.onItemClickListener =
                        OnItemClickListener { _: AdapterView<*>?, _: View?, i: Int, l: Long ->
                            val model = contactsList!![i]
                            dialog.dismiss()
                            sendContact(model)
                        }
                dialog.show()
            }

            popUpMenuBtn.setOnClickListener { view: View? ->
                val options = arrayOf<CharSequence>(
                        if (isUserBlock) "Unblock User" else "Block User",
                        "Clear Chats",
                        "Delete Conversations",
                        "Change Chat Wallpaper"
                )
                val builder =
                        AlertDialog.Builder(this)
                builder.setTitle("Select Options")
                builder.setItems(
                        options
                ) { dialogInterface: DialogInterface?, i: Int ->
                    when (i) {
                        0 -> blockUser()
                        1 -> clearChats()
                        2 -> deleteConv()
                        3 -> {
                            startActivity(Intent(this, ChangeChatBack::class.java))
                        }
                        else -> {
                        }
                    }
                }
                builder.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteConv() {
        val builder =
                AlertDialog.Builder(this)
                        .setTitle("Delete Conversations")
                        .setMessage("All your conversations including chat history will be deleted")
                        .setNegativeButton(
                                "Cancel"
                        ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
                        .setPositiveButton(
                                "OK"
                        ) { dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.dismiss()
                            val dialog = ProgressDialog(this)
                            dialog.setTitle("Deleting Conversations...")
                            dialog.setMessage("please wait")
                            dialog.setCancelable(false)
                            dialog.show()
                            rootRef.child("messages").child(userID).child(otherUser).removeValue()
                                    .addOnCompleteListener { task: Task<Void?> ->
                                        if (task.isSuccessful) {
                                            finish()
                                            dialog.dismiss()
                                            Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT)
                                                    .show()
                                        }
                                    }
                        }
        builder.create().show()
    }

    private fun clearChats() {
        val builder =
                AlertDialog.Builder(this)
                        .setTitle("Clear Chats")
                        .setMessage("All your conversations will be cleared")
                        .setNegativeButton(
                                "Cancel"
                        ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
                        .setPositiveButton(
                                "OK"
                        ) { dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.dismiss()
                            val dialog = ProgressDialog(this)
                            dialog.setTitle("Clearing Chats...")
                            dialog.setMessage("please wait")
                            dialog.setCancelable(false)
                            dialog.show()
                            rootRef.child("messages").child(userID).child(otherUser).removeValue()
                                    .addOnCompleteListener { task: Task<Void?> ->
                                        if (task.isSuccessful) {
                                            loadMessages()
                                            dialog.dismiss()
                                            Toast.makeText(
                                                    this,
                                                    "Chats cleared successfully",
                                                    Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                        }
        builder.create().show()
    }

    private fun blockUser() {
        val builder =
                AlertDialog.Builder(this)
                        .setTitle(if (isUserBlock) "Unblock User?" else "Block User?")
                        .setMessage("Sure to continue?")
                        .setNegativeButton(
                                "Cancel"
                        ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
                        .setPositiveButton(
                                "Sure"
                        ) { dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.dismiss()
                            val dialog = ProgressDialog(this)
                            dialog.setTitle(if (isUserBlock) "Unblocking..." else "Blocking...")
                            dialog.setMessage("please wait")
                            dialog.setCancelable(false)
                            dialog.show()
                            rootRef.child("Chat").child(otherUser).child(userID).child("is_block")
                                    .setValue(!isUserBlock)
                                    .addOnCompleteListener { task: Task<Void?> ->
                                        if (task.isSuccessful) {
                                            dialog.dismiss()
                                            Toast.makeText(
                                                    this,
                                                    if (isUserBlock) "User unblocked successfully" else "User blocked successfully",
                                                    Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                        }
        builder.create().show()
    }

    private fun sendContact(model: ContactModel) {
        val current_user_ref = "messages/$userID/$otherUser"
        val chat_user_ref = "messages/$otherUser/$userID"
        val user_message_push = rootRef.child("messages")
                .child(userID).child(otherUser).push()
        val push_id = user_message_push.key
        val messageMap: MutableMap<String, Any> = HashMap()
        messageMap["msg_body"] = model.mobileNumber
        messageMap["key"] = push_id!!
        messageMap["seen"] = false
        messageMap["isRead"] = false
        messageMap["msg_type"] = "contact"
        messageMap["time_stamp"] = ServerValue.TIMESTAMP
        messageMap["from"] = userID
        messageMap["to"] = otherUser
        messageMap["msg_name"] = model.name
        messageMap["fromUsername"] = username
        messageMap["toUsername"] = otherUsername
        val messageUserMap: MutableMap<String, Any> = HashMap()
        messageUserMap["$current_user_ref/$push_id"] = messageMap
        messageUserMap["$chat_user_ref/$push_id"] = messageMap
        editText.setText("")
        rootRef.child("Chat").child(userID).child(otherUser).child("seen").setValue(true)
        rootRef.child("Chat").child(userID).child(otherUser).child("time_stamp")
                .setValue(ServerValue.TIMESTAMP)
        rootRef.child("Chat").child(otherUser).child(userID).child("seen").setValue(false)
        rootRef.child("Chat").child(otherUser).child(userID).child("time_stamp")
                .setValue(ServerValue.TIMESTAMP)
        rootRef.updateChildren(messageUserMap,
                DatabaseReference.CompletionListener { databaseError: DatabaseError?, _: DatabaseReference? ->
                    if (databaseError == null) {
                        rootRef.child("messages").child(userID).child(otherUser).child(push_id)
                                .child("seen").setValue(true)
                        rootRef.child("messages").child(userID).child(otherUser).child(push_id)
                                .child("isRead").setValue(true)
                    }
                }
        )
    }

    private fun getContacts(): List<ContactModel>? {
        val list: MutableList<ContactModel> = ArrayList<ContactModel>()
        val phones = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        )
        while (phones!!.moveToNext()) {
            val name =
                    phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber =
                    phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val model = ContactModel(name, phoneNumber)
            list.add(model)
        }
        phones.close()
        return list
    }

    private fun sendAudio(uri: Uri) {
        bar.visibility = View.VISIBLE
        Toast.makeText(this, "sending audio...", Toast.LENGTH_SHORT).show()
        val current_user_ref = "messages/$userID/$otherUser"
        val chat_user_ref = "messages/$otherUser/$userID"
        val user_message_push = rootRef.child("messages")
                .child(userID).child(otherUser).push()
        val push_id = user_message_push.key
        val thumbRef = mImageStorage.child("message_audio").child(fileName)
        val uploadTask = thumbRef.putFile(uri)
        uploadTask.addOnCompleteListener { task: Task<UploadTask.TaskSnapshot?> ->
            if (task.isSuccessful) {
                thumbRef.downloadUrl
                        .addOnSuccessListener { uri1: Uri ->
                            val thumb_url = uri1.toString()
                            val messageMap: MutableMap<String, Any> = HashMap()
                            messageMap["msg_body"] = thumb_url
                            messageMap["key"] = push_id!!
                            messageMap["seen"] = false
                            messageMap["msg_type"] = "audio"
                            messageMap["time_stamp"] = ServerValue.TIMESTAMP
                            messageMap["from"] = userID
                            messageMap["to"] = otherUser
                            messageMap["msg_name"] = fileName
                            messageMap["fromUsername"] = username
                            messageMap["toUsername"] = otherUsername
                            val messageUserMap: MutableMap<String, Any> = HashMap()
                            messageUserMap["$current_user_ref/$push_id"] = messageMap
                            messageUserMap["$chat_user_ref/$push_id"] = messageMap
                            editText.setText("")
                            rootRef.child("Chat").child(userID).child(otherUser).child("seen")
                                    .setValue(true)
                            rootRef.child("Chat").child(userID).child(otherUser).child("time_stamp")
                                    .setValue(ServerValue.TIMESTAMP)
                            rootRef.child("Chat").child(otherUser).child(userID).child("seen")
                                    .setValue(false)
                            rootRef.child("Chat").child(otherUser).child(userID).child("time_stamp")
                                    .setValue(ServerValue.TIMESTAMP)
                            rootRef.updateChildren(messageUserMap,
                                    DatabaseReference.CompletionListener { databaseError: DatabaseError?, _: DatabaseReference? ->
                                        if (databaseError == null) {
                                            bar.visibility = View.INVISIBLE
                                            Toast.makeText(
                                                    this@ChatActivity,
                                                    "uploaded successfully",
                                                    Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            )
                        }
            } else {
                bar.visibility = View.INVISIBLE
                Toast.makeText(
                        this@ChatActivity,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun sendVideo(uri: Uri) {
        bar.visibility = View.VISIBLE
        Toast.makeText(this, "sending video...", Toast.LENGTH_SHORT).show()
        val current_user_ref = "messages/$userID/$otherUser"
        val chat_user_ref = "messages/$otherUser/$userID"
        val user_message_push = rootRef.child("messages")
                .child(userID).child(otherUser).push()
        val push_id = user_message_push.key
        val thumbRef = mImageStorage.child("message_video").child(fileName)
        val uploadTask = thumbRef.putFile(uri)
        uploadTask.addOnCompleteListener { task: Task<UploadTask.TaskSnapshot?> ->
            if (task.isSuccessful) {
                thumbRef.downloadUrl
                        .addOnSuccessListener { uri1: Uri ->
                            val thumb_url = uri1.toString()
                            val messageMap: MutableMap<String, Any> = HashMap()
                            messageMap["msg_body"] = thumb_url
                            messageMap["key"] = push_id!!
                            messageMap["seen"] = false
                            messageMap["msg_type"] = "video"
                            messageMap["time_stamp"] = ServerValue.TIMESTAMP
                            messageMap["from"] = userID
                            messageMap["to"] = otherUser
                            messageMap["msg_name"] = fileName
                            messageMap["fromUsername"] = username
                            messageMap["toUsername"] = otherUsername
                            val messageUserMap: MutableMap<String, Any> = HashMap()
                            messageUserMap["$current_user_ref/$push_id"] = messageMap
                            messageUserMap["$chat_user_ref/$push_id"] = messageMap
                            editText.setText("")
                            rootRef.child("Chat").child(userID).child(otherUser).child("seen")
                                    .setValue(true)
                            rootRef.child("Chat").child(userID).child(otherUser).child("time_stamp")
                                    .setValue(ServerValue.TIMESTAMP)
                            rootRef.child("Chat").child(otherUser).child(userID).child("seen")
                                    .setValue(false)
                            rootRef.child("Chat").child(otherUser).child(userID).child("time_stamp")
                                    .setValue(ServerValue.TIMESTAMP)
                            rootRef.updateChildren(messageUserMap,
                                    DatabaseReference.CompletionListener { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
                                        if (databaseError == null) {
                                            bar.visibility = View.INVISIBLE
                                            Toast.makeText(
                                                    this@ChatActivity,
                                                    "uploaded successfully",
                                                    Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            )
                        }
            } else {
                bar.visibility = View.INVISIBLE
                Toast.makeText(
                        this@ChatActivity,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun startRecording() {
        isRecording = true
        btnSend.setImageResource(R.drawable.ic_pause_white_24dp)
        Toast.makeText(this, "recording started", Toast.LENGTH_SHORT).show()

        val time = System.currentTimeMillis().toString()
        dir = rootFile.absolutePath + "/" + time + ".3gp"
        fileName = "$time.3gp"
        /* val file = File(rootFile.absolutePath + "/")
         if (!file.exists()) file.mkdir()*/
        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        mediaRecorder.setOutputFile(dir)
        mediaRecorder.prepare()
        mediaRecorder.start()

    }

    private fun sendMessage(message: String) {
        if (!TextUtils.isEmpty(message)) {
            val current_user_ref = "messages/$userID/$otherUser"
            val chat_user_ref = "messages/$otherUser/$userID"
            val user_message_push = rootRef.child("messages")
                    .child(userID).child(otherUser).push()
            val push_id = user_message_push.key
            val messageMap: MutableMap<String, Any> = HashMap()
            messageMap["msg_body"] = message
            messageMap["key"] = push_id!!
            messageMap["seen"] = false
            messageMap["isRead"] = false
            messageMap["msg_type"] = "text"
            messageMap["time_stamp"] = ServerValue.TIMESTAMP
            messageMap["from"] = userID
            messageMap["to"] = otherUser
            messageMap["msg_name"] = message
            messageMap["fromUsername"] = username
            messageMap["toUsername"] = otherUsername
            val messageUserMap: MutableMap<String, Any> = HashMap()
            messageUserMap["$current_user_ref/$push_id"] = messageMap
            messageUserMap["$chat_user_ref/$push_id"] = messageMap
            editText.setText("")
            rootRef.child("Chat").child(userID).child(otherUser).child("seen").setValue(true)
            rootRef.child("Chat").child(userID).child(otherUser).child("time_stamp")
                    .setValue(ServerValue.TIMESTAMP)
            rootRef.child("Chat").child(otherUser).child(userID).child("seen").setValue(false)
            rootRef.child("Chat").child(otherUser).child(userID).child("time_stamp")
                    .setValue(ServerValue.TIMESTAMP)
            rootRef.updateChildren(messageUserMap,
                    DatabaseReference.CompletionListener { databaseError: DatabaseError?, _: DatabaseReference? ->
                        if (databaseError == null) {
                            rootRef.child("messages").child(userID).child(otherUser).child(push_id)
                                    .child("seen").setValue(true)
                            rootRef.child("messages").child(userID).child(otherUser).child(push_id)
                                    .child("isRead").setValue(true)
                        }
                    }
            )
        }
    }

    override fun onRefresh() {
        mCurrentPage++
        itemPos = 0
        loadMoreMessages()
    }

    private fun loadMoreMessages() {
        val messageRef = rootRef.child("messages").child(userID).child(otherUser)
        val messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(15)

        messageQuery.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.hasChildren()) {
                    rootRef.child("messages").child(userID).child(otherUser)
                            .child(dataSnapshot.key!!).child("seen").setValue(true)
                    rootRef.child("messages").child(userID).child(otherUser)
                            .child(dataSnapshot.key!!).child("isRead").setValue(true)
                    val message: ChatDao = dataSnapshot.getValue(ChatDao::class.java)!!
                    val messageKey = dataSnapshot.key!!
                    if (mPrevKey != messageKey) {
                        list.add(itemPos++, message)
                    } else {
                        mPrevKey = mLastKey
                    }
                    if (itemPos == 1) {
                        mLastKey = messageKey
                    }
                    adapter.notifyDataSetChanged()
                    swipeRefreshLayout.isRefreshing = false
                    mLinearLayout.scrollToPositionWithOffset(10, 0)
                } else swipeRefreshLayout.isRefreshing = false
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private fun loadMessages() {
        swipeRefreshLayout.isRefreshing = true
        val chatRef = rootRef.child("messages").child(userID).child(otherUser)
        val messageQuery =
                chatRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD)
        list.clear()
        messageQuery.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.hasChildren()) {
                    val message = dataSnapshot.getValue(ChatDao::class.java)
                    if (pos == 0 && message!!.seen) pos = itemPos
                    rootRef.child("messages").child(userID).child(otherUser)
                            .child(dataSnapshot.key!!).child("seen").setValue(true)
                    rootRef.child("messages").child(userID).child(otherUser)
                            .child(dataSnapshot.key!!).child("isRead").setValue(true)
                    itemPos++
                    if (itemPos == 1) {
                        val messageKey = dataSnapshot.key
                        mLastKey = messageKey!!
                        mPrevKey = messageKey
                    }

                    if (!list.contains(message))
                        list.add(message!!)

                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1)
                    swipeRefreshLayout.isRefreshing = false
                } else swipeRefreshLayout.isRefreshing = false
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DC_RESULT && resultCode == Activity.RESULT_OK) {
            try {

                val imageUri: Uri

                if (data?.data == null) {
                    val bitmap = data!!.extras?.get("data") as Bitmap
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                    val thumb_byte = stream.toByteArray()
                    doUpload(thumb_byte)
                } else {
                    imageUri = data.data!!
                    val imgContain = ImageView(this)
                    imgContain.setImageURI(imageUri)
                    val bitmap = (imgContain.drawable as BitmapDrawable).bitmap
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                    val thumb_byte = stream.toByteArray()
                    doUpload(thumb_byte)
                }

            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }

        } else if (requestCode == AUDIO_RESULT && resultCode == Activity.RESULT_OK) {
            val audioUri = data?.data
            if (audioUri != null) {
                dir = audioUri.path!!
                /*val file = File(
                    getFilePath(audioUri, arrayOf(MediaStore.Audio.Media.DATA))!!
                )*/
                val file = File(
                        FileUtility.getPath(this, audioUri)
                )
                fileName = file.name
                if (getFileSize(audioUri, arrayOf(OpenableColumns.SIZE)) > 5
                ) Toast.makeText(
                        this,
                        "you cannot send audio file greater than 5mb",
                        Toast.LENGTH_SHORT
                ).show() else {
                    val dirDest = rootFile.absolutePath + "/"
                    try {
                        saveFile(file, File(dirDest))
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    sendAudio(audioUri)
                    // Toast.makeText(this, fileName, Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == VIDEO_RESULT && resultCode == Activity.RESULT_OK) {
            val videoUri = data?.data!!

            dir = videoUri.path!!

            /* val file = File(
                 getFilePath(videoUri, arrayOf(MediaStore.Video.Media.DATA))!!
             )*/

            val file = File(
                    FileUtility.getPath(this, videoUri)
            )

            fileName = file.name
            if (getFileSize(videoUri, arrayOf(OpenableColumns.SIZE)) > 5
            ) Toast.makeText(
                    this,
                    "you cannot send video file greater than 5mb",
                    Toast.LENGTH_SHORT
            ).show() else {
                val dirDest = rootFile.absolutePath + "/"
                try {
                    saveFile(file, File(dirDest))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                sendVideo(videoUri)
            }

        } else if (requestCode == DOC_RESULT && resultCode == Activity.RESULT_OK) {

            val files: java.util.ArrayList<MediaFile>? = data?.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)

            val docUri = files!![0].uri

            if (docUri != null) {

                //Toast.makeText(this, FileUtility.getPath(this, docUri), Toast.LENGTH_SHORT).show()
                dir = docUri.path!!

                /*val file = File(
                    getFilePath(
                        docUri,
                        arrayOf(MediaStore.Files.FileColumns.DATA)
                    )!!
                )*/

                var file: File? = null

                file = if (Build.VERSION.SDK_INT == 26 || Build.VERSION.SDK_INT == 27)
                    File(getPathForOreo(docUri))
                else
                    File(FileUtility.getPath(this, docUri))

                //  File(getFilePaths(this, docUri)!!)
                fileName = file.name
                if (getFileSize(
                                docUri,
                                arrayOf(OpenableColumns.SIZE)
                        ) > 5
                ) Toast.makeText(
                        this,
                        "you cannot send a file greater than 5mb",
                        Toast.LENGTH_SHORT
                ).show() else {
                    val dirDest = rootFile.absolutePath + "/"
                    try {
                        saveFile(file, File(dirDest))
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    sendFile(docUri)
                }

            }
        }
    }

    private fun sendFile(uri: Uri) {
        bar.visibility = View.VISIBLE
        Toast.makeText(this, "sending file...", Toast.LENGTH_SHORT).show()
        val current_user_ref = "messages/$userID/$otherUser"
        val chat_user_ref = "messages/$otherUser/$userID"
        val user_message_push = rootRef.child("messages").child(userID).child(otherUser).push()
        val push_id = user_message_push.key
        val thumbRef = mImageStorage.child("message_files").child(fileName)
        val uploadTask = thumbRef.putFile(uri)
        uploadTask.addOnCompleteListener { task: Task<UploadTask.TaskSnapshot?> ->
            if (task.isSuccessful) {
                thumbRef.downloadUrl
                        .addOnSuccessListener { uri1: Uri ->
                            val thumb_url = uri1.toString()
                            val messageMap: MutableMap<String, Any> = HashMap()
                            messageMap["msg_body"] = thumb_url
                            messageMap["key"] = push_id!!
                            messageMap["seen"] = false
                            messageMap["msg_type"] = "file"
                            messageMap["time_stamp"] = ServerValue.TIMESTAMP
                            messageMap["from"] = userID
                            messageMap["to"] = otherUser
                            messageMap["msg_name"] = fileName
                            messageMap["fromUsername"] = username
                            messageMap["toUsername"] = otherUsername
                            val messageUserMap: MutableMap<String, Any> = HashMap()
                            messageUserMap["$current_user_ref/$push_id"] = messageMap
                            messageUserMap["$chat_user_ref/$push_id"] = messageMap
                            editText.setText("")
                            rootRef.child("Chat").child(userID).child(otherUser).child("seen")
                                    .setValue(true)
                            rootRef.child("Chat").child(userID).child(otherUser).child("time_stamp")
                                    .setValue(ServerValue.TIMESTAMP)
                            rootRef.child("Chat").child(otherUser).child(userID).child("seen")
                                    .setValue(false)
                            rootRef.child("Chat").child(otherUser).child(userID).child("time_stamp")
                                    .setValue(ServerValue.TIMESTAMP)
                            rootRef.updateChildren(messageUserMap,
                                    DatabaseReference.CompletionListener { databaseError: DatabaseError?, _: DatabaseReference? ->
                                        if (databaseError == null) {
                                            bar.visibility = View.INVISIBLE
                                            Toast.makeText(
                                                    this@ChatActivity,
                                                    "uploaded successfully",
                                                    Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            )
                        }
            } else {
                bar.visibility = View.INVISIBLE
                Toast.makeText(
                        this@ChatActivity,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun saveFile(src: File, dst: File) {
        FileUtils.copyFileToDirectory(src, dst)
    }

    private fun getPathForOreo(uri: Uri): String {
        val file = File(uri.path) //create path from uri
        val split = file.path.split(":".toRegex()).toTypedArray() //split the path.
        return split[1] //assign it to a string(your choice).
    }

    private fun getFilePath(
            uri: Uri,
            filePathColumn: Array<String>
    ): String? {
        var picturePath: String = ""
        try {
            val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
            val columnIndex = cursor!!.getColumnIndex(filePathColumn[0])
            cursor.moveToFirst()
            picturePath = cursor.getString(columnIndex)!!
            cursor.close()
        } catch (e: Exception) {
        }
        return picturePath
    }

    private fun getFileSize(
            uri: Uri,
            filePathColumn: Array<String>
    ): Long {
        val cursor =
                contentResolver.query(uri, filePathColumn, null, null, null)
        cursor!!.moveToFirst()
        val size = cursor.getColumnIndex(filePathColumn[0])
        val bytes = cursor.getLong(size)
        cursor.close()
        val kilo = bytes / 1024
        return kilo / 1024
    }

    private fun doUpload(thumb_byte: ByteArray) {
        bar.visibility = View.VISIBLE
        Toast.makeText(this, "uploading....", Toast.LENGTH_LONG).show()
        val current_user_ref = "messages/$userID/$otherUser"
        val chat_user_ref = "messages/$otherUser/$userID"
        val user_message_push = rootRef.child("messages")
                .child(userID).child(otherUser).push()
        val push_id = user_message_push.key
        val thumbRef =
                mImageStorage.child("message_images").child("$push_id.jpg")
        val uploadTask = thumbRef.putBytes(thumb_byte)
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                thumbRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            val thumb_url = uri.toString()
                            val messageMap: MutableMap<String, Any> = HashMap()
                            messageMap["msg_body"] = thumb_url
                            messageMap["key"] = push_id!!
                            messageMap["seen"] = false
                            messageMap["msg_type"] = "image"
                            messageMap["time_stamp"] = ServerValue.TIMESTAMP
                            messageMap["from"] = userID
                            messageMap["to"] = otherUser
                            messageMap["msg_name"] = "$push_id.jpg"
                            messageMap["fromUsername"] = username
                            messageMap["toUsername"] = otherUsername
                            val messageUserMap: MutableMap<String, Any> = HashMap()
                            messageUserMap["$current_user_ref/$push_id"] = messageMap
                            messageUserMap["$chat_user_ref/$push_id"] = messageMap
                            editText.setText("")
                            rootRef.child("Chat").child(userID).child(otherUser).child("seen")
                                    .setValue(true)
                            rootRef.child("Chat").child(userID).child(otherUser)
                                    .child("time_stamp").setValue(ServerValue.TIMESTAMP)
                            rootRef.child("Chat").child(otherUser).child(userID).child("seen")
                                    .setValue(false)
                            rootRef.child("Chat").child(otherUser).child(userID)
                                    .child("time_stamp").setValue(ServerValue.TIMESTAMP)
                            rootRef.updateChildren(messageUserMap,
                                    DatabaseReference.CompletionListener { databaseError: DatabaseError?, _: DatabaseReference? ->
                                        if (databaseError == null) {
                                            bar.visibility = View.INVISIBLE
                                            Toast.makeText(
                                                    this@ChatActivity,
                                                    "uploaded successfully",
                                                    Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            )
                        }
            } else {
                bar.visibility = View.INVISIBLE
                Toast.makeText(
                        this@ChatActivity,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun getUserPhoto(): String? {
        return imgUrl
    }

    override fun getUsernam(): String? {
        return otherUsername
    }

    override fun startProgress(view: Int) {
        bar.visibility = view
    }

    override fun reload() {
        loadMessages()
    }

    override fun shareMessage(dao: ChatDao?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
                Intent.EXTRA_TEXT,
                dao?.msg_body
        )
        startActivityForResult(Intent.createChooser(intent, "Share Via"), 0)
    }

    override fun onPause() {
        super.onPause()
        rootRef.child("Chat").child(otherUser).child(userID).child("typing").setValue("false")
    }

    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
                this,
                permission
        ) != PackageManager.PERMISSION_GRANTED
    }

    fun askForMultiplePermissions() {
        val writeExternalStoragePermission =
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        val readExternalStoragePermission =
                Manifest.permission.READ_EXTERNAL_STORAGE
        val accessNetworkState = Manifest.permission.ACCESS_NETWORK_STATE
        val readPhoneState = Manifest.permission.READ_PHONE_STATE
        val recordAudio = Manifest.permission.RECORD_AUDIO
        val camera = Manifest.permission.CAMERA
        val contacts = Manifest.permission.READ_CONTACTS
        val permissionList: ArrayList<String> = ArrayList()
        if (hasPermission(writeExternalStoragePermission)) {
            permissionList.add(writeExternalStoragePermission)
        }
        if (hasPermission(readExternalStoragePermission)) {
            permissionList.add(readExternalStoragePermission)
        }
        if (hasPermission(accessNetworkState)) {
            permissionList.add(accessNetworkState)
        }
        if (hasPermission(readPhoneState)) {
            permissionList.add(readPhoneState)
        }
        if (hasPermission(recordAudio)) {
            permissionList.add(recordAudio)
        }
        if (hasPermission(camera)) {
            permissionList.add(camera)
        }
        if (hasPermission(contacts)) {
            permissionList.add(contacts)
        }
        if (!permissionList.isEmpty()) {
            val permissions: Array<String> =
                    permissionList.toArray(arrayOfNulls<String>(0))
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            if (grantResults.size != 0) {
                val list: ArrayList<Int> = ArrayList()
                for (i in grantResults) {
                    list.add(i)
                }
                if (list.contains(PackageManager.PERMISSION_DENIED)) {
                    askForMultiplePermissions()
                }
            }
        }

        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(
                    this,
                    "This application needs permission to use your microphone to function properly.",
                    Toast.LENGTH_LONG
            ).show()
            finish()
        }

    }

    @SuppressLint("NewApi")
    fun getFilePaths(context: Context, uri: Uri): String? {
        var uri = uri
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(
                        context.getApplicationContext(),
                        uri
                )
        ) {
            if (isExternalStorageDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                return Environment.getExternalStorageDirectory()
                        .toString() + "/" + split[1]
            } else if (isDownloadsDocument(uri)) {
                val id: String = DocumentsContract.getDocumentId(uri)
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                )
            } else if (isMediaDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("image" == type) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(
                        split[1]
                )
            }
        }
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            if (isGooglePhotosUri(uri)) {
                return uri.lastPathSegment
            }
            val projection = arrayOf(
                    MediaStore.Images.Media.DATA
            )
            var cursor: Cursor? = null
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null)
                val column_index: Int =
                        cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)!!
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            cursor?.close()
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    override fun onResume() {
        super.onResume()
        if (ChatDatabase(this).findChatImage("1") == null) chatBackImg.setImageResource(R.drawable.chat_back_img) else chatBackImg.setImageBitmap(
                BitmapFactory.decodeByteArray(ChatDatabase(this).findChatImage("1"), 0, ChatDatabase(this).findChatImage("1").size)
        )
    }
}