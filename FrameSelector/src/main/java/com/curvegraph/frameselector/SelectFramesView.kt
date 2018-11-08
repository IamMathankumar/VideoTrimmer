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
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.AppCompatSeekBar
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import kotlinx.android.synthetic.main.select_frame_view.view.*
import wseemann.media.FFmpegMediaMetadataRetriever


class SelectFramesView : ConstraintLayout, FramesAdapter.ItemClickListener {


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    override fun onItemClickListener(position: Int) {

    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.select_frame_view, this)

    }

    fun setLocalUrl(localUrl : String){
        // val str =  "/storage/emulated/0/Video/96 Songs _ The Life of Ram Song Lyrical _ Vijay Sethupathi, Trisha _ Govind Vasa_Full-HD.mp4"
        frameList.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        frameList.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        // do whatever
                        callAdapter(localUrl)

                    }
                })
    }

    private fun callAdapter(localUrl : String){
        val med = FFmpegMediaMetadataRetriever()
        med.setDataSource(localUrl)
        val mVideoDuration = med.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION)
        val mTimeInMilliseconds = java.lang.Long.parseLong(mVideoDuration) *1000
        val adapter = FramesAdapter(frameList.height, localUrl,/*(frameList.width/frameList.height)+1*/ 20, context, this,mTimeInMilliseconds/10)
        frameList.adapter = adapter
        frameList.addOnItemTouchListener(RecyclerViewScrollDisable())
    }

    fun getMediaProgressView(): AppCompatSeekBar {
        return viewParent.getMediaProgressView()
    }

    inner class RecyclerViewScrollDisable : RecyclerView.OnItemTouchListener {

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return true
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }
    }
}
