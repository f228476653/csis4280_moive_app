package com.example.movieapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.movieapp.R
import com.example.movieapp.models.Movie
import com.example.movieapp.models.User
import com.example.movieapp.service.MovieService
import com.example.movieapp.service.ServiceBuilder
import com.example.movieapp.service.ServiceBuilderRe
import com.example.movieapp.service.SharedPrefHelper
import com.example.movieapp.ui.activities.LoginActivity
import com.example.movieapp.ui.activities.MainActivity
import com.example.movieapp.ui.activities.RegisterActivity
import com.example.movieapp.ui.adapters.HomeRecyclerViewAdapter
import com.google.gson.GsonBuilder
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_discover.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.toolbar_activity_post.*
import org.json.JSONObject
//import com.example.movieapp.adapters.ProfileRecyclerViewAdapter
//import com.example.movieapp.models.Post
//import com.example.movieapp.models.User
//import com.example.movieapp.services.api.PostService
//import com.example.movieapp.services.api.ServiceBuilder
//import com.example.movieapp.services.SharedPrefHelper
//import com.example.movieapp.services.api.UserService
//import com.example.movieapp.ui.activities.AddPostActivity
//import com.example.movieapp.ui.activities.LoginActivity
//import com.example.movieapp.ui.activities.RegisterActivity
//import kotlinx.android.synthetic.main.fragment_profile.*
//import kotlinx.android.synthetic.main.toolbar_activity.toolbar_activity_back
//import kotlinx.android.synthetic.main.toolbar_activity.toolbar_activity_title
//import kotlinx.android.synthetic.main.toolbar_activity_post.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Like_recyclerview.setHasFixedSize(true)
        toolbar_activity_title.text = "Profile"
        toolbar_activity_share.text = "Logout"
        toolbar_activity_back.visibility = View.GONE
        toolbar_activity_share.setOnClickListener {
            onLogOutPressed()
        }
        //edit profile event
        profile_btn_edit_profile.setOnClickListener {
            onEditProfilePressed()
        }
        setText()
        //get movies that have been saved
        callLikedMovies()
        profile_swipe_refresh.isRefreshing =false
    }

    override fun onResume() {
        super.onResume()

        setText()
    }
    //display
    private fun callLikedMovies() {
        val pref = SharedPrefHelper(activity!!).getAccount()
        val service: Call<List<Movie>> =
            ServiceBuilderRe.buildService(MovieService::class.java).getLikedMovies(pref.email);
        service.enqueue(object : Callback<List<Movie>> {
            override fun onResponse(call: Call<List<Movie>>, response: Response<List<Movie>>) {
                if (response.isSuccessful) {
                    val listMovie: List<Movie> = response.body()!!
                    Like_recyclerview.layoutManager = GridLayoutManager(activity!!.applicationContext, 2)
                    val adapter = HomeRecyclerViewAdapter(
                        listMovie,
                        activity!!.applicationContext,
                        activity!!.supportFragmentManager
                    )
                    Like_recyclerview.adapter = adapter

                } else {
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                }
                profile_swipe_refresh.isRefreshing = false
            }

            override fun onFailure(call: Call<List<Movie>>, t: Throwable) {
                Toast.makeText(activity, "Error : ${t.message}", Toast.LENGTH_SHORT).show()
                profile_swipe_refresh.isRefreshing = false

            }
        })
    }

    private fun onEditProfilePressed() {
        activity!!.startActivity(
                Intent(activity!!, RegisterActivity::class.java).putExtra(
                        "isEdit",
                        true
                )
        )
    }


    private fun onLogOutPressed() {
        val pref = SharedPrefHelper(activity!!)
        pref.clearUser()
        startActivity(
                Intent(
                        activity!!,
                        LoginActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }



    private fun setText() {
        val user = SharedPrefHelper(activity!!).getAccount()
        profile_tv_name.text = user.name
        profile_email.text = user.email
    }




    companion object {
        fun newInstance(): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}

