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
package com.curvegraph.frameselector

import android.annotation.SuppressLint
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.adapter_frame.view.*
import java.lang.ref.WeakReference


class FramesAdapterCopy(private val viewSize: Int, private val localUrl: String, private var itemTotalCounts: Int, private val context: Context, private val itemClickListener: ItemClickListener, private var singleLengthToGetBitmap: Long) : RecyclerView.Adapter<FramesAdapterCopy.ViewHolder>() {

    private var duration = 0L

    interface ItemClickListener {
        fun onItemClickListener(position: Int)
    }

    fun onClick(view: View?) {
        val viewHolder = view!!.tag as ViewHolder

        itemClickListener.onItemClickListener(viewHolder.adapterPosition)
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return itemTotalCounts
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_frame, parent, false)
        view.layoutParams.width = viewSize
        view.layoutParams.height = viewSize
        view.setOnClickListener { onClick(it) }
        return ViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = holder
        println("val popopopo")
        duration += singleLengthToGetBitmap
        UpdateCatalogTask(WeakReference(context), WeakReference(holder.frame), localUrl, duration).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        //
    }

    // Inflates the item views

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val frame = view.frame!!
    }


    private class UpdateCatalogTask(val context: WeakReference<Context>, val view: WeakReference<SquareView>, val stringUrl: String, val microsecond: Long) :
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

        /**
         * reduces the size of the image
         * @param image
         * @param maxSize
         * @return
         */
        fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
            var width = image.width
            var height = image.height

            val bitmapRatio = width.toFloat() / height.toFloat()
            if (bitmapRatio > 1) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }
            return Bitmap.createScaledBitmap(image, width, height, true)
        }

        override fun onPostExecute(mediaItem: Bitmap?) {
            super.onPostExecute(mediaItem)
            if (view.get() != null && context.get() != null && mediaItem!= null)
                Glide.with(context.get()!!.applicationContext).load(mediaItem).thumbnail(0.2f).apply(RequestOptions().centerCrop()).into(view.get()!!)
        }
    }


}

