package com.example.movieapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.models.Review

// use this class to display movie reviews
class ReviewRecyclerViewAdapter(
    private val mValues: List<Review>,
    private val mContext: Context
): RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewRecyclerViewAdapter.ViewHolder {
        val inflater = LayoutInflater.from(mContext).inflate(R.layout.review_item, parent, false)
        return ViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return mValues.size!!
    }

    override fun onBindViewHolder(holder: ReviewRecyclerViewAdapter.ViewHolder, position: Int) {
        val item = mValues[position]
        holder.username.text = "'"+item?.author+"' said:"
        holder.content.text =item?.content

    }

    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val content: TextView = itemView.findViewById(R.id.content)
    }

}