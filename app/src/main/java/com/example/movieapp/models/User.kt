package com.example.movieapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
//User data class
@Parcelize
data class User(
    var email: String,
    val password: String,
    var name: String
) : Parcelable