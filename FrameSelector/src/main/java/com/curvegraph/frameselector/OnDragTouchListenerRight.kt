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

import android.view.MotionEvent
import android.view.View

class OnDragTouchListenerRight @JvmOverloads constructor(view: View, parent: View = view.parent as View, onDragActionListener: OnDragActionListener? = null) : View.OnTouchListener {

    private var mView: View? = null
    private var mParent: View? = null
    private var isDragging: Boolean = false
    private var isInitialized = false

    private var width: Int = 0
    private var xWhenAttached: Float = 0.toFloat()
    private var maxLeft: Float = 0.toFloat()
    private var maxRight: Float = 0.toFloat()
    private var dX: Float = 0.toFloat()

    private var height: Int = 0
    private var yWhenAttached: Float = 0.toFloat()
    private var maxTop: Float = 0.toFloat()
    private var maxBottom: Float = 0.toFloat()
    private var dY: Float = 0.toFloat()

    private var mOnDragActionListener: OnDragActionListener? = null

    /**
     * Callback used to indicate when the drag is finished
     */
    interface OnDragActionListener {
        /**
         * Called when drag event is started
         *
         * @param view The view dragged
         */
        fun onDragStart(view: View?)

        /**
         * Called when drag event is completed
         *
         * @param view The view dragged
         */
        fun onDragEnd(view: View?)

        fun onDragging(view: View?)
    }

    constructor(view: View, onDragActionListener: OnDragActionListener) : this(view, view.parent as View, onDragActionListener) {}

    init {
        initListener(view, parent)
        setOnDragActionListener(onDragActionListener)
    }

    fun setOnDragActionListener(onDragActionListener: OnDragActionListener?) {
        mOnDragActionListener = onDragActionListener
    }

    fun initListener(view: View, parent: View) {
        mView = view
        mParent = parent
        isDragging = false
        isInitialized = false
    }

    fun updateBounds() {
        updateViewBounds()
        updateParentBounds()
        isInitialized = true
    }

    fun updateViewBounds() {
        width = mView!!.width
        xWhenAttached = mView!!.x
        dX = 0f

        height = mView!!.height
        yWhenAttached = mView!!.y
        dY = 0f
    }

    fun updateParentBounds() {
        maxLeft = mParent!!.x
        maxRight = maxLeft + mParent!!.width

        maxTop = 0f
        maxBottom = maxTop + mParent!!.height
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (isDragging) {
            if (mOnDragActionListener != null) {
                mOnDragActionListener!!.onDragging(mView)
            }
            val bounds = FloatArray(4)
            // LEFT
            bounds[0] = event.rawX + dX
            if (bounds[0] < maxLeft) {
                bounds[0] = maxLeft
            }
            // RIGHT
            bounds[2] = bounds[0] + width
            if (bounds[2] > maxRight) {
                bounds[2] = maxRight
                bounds[0] = bounds[2] - width
            }
            // TOP
            bounds[1] = event.rawY + dY
            if (bounds[1] < maxTop) {
                bounds[1] = maxTop
            }
            // BOTTOM
            bounds[3] = bounds[1] + height
            if (bounds[3] > maxBottom) {
                bounds[3] = maxBottom
                bounds[1] = bounds[3] - height
            }

            when (event.action) {
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> onDragFinish()
                MotionEvent.ACTION_MOVE -> mView!!.animate().x(bounds[0]).y(bounds[1]).setDuration(0).start()
            }
            return true
        } else {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isDragging = true
                    if (!isInitialized) {
                        updateBounds()
                    }
                    dX = v.x - event.rawX
                    dY = v.y - event.rawY
                    if (mOnDragActionListener != null) {
                        mOnDragActionListener!!.onDragStart(mView)
                    }
                    return true
                }
            }
        }
        return false
    }

    private fun onDragFinish() {
        if (mOnDragActionListener != null) {
            mOnDragActionListener!!.onDragEnd(mView)
        }

        dX = 0f
        dY = 0f
        isDragging = false
    }
}