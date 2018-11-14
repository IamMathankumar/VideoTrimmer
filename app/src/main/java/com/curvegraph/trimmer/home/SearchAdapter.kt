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
package com.curvegraph.trimmer.home

import android.annotation.SuppressLint
import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.curvegraph.trimmer.R
import kotlinx.android.synthetic.main.adapter_folder.view.*


class SearchAdapter(private val context: Activity, private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var items: List<String> = ArrayList()

    fun addAllItems(items: List<String>){
        this.items = items
        notifyDataSetChanged()
    }
    interface ItemClickListener {
        fun onSearchItemClickListener(video: String)
    }


    fun onClick(view: View?) {
        val viewHolder = view!!.tag as ViewHolder

            itemClickListener.onSearchItemClickListener(items[viewHolder.adapterPosition])
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_search_result, parent, false)
        view.setOnClickListener { onClick(it) }
        return ViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         holder.title.text = items[position].substring(items[position].lastIndexOf("/") + 1)
        holder.itemView.tag = holder
    }

    // Inflates the item views

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val title = view.title!!

    }


}

