package org.ajar.scythemobile.display

import android.graphics.Canvas
import org.ajar.scythemobile.model.map.MapFeature
import org.ajar.scythemobile.model.map.MapHex

interface MapHexPainter<C> {
    val featurePainters: Map<MapFeature, FeaturePainter>
    fun paintHex(canvas: C, mapHex: MapHex)
}

class CanvasMapHexPainter(override val featurePainters: Map<MapFeature, FeaturePainter>) : MapHexPainter<Canvas> {
    override fun paintHex(canvas: Canvas, mapHex: MapHex) {

    }
}

interface FeaturePainter {
    fun paintFeature(canvas: Canvas, mapHex: MapHex)
}