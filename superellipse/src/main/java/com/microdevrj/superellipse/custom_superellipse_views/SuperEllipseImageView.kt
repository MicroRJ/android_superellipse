package com.microdevrj.superellipse.custom_superellipse_views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ImageView
import com.microdevrj.superellipse.R.styleable.*
import com.microdevrj.superellipse.performance.SuperEllipsePerformanceCalculations.getCachedBitmap


/**
 * You can follow this same pattern to implement the squircle background on other views and view-groups
 */
open class SuperEllipseImageView : ImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        attrsPassed(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        attrsPassed(attrs)
    }


    private val paint = Paint()

    private var strokeColor: Int = -1

    private var w = 0

    private var h = 0

    private var bitmapPadding = 0

    private var squircleBitmap: Bitmap? = null

    private fun attrsPassed(attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val t = context.obtainStyledAttributes(attrs, SuperEllipseImageView)
        paint.apply {
            color = t.getColor(SuperEllipseImageView_colorFill, Color.WHITE)
            strokeWidth = t.getDimension(SuperEllipseImageView_strokeWidth, 0F)
            style = when (t.getInt(SuperEllipseImageView_paintStyle, 0)) {
                0 -> Paint.Style.FILL
                1 -> Paint.Style.STROKE
                2 -> Paint.Style.FILL_AND_STROKE
                else -> Paint.Style.FILL
            }
        }
        strokeColor = t.getInt(SuperEllipseImageView_colorStroke, -1)
        bitmapPadding = paint.strokeWidth.toInt()
        t.recycle()
    }

    init {
        /*
        Init default values
         */
        paint.strokeWidth = 6f
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        /**
         * I suggest the shape padding be no less that the stroke width to prevent
         * the shape borders/outskirts from going out of bounds, add additional padding if necessary
         */
        bitmapPadding = paint.strokeWidth.toInt()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.w = w
        this.h = h
        /**
         * Request a new squircle bitmap, this is a smart method and it will only return a new Bitmap
         * instance if it cannot find a previously created Bitmap with the same size (width and height),
         * this will increase performance, specially for things like recycler views or list views.
         */

        squircleBitmap = getCachedBitmap(this.h, this.w, bitmapPadding, paint, strokeColor)

        postInvalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        if (squircleBitmap == null) {
            return
        }
        /**
         * Draw a previously instantiated Bitmap instead of a path, this will increase performance drastically.
         * The bitmap is already centered for you.
         */
        canvas?.drawBitmap(squircleBitmap!!, 0f, 0f, null)
        super.onDraw(canvas)

    }
}
