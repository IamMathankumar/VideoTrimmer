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
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatSeekBar
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import kotlinx.android.synthetic.main.select_view.view.*


@Suppress("unused")
class SelectView : ConstraintLayout, OnDragTouchListener.OnDragActionListener, OnDragTouchListenerRight.OnDragActionListener {

    private var startX: Float = 0f
    private var endX: Float = 0f
    private var duration: Long = 0
    private var onMinMaxDurationListener: OnMinMaxDurationListener? = null

    override fun onDragStart(view: View?) {
        /* If user didn't called setMaxDuration function we can get the */
        if (duration == 0L) {
            setMaxDuration(mediaProgress.max.toLong())
        }
        println("duration Android :$duration")
        drawView()
        if (onMinMaxDurationListener != null) {
            onMinMaxDurationListener!!.draggingStarted()
        }

        when (view!!.id) {
            R.id.seekLeft -> {
                seekLeft.bringToFront()
                onMinMaxDurationListener!!.minDuration(((seekLeft.x * duration) / endX).toLong())
            }
            R.id.seekRight -> {
                seekRight.bringToFront()
                onMinMaxDurationListener!!.maxDuration(((seekRight.x * duration) / endX).toLong())
            }
        }
    }

    /*Need to set video total duration to find the minimum and maximum duration
     while dragging seekLeft and seekRight*/
    fun setMaxDuration(duration: Long) {
        this.duration = duration
        seekLeft.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        seekLeft.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        startX = 0f
                        endX = (width-seekRight.width).toFloat()
                        if (onMinMaxDurationListener != null) {
                            onMinMaxDurationListener!!.minMaxDuration(((seekLeft.x * duration) / endX).toLong(), ((seekRight.x * duration) / endX).toLong())
                        }
                    }
                })
    }


    fun setMinMaxListener(listener: OnMinMaxDurationListener) {
        onMinMaxDurationListener = listener

    }

    interface OnMinMaxDurationListener {

        fun minMaxDuration(minDuration: Long, maxDuration: Long)

        fun minDuration(minDuration: Long)

        fun maxDuration(maxDuration: Long)
        fun draggingFinished()
        fun draggingStarted()
    }

    override fun onDragEnd(view: View?) {
        drawView()
        if (onMinMaxDurationListener != null) {
            onMinMaxDurationListener!!.draggingFinished()
        }
    }

    override fun onDragging(view: View?) {
        if (init) {
            pointFStart.x = seekLeftEnd.x
            pointFStart.y = seekLeftEnd.y
            pointFEnd.x = seekRightEnd.x
            pointFEnd.y = seekRightEnd.y
            init = false
        }

        if (onMinMaxDurationListener != null) {
            onMinMaxDurationListener!!.minMaxDuration(((seekLeft.x * duration) / endX).toLong(), ((seekRight.x * duration) / endX).toLong())
        }
        when (view!!.id) {
            R.id.seekLeft -> {
                onMinMaxDurationListener!!.minDuration(((seekLeft.x * duration) / endX).toLong())
            }
            R.id.seekRight -> {
                onMinMaxDurationListener!!.maxDuration(((seekRight.x * duration) / endX).toLong())
            }
        }
        drawPatternView()
    }


    fun getMinDuration(): Long {
        return ((seekLeft.x * duration) / endX).toLong()
    }

    fun getMaxDuration(): Long {
        return ((seekRight.x * duration) / endX).toLong()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.select_view, this)
        // val str =  "/storage/emulated/0/Video/96 Songs _ The Life of Ram Song Lyrical _ Vijay Sethupathi, Trisha _ Govind Vasa_Full-HD.mp4"
        seekLeft.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        seekLeft.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        initialize()

                    }
                })
        mediaProgress.setOnTouchListener { _, _ -> true }
    }

    private fun initialize() {

        // do whatever
        seekLeftDragListener = OnDragTouchListener(seekLeft, seekLeftDragLimit, this)
        seekLeft.setOnTouchListener(seekLeftDragListener)
        seekRightDragListener = OnDragTouchListenerRight(seekRight, seekRightDragLimit, this)
        seekRight.setOnTouchListener(seekRightDragListener)

        seekTop.color(ContextCompat.getColor(context, R.color.colorDefault))
        seekBottom.color(ContextCompat.getColor(context, R.color.colorDefault))
        drawPatternView()
        drawView()
        setBitmapToThumbs(context)
    }

    private var pointFStart = PointF(0f, 0f)
    private var pointFEnd = PointF(0f, 0f)
    private var init = true
    private lateinit var seekLeftDragListener: OnDragTouchListener
    private lateinit var seekRightDragListener: OnDragTouchListenerRight


    private fun drawView() {
        seekTop.drawLine(seekLeft.x + seekLeft.width / 2, seekLeft.y, seekRight.x + seekLeft.width / 2, seekRight.y)
        seekBottom.drawLine(seekLeft.x + seekLeft.width / 2, seekLeft.y + seekLeft.height, seekRight.x + seekLeft.width / 2, seekRight.y + seekLeft.height)
    }

    private fun drawPatternView() {
        //   seekRightDragLimit. drawLine(seekLeftEnd.getX(),seekLeftEnd.getY()-seekLeft.getHeight(),seekLeft.getX(),seekLeft.getY()+seekLeft.getHeight());
        //     seekLeftDragLimit. drawLine(seekLeftEnd.getX(),seekLeftEnd.getY()+seekRight.getHeight(),seekRight.getX(), seekRight.getY());
        //     seekRightDragLimit. drawLine(seekRightEnd.getX(),seekRightEnd.getY()+seekLeft.getHeight(),seekLeft.getX(), seekLeft.getY());

        (seekLeftDragLimit.layoutParams as ConstraintLayout.LayoutParams).width = (seekRight.x + seekRight.width).toInt()
        val lp = seekLeftDragLimit.layoutParams as ConstraintLayout.LayoutParams
        // lp.height = (int)seekRight.getX();
        seekLeftDragLimit.layoutParams = lp

        (seekRightDragLimit.layoutParams as ConstraintLayout.LayoutParams).width = (viewParent.width - seekLeft.x).toInt()
        val lpRight = seekRightDragLimit.layoutParams as ConstraintLayout.LayoutParams
        // lp.height = (int)seekRight.getX();
        seekRightDragLimit.layoutParams = lpRight

        (seekRightEnd.layoutParams as ConstraintLayout.LayoutParams).width = (viewParent.width - seekRight.x - seekRight.width).toInt()
        val lpSeekRightEnd = seekRightEnd.layoutParams as ConstraintLayout.LayoutParams
        // lp.height = (int)seekRight.getX();
        lpSeekRightEnd.marginEnd = (seekLeft.width / 2)
        seekRightEnd.layoutParams = lpSeekRightEnd



        (seekLeftEnd.layoutParams as ConstraintLayout.LayoutParams).width = seekLeft.x.toInt()
        val lpSeekLeftEnd = seekLeftEnd.layoutParams as ConstraintLayout.LayoutParams
        // lp.height = (int)seekRight.getX();
        lpSeekLeftEnd.marginStart = (seekLeft.width / 2)
        seekLeftEnd.layoutParams = lpSeekLeftEnd


        seekLeftDragListener.updateParentBounds()
        seekRightDragListener.updateParentBounds()

        drawView()

        findDistance()

    }

    fun getMediaProgressView(): AppCompatSeekBar {
        return mediaProgress
    }

    private fun findDistance() {
        val startingPoint = seekLeft.x + (seekLeft.width / 2)
        val endingPoint = seekRight.x + (seekRight.width / 2)
        println("Distance : ${endingPoint - startingPoint}")
    }

    private fun setBitmapToThumbs(context: Context) {
        val resID = R.layout.thumb
        val bitmap = layout(resID, context)
        val drawable = BitmapDrawable(resources, bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        mediaProgress.thumb = drawable
        mediaProgress.thumbOffset = 0
    }

    private fun layout(LayoutId: Int, context: Context): Bitmap {
        val child = LayoutInflater.from(context).inflate(LayoutId, null)
        val widthIs = 10
        val height = height
        child.layout(0, 0, widthIs, height)
        child.isDrawingCacheEnabled = true
        return getViewBitmap(child)
    }

    private fun getViewBitmap(view: View): Bitmap {
        //Get the dimensions of the view so we can re-layout the view at its current size
        //and create a bitmap of the same size
        val width = view.width
        val height = view.height

        val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        //Cause the view to re-layout
        view.measure(measuredWidth, measuredHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        //Create a bitmap backed Canvas to draw the view into
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)

        //Now that the view is laid out and we have a canvas, ask the view to draw itself into the canvas
        view.draw(c)

        return b
    }

    fun seekRightWidth( width: Int):Float{
        // Converts 14 dip into its equivalent px
        val dip = width.toFloat()
        val r = resources
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.displayMetrics
        )
    }

}


