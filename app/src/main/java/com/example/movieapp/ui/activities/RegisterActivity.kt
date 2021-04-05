package com.example.movieapp.ui.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.movieapp.R
import com.example.movieapp.service.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.movieapp.models.User
import com.example.movieapp.service.SharedPrefHelper
import com.example.movieapp.ui.activities.MainActivity
import com.google.gson.GsonBuilder
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.toolbar_activity_post.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*


class RegisterActivity : AppCompatActivity() {

    private var isEdit = false
    private var mSocket: Socket? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isEdit = intent.getBooleanExtra("isEdit", false)
        setContentView(R.layout.activity_register)
        //Socket instance
        val app: ServiceBuilder = application as ServiceBuilder
        mSocket = app.getMSocket()
        //new register or edit profile
        isEdit = intent.getBooleanExtra("isEdit", false)
        //if edit
        if (isEdit) {
            //then load user data
            initProfile()
            login_btn_register.setOnClickListener {
                onDoneSavePressed()
            }
        }else{
            login_btn_register.setOnClickListener {
                onDoneRegisterPressed()
            }
        }

}
    private fun initProfile() {
        val pref = SharedPrefHelper(this@RegisterActivity).getAccount()
        register_et_email.setText(pref.email)
        register_et_email.isEnabled = true
        register_et_name.setText(pref.name)
        login_btn_register.text = "Save"
    }

    private fun onDoneSavePressed() {
        //socket connected
        mSocket?.connect()
        val pref = SharedPrefHelper(this@RegisterActivity)
        var email=register_et_email.text.toString()
        var pwd=register_et_password.text.toString()
        var name=register_et_name.text.toString()
        //send user input to server
        val jsonstring: String = "{'email': ${email}, 'password1': '${pwd}', 'password2': '${pwd}','name': '${name}'}"
        val editData = JSONObject(jsonstring)
        mSocket?.emit("editProfile", editData)
        mSocket?.on("RespeditData", Emitter.Listener {
            runOnUiThread(Runnable {
                var returnData = JSONObject(it[0].toString())
                //if save successfully
                if (returnData["success"] == true) {
                    Toast.makeText(this@RegisterActivity, "Save Success", Toast.LENGTH_SHORT)
                            .show()
                    var userData =User(email,"",name)
                    // save to share preference
                    pref.saveUser(
                            userData
                    )
                    onBackPressed()
                }else{
                    Toast.makeText(this@RegisterActivity, returnData["message"].toString(), Toast.LENGTH_SHORT)
                            .show()
                }

            })
        })
    }
    private fun onDoneRegisterPressed() {
        mSocket?.connect()
        var email=register_et_email.text.toString()
        var pwd=register_et_password.text.toString()
        var name=register_et_name.text.toString()
        //send user input to server
        val jsonstring: String = "{'email': ${email}, 'password1': '${pwd}', 'password2': '${pwd}','name': '${name}'}"
        val registerj = JSONObject(jsonstring)
        mSocket?.emit("register", registerj)
        mSocket?.on("RespRegistered", Emitter.Listener {
            runOnUiThread(Runnable {
                var returnData = JSONObject(it[0].toString())
                //if save successfully
                if (returnData["success"] == true) {
                    Toast.makeText(this@RegisterActivity, "Register Success", Toast.LENGTH_SHORT)
                            .show()
                    onBackPressed()
                }else{
                    Toast.makeText(this@RegisterActivity, returnData["message"].toString(), Toast.LENGTH_SHORT)
                            .show()
                }

            })
        })
    }


    override  fun onDestroy() {
            super.onDestroy()
            mSocket?.disconnect()

    }
}
