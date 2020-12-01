package net.glochat.dev.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sinch.android.rtc.MissingPermissionException
import net.glochat.dev.R
import net.glochat.dev.SinchService
import net.glochat.dev.adapter.UsersCallAdapter
import net.glochat.dev.base.BaseActivity2
import net.glochat.dev.models.Users
import net.glochat.dev.view.VoiceVideoCallView
import kotlin.collections.ArrayList

class CallVideoOrAudio : BaseActivity2(), VoiceVideoCallView {

    var recyclerView: RecyclerView? = null

    var progressBar: ProgressBar? = null

    private var mCurrentUserId: String? = null
    private var mUsersDatabase: DatabaseReference? = null

    private val userList: ArrayList<Users> = ArrayList()

    private var adapter: UsersCallAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_call_video_audio)

        recyclerView = findViewById(R.id.activity_call_or_video_recycler_view)
        progressBar = findViewById(R.id.activity_call_or_video_progress_bar)

        mCurrentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        mUsersDatabase = FirebaseDatabase.getInstance().reference.child("users")
        //mUsersDatabase.keepSynced(true);

        adapter = UsersCallAdapter(this, userList, this)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = adapter

        loadUsers()
    }

    private fun callButtonClicked(otherUsername: String) {

        val userName: String = otherUsername

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show()
            return
        }

        try {
            val call = sinchServiceInterface.callUser(userName)
            if (call == null) {
                // Service failed for some reason, show a Toast and abort
                Toast.makeText(
                        this,
                        "Service is not started. Try stopping the service and starting it again before "
                                + "placing a call.",
                        Toast.LENGTH_LONG
                ).show()
                return
            }
            val callId = call.callId
            val callScreen = Intent(this, CallScreenActivity::class.java)
            callScreen.putExtra(SinchService.CALL_ID, callId)
            startActivity(callScreen)
        } catch (e: MissingPermissionException) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(e.requiredPermission),
                    0
            )
        }
    }

    private fun callVideoButton(otherUsername: String) {
        val userName: String = otherUsername

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show()
            return
        }

        val call =
                sinchServiceInterface.callUserVideo(userName)
        val callId = call.callId

        val callScreen = Intent(this, VideoCallScreenActivity::class.java)
        callScreen.putExtra(SinchService.CALL_ID, callId)
        startActivity(callScreen)

    }

    private fun loadUsers() {
        progressBar?.visibility = View.VISIBLE
        userList.clear()
        mUsersDatabase?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snaps in snapshot.children) {
                    val users = snaps.getValue(Users::class.java)
                    if (users != null) {
                        if (users.uid != mCurrentUserId) userList.add(users)
                    }
                }
                adapter?.notifyDataSetChanged()
                progressBar?.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun doVoiceCall(name: String?) {
        callButtonClicked(name!!)
    }

    override fun doVideoCall(name: String?) {
        callVideoButton(name!!)
    }
}