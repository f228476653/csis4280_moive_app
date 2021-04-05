package com.example.movieapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.models.Movie
import com.example.movieapp.ui.adapters.HomeRecyclerViewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.android.synthetic.main.fragment_home.*
import com.example.movieapp.service.MovieService
import com.example.movieapp.service.ServiceBuilderRe


class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        home_recyclerview.layoutManager = LinearLayoutManager(activity)
        home_recyclerview.setHasFixedSize(true)
        //init data
        callDataMovies()
        home_swipe_refresh.setOnRefreshListener {
            callDataMovies()
        }
        home_swipe_refresh.isRefreshing = false

    }

    override fun onResume() {
        super.onResume()
        callDataMovies()
    }
    private fun callDataMovies() {
        val service: Call<List<Movie>> =
            ServiceBuilderRe.buildService(MovieService::class.java).getTrendMovies();
        service.enqueue(object : Callback<List<Movie>> {
            override fun onResponse(call: Call<List<Movie>>, response: Response<List<Movie>>) {
                if (response.isSuccessful) {
                    val listMovie: List<Movie> = response.body()!!
                    home_recyclerview.layoutManager = GridLayoutManager(activity!!.applicationContext, 2)
                    val adapter = HomeRecyclerViewAdapter(
                            listMovie,
                        activity!!.applicationContext,
                        activity!!.supportFragmentManager
                    )
                    home_recyclerview.adapter = adapter

                } else {
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                }
                home_swipe_refresh.isRefreshing = false
            }

            override fun onFailure(call: Call<List<Movie>>, t: Throwable) {
                Toast.makeText(activity, "Error : ${t.message}", Toast.LENGTH_SHORT).show()
                home_swipe_refresh.isRefreshing = false
            }
        })
    }

    companion object {
        fun newInstance(): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }


}
