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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

class DrawView : View {

    internal var paint = Paint()
    internal var pointFStart = PointF(0f, 0f)
    internal var pointFEnd = PointF(0f, 0f)

    private fun init() {
        paint.color = Color.BLACK
    }

    fun color(color: Int) {
        paint.color = color
    }


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    public override fun onDraw(canvas: Canvas) {
        canvas.drawRect(pointFStart.x, pointFStart.y, pointFEnd.x, pointFEnd.y, paint)

    }

    fun drawLine(startX: Float, startY: Float, stopX: Float, stopY: Float) {
        pointFEnd.x = stopX
        pointFEnd.y = stopY
        pointFStart.x = startX
        pointFStart.y = startY
        invalidate()

    }

}
