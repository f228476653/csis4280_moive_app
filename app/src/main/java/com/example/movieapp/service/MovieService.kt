package com.example.movieapp.service

import com.example.movieapp.models.*
import retrofit2.Call
import retrofit2.http.*
//retrofit2 service
//Retrofit turns your HTTP API into an interface.
interface MovieService {
    //search movies by user input
    @GET("movie/bySearch")
    fun getMoviesBySearch(
            @Query("search") search: String
    ): Call<List<Movie>>

    //get movies by the most popular
    @GET("movie/trend")
    fun getTrendMovies(): Call<List<Movie>>

    //get movies by user have saved in "liked"
    @GET("movie/liked")
    fun getLikedMovies(@Query("user") user_email: String): Call<List<Movie>>

    //get reviews by single movie
    @GET("review/byMovie")
    fun getReviewByMovie(
            @Query("movie") movie_id: Int
    ): Call<List<Review>>

    //get review which post by single user
    @GET("review/User")
    fun getReviewByUser(
            @Query("user") user_email: String
    ): Call<List<Review>>

}