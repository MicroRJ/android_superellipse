package com.microdevrj.superellipse.performance

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style.*
import android.graphics.Path
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin


object SuperEllipsePerformanceCalculations {

    private const val DEF_CORNERS_CONSTANT = 0.6

    private val cachedBitmaps = Hashtable<String, Bitmap>()

    private var canvas: Canvas? = Canvas()

    private var path = Path()

    /**
     * Notice that a cached bitmap will be returned if the width,
     * height and color of the arguments matches any of the values
     * of any other cached bitmap.
     * These values serve as a key for the hashtable.
     *
     * @return if(cachedBitmap) cachedBitmap else freshBitmap.
     */
    fun getCachedBitmap(w: Int, h: Int, padding: Int, paint: Paint, sC: Int): Bitmap {
        val k = "$w$h${paint.color}"
        if (cachedBitmaps[k] == null)
            cachedBitmaps[k] = getSquircleBitmapBackground(w, h, padding, paint, sC)
        return cachedBitmaps[k]!!
    }

    /**
     * Use this method if you don't want to cache bitmaps or receive previously cached bitmaps.
     * @return freshBitmap.
     */
    fun getBitmap(w: Int, h: Int, padding: Int, paint: Paint, sC: Int) =
        getSquircleBitmapBackground(w, h, padding, paint, sC)

    /**
     * @param w width of the bitmap.
     * @param h height of the bitmap.
     * @param strkClr color of the stroke (only applies if Paint.style == STROKE || STROKE_AND_FILL)
     * @param pddng bitmap pddng (this will not off-center the bitmap).
     * @param pnt pnt to use for drawing the bitmap.
     * If canvas not centered, center it.
     * Create drawing bitmap.
     * Draw squircle path on bitmap.
     * @return squircleBitmap.
     */
    private fun getSquircleBitmapBackground(
        w: Int,
        h: Int,
        pddng: Int,
        pnt: Paint,
        strkClr: Int
    ): Bitmap {
        val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        this.canvas!!.setBitmap(b)
        this.canvas!!.translate(w / 2f, h / 2f)

        recalculatePath((w / 2) - pddng, (h / 2) - pddng)


        val ogStl = pnt.style
        val ogClr = pnt.color

        if (ogStl == FILL || ogStl == FILL_AND_STROKE) {
            canvas!!.drawPath(path, pnt)
        }
        if (ogStl == STROKE || ogStl == FILL_AND_STROKE) {

            pnt.color = strkClr
            pnt.style = STROKE

            canvas!!.drawPath(path, pnt)
            //Reassign values to not interfere with objects og values
            pnt.style = ogStl
            pnt.color = ogClr
        }

        return b
    }


    private fun recalculatePath(radX: Int, radY: Int) {
        var l = 0.0
        var angle: Double
        path.reset()
        for (i in 0 until 360) {
            angle = Math.toRadians(l)
            val x = getX(radX, angle, DEF_CORNERS_CONSTANT)
            val y = getY(radY, angle, DEF_CORNERS_CONSTANT)
            if (i == 0) {
                path.moveTo(x, y)
            }
            l++
            angle = Math.toRadians(l)
            val x2 = getX(radX, angle, DEF_CORNERS_CONSTANT)
            val y2 = getY(radY, angle, DEF_CORNERS_CONSTANT)
            path.lineTo(x2, y2)
        }
        path.close()
    }

    private fun getX(radX: Int, angle: Double, corners: Double) =
        (abs(cos(angle)).pow(corners) * radX * sgn(
            cos(angle)
        )).toFloat()

    private fun getY(radY: Int, angle: Double, corners: Double) =
        (abs(sin(angle)).pow(corners) * radY * sgn(
            sin(angle)
        )).toFloat()

    private fun sgn(value: Double) = if (value > 0.0) 1.0 else if (value < 0.0) -1.0 else 0.0

    /**
     * I suggest you call this method on the activity's on destroy event
     */
    fun release() {
        cachedBitmaps.forEach {
            it.value.recycle()
        }
        cachedBitmaps.clear()
        canvas = null
    }

}
