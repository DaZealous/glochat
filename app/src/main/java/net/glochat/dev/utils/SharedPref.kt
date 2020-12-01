package net.glochat.dev.utils

import android.content.SharedPreferences
import android.content.Context
import net.glochat.dev.models.Users

class SharedPref(private val context: Context) {
    private var pref: SharedPreferences? = null

    val userId: String
        get() = getPref().getString("userId", "")!!

    val phone: String
        get() = getPref().getString("phone", "")!!

    val image_url: String
        get() = getPref().getString("image_url", "")!!

    val fullName: String
        get() = getPref().getString("name", "")!!

    val followers: String
        get() = getPref().getString("followers", "")!!

    val username: String
        get() = getPref().getString("username", "")!!

    val following: Boolean
        get() = getPref().getBoolean("following", false)

    val isBuffering: Boolean
        get() = getPref().getBoolean("isBuffering", true)

    init {
        pref = getPref()
    }

    fun addBuffering(buffer : Boolean){
        val editor = pref!!.edit()
        editor.putBoolean("isBuffering", buffer)
        editor.apply()
    }

    private fun getPref(): SharedPreferences {
        if (pref == null) {
            pref = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        }
        return pref!!
    }

    fun addUser(user: Users) {
        val editor = pref!!.edit()
        editor.putString("userId", user.uid)
        editor.putString("phone", user.phone)
        editor.putString("image_url", user.photoUrl)
        editor.putString("name", user.name)
        editor.putString("username", user.bio)
        editor.putString("followers", user.followers)
        editor.putString("following", user.following)
        editor.apply()
    }

    fun removeUser(){
        val editor = pref!!.edit()
        editor.clear()
        editor.apply()
    }

    fun addImage(img: String) {
        val editor = pref!!.edit()
        editor.putString("image_url", img)
        editor.apply()
    }

    companion object {
        private var instance: SharedPref? = null

        @Synchronized
        fun getInstance(context: Context): SharedPref {
            if (instance == null) {
                instance = SharedPref(context.applicationContext)
            }
            return instance as SharedPref
        }
    }
}
