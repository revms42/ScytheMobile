package org.ajar.scythemobile.ui.view

import android.graphics.ColorMatrix

object Desaturate {

    fun makeMatrix(): ColorMatrix {
        val array = floatArrayOf(
                1.0f, 0.5f, 0.5f, 0.0f, 0f,
                0.5f, 1.0f, 0.5f, 0.0f, 0f,
                0.5f, 0.5f, 1.0f, 0.0f, 0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0f
        )
        return ColorMatrix().let { it.setSaturation(0.4f) ; it.postConcat(ColorMatrix(array)) ; it }
    }
}