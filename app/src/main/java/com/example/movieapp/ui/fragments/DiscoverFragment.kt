package com.example.movieapp.ui.fragments

//import com.example.movieapp.adapters.HomeRecyclerViewAdapter
//import com.example.movieapp.adapters.TagRecyclerViewAdapter
//import com.example.movieapp.models.Category
//import com.example.movieapp.models.Post
//import com.example.movieapp.services.api.PostService
//import com.example.movieapp.services.api.ServiceBuilder
//import com.example.movieapp.services.SharedPrefHelper
//import kotlinx.android.synthetic.main.fragment_discover.*
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.models.Movie
import com.example.movieapp.service.MovieService
import com.example.movieapp.service.ServiceBuilderRe
import com.example.movieapp.ui.adapters.HomeRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_discover.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
//search fragment
class DiscoverFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    private var search: String = " "
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        discover_recyclerview.setHasFixedSize(true)
        with(discover_searchview) {
            setDismissOnTouchOutside(true)
            //set user query
            setOnSearchConfirmedListener { searchView, query ->
                search = if (query == "") {
                    " "
                } else {
                    query
                }
                searchView.collapse(true)
                searchMovies()
            }

        }

        discover_swipe_refresh.setOnRefreshListener {
            search = ""
            discover_searchview.inputQuery = ""
            //call backend service to get the result
            searchMovies()
        }
        discover_swipe_refresh.isRefreshing =false
    }

    private fun searchMovies() {
        val service: Call<List<Movie>> =
            ServiceBuilderRe.buildService(MovieService::class.java).getMoviesBySearch(search);
        service.enqueue(object : Callback<List<Movie>> {
            override fun onResponse(call: Call<List<Movie>>, response: Response<List<Movie>>) {
                if (response.isSuccessful) {
                    val listMovie: List<Movie> = response.body()!!
                    discover_recyclerview.layoutManager = LinearLayoutManager(activity)
                    val adapter = HomeRecyclerViewAdapter(
                        listMovie,
                        activity!!.applicationContext,
                        activity!!.supportFragmentManager
                    )
                    discover_recyclerview.adapter = adapter

                } else {
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Movie>>, t: Throwable) {
                Toast.makeText(activity, "Error : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        fun newInstance(): DiscoverFragment {
            val fragment = DiscoverFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}