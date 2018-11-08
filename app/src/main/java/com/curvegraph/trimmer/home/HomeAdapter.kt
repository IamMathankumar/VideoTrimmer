package com.curvegraph.trimmer.home

import android.annotation.SuppressLint
import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.curvegraph.trimmer.R
import kotlinx.android.synthetic.main.adapter_folder.view.*


class HomeAdapter(private var items: List<ModelFolder>, private val context: Activity, private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {


    interface ItemClickListener {
        fun onItemClickListener(position: Int)
    }


    fun onClick(view: View?) {
        val viewHolder = view!!.tag as ViewHolder

            itemClickListener.onItemClickListener(viewHolder.adapterPosition)
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_folder, parent, false)
        view.setOnClickListener { onClick(it) }
        return ViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = items[position].folderNameDisplay
        holder.itemView.tag = holder
        holder.subTitle.text = "${items[position].videosCount} videos"
    }

    // Inflates the item views

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val title = view.title!!

        val subTitle = view.subTitle!!
    }


}

