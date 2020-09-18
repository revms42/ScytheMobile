package org.ajar.scythemobile.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.view.View
import androidx.collection.SparseArrayCompat
import androidx.collection.set
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.map.*

class MapView(context: Context) : View(context) {

    private val map: List<MapHex> by lazy { GameMap.currentMap.mapHexes }
    private val widthCount: Int by lazy { computeWidestPoint() }
    private val heightCount: Int by lazy { computeTallestPoint() }

    private var measuredTileSize: Int = 0
    private val rect: Rect by lazy { Rect(0, 0, measuredTileSize, measuredTileSize) }

    private val _displayMapping = SparseArrayCompat<PointF>()
    private val displayMapping: SparseArrayCompat<PointF>
            get() {
                if(_displayMapping.isEmpty()) {
                    GameMap.currentMap.findAllMatching { TerrainFeature.valueOf(it!!.terrain) == TerrainFeature.FACTORY }?.first()?.also {
                        displayMapping[it.loc] = PointF(0.0F, 0.0F)
                        mapNeigbors(it)
                    }
                }
                return _displayMapping
            }

    private fun mapNeigbors(mapHex: MapHex) {
        val grab: (Int) -> MapHex? = fun(index: Int): MapHex? {
            return if(index != -1 && _displayMapping[mapHex.loc] == null) GameMap.currentMap.findHexAtIndex(index) else null
        }
        val currentPoint = _displayMapping[mapHex.loc]
        with(mapHex.data.neighbors) {
            if(_displayMapping[this.w] == null) grab(this.w)?.also { _displayMapping[this.w] = PointF(currentPoint!!.x - 1.0f, currentPoint.y) ; mapNeigbors(it) }
            if(_displayMapping[this.nw] == null) grab(this.nw)?.also { _displayMapping[this.nw] = PointF(currentPoint!!.x - 0.5f, currentPoint.y - 0.75f) ; mapNeigbors(it) }
            if(_displayMapping[this.ne] == null) grab(this.ne)?.also { _displayMapping[this.ne] = PointF(currentPoint!!.x + 0.5f, currentPoint.y - 0.75f) ; mapNeigbors(it) }
            if(_displayMapping[this.e] == null) grab(this.e)?.also { _displayMapping[this.e] = PointF(currentPoint!!.x + 1.0f, currentPoint.y) ; mapNeigbors(it) }
            if(_displayMapping[this.se] == null) grab(this.se)?.also { _displayMapping[this.se] = PointF(currentPoint!!.x + 0.5f, currentPoint.y + 0.75f) ; mapNeigbors(it) }
            if(_displayMapping[this.sw] == null) grab(this.sw)?.also { _displayMapping[this.sw] = PointF(currentPoint!!.x - 0.5f, currentPoint.y + 0.75f) ; mapNeigbors(it) }
        }
    }

    private fun computeWidestPoint(): Int {
        var widest = 0
        map.forEach { hex ->
            if(hex.data.neighbors.e == -1) {
                var count = 0
                var track = hex
                while(track.data.neighbors.w != -1) {
                    count++
                    track = GameMap.currentMap.findHexAtIndex(track.data.neighbors.w)!!
                }
                if(count > widest) widest = count
            }
        }
        return widest
    }

    private fun computeTallestPoint(): Int {
        var tallest = 0
        map.forEach { hex ->
            if(hex.data.neighbors.ne == -1 && hex.data.neighbors.nw == -1) {
                val count = crawlOneDown(hex.loc)

                if(count > tallest) {
                    tallest = count
                }
            }
        }
        return tallest
    }

    private fun crawlOneDown(id: Int): Int {
        val hex = GameMap.currentMap.findHexAtIndex(id)
        val se = 1 + if(hex?.data?.neighbors?.se?: -1 != -1) {
            crawlOneDown(hex!!.data.neighbors.se)
        } else 0
        val sw = 1 + if(hex?.data?.neighbors?.sw?: -1 != -1) {
            1 + crawlOneDown(hex!!.data.neighbors.sw)
        } else 0

        return if(se > sw) se else sw
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val rawTileWidth = widthMeasureSpec / widthCount
        val rawTileHeight = heightMeasureSpec / heightCount

        measuredTileSize = if(rawTileHeight < rawTileWidth) rawTileHeight else rawTileWidth

        this.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    private fun setRectBounds(mapHex: MapHex) {
        val point = displayMapping[mapHex.loc]

        val x = ((widthCount / 2) * point!!.x) * measuredTileSize
        val y = ((heightCount / 2) * point!!.y) * measuredTileSize

        rect.set(x.toInt(), y.toInt(), measuredTileSize, measuredTileSize)
    }

    override fun onDraw(canvas: Canvas) {
        map.forEach { hex ->
            setRectBounds(hex)
            drawBaseTerrain(hex, canvas)
            drawRivers(hex, canvas)
            drawOtherFeatures(hex, canvas)
        }
        //TODO: Draw units, resources if required.
    }

    private fun drawDisplayable(id: Int, canvas: Canvas) {
        val drawable = resources.getDrawable(id, null)?.mutate()

        drawable?.also {
            it.bounds = rect
            it.draw(canvas)
        }
    }

    private fun drawRivers(hex: MapHex, canvas: Canvas) {
        with(hex.data.rivers) {
            arrayOf(
                    if(this.riverE) RiverFeature(Direction.E).displayable else null,
                    if(this.riverNE) RiverFeature(Direction.NE).displayable else null,
                    if(this.riverNW) RiverFeature(Direction.NW).displayable else null,
                    if(this.riverW) RiverFeature(Direction.W).displayable else null,
                    if(this.riverSW) RiverFeature(Direction.SW).displayable else null,
                    if(this.riverSE) RiverFeature(Direction.SE).displayable else null
            ).forEach { displayable ->
                displayable?.also { drawDisplayable(it, canvas) }
            }
        }
    }

    private fun drawBaseTerrain(hex: MapHex, canvas: Canvas) {
        hex.terrain.displayable?.also { id ->
            drawDisplayable(id, canvas)
        }
    }

    private fun drawOtherFeatures(hex: MapHex, canvas: Canvas) {
        when {
            hex.data.tunnel -> drawDisplayable(SpecialFeature.TUNNEL.displayable!!, canvas)
            hex.data.encounter != null || hex.data.encounter != -1 -> drawDisplayable(SpecialFeature.ENCOUNTER.displayable!!, canvas)
            hex.terrain ==  TerrainFeature.FACTORY -> drawDisplayable(R.drawable.ic_factory, canvas)
        }
    }
}