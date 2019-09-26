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


object SuperellipseLogic {

    private const val DEF_CORNERS_CONSTANT = 0.6

    private val cachedBitmaps = Hashtable<String, Bitmap>()

    private val canvas = Canvas()

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
     * @param strokeColor color of the stroke (only applies if Paint.style == STROKE || STROKE_AND_FILL)
     * @param padding bitmap padding (this will not off-center the bitmap).
     * @param paint paint to use for drawing the bitmap.
     * If canvas not centered, center it.
     * Create drawing bitmap.
     * Draw squircle path on bitmap.
     * @return squircleBitmap.
     */
    private fun getSquircleBitmapBackground(
        w: Int,
        h: Int,
        padding: Int,
        paint: Paint,
        strokeColor: Int
    ): Bitmap {
        val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        this.canvas.setBitmap(b)
        this.canvas.translate(w / 2f, h / 2f)

        recalculatePath((w / 2) - padding, (h / 2) - padding)


        val ogStl = paint.style
        val ogClr = paint.color

        if (ogStl == FILL || ogStl == FILL_AND_STROKE) {
            canvas.drawPath(path, paint)
        }
        if (ogStl == STROKE || ogStl == FILL_AND_STROKE) {

            paint.color = strokeColor
            paint.style = STROKE

            canvas.drawPath(path, paint)
            //Reassign values to not interfere with objects og values
            paint.style = ogStl
            paint.color = ogClr
        }

        return b
    }


    private fun getSuperEllipsePath(
        radX: Int,
        radY: Int,
        corners: Double = DEF_CORNERS_CONSTANT
    ): Path {
        val newPath = Path()
        addSuperEllipseToPath(newPath, radX, radY, corners)
        return newPath
    }

    private fun addSuperEllipseToPath(p: Path, radX: Int, radY: Int, corners: Double) {
        var l = 0.0
        var angle: Double
        for (i in 0 until 360) {
            angle = Math.toRadians(l)
            val x = getX(radX, angle, corners)
            val y = getY(radY, angle, corners)
            if (i == 0) {
                p.moveTo(x, y)
            }
            l++
            angle = Math.toRadians(l)
            val x2 = getX(radX, angle, corners)
            val y2 = getY(radY, angle, corners)
            p.lineTo(x2, y2)
        }
        p.close()
    }


    private fun recalculatePath(radX: Int, radY: Int, corners: Double = DEF_CORNERS_CONSTANT) {
        path.reset()
        addSuperEllipseToPath(path, radX, radY, corners)
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
    }

}
