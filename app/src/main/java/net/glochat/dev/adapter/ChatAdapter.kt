package net.glochat.dev.adapter

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.*
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import net.glochat.dev.R
import net.glochat.dev.activity.FileViewer
import net.glochat.dev.models.ChatDao
import net.glochat.dev.view.ChatsView
import java.io.ByteArrayOutputStream
import java.io.File

class ChatAdapter(
        val context: Context,
        private val list: List<ChatDao>,
        private val chatsView: ChatsView
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var handler: Handler? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.chats_layout, parent, false)
        return ViewHolder(chatsView, context, v)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.adminPlayVideoBtn.visibility = View.INVISIBLE
            holder.userPlayVideoBtn.visibility = View.INVISIBLE
            holder.userVideoBar.visibility = View.INVISIBLE
            holder.adminVideoBar.visibility = View.INVISIBLE
            val dao = list[position]
            val (msg_body, key, _, msg_type, time_stamp, from, to, msg_name, fromUsername, toUsername) = list[position]
            holder.checkUser(from)
            holder.checkType(msg_type, msg_body, msg_name)
            holder.setTextAdmin(msg_body)
            holder.setTextUser(msg_body)
            holder.setTextUserTime(time_stamp.toString())
            holder.setTextAdminTime(time_stamp.toString())
            holder.imgChatUserTime.text = DateUtils.getRelativeTimeSpanString(time_stamp)
            holder.imgChatAdminTime.text = DateUtils.getRelativeTimeSpanString(time_stamp)
            holder.userAudioTime.text = DateUtils.getRelativeTimeSpanString(time_stamp)
            holder.adminAudioTime.text = DateUtils.getRelativeTimeSpanString(time_stamp)
            holder.textAdminFileTime.text = DateUtils.getRelativeTimeSpanString(time_stamp)
            holder.textUserFileTime.text = DateUtils.getRelativeTimeSpanString(time_stamp)
            holder.textAdminContactTime.text = DateUtils.getRelativeTimeSpanString(time_stamp)
            holder.textUserContactTime.text = DateUtils.getRelativeTimeSpanString(time_stamp)
            mediaPlayer = null
            isPlaying = false
            handler = Handler()
            holder.imgLayout1.setOnClickListener { _: View? ->
                if (msg_type.equals("image", ignoreCase = true)) {
                    if (holder.chatAdminImg.drawable != null) {
                        val bitmap =
                            (holder.chatAdminImg.drawable as BitmapDrawable).bitmap
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        val thumb_byte = stream.toByteArray()
                        holder.viewImg(
                            thumb_byte, holder.chatAdminImg
                            , toUsername, time_stamp, chatsView.userPhoto
                        )
                    }
                } else {
                    val file = File(
                        Environment.getExternalStorageDirectory()
                            .absolutePath + "/MyCligApp/" + msg_name
                    )
                    if (file.exists()) {
                        holder.viewVideo(
                            file.absolutePath,
                            fromUsername, time_stamp, chatsView.userPhoto
                        )
                    } else {
                        holder.downloadVideo(msg_name, msg_body)
                    }
                }
            }
            holder.imgLayout2.setOnClickListener { _: View? ->
                if (msg_type.equals("image", ignoreCase = true)) {
                    if (holder.chatUserImg.drawable != null) {
                        val bitmap =
                            (holder.chatUserImg.drawable as BitmapDrawable).bitmap
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        val thumb_byte = stream.toByteArray()
                        holder.viewImg(
                            thumb_byte, holder.chatUserImg
                            , fromUsername, time_stamp, chatsView.userPhoto
                        )
                    }
                } else {
                    val file = File(
                        Environment.getExternalStorageDirectory()
                            .absolutePath + "/MyCligApp/" + msg_name
                    )
                    if (file.exists()) {
                        holder.viewVideo(
                            file.absolutePath,
                            fromUsername, time_stamp, chatsView.userPhoto
                        )
                    } else {
                        holder.downloadVideo(msg_name, msg_body)
                    }
                }
            }

            holder.userAudioPlayBtn.setOnClickListener { view: View? ->
                if (!File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/",
                        msg_name
                    ).exists()
                ) {
                    holder.downloadAudio(msg_name, msg_body)
                } else {
                    if (isPlaying) {
                        holder.userAudioPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                        mediaPlayer?.pause()
                        isPlaying = false
                    } else {
                        try {
                            holder.userAudioSeekBar.setOnSeekBarChangeListener(object :
                                OnSeekBarChangeListener {
                                override fun onProgressChanged(
                                    seekBar: SeekBar,
                                    i: Int,
                                    b: Boolean
                                ) {
                                    if (mediaPlayer != null && b) {
                                        mediaPlayer?.seekTo(i)
                                    }
                                }

                                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                                override fun onStopTrackingTouch(seekBar: SeekBar) {}
                            })
                            isPlaying = true
                            mediaPlayer = MediaPlayer()
                            mediaPlayer!!.setDataSource(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/" + msg_name
                            )
                            mediaPlayer!!.prepare()
                            holder.userAudioSeekBar.max = mediaPlayer!!.duration / 1000
                            mediaPlayer!!.start()
                            holder.userAudioPlayBtn.setImageResource(R.drawable.ic_pause_white_24dp)
                            handler!!.postDelayed({
                                if (mediaPlayer != null) {
                                    holder.userAudioSeekBar.progress =
                                        mediaPlayer!!.currentPosition / 1000
                                    holder.adminAudioSeekBar.progress =
                                        mediaPlayer!!.currentPosition / 1000
                                }
                                mediaPlayer!!.setOnCompletionListener { _: MediaPlayer? ->
                                    isPlaying = false
                                    holder.userAudioPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                                    holder.adminAudioPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                                }
                            }, 1000)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            holder.adminAudioPlayBtn.setOnClickListener { view: View? ->
                if (!File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/",
                        msg_name
                    ).exists()
                ) {
                    holder.downloadAudio(msg_name, msg_body)
                } else {
                    if (isPlaying) {
                        holder.adminAudioPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                        if (mediaPlayer != null) mediaPlayer?.pause()
                        isPlaying = false
                    } else {
                        holder.adminAudioSeekBar.setOnSeekBarChangeListener(object :
                            OnSeekBarChangeListener {
                            override fun onProgressChanged(
                                seekBar: SeekBar,
                                i: Int,
                                b: Boolean
                            ) {
                                if (mediaPlayer != null && b) {
                                    mediaPlayer?.seekTo(i)
                                }
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar) {}
                            override fun onStopTrackingTouch(seekBar: SeekBar) {}
                        })
                        isPlaying = true
                        mediaPlayer = MediaPlayer()
                        try {
                            mediaPlayer!!.setDataSource(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .absolutePath + "/" + msg_name
                            )
                            mediaPlayer!!.prepare()
                            mediaPlayer!!.start()
                            holder.adminAudioSeekBar.max = mediaPlayer!!.duration / 1000
                            holder.adminAudioPlayBtn.setImageResource(R.drawable.ic_pause_white_24dp)
                            handler!!.postDelayed({
                                if (mediaPlayer != null) {
                                    holder.userAudioSeekBar.progress =
                                        mediaPlayer!!.currentPosition / 1000
                                    holder.adminAudioSeekBar.progress =
                                        mediaPlayer!!.currentPosition / 1000
                                }
                                mediaPlayer!!.setOnCompletionListener { _: MediaPlayer? ->
                                    isPlaying = false
                                    holder.userAudioPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                                    holder.adminAudioPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                                }
                            }, 1000)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            holder.userFileImage.setOnClickListener {
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/",
                    msg_name
                )
                if(!file.exists()){
                    holder.downloadFile(dao.msg_name, dao.msg_body)
                }else{
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.data = FileProvider.getUriForFile(context, context.applicationContext.packageName+".provider", file)
                    context.startActivity(intent)
                }
            }
            holder.cardUserFile.setOnClickListener {
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/",
                    msg_name
                )
                if(!file.exists()){
                    holder.downloadFile(dao.msg_name, dao.msg_body)
                }else{
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.data = FileProvider.getUriForFile(context, context.applicationContext.packageName+".provider", file)
                    context.startActivity(intent)
                }
            }

            holder.adminFileImage.setOnClickListener {
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/",
                    msg_name
                )
                if(!file.exists()){
                    holder.downloadFile(dao.msg_name, dao.msg_body)
                }else{
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.setData(FileProvider.getUriForFile(context, context.applicationContext.packageName+".provider", file))
                    context.startActivity(intent)
                }
            }

            holder.cardAdminFile.setOnClickListener {
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/",
                    msg_name
                )
                if(!file.exists()){
                    holder.downloadFile(dao.msg_name, dao.msg_body)
                }else{
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.setData(FileProvider.getUriForFile(context, context.applicationContext.packageName+".provider", file))
                    context.startActivity(intent)
                }
            }

            holder.imgUser.setOnLongClickListener {
                holder.showDeleteShare(dao, position)
                true
            }

            holder.imgLayout2.setOnLongClickListener {
                holder.showDeleteShare(dao, position)
                true
            }

            holder.imgAdmin.setOnLongClickListener {
                holder.showShare(dao, position)
                true
            }

            holder.imgLayout1.setOnLongClickListener {
                holder.showShare(dao, position)
                true
            }

            holder.textAdminCard.setOnLongClickListener { view: View? ->
                holder.showShareCopy(dao, position)
                true
            }

            holder.textAdmin.setOnLongClickListener { view: View? ->
                holder.showShareCopy(dao, position)
                false
            }

            holder.textUserCard.setOnLongClickListener { view: View? ->
                holder.showDeleteShareCopy(dao, position)
                true
            }

            holder.textUser.setOnLongClickListener { view: View? ->
                holder.showDeleteShareCopy(dao, position)
                false
            }

            holder.cardUserAudio.setOnLongClickListener { view: View? ->
                holder.showDeleteShare(dao, position)
                true
            }

            holder.cardAdminAudio.setOnLongClickListener { view: View? ->
                holder.showShare(dao, position)
                true
            }

            holder.cardUserFile.setOnLongClickListener { view: View? ->
                holder.showDeleteShare(dao, position)
                true
            }

            holder.cardAdminFile.setOnLongClickListener { view: View? ->
                holder.showShare(dao, position)
                true
            }

            holder.cardUserContact.setOnLongClickListener { view: View? ->
                holder.showDeleteShareCopy(dao, position)
                true
            }

            holder.cardAdminContact.setOnLongClickListener { view: View? ->
                holder.showShareCopy(dao, position)
                true
            }

            holder.userPlayVideoBtn.setOnClickListener { view: View? ->
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/",
                    msg_name
                )
                if (file.exists()) {
                    holder.viewVideo(
                        file.absolutePath,
                        fromUsername, time_stamp, chatsView.userPhoto
                    )
                } else {
                    holder.downloadVideo(msg_name, msg_body)
                }
            }
            holder.adminPlayVideoBtn.setOnClickListener { view1: View? ->
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/",
                    msg_name
                )
                if (file.exists()) {
                    holder.viewVideo(
                        file.absolutePath,
                        fromUsername, time_stamp, chatsView.userPhoto
                    )
                } else {
                    holder.downloadVideo(msg_name, msg_body)
                }
            }
            holder.userContactBtnView.setOnClickListener { view1: View? ->
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$msg_body")
                context.startActivity(intent)
            }
            holder.adminContactBtnView.setOnClickListener { view1: View? ->
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$msg_body")
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(
            private val chatsView: ChatsView,
            private val context: Context,
            itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {
        var adminLinear: LinearLayout
        var userLinear: RelativeLayout
        var imgLayout1: RelativeLayout
        var imgLayout2: RelativeLayout
        var imgUser: CardView
        var imgAdmin: CardView
        var textAdminCard: CardView
        var textUserCard: CardView
        var cardAdminAudio: CardView
        var cardUserAudio: CardView
        var cardUserFile: CardView
        var cardAdminFile: CardView
        var cardUserContact: CardView
        var cardAdminContact: CardView
        var textUser: TextView
        var textAdmin: TextView
        var adminTime: TextView
        var userTime: TextView
        var imgChatUserTime: TextView
        var imgChatAdminTime: TextView
        var adminAudioTime: TextView
        var userAudioTime: TextView
        var textUserFile: TextView
        var textUserFileTime: TextView
        var textAdminFile: TextView
        var textAdminFileTime: TextView
        var textUserContact: TextView
        var textUserContactTime: TextView
        var textAdminContact: TextView
        var textAdminContactTime: TextView
        var chatAdminImg: ImageView
        var chatUserImg: ImageView
        var userFileImage: ImageView
        var adminFileImage: ImageView
        var adminAudioPlayBtn: ImageButton
        var userAudioPlayBtn: ImageButton
        var adminPlayVideoBtn: ImageButton
        var userPlayVideoBtn: ImageButton
        var adminAudioSeekBar: SeekBar
        var userAudioSeekBar: SeekBar
        var adminVideoBar: ProgressBar
        var userVideoBar: ProgressBar
        var adminAudioBar: ProgressBar
        var userAudioBar: ProgressBar
        var userContactBtnView: Button
        var adminContactBtnView: Button

        fun makeLinkClickable(strBuilder: SpannableStringBuilder, span: URLSpan) {
            val start = strBuilder.getSpanStart(span)
            val end = strBuilder.getSpanEnd(span)
            val flags = strBuilder.getSpanFlags(span)
            val clickable: ClickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    Toast.makeText(context, span.url, Toast.LENGTH_SHORT).show()
                }
            }
            strBuilder.setSpan(clickable, start, end, flags)
            strBuilder.removeSpan(span)
        }

        fun setTextViewHTML(text: TextView, html: String?) {
            val sequence: CharSequence = Html.fromHtml(html)
            val strBuilder = SpannableStringBuilder(sequence)
            val urls =
                strBuilder.getSpans(0, sequence.length, URLSpan::class.java)
            for (span in urls) {
                makeLinkClickable(strBuilder, span)
            }
            text.text = strBuilder
            text.movementMethod = LinkMovementMethod.getInstance()
        }

        fun setTextUser(text: String) {
            setTextViewHTML(textUser, text)
        }

        fun setTextAdmin(text: String) {
            setTextViewHTML(textAdmin, text)
        }

        fun setTextUserTime(text: String) {
            userTime.text = DateUtils.getRelativeTimeSpanString(text.toLong())
        }

        fun setTextAdminTime(time: String) {
            adminTime.text = DateUtils.getRelativeTimeSpanString(time.toLong())
        }

        fun checkUser(user: String) {
            if (user.equals(FirebaseAuth.getInstance().currentUser?.uid, ignoreCase = true)) {
                adminLinear.visibility = View.GONE
                userLinear.visibility = View.VISIBLE
            } else {
                userLinear.visibility = View.GONE
                adminLinear.visibility = View.VISIBLE
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        fun checkType(
            msg_type: String,
            img: String,
            msg_name: String
        ) {
            if (msg_type.equals("text", ignoreCase = true)) {
                textAdminCard.visibility = View.VISIBLE
                textUserCard.visibility = View.VISIBLE
                imgAdmin.visibility = View.GONE
                imgUser.visibility = View.GONE
                cardUserAudio.visibility = View.GONE
                cardAdminAudio.visibility = View.GONE
                cardAdminFile.visibility = View.GONE
                cardUserFile.visibility = View.GONE
                cardAdminContact.visibility = View.GONE
                cardUserContact.visibility = View.GONE
            } else if (msg_type.equals("image", ignoreCase = true)) {
                Glide.with(context).load(img).into(chatAdminImg)
                Glide.with(context).load(img).into(chatUserImg)
                imgAdmin.visibility = View.VISIBLE
                imgUser.visibility = View.VISIBLE
                textAdminCard.visibility = View.GONE
                textUserCard.visibility = View.GONE
                cardUserAudio.visibility = View.GONE
                cardAdminAudio.visibility = View.GONE
                userPlayVideoBtn.visibility = View.INVISIBLE
                adminPlayVideoBtn.visibility = View.INVISIBLE
                cardAdminFile.visibility = View.GONE
                cardUserFile.visibility = View.GONE
                cardAdminContact.visibility = View.GONE
                cardUserContact.visibility = View.GONE
            } else if (msg_type.equals("video", ignoreCase = true)) {
                imgAdmin.visibility = View.VISIBLE
                imgUser.visibility = View.VISIBLE
                textAdminCard.visibility = View.GONE
                textUserCard.visibility = View.GONE
                cardUserAudio.visibility = View.GONE
                cardAdminAudio.visibility = View.GONE
                cardAdminFile.visibility = View.GONE
                cardUserFile.visibility = View.GONE
                cardAdminContact.visibility = View.GONE
                cardUserContact.visibility = View.GONE
                userPlayVideoBtn.visibility = View.VISIBLE
                adminPlayVideoBtn.visibility = View.VISIBLE
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/",
                    msg_name
                )
                if (file.exists()) {
                    Glide.with(context).load(file.absolutePath).into(chatAdminImg)
                    Glide.with(context).load(file.absolutePath).into(chatUserImg)
                    userPlayVideoBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                    adminPlayVideoBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                } else {
                    Glide.with(context).load(img).into(chatAdminImg)
                    Glide.with(context).load(img).into(chatUserImg)
                    userPlayVideoBtn.setImageResource(R.drawable.ic_file_download_white_24dp)
                    adminPlayVideoBtn.setImageResource(R.drawable.ic_file_download_white_24dp)
                }
            } else if (msg_type.equals("file", ignoreCase = true)) {
                imgAdmin.visibility = View.GONE
                imgUser.visibility = View.GONE
                textAdminCard.visibility = View.GONE
                textUserCard.visibility = View.GONE
                cardUserAudio.visibility = View.GONE
                cardAdminAudio.visibility = View.GONE
                cardAdminFile.visibility = View.VISIBLE
                cardUserFile.visibility = View.VISIBLE
                cardAdminContact.visibility = View.GONE
                cardUserContact.visibility = View.GONE
                textUserFile.text = msg_name
                textAdminFile.text = msg_name
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/",
                    msg_name
                )
                if (file.exists()) {
                    Glide.with(context).load(file.absolutePath)
                        .placeholder(R.drawable.ic_insert_drive_file_white_24dp).into(userFileImage)
                    Glide.with(context).load(file.absolutePath)
                        .placeholder(R.drawable.ic_insert_drive_file_white_24dp)
                        .into(adminFileImage)
                } else {
                    Glide.with(context).load(img)
                        .placeholder(R.drawable.ic_file_download_white_24dp).into(userFileImage)
                    Glide.with(context).load(img)
                        .placeholder(R.drawable.ic_file_download_white_24dp)
                        .into(adminFileImage)
                }
            } else if (msg_type.equals("contact", ignoreCase = true)) {
                imgAdmin.visibility = View.GONE
                imgUser.visibility = View.GONE
                textAdminCard.visibility = View.GONE
                textUserCard.visibility = View.GONE
                cardUserAudio.visibility = View.GONE
                cardAdminAudio.visibility = View.GONE
                cardAdminFile.visibility = View.GONE
                cardUserFile.visibility = View.GONE
                cardAdminContact.visibility = View.VISIBLE
                cardUserContact.visibility = View.VISIBLE
                textUserContact.text = msg_name
                textAdminContact.text = msg_name
            } else {
                textAdminCard.visibility = View.GONE
                textUserCard.visibility = View.GONE
                imgAdmin.visibility = View.GONE
                imgUser.visibility = View.GONE
                cardUserAudio.visibility = View.VISIBLE
                cardAdminAudio.visibility = View.VISIBLE
                cardAdminFile.visibility = View.GONE
                cardUserFile.visibility = View.GONE
                cardAdminContact.visibility = View.GONE
                cardUserContact.visibility = View.GONE
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/",
                    msg_name
                )
                if (file.exists()) {
                    userAudioPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                    adminAudioPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                } else {
                    userAudioPlayBtn.setImageResource(R.drawable.ic_file_download_white_24dp)
                    adminAudioPlayBtn.setImageResource(R.drawable.ic_file_download_white_24dp)
                }
            }
        }

        fun viewImg(
            thumb_byte: ByteArray,
            imgAdmin: ImageView,
            username: String,
            time: Long,
            img: String
        ) {
            val optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (context as Activity),
                    imgAdmin, "image"
                )
            //file view activity
            context.startActivity(
                Intent(context, FileViewer::class.java).putExtra("username", username)
                    .putExtra("photo", thumb_byte).putExtra("time", time).putExtra("img_url", img)
                    .putExtra("type", "image"), optionsCompat.toBundle()
            )
        }

        @Throws(NullPointerException::class)
        fun downloadAudio(file: String, msgBody: String) {
            try {// chatsView.startProgress(View.VISIBLE)
                userAudioBar.visibility = View.VISIBLE
                adminAudioBar.visibility = View.VISIBLE
                userAudioPlayBtn.visibility = View.GONE
                adminAudioPlayBtn.visibility = View.GONE

                val downloadManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val request = DownloadManager.Request(Uri.parse(msgBody))
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file)
                downloadManager.enqueue(request)
                val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(
                        context: Context,
                        intent: Intent
                    ) {
                        try {
                            // chatsView.startProgress(View.GONE)
                            userAudioPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                            adminAudioPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                            userAudioBar.visibility = View.GONE
                            adminAudioBar.visibility = View.GONE
                            userAudioPlayBtn.visibility = View.VISIBLE
                            adminAudioPlayBtn.visibility = View.VISIBLE
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                context.applicationContext.registerReceiver(
                    onComplete,
                    IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @Throws(NullPointerException::class)
        fun downloadFile(file: String, msgBody: String) {
            try {// chatsView.startProgress(View.VISIBLE)
                Toast.makeText(context, "downloading...", Toast.LENGTH_SHORT).show()
                chatsView.startProgress(View.VISIBLE)
                val downloadManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val request = DownloadManager.Request(Uri.parse(msgBody))
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file)
                downloadManager.enqueue(request)
                val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(
                        context: Context,
                        intent: Intent
                    ) {
                        try {
                            // chatsView.startProgress(View.GONE)
                            userFileImage.setImageResource(R.drawable.ic_insert_drive_file_white_24dp)
                            adminFileImage.setImageResource(R.drawable.ic_insert_drive_file_grey_24dp)
                            chatsView.startProgress(View.GONE)
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                context.applicationContext.registerReceiver(
                    onComplete,
                    IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun deleteMsgImage(
            key: String,
            from: String,
            to: String,
            msg_name: String,
            position: Int
        ) {
            chatsView.startProgress(View.VISIBLE)
            FirebaseDatabase.getInstance().reference.child("messages").child(from).child(to)
                .child(key).removeValue().addOnSuccessListener { aVoid: Void? ->
                    FirebaseDatabase.getInstance().reference.child("messages").child(to)
                        .child(from).child(key).removeValue()
                        .addOnSuccessListener { aVoid1: Void? ->
                            val thumbRef =
                                FirebaseStorage.getInstance().reference.child("message_images")
                                    .child(msg_name)
                            thumbRef.delete()
                                .addOnCompleteListener { task: Task<Void?>? ->
                                    chatsView.startProgress(View.GONE)
                                    chatsView.reload()
                                }
                        }
                }
        }

        fun deleteMsgText(
            key: String,
            from: String,
            to: String,
            msg_name: String,
            position: Int
        ) {
            chatsView.startProgress(View.VISIBLE)
            FirebaseDatabase.getInstance().reference.child("messages").child(from).child(to)
                .child(key).removeValue().addOnSuccessListener { aVoid: Void? ->
                    FirebaseDatabase.getInstance().reference.child("messages").child(to)
                        .child(from).child(key).removeValue()
                        .addOnSuccessListener { aVoid1: Void? ->
                            chatsView.startProgress(View.GONE)
                            chatsView.reload()
                        }
                }
        }

        fun deleteMsgAudio(
            key: String,
            from: String,
            to: String,
            msg_name: String,
            position: Int
        ) {
            chatsView.startProgress(View.VISIBLE)
            FirebaseDatabase.getInstance().reference.child("messages").child(from).child(to)
                .child(key).removeValue().addOnSuccessListener { aVoid: Void? ->
                    FirebaseDatabase.getInstance().reference.child("messages").child(to)
                        .child(from).child(key).removeValue()
                        .addOnSuccessListener { aVoid1: Void? ->
                            val thumbRef =
                                FirebaseStorage.getInstance().reference.child("message_audio")
                                    .child(msg_name)
                            thumbRef.delete()
                                .addOnCompleteListener { task: Task<Void?>? ->
                                    chatsView.startProgress(View.GONE)
                                    chatsView.reload()
                                }
                        }
                }
        }

        fun deleteMsgFile(
            key: String,
            from: String,
            to: String,
            msg_name: String,
            position: Int
        ) {
            chatsView.startProgress(View.VISIBLE)
            FirebaseDatabase.getInstance().reference.child("messages").child(from).child(to)
                .child(key).removeValue().addOnSuccessListener { aVoid: Void? ->
                    FirebaseDatabase.getInstance().reference.child("messages").child(to)
                        .child(from).child(key).removeValue()
                        .addOnSuccessListener { aVoid1: Void? ->
                            val thumbRef =
                                FirebaseStorage.getInstance().reference.child("message_files")
                                    .child(msg_name)
                            thumbRef.delete()
                                .addOnCompleteListener { task: Task<Void?>? ->
                                    chatsView.startProgress(View.GONE)
                                    chatsView.reload()
                                }
                        }
                }
        }

        fun deleteMsgVideo(
            key: String,
            from: String,
            to: String,
            msg_name: String,
            position: Int
        ) {
            chatsView.startProgress(View.VISIBLE)
            FirebaseDatabase.getInstance().reference.child("messages").child(from).child(to)
                .child(key).removeValue().addOnSuccessListener { aVoid: Void? ->
                    FirebaseDatabase.getInstance().reference.child("messages").child(to)
                        .child(from).child(key).removeValue()
                        .addOnSuccessListener { aVoid1: Void? ->
                            val thumbRef =
                                FirebaseStorage.getInstance().reference.child("message_video")
                                    .child(msg_name)
                            thumbRef.delete()
                                .addOnCompleteListener { task: Task<Void?>? ->
                                    chatsView.startProgress(View.GONE)
                                    chatsView.reload()
                                }
                        }
                }
        }

        fun viewVideo(
            filePath: String,
            fromUsername: String,
            time_stamp: Long,
            userPhoto: String
        ) {
            //file viewer activity
            context.startActivity(
                Intent(context, FileViewer::class.java).putExtra("username", fromUsername)
                    .putExtra("file", filePath).putExtra("time", time_stamp)
                    .putExtra("img_url", userPhoto).putExtra("type", "video")
            )
        }

        fun downloadVideo(file: String, msgBody: String) {
            try {
                userVideoBar.visibility = View.VISIBLE
                adminVideoBar.visibility = View.VISIBLE
                userPlayVideoBtn.visibility = View.INVISIBLE
                adminPlayVideoBtn.visibility = View.INVISIBLE
                val downloadManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val request =
                    DownloadManager.Request(Uri.parse(msgBody))
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file)
                downloadManager.enqueue(request)
                val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(
                        context: Context,
                        intent: Intent
                    ) {
                        try {
                            userVideoBar.visibility = View.INVISIBLE
                            adminVideoBar.visibility = View.INVISIBLE
                            userPlayVideoBtn.visibility = View.VISIBLE
                            adminPlayVideoBtn.visibility = View.VISIBLE
                            userPlayVideoBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                            adminPlayVideoBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                        } catch (e: Exception) {
                        }
                    }
                }
                context.applicationContext.registerReceiver(
                    onComplete,
                    IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun showDeleteShare(dao: ChatDao, position: Int) {
            val options = arrayOf<CharSequence>("Delete", "Share")
            AlertDialog.Builder(context)
                .setItems(options) { dialogInterface, i ->
                    dialogInterface.dismiss()
                    if (options[i] == "Delete") {
                        val builder =
                            AlertDialog.Builder(context)
                                .setTitle("Delete Message")
                                .setMessage("Are you sure you want to delete this message?")
                                .setPositiveButton(
                                    "Sure"
                                ) { _: DialogInterface?, i: Int ->
                                    if (dao.msg_type.equals(
                                            "image",
                                            ignoreCase = true
                                        )
                                    ) deleteMsgImage(
                                        dao.key,
                                        dao.from,
                                        dao.to,
                                        dao.msg_name,
                                        position
                                    ) else if (dao.msg_type.equals(
                                            "text",
                                            ignoreCase = true
                                        )
                                    ) deleteMsgText(
                                        dao.key,
                                        dao.from,
                                        dao.to,
                                        dao.msg_name,
                                        position
                                    )
                                    else if (dao.msg_type.equals("file"))
                                        deleteMsgFile(
                                            dao.key,
                                            dao.from,
                                            dao.to,
                                            dao.msg_name,
                                            position
                                        )
                                    else if (dao.msg_type.equals("video"))
                                        deleteMsgVideo(
                                            dao.key,
                                            dao.from,
                                            dao.to,
                                            dao.msg_name,
                                            position
                                        )
                                    else deleteMsgAudio(
                                        dao.key,
                                        dao.from,
                                        dao.to,
                                        dao.msg_name,
                                        position
                                    )
                                }
                                .setNegativeButton(
                                    "Cancel"
                                ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
                        builder.create().show()
                    } else {
                        shareMessage(dao, position)
                    }
                }.create()
                .show()
        }

        fun showDeleteShareCopy(dao: ChatDao, position: Int) {
            val options = arrayOf<CharSequence>("Delete", "Share", "Copy")
            AlertDialog.Builder(context)
                .setItems(options) { dialogInterface, i ->
                    dialogInterface.dismiss()
                    if (options[i] == "Delete") {
                        val builder =
                            AlertDialog.Builder(context)
                                .setTitle("Delete Message")
                                .setMessage("Are you sure you want to delete this message?")
                                .setPositiveButton(
                                    "Sure"
                                ) { _: DialogInterface?, i: Int ->
                                    if (dao.msg_type.equals(
                                            "image",
                                            ignoreCase = true
                                        )
                                    ) deleteMsgImage(
                                        dao.key,
                                        dao.from,
                                        dao.to,
                                        dao.msg_name,
                                        position
                                    ) else if (dao.msg_type.equals(
                                            "text",
                                            ignoreCase = true
                                        )
                                    ) deleteMsgText(
                                        dao.key,
                                        dao.from,
                                        dao.to,
                                        dao.msg_name,
                                        position
                                    ) else if (dao.msg_type.equals("file"))
                                        deleteMsgFile(
                                            dao.key,
                                            dao.from,
                                            dao.to,
                                            dao.msg_name,
                                            position
                                        )
                                    else if (dao.msg_type.equals("video"))
                                        deleteMsgVideo(
                                            dao.key,
                                            dao.from,
                                            dao.to,
                                            dao.msg_name,
                                            position
                                        )
                                    else
                                        deleteMsgAudio(
                                            dao.key,
                                            dao.from,
                                            dao.to,
                                            dao.msg_name,
                                            position
                                        )
                                }
                                .setNegativeButton(
                                    "Cancel"
                                ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
                        builder.create().show()
                    } else if (options[i] == "Share") {
                        shareMessage(dao, position)
                    } else {
                        copyMsg(dao, position)
                    }
                }.create()
                .show()
        }

        private fun copyMsg(dao: ChatDao, position: Int) {
            val clipboard =
                context?.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Copied To Clipboard", dao.msg_body)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Message copied", Toast.LENGTH_SHORT).show()
        }

        private fun shareMessage(dao: ChatDao, position: Int) {
            chatsView.shareMessage(dao)
        }

        fun showShareCopy(dao: ChatDao, position: Int) {
            val options = arrayOf<CharSequence>("Share", "Copy")
            AlertDialog.Builder(context)
                .setItems(options) { dialogInterface, i ->
                    dialogInterface.dismiss()
                   if (options[i] == "Share") {
                        shareMessage(dao, position)
                    } else {
                        copyMsg(dao, position)
                    }
                }.create()
                .show()
        }

        fun showShare(dao: ChatDao, position: Int) {
            val options = arrayOf<CharSequence>("Share")
            AlertDialog.Builder(context)
                .setItems(options) { dialogInterface, i ->
                    dialogInterface.dismiss()
                    if (options[i] == "Share")
                        shareMessage(dao, position)
                }.create()
                .show()
        }

        init {
            userLinear = itemView.findViewById(R.id.chat_layout_user_layout)
            adminLinear = itemView.findViewById(R.id.chat_layout_admin_layout)
            imgUser = itemView.findViewById(R.id.chat_user_img_card)
            imgAdmin = itemView.findViewById(R.id.chat_admin_img_card)
            textUser = itemView.findViewById(R.id.chat_user_text)
            textAdmin = itemView.findViewById(R.id.chat_admin_text)
            textAdminCard = itemView.findViewById(R.id.chat_admin_text_card)
            textUserCard = itemView.findViewById(R.id.chat_user_text_card)
            adminTime = itemView.findViewById(R.id.chat_admin_text_time)
            userTime = itemView.findViewById(R.id.chat_user_text_time)
            chatAdminImg = itemView.findViewById(R.id.chat_admin_img_btn)
            chatUserImg = itemView.findViewById(R.id.chat_user_img_btn)
            imgChatAdminTime = itemView.findViewById(R.id.chat_img_admin_time)
            imgChatUserTime = itemView.findViewById(R.id.chat_img_user_time)
            imgLayout1 = itemView.findViewById(R.id.chat_admin_img_btn_layout)
            imgLayout2 = itemView.findViewById(R.id.chat_user_img_btn_layout)
            cardAdminAudio = itemView.findViewById(R.id.chat_admin_card_audio)
            cardUserAudio = itemView.findViewById(R.id.chat_user_card_audio)
            adminAudioTime = itemView.findViewById(R.id.chat_admin_audio_time)
            userAudioTime = itemView.findViewById(R.id.chat_user_audio_time)
            userAudioPlayBtn = itemView.findViewById(R.id.chat_user_audio_play_btn)
            adminAudioPlayBtn = itemView.findViewById(R.id.chat_admin_audio_play_btn)
            userAudioBar = itemView.findViewById(R.id.chat_user_audio_play_btn_progress_bar)
            adminAudioBar = itemView.findViewById(R.id.chat_admin_audio_play_btn_progress_bar)
            adminAudioSeekBar = itemView.findViewById(R.id.chat_admin_audio_seek_bar)
            userAudioSeekBar = itemView.findViewById(R.id.chat_user_audio_seek_bar)
            adminVideoBar =
                itemView.findViewById(R.id.chat_video_admin_progress_bar)
            userVideoBar =
                itemView.findViewById(R.id.chat_video_user_progress_bar)
            adminPlayVideoBtn =
                itemView.findViewById(R.id.chat_video_admin_download_play_btn)
            userPlayVideoBtn =
                itemView.findViewById(R.id.chat_video_user_download_play_btn)
            cardUserFile = itemView.findViewById(R.id.chat_user_card_file)
            cardAdminFile = itemView.findViewById(R.id.chat_admin_card_file)
            cardUserContact = itemView.findViewById(R.id.chat_user_card_contact)
            cardAdminContact = itemView.findViewById(R.id.chat_admin_card_contact)
            textUserFile = itemView.findViewById(R.id.chat_user_file_text_name)
            textUserFileTime = itemView.findViewById(R.id.chat_user_file_text_time)
            userFileImage = itemView.findViewById(R.id.chat_user_file_img)
            textAdminFile = itemView.findViewById(R.id.chat_admin_file_text_name)
            textAdminFileTime = itemView.findViewById(R.id.chat_admin_file_text_time)
            adminFileImage =
                itemView.findViewById(R.id.chat_admin_file_img)
            textUserContact = itemView.findViewById(R.id.chat_user_contact_text_name)
            textAdminContact = itemView.findViewById(R.id.chat_admin_contact_text_name)
            textUserContactTime = itemView.findViewById(R.id.chat_user_contact_text_time)
            textAdminContactTime =
                itemView.findViewById(R.id.chat_admin_contact_text_time)
            userContactBtnView =
                itemView.findViewById(R.id.chat_user_contact_view_btn)
            adminContactBtnView =
                itemView.findViewById(R.id.chat_admin_contact_view_btn)
        }
    }


}