package com.example.movieapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
//Movie data class
@Parcelize
data class Movie(
    val id: Int,
    val original_language: String,
    val original_title: String,
    val poster_path: String,
    val vote_average: String,
    val overview: String,
    val release_date: String,
    val vote_count: Int,
    val adult: Boolean,
    val backdrop_path: String,
    val title: String,
    val popularity: Float
    //val media_type: String
) : Parcelable

