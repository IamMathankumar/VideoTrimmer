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

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.adapter_frame.view.*


class FramesAdapter(private val viewSize: Int, private var itemTotalCounts: Int, private val context: Context, private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<FramesAdapter.ViewHolder>() {

    private var duration = 0L

    interface ItemClickListener {
        fun onItemClickListener(position: Int)
    }

    fun onClick(view: View?) {
        val viewHolder = view!!.tag as ViewHolder
        itemClickListener.onItemClickListener(viewHolder.adapterPosition)
    }

    fun addImage(recyclerView: RecyclerView, position: Int, bitmap: Bitmap) {
        if (position < itemTotalCounts && null != recyclerView.findViewHolderForAdapterPosition(position)) {
            val holder = recyclerView.findViewHolderForAdapterPosition(position) as RecyclerView.ViewHolder
            val imageView = holder.itemView.findViewById<ImageView>(R.id.frame)
            Glide.with(context.applicationContext).load(bitmap).thumbnail(0.2f).apply(RequestOptions().centerCrop()).into(imageView)
        }
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


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = holder
//        Glide.with(context.applicationContext).load(localUrl).thumbnail(0.2f).apply(RequestOptions().centerCrop()).into(holder.frame)

    }

    // Inflates the item views

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val frame = view.frame!!
    }

}

