/*
 *
 *  Copyright 2018 Mathankumar K. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.curvegraph.trimmer.videos

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.curvegraph.trimmer.R
import kotlinx.android.synthetic.main.adapter_videos.view.*
import java.io.File
import java.util.concurrent.TimeUnit


class VideosAdapter(private var items: List<String>, private val context: Activity, private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<VideosAdapter.ViewHolder>() {


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
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_videos, parent, false)
        view.setOnClickListener { onClick(it) }
        return ViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = items[position].substring(items[position].lastIndexOf("/") + 1)
        holder.itemView.tag = holder

        val retriever = MediaMetadataRetriever()
//use one of overloaded setDataSource() functions to set your data source
        if(File(items[position]).exists()) {
            retriever.setDataSource(context, Uri.fromFile(File(items[position])))
            val time: String = if (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!= null) retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) else "0"
            //  UpdateThump(WeakReference(holder.icon), WeakReference(context)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, items[position])
            holder.subTitle.text = millisToString(time.toLong())
            Glide.with(context).load(Uri.fromFile(File(items[position]))).thumbnail(0.2f).apply(RequestOptions().centerCrop()).into(holder.icon)
        }
        retriever.release()
        println("media External storage: ${items[position]}")
    }

    // Inflates the item views

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val title = view.title!!
        val icon = view.icon!!
        val subTitle = view.subTitle!!
    }



   private fun millisToString(millis : Long) : String{
      return (String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1))).replace("00:00","00")
    }

}

