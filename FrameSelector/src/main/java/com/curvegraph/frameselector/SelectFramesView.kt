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
