package com.example.movieapp.ui.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.movieapp.R
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import androidx.fragment.app.FragmentManager
import com.example.movieapp.models.Movie
import com.example.movieapp.ui.activities.DetailActivity

//display movies
class HomeRecyclerViewAdapter(
        private val mValues: List<Movie>,
        private val mContext: Context,
        private val mFragmentManager: FragmentManager
) :
    RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>() {
    //user The movie db api to get poster url
    private val img_api_URL = "https://image.tmdb.org/t/p/w200"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.movie_collection_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Movie = mValues[position]
        val requestOptions = RequestOptions().transform(CenterCrop(), RoundedCorners(16))
        if (item != null) {
            //load img to image view
            Glide.with(holder.itemView.context)
                .load("${img_api_URL}${item.poster_path}")
                .apply(requestOptions)
                .into(holder.img)
        }

        if (item != null) {
            //moive name
            holder.tvTitle.text = item.title
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, DetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("movie", item)
            mContext.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        var img: ImageView = itemView.findViewById(R.id.imageview)
        var tvTitle: TextView = itemView.findViewById(R.id.titleview)
    }
}
