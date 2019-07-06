package com.devrj.helium.custom


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import com.devrj.helium.R
import com.devrj.helium.log
import com.devrj.helium.performance.SuperEllipsePerformanceCalculations


open class SuperEllipseImageView : ImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val paint = Paint()

    private var shapePadding = 0
    private var w = 0
    private var h = 0

    private var squircleBitmap: Bitmap? = null


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.w = w
        this.h = h

        /**
         * Request a new squircle bitmap, this is a smart method and it will only return a new Bitmap
         * instance if it cannot find a previously created Bitmap with the same size (width and height),
         * this will increase performance
         */
        squircleBitmap = SuperEllipsePerformanceCalculations.getBitmap(this.h, this.w, shapePadding, paint)

        postInvalidate()
    }


    init {

        /**
         * You can change this to any color you want
         */
        val typedValue = TypedValue()
        val theme = context!!.theme
        theme.resolveAttribute(R.attr.colorAccentTheme, typedValue, true)


        paint.color = typedValue.data
        paint.strokeWidth = 6f
        /**
         * Here's the problem, the shape is not being filled, only the stroke
         * is drawn, I have a few theories of what's happening.
         */
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.isAntiAlias = true

        /**
         * I suggest the shape padding be no less that the stroke width to prevent
         * the shape borders/outskirts from going out of bounds
         */
        shapePadding = paint.strokeWidth.toInt()


    }


    override fun onDraw(canvas: Canvas?) {
        if (squircleBitmap == null) {
            return
        }
        /**
         * Draw a previously instantiated Bitmap instead of a path, this will increase performance drastically
         */
        canvas?.drawBitmap(squircleBitmap!!, 0f, 0f, null)
        super.onDraw(canvas)

    }
}
