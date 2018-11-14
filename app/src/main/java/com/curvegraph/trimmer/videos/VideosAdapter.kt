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
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.curvegraph.trimmer.R
import com.curvegraph.trimmer.utils.CommonMethod.getResizedBitmap
import kotlinx.android.synthetic.main.adapter_videos.view.*
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import java.lang.ref.WeakReference
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

        val retriever = FFmpegMediaMetadataRetriever()
//use one of overloaded setDataSource() functions to set your data source
        if (File(items[position]).exists()) {
            try {
                retriever.setDataSource(context, Uri.fromFile(File(items[position])))
                val time: String = if (retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION) != null) retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION) else "0"
                UpdateCatalogTask(WeakReference(context), WeakReference(holder.icon), items[position], 1000).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                holder.subTitle.text = millisToString(time.toLong())
            } catch (ignore: Exception) {
            } finally {
                retriever.release()
                println("media External storage: ${items[position]}")
            }

            //    Glide.with(context).load(retriever.getFrameAtTime(1, FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC)).thumbnail(0.2f).apply(RequestOptions().centerCrop()).into(holder.icon)
        }

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

    private class UpdateCatalogTask(val context: WeakReference<Context>, val view: WeakReference<ImageView>, val stringUrl: String, val microsecond: Long) :
            AsyncTask<Uri, Void, Bitmap?>() {

        override fun doInBackground(vararg params: Uri): Bitmap? {
            //  val retriever = MediaMetadataRetriever()
            lateinit var bitmap: Bitmap
            return try {
                //   val f = File(stringUrl)
                //      retriever.setDataSource(f.absolutePath)
                val med = MediaMetadataRetriever()
                med.setDataSource(stringUrl)

                bitmap = getResizedBitmap(med.getFrameAtTime(microsecond, MediaMetadataRetriever.OPTION_CLOSEST), view.get()!!.width)
                //   bitmap = getResizedBitmap( retriever.getFrameAtTime(microsecond, MediaMetadataRetriever.OPTION_CLOSEST), view.get()!!.width)
                bitmap
            } catch (e: Exception) {
                if (context.get() != null && context.get()!!.applicationContext != null) {
                    BitmapFactory.decodeResource(context.get()!!.applicationContext.resources,
                            R.drawable.circle)
                } else
                    null
            }
        }



        override fun onPostExecute(mediaItem: Bitmap?) {
            super.onPostExecute(mediaItem)
            if (view.get() != null && context.get() != null && mediaItem != null)
                Glide.with(context.get()!!.applicationContext).load(mediaItem).thumbnail(0.2f).apply(RequestOptions().centerCrop()).into(view.get()!!)
        }
    }

}

