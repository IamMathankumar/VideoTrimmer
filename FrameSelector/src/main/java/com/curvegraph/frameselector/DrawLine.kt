package com.curvegraph.frameselector

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

class DrawLine : View {

    internal var paint = Paint()
    internal var pointFStart = PointF(0f, 0f)
    internal var pointFEnd = PointF(0f, 0f)

    private fun init() {
        paint.color = Color.BLACK
        paint.strokeWidth = 13f
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
        canvas.drawLine(pointFStart.x, pointFStart.y, pointFEnd.x, pointFEnd.y, paint)

    }

    fun drawLine(startX: Float, startY: Float, stopX: Float, stopY: Float) {
        pointFEnd.x = stopX
        pointFEnd.y = stopY
        pointFStart.x = startX
        pointFStart.y = startY
        invalidate()

    }

}
