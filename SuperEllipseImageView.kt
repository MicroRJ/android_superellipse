package com.devrj.helium.custom


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ImageView
import com.devrj.helium.R.styleable.SuperEllipseImageView
import com.devrj.helium.R.styleable.SuperEllipseImageView_canvasColor
import com.devrj.helium.performance.SuperEllipsePerformanceCalculations.getCachedBitmap


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

    private var shapePadding = 0
    private var w = 0
    private var h = 0

    private var squircleBitmap: Bitmap? = null

    private fun attrsPassed(attrs: AttributeSet?) {
        if (attrs == null) {
            paint.color = Color.WHITE
            return
        }
        val t = context.obtainStyledAttributes(attrs, SuperEllipseImageView)
        paint.color = t.getColor(SuperEllipseImageView_canvasColor, Color.WHITE)
        t.recycle()
    }

    init {
        paint.strokeWidth = 6f
        /**
         * Problem fixed, now the shape will close
         */
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        /**
         * I suggest the shape padding be no less that the stroke width to prevent
         * the shape borders/outskirts from going out of bounds, add additional padding if necessary
         */
        shapePadding = paint.strokeWidth.toInt()

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.w = w
        this.h = h
        /**
         * Request a new squircle bitmap, this is a smart method and it will only return a new Bitmap
         * instance if it cannot find a previously created Bitmap with the same size (width and height),
         * this will increase performance, specially for things like recycler views or list views
         */
        squircleBitmap = getCachedBitmap(this.h, this.w, shapePadding, paint)

        postInvalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        if (squircleBitmap == null) {
            return
        }
        /**
         * Draw a previously instantiated Bitmap instead of a path, this will increase performance drastically.
         * The bitmap is already centered for you
         */
        canvas?.drawBitmap(squircleBitmap!!, 0f, 0f, null)
        super.onDraw(canvas)

    }
}
