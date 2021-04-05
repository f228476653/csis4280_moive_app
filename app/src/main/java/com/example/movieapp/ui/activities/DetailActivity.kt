package com.example.movieapp.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.movieapp.R
import com.example.movieapp.models.Movie
import com.example.movieapp.models.Review
import com.example.movieapp.service.MovieService
import com.example.movieapp.service.ServiceBuilder
import com.example.movieapp.service.ServiceBuilderRe
import com.example.movieapp.service.SharedPrefHelper
import com.example.movieapp.ui.adapters.ReviewRecyclerViewAdapter
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.movie_page_layout.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {
    private var mSocket: Socket? = null
    var isLike:Boolean =false
    private val img_api_URL = "https://image.tmdb.org/t/p/w200"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_page_layout)
        var movie = intent.getParcelableExtra<Movie>("movie")
        val requestOptions = RequestOptions().transform(CenterCrop(), RoundedCorners(16))
        Log.d("poster6",movie!!.poster_path)
        var s= movie!!.poster_path.toString()
        Glide.with(this@DetailActivity)
                .load("${img_api_URL}${s}")
                .apply(requestOptions)
                .into(poster_view)
        rating_view.text = movie!!.vote_average +"/10"
        title_view.text = movie!!.title
        plot_view.text = movie!!.overview
        released_view.text = movie!!.release_date
        getReviews(movie!!.id)
        val pref = SharedPrefHelper(this@DetailActivity)
        var email = pref.getAccount().email
        getIsLike(movie!!.id,email)
        fab.setOnClickListener {
            toggleLike(movie!!.id)
        }
        review_btn_add.setOnClickListener {
            showReviewArea()
        }
        submitReview.setOnClickListener {
            writeReview(movie!!.id, email)
        }
    }
    private fun writeReview(movie_id: Int,email:String) {
        val app: ServiceBuilder = application as ServiceBuilder
        mSocket = app.getMSocket()
        //connecting socket

        mSocket?.connect()
        mSocket?.on(Socket.EVENT_CONNECT, Emitter.Listener {
            mSocket?.emit("messages", "hi")
        });
        mSocket?.connect()
        var review =editTextTextMultiLine.text.toString()
        val jsonstring: String = "{'email': ${email}, 'movie_id': '${movie_id}', 'review': ${review}}"
        val jobj = JSONObject(jsonstring)
        mSocket?.emit("postReview", jobj)
        getReviews(movie_id)
        write_area.visibility = View.GONE
    }
    private fun showReviewArea() {
        no_review.visibility = View.GONE
        review_recyclerview.visibility = View.GONE
        write_area.visibility = View.VISIBLE
        btn_area.visibility = View.GONE
    }
    private fun getIsLike(movie_id: Int,email:String) {
        val app: ServiceBuilder = application as ServiceBuilder
        mSocket = app.getMSocket()
        //connecting socket

        mSocket?.connect()
        mSocket?.on(Socket.EVENT_CONNECT, Emitter.Listener {
            mSocket?.emit("messages", "hi")
        });
        mSocket?.connect()
        val jsonstring: String = "{'movie_id': '${movie_id}','email':${email}}"
        val jobj = JSONObject(jsonstring)
        mSocket?.emit("isLike", jobj)
        mSocket?.on("getIsLike", Emitter.Listener {
            runOnUiThread(Runnable {
                var returnData = JSONObject(it[0].toString())
                if (returnData["success"] == true) {
                    fab.text="unLike"
                    //(ContextCompat.getDrawable(this@DetailActivity, R.drawable.ic_favorite_border_yellow_18dp));
                }
                isLike = returnData["success"] as Boolean
            })
        })
    }

    private fun toggleLike(movie_id: Int) {
        val pref = SharedPrefHelper(this@DetailActivity)
        var email = pref.getAccount().email
        if(isLike){
            unlike_post(movie_id,email)
        }else{
            like_post(movie_id,email)
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun like_post(movie_id: Int, email:String) {
        val app: ServiceBuilder = application as ServiceBuilder
        mSocket = app.getMSocket()
        //connecting socket

        mSocket?.connect()
        mSocket?.on(Socket.EVENT_CONNECT, Emitter.Listener {
            mSocket?.emit("messages", "hi")
        });
        mSocket?.connect()
        val jsonstring: String = "{'email': ${email}, 'movie_id': '${movie_id}', 'type': 'like'}"
        val jobj = JSONObject(jsonstring)
        mSocket?.emit("toggleLike", jobj)
        mSocket?.on("reToggleLike", Emitter.Listener {
            runOnUiThread(Runnable {
//                Toast.makeText(
//                    this@DetailActivity,
//                    "Liked",
//                    Toast.LENGTH_SHORT
//                ).show()
                isLike = !isLike
                fab.text="unLike"
                    //fab.setImageDrawable(ContextCompat.getDrawable(this@DetailActivity, R.drawable.ic_favorite_border_yellow_18dp));
            })
        })

    }

    private fun unlike_post(movie_id: Int, email:String) {
        val app: ServiceBuilder = application as ServiceBuilder
        mSocket = app.getMSocket()
        //connecting socket

        mSocket?.connect()
        mSocket?.on(Socket.EVENT_CONNECT, Emitter.Listener {
            mSocket?.emit("messages", "hi")
        });
        mSocket?.connect()
        val jsonstring: String = "{'email': ${email}, 'movie_id': '${movie_id}', 'type': 'unlike'}"
        val jobj = JSONObject(jsonstring)
        mSocket?.emit("toggleLike", jobj)
        mSocket?.on("reToggleLike", Emitter.Listener {
            runOnUiThread(Runnable {
//                Toast.makeText(
//                    this@DetailActivity,
//                    "UnLiked",
//                    Toast.LENGTH_SHORT
//                ).show()
                isLike = !isLike
                fab.text="Like"
                //fab.setImageDrawable(ContextCompat.getDrawable(this@DetailActivity, R.drawable.ic_favorite_border_onyx_24dp));
            })
        })
    }
    private fun getReviews(id: Int) {
        val service: Call<List<Review>> =
                ServiceBuilderRe.buildService(MovieService::class.java).getReviewByMovie(id);
        review_btn_add.visibility = View.VISIBLE
        service.enqueue(object : Callback<List<Review>> {
            override fun onResponse(call: Call<List<Review>>, response: Response<List<Review>>) {
                review_recyclerview.layoutManager =
                        LinearLayoutManager(
                            this@DetailActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                if (response.isSuccessful) {
                    if (response.body()!!.isEmpty()) {
                        no_review.visibility = View.VISIBLE
                        review_recyclerview.visibility = View.GONE
                    }else{
                        no_review.visibility = View.GONE
                    }
                    val listMovie: List<Review> = response.body()!!
                    val adapter = ReviewRecyclerViewAdapter(
                            listMovie,
                            applicationContext
                    )
                    review_recyclerview.adapter = adapter

                } else {
                    Toast.makeText(this@DetailActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Review>>, t: Throwable) {
                Toast.makeText(this@DetailActivity, "Error : ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}