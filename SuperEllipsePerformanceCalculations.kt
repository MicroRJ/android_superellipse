package com.devrj.helium.performance

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.devrj.helium.log
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


object SuperEllipsePerformanceCalculations {

//    var superEllipseBitmap: Bitmap? = null


    private val bitmapForSizes = ArrayList<Pair<Bitmap, Int>>()

    fun getBitmap(w: Int, h: Int, p: Int, paint: Paint): Bitmap {

        bitmapForSizes.forEach {
            /**
             * Check if there are other bitmaps with the same specifications, to avoid creating
             * and recalculating, this increases performance drastically.
             */
            if (it.first.width == w && it.first.height == h && it.second == p) {
                return it.first
            }
        }

        /**
         * If no Bitmaps were found create a new one, and store for other views
         * that might use it
         */
        val newB = getSquircleBitmapBackground(w, h, p, paint)
        bitmapForSizes.add(Pair(newB, p))

        log("New bitmap added Total: ${bitmapForSizes.size}")
        return newB

    }


    private fun getSquircleBitmapBackground(w: Int, h: Int, p: Int, paint: Paint): Bitmap {
        val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        c.translate(w / 2f, h / 2f)
        c.drawPath(getPath((w / 2) - p, (h / 2) - p), paint)
        return b
    }


    //todo fix path not closing
    private fun getPath(radX: Int, radY: Int): Path {
        val corners = 0.6
        var l = 0.0

        var angle: Double

        val path = Path()

        for (i in 0..360) {

            angle = Math.toRadians(l)

            val x = getX(radX, angle, corners)
            val y = getY(radY, angle, corners)

            path.moveTo(x, y)

            l++

            angle = Math.toRadians(l)

            val x2 = getX(radX, angle, corners)
            val y2 = getY(radY, angle, corners)

            path.lineTo(x2, y2)

        }

        path.close()

        return path

    }

    private fun getX(radX: Int, angle: Double, corners: Double) =
        (Math.pow(abs(cos(angle)), corners) * radX * sgn(cos(angle))).toFloat()

    private fun getY(radY: Int, angle: Double, corners: Double) =
        (Math.pow(abs(sin(angle)), corners) * radY * sgn(sin(angle))).toFloat()

    private fun sgn(value: Double) = if (value > 0.0) 1.0 else if (value < 0.0) -1.0 else 0.0

    /**
     * I suggest you call this method on the activity's on destroy event
     */
    fun release() {
        bitmapForSizes.forEach { it.first.recycle() }
        bitmapForSizes.clear()
    }

}
