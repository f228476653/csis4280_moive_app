package com.example.movieapp.service

import android.content.Context
import android.content.SharedPreferences
import com.example.movieapp.models.User
//handle SharedPreferences
class SharedPrefHelper(val context: Context) {
    private val PREF_NAME = "anitapref"
    private val ID_KEY = "id_key"
    private val EMAIL_KEY = "email_key"
    private val PASS_KEY = "pass_key"
    private val NAME_KEY = "name_key"
    private val LIKE_KEY = "like_key"

    val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(EMAIL_KEY, user.email)
        editor.putString(PASS_KEY, user.password)
        editor.putString(NAME_KEY, user.name)

        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPref.getInt(ID_KEY, 0) != 0
    }

    fun getAccount(): User {
        return User(
            sharedPref.getString(EMAIL_KEY, null)!!,
            sharedPref.getString(PASS_KEY, "test")!!,
            sharedPref.getString(NAME_KEY, null)!!
        )
    }

    fun clearUser() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
}