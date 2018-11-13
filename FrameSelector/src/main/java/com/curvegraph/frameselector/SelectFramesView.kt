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
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.AppCompatSeekBar
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewTreeObserver
import kotlinx.android.synthetic.main.select_frame_view.view.*


class SelectFramesView : ConstraintLayout, FramesAdapter.ItemClickListener, ExecutorCallBack<Bitmap, Int, Int> {
    override fun onExecutorCallback(bitmap: Bitmap?, itemPosition: Int?, framePosition: Int?) {
        frameList.post {
            // set the downloaded image here
            adapter.addImage(frameList, itemPosition!!, bitmap!!)
        }

    }


    private lateinit var adapter: FramesAdapter

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    fun getSelectView(): SelectView {
        return viewParent
    }

    override fun onItemClickListener(position: Int) {

    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.select_frame_view, this)

    }

    fun setLocalUrl(localUrl: String) {
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

    private fun callAdapter(localUrl: String) {
        val med = MediaMetadataRetriever()
        try {
        med.setDataSource(localUrl)
        val mVideoDuration = med.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        viewParent.setMaxDuration(java.lang.Long.parseLong(mVideoDuration))
        val mTimeInMilliseconds = java.lang.Long.parseLong(mVideoDuration) * 1000
        //  val adapter = FramesAdapter(frameList.height, localUrl, (frameList.width / frameList.height) + 4, context, this, mTimeInMilliseconds / ((frameList.width / frameList.height) + 4))

        val frameDefaultDuration = mTimeInMilliseconds / ((frameList.width / frameList.height) + 4)
        val itemTotalCount = (frameList.width / frameList.height) + 4
        adapter = FramesAdapter(frameList.height, (frameList.width / frameList.height) + 4, context, this)
        frameList.adapter = adapter
        frameList.addOnItemTouchListener(RecyclerViewScrollDisable())
        VideoUtil().backgroundShootVideoThumb(context, Uri.parse(localUrl), frameList.height, itemTotalCount, frameDefaultDuration, this)
        } catch (ignore: Exception) {
        } finally {
            med.release()
        }
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

    fun getMinDuration(): Long {
        return viewParent.getMinDuration()
    }

    fun getMaxDuration(): Long {
        return viewParent.getMaxDuration()
    }
}
