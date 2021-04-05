package com.example.movieapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.movieapp.R
import com.example.movieapp.models.User
import com.example.movieapp.service.ServiceBuilder
import com.example.movieapp.service.SharedPrefHelper
import com.google.gson.GsonBuilder
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {
    private var mSocket: Socket? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val app: ServiceBuilder = application as ServiceBuilder
        mSocket = app.getMSocket()
        //connecting socket
        mSocket?.connect()
        mSocket?.on(Socket.EVENT_CONNECT, Emitter.Listener {
            mSocket?.emit("messages", "hi")
        });
        //register event
        login_register.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
        //login event
        login_btn_login.setOnClickListener {
            onLoginPressed()
        }
    }

    private fun onLoginPressed() {
        var email = login_et_email.text.toString()
        var password = login_et_password.text.toString()
        val jsonstring: String = "{'email': ${email}, 'password': '${password}'}"
        val jobj = JSONObject(jsonstring)
        //send email and password to server
        mSocket?.connect()
        mSocket?.emit("login", jobj)
        mSocket?.on("ifLogin", Emitter.Listener {
            var returnData = JSONObject(it[0].toString())
            runOnUiThread(Runnable {
                //if login successfully
                if(returnData["success"] as Boolean) {
                    returnData =JSONObject(returnData["data"].toString())
                    var userData = GsonBuilder().create().fromJson(it[0].toString(), User::class.java)
                    //Then get user data
                    if (userData != null) {
                        val pref = SharedPrefHelper(this@LoginActivity)
                        //store in share preference
                        userData.name = returnData["name"] as String
                        userData.email = returnData["email"] as String
                        pref.saveUser(
                            userData
                        )
                        Log.d("user login", "id : ${userData!!.email}")
                        //start main activity
                        startActivity(
                            Intent(
                                this@LoginActivity,
                                MainActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    }
                }else{
                    Toast.makeText(this@LoginActivity, "user login false :${returnData["message"]}", Toast.LENGTH_SHORT).show()
                }
            })
        })
    }
}
