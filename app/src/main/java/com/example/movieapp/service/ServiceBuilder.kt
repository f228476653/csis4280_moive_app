package com.example.movieapp.service

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import java.io.InputStream
import java.net.URISyntaxException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//cloud9 url
private const val URL = "http://44.192.78.222:5000"
class ServiceBuilder : Application() {
    //a method return socket.io instant
    private var mSocket: Socket? = null

    override fun onCreate() {
        super.onCreate()
        try {
    //creating socket instance
            mSocket = IO.socket(URL)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }
    //return socket instance
    fun getMSocket(): Socket? {
        return mSocket
    }
}