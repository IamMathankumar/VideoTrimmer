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
package com.curvegraph.deocut.videos

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions
import com.curvegraph.deocut.GlideApp
import com.curvegraph.deocut.R
import com.curvegraph.deocut.VideoIconUtil
import com.curvegraph.frameselector.ExecutorCallBack
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.adapter_videos.view.*
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import java.util.concurrent.TimeUnit


class VideosAdapter(private var items: List<String>, private val context: Activity, private val recyclerView: RecyclerView, private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<VideosAdapter.ViewHolder>(), ExecutorCallBack<Bitmap, Int, Int> {

    override fun onExecutorCallback(bitmap: Bitmap?, itemPosition: Int?, framePosition: Int?) {
        recyclerView.post {
            // set the downloaded image here
            addImage(recyclerView, itemPosition!!, bitmap!!)
        }

    }

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

    private fun addImage(recyclerView: RecyclerView, position: Int, bitmap: Bitmap) {
        if (position < items.size && null != recyclerView.findViewHolderForAdapterPosition(position)) {
            val holder = recyclerView.findViewHolderForAdapterPosition(position) as RecyclerView.ViewHolder
            val icon = holder.itemView.findViewById<ImageView>(R.id.icon)
            GlideApp.with(icon.context.applicationContext).load(bitmap).thumbnail(0.2f).centerCrop().into(icon)
            icon.tag = bitmap
        }
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

        val retriever = FFmpegMediaMetadataRetriever()
//use one of overloaded setDataSource() functions to set your data source
        if (File(items[position]).exists()) {
            try {
                retriever.setDataSource(context, Uri.fromFile(File(items[position])))
                val time: String = if (retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION) != null) retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION) else "0"
                holder.subTitle.text = millisToString(time.toLong())
                if(holder.icon.tag == null)
                VideoIconUtil().backgroundShootVideoThumb(context, Uri.fromFile(File(items[position])), position, context.resources.getDimension(R.dimen.videosIconSizeWidth).toInt(), this)
                else
                    GlideApp.with(context.applicationContext).load(holder.icon.tag as Bitmap).thumbnail(0.2f).centerCrop().into(holder.icon)

            } catch (ignore: Exception) {
            } finally {
                retriever.release()
                println("media External storage: ${items[position]}")
            }


        }
        holder.itemView.tag = holder

    }

    // Inflates the item views

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val title = view.title!!
        val icon = view.icon!!
        val subTitle = view.subTitle!!
    }


    private fun millisToString(millis: Long): String {
        return (String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1))).replace("00:00", "00")
    }


}

