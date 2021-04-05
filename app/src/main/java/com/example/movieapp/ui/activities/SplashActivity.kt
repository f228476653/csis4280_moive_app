package com.example.movieapp.ui.activities

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.movieapp.R
import com.example.movieapp.models.User
import com.example.movieapp.service.SharedPrefHelper
import com.example.movieapp.service.ServiceBuilder
import com.google.gson.GsonBuilder
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

class SplashActivity : AppCompatActivity() {
    private var mSocket: Socket? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val app: ServiceBuilder = application as ServiceBuilder
        mSocket = app.getMSocket()
        val pref = SharedPrefHelper(this@SplashActivity)
        //determine if user have login
        Handler().postDelayed({
            DoAsync(
                { if (pref.isLoggedIn()) { saveUser() } },
                {
                    if (pref.isLoggedIn()) {
                        isLogin()
                    } else {
                        isNotLogin()
                    }
                }
            ).execute()
        }, 2000)
    }

    private fun isLogin() {
        startActivity(
            Intent(
                this@SplashActivity,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    private fun isNotLogin() {
        startActivity(
            Intent(
                this@SplashActivity,
                LoginActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    private fun saveUser() {
        val pref = SharedPrefHelper(this@SplashActivity)
        mSocket?.connect()
        mSocket?.emit("getUserByID", pref.getAccount().email)
        mSocket?.on("getUserByIDReturn", Emitter.Listener {
            var userData = GsonBuilder().create().fromJson(it[0].toString(), User::class.java)
            runOnUiThread(Runnable {
                if (userData != null) {
                    pref.saveUser(
                        userData
                    )
                    Log.d("user login", "id : ${userData!!.email}")
                }else{
                    Toast.makeText(
                            this@SplashActivity,
                            "Error : SplashActivity getUserByIDReturn${pref.getAccount().email}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        Log.e("SplashActivity getUserByIDReturn", pref.getAccount().email.toString())
                }
            })
        })
    }

    private class DoAsync(val onBackground: () -> Unit, val onPost: () -> Unit) :
        AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            onBackground()
            return null
        }

        override fun onPostExecute(result: Void?) {
            onPost()
        }
    }
}
