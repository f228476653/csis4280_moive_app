package com.example.movieapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
//Review data class
@Parcelize
data class Review(
        val author: String,
        val content: String,
        val id: String
) : Parcelable

