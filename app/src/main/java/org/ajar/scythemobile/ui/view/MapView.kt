package org.ajar.scythemobile.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.ScaleGestureDetector
import android.view.View
import org.ajar.scythemobile.model.map.*
import java.lang.RuntimeException
import android.view.MotionEvent
import androidx.collection.*
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType


class MapView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet), ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnGestureListener {

    private val minX: Float by lazy { minimumXPoint() }
    private val minY: Float by lazy { minimumYPoint() }
    private val maxX: Float by lazy { maximumXPoint() }
    private val maxY: Float by lazy { maximumYPoint() }

    private val unitWidth: Float by lazy { maxX - minX }
    private val unitHeight: Float by lazy { maxY - minY }

    private var rawWidth: Int = 0
    private var rawHeight: Int = 0

    private var debugText = false
    private val debugTextPaint by lazy {
        Paint().also {
            it.color = Color.BLACK
            it.textSize = measuredTileSize / 5.0f
        }
    }
    private var unitTextPaint = SparseArrayCompat<Pair<Paint,Paint>>()
    private var unitTextSize: Float? = null
    private var unitTextYOffset: Float? = null

    private fun getTextPaint(playerInstance: PlayerInstance) : Pair<Paint,Paint> {
        if(!unitTextPaint.containsKey(playerInstance.playerId)) {
            unitTextPaint[playerInstance.playerId] = Pair(
                    Paint().also {
                        it.color = resources.getColor(playerInstance.resources.primaryColorRes, null)
                        it.textSize = measuredTileSize / 2.0f
                        it.textAlign = Paint.Align.CENTER
                    },
                    Paint().also {
                        it.color = resources.getColor(playerInstance.resources.secondaryColorRes, null)
                        it.textSize = measuredTileSize / 2.0f
                        it.textAlign = Paint.Align.CENTER
                    }
            )
        }
        return unitTextPaint[playerInstance.playerId]!!
    }

    private val map: List<MapHex> by lazy { GameMap.currentMap.mapHexes }
    private val unitGroupings: SparseArrayCompat<out List<GameUnit>>
        get() {
            val map = SparseArrayCompat<MutableList<GameUnit>>()

            GameMap.currentMap.mapHexes.forEach { mapHex ->
                map[mapHex.loc] = ArrayList()
            }
            ScytheDatabase.unitDao()?.getUnits()?.filter { it.loc > 0 }?.forEach { unitData ->
                map[unitData.loc]!!.add(GameUnit.load(unitData))
            }

            return map
        }

    private var measuredTileSize: Float = 0.0F
    private var rect: Rect? = null

    private val _displayMapping = SparseArrayCompat<PointF>()
    private val displayMapping: SparseArrayCompat<PointF>
            get() {
                if(_displayMapping.isEmpty) {
                    GameMap.currentMap.findAllMatching {
                        TerrainFeature.valueOf(it!!.terrain) == TerrainFeature.FACTORY
                    }?.first()?.also {
                        _displayMapping[it.loc] = PointF(0.0F, 0.0F)
                        mapNeighbors(it)
                    }
                    GameMap.currentMap.startingHexes.forEach { mapBases(it) }
                }
                return _displayMapping
            }

    private fun grab(index: Int): MapHex? {
        return if(index != -1 && _displayMapping[index] == null) GameMap.currentMap.findHexAtIndex(index) else null
    }

    private fun mapNeighbors(mapHex: MapHex) {
        val currentPoint = _displayMapping[mapHex.loc]
        with(mapHex.data.neighbors) {
            if(_displayMapping[this.w] == null) grab(this.w)?.also { _displayMapping[this.w] = PointF(currentPoint!!.x - 1.0f, currentPoint.y) ; mapNeighbors(it) }
            if(_displayMapping[this.nw] == null) grab(this.nw)?.also { _displayMapping[this.nw] = PointF(currentPoint!!.x - 0.5f, currentPoint.y - 0.75f) ; mapNeighbors(it) }
            if(_displayMapping[this.ne] == null) grab(this.ne)?.also { _displayMapping[this.ne] = PointF(currentPoint!!.x + 0.5f, currentPoint.y - 0.75f) ; mapNeighbors(it) }
            if(_displayMapping[this.e] == null) grab(this.e)?.also { _displayMapping[this.e] = PointF(currentPoint!!.x + 1.0f, currentPoint.y) ; mapNeighbors(it) }
            if(_displayMapping[this.se] == null) grab(this.se)?.also { _displayMapping[this.se] = PointF(currentPoint!!.x + 0.5f, currentPoint.y + 0.75f) ; mapNeighbors(it) }
            if(_displayMapping[this.sw] == null) grab(this.sw)?.also { _displayMapping[this.sw] = PointF(currentPoint!!.x - 0.5f, currentPoint.y + 0.75f) ; mapNeighbors(it) }
        }
    }

    private fun mapBases(mapHex: MapHex) {
        val neighborDirection = Direction.values().first { mapHex.data.neighbors.getDirection(it) != -1 }
        val neighbor = _displayMapping[mapHex.data.neighbors.getDirection(neighborDirection)]
        _displayMapping[mapHex.loc] = when(neighborDirection) {
            Direction.E -> PointF(neighbor!!.x - 1.0f, neighbor.y)
            Direction.SE -> PointF(neighbor!!.x - 0.5f, neighbor.y - 0.75F)
            Direction.SW -> PointF(neighbor!!.x + 0.5f, neighbor.y - 0.75F)
            Direction.W -> PointF(neighbor!!.x + 1.0f, neighbor.y)
            Direction.NW -> PointF(neighbor!!.x + 0.5f, neighbor.y + 0.75F)
            Direction.NE -> PointF(neighbor!!.x - 0.5f, neighbor.y + 0.75F)
        }
    }

    private fun minimumXPoint() : Float {
        var leastX = Float.MAX_VALUE
        displayMapping.forEach { _, value -> if(leastX > value.x) leastX = value.x }
        return leastX
    }

    private fun minimumYPoint() : Float {
        var leastY = Float.MAX_VALUE
        displayMapping.forEach { _, value -> if(leastY > value.y) leastY = value.y }
        return leastY
    }

    private fun maximumXPoint() : Float {
        var mostX = Float.MIN_VALUE
        displayMapping.forEach { _, value -> if(mostX < value.x) mostX = value.x }
        return mostX
    }

    private fun maximumYPoint() : Float {
        var mostY = Float.MIN_VALUE
        displayMapping.forEach { _, value -> if(mostY > value.y) mostY = value.y }
        return mostY
    }

    // Scale and Pan
    private var scaleFactor = 1.0f
    private var scaleGestureDetector = ScaleGestureDetector(this.context, this)
    private var translate = PointF(0.0F, 0.0F)
    private var translateGestureDetector = GestureDetector(this.context, this)

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean = true

    override fun onScaleEnd(detector: ScaleGestureDetector?) {
        scaleFactor *= detector?.scaleFactor?: 1.0f

        scaleFactor = when {
            scaleFactor > 3.0f -> 3.0f
            scaleFactor < 1.0f -> 1.0f
            else -> scaleFactor
        }
        invalidate()
    }

    override fun onScale(detector: ScaleGestureDetector?): Boolean = false

    override fun onShowPress(e: MotionEvent?) {}

    override fun onSingleTapUp(e: MotionEvent?): Boolean = true

    override fun onDown(e: MotionEvent?): Boolean = true

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean = true

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        translate.x -= distanceX
        translate.y -= distanceY
        invalidate()
        return true
    }

    override fun onLongPress(e: MotionEvent?) {}

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        translateGestureDetector.onTouchEvent(ev)
        scaleGestureDetector.onTouchEvent(ev)
        return true
    }

    // View
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        rawWidth = MeasureSpec.getSize(widthMeasureSpec)
        rawHeight = MeasureSpec.getSize(heightMeasureSpec)
        val rawTileWidth = rawWidth / (unitWidth + 1)
        val rawTileHeight =  rawHeight / (unitHeight + 1)

        measuredTileSize = if(rawTileHeight < rawTileWidth) rawTileHeight else rawTileWidth

        this.setMeasuredDimension(rawWidth, rawHeight)
    }

    private fun setRectBounds(mapHex: MapHex) {
        val point = displayMapping[mapHex.loc] ?: throw RuntimeException("Point $mapHex is null!\n$displayMapping")

        val x = ((point.x - minX) * measuredTileSize).toInt()
        val y = ((point.y - minY) * measuredTileSize).toInt()

        rect = Rect(x, y, x + measuredTileSize.toInt(), y + measuredTileSize.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        if(displayMapping.isNotEmpty()) {
            val unitGroupings = unitGroupings
            canvas.save()
            canvas.translate(translate.x, translate.y)
            canvas.scale(scaleFactor, scaleFactor)
            map.forEach { hex ->
                setRectBounds(hex)
                drawBaseTerrain(hex, canvas)
                drawRivers(hex, canvas)
                drawOtherFeatures(hex, canvas)
                unitGroupings[hex.loc]?.also { if(it.isNotEmpty()) drawUnits(it, canvas) }
                if(debugText) {
                    val x = rect!!.centerX().toFloat() - (measuredTileSize / 10.0f)
                    val y = rect!!.centerY().toFloat() - (measuredTileSize / 10.0f)
                    canvas.drawText(hex.loc.toString(), x, y, debugTextPaint)
                }
            }

            canvas.restore()
            //TODO: Draw units, resources if required.
        } else {
            super.onDraw(canvas)
        }
    }

    private fun drawDisplayable(id: Int, canvas: Canvas, rect: Rect = this.rect!!) {
        val drawable = resources.getDrawable(id, null)?.mutate()

        drawable?.also {
            it.bounds.set(rect)
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
        if(hex.data.faction != null) {
            hex.faction?.displayable?.also { id ->
                if(id != 0) drawDisplayable(id, canvas)
            }
        } else {
            hex.terrain.displayable?.also { id ->
                drawDisplayable(id, canvas)
            }
        }
    }

    private fun drawOtherFeatures(hex: MapHex, canvas: Canvas) {
        when {
            hex.data.tunnel -> drawDisplayable(SpecialFeature.TUNNEL.displayable!!, canvas)
            hex.data.encounter != null || hex.data.encounter?: -1 >= 0 -> drawDisplayable(SpecialFeature.ENCOUNTER.displayable!!, canvas)
        }
    }

    private fun drawUnits(unitsPresent: List<GameUnit>, canvas: Canvas) {
        // ArrayList<Player, HashMap<UnitResourceID, Count>>

        val playersPresent = unitsPresent.map { it.controllingPlayer }.toSet()

        if(playersPresent.size > 1) {
            TODO("Cannot draw multiple players in one hex yet!")
        } else {
            val types = HashMap<UnitType, MutableList<GameUnit>>()
            unitsPresent.forEach {
                if(!types.containsKey(it.type)) {
                    types[it.type] = ArrayList()
                }
                types[it.type]!!.add(it)
            }

            if(types.size > 1) {
                var conflictCount = 0
                var workerCount = 0
                var building: GameUnit? = null

                types.forEach { type, list ->
                    when {
                        UnitType.CHARACTER == type -> conflictCount += list.size
                        UnitType.MECH == type -> conflictCount += list.size
                        UnitType.WORKER == type -> workerCount = list.size
                        UnitType.structures.contains(type) -> building = list.first()
                    }
                }

                building?.image?.also { drawDisplayable(it, canvas) }

                when {
                    (conflictCount > 0 && workerCount > 0) -> {
                        types[UnitType.WORKER]?.first()?.also {
                            drawDiamondWithDoubleCount(it.controllingPlayer.resources.diamondRes, it.controllingPlayer, canvas, conflictCount, workerCount)
                        }
                    }
                    workerCount > 0 -> {
                        types[UnitType.WORKER]?.first()?.also {
                            drawDisplayableWithCount(it.image!!, it.controllingPlayer, canvas, workerCount)
                        }
                    }
                    types.containsKey(UnitType.CHARACTER) && types.containsKey(UnitType.MECH) -> {
                        types[UnitType.CHARACTER]?.first()?.also {
                            drawDisplayableWithCount(it.controllingPlayer.resources.diamondRes, it.controllingPlayer, canvas, conflictCount)
                        }
                    }
                    types.containsKey(UnitType.MECH) -> {
                        types[UnitType.MECH]?.first()?.also {
                            drawDisplayableWithCount(it.image!!, it.controllingPlayer, canvas, conflictCount)
                        }
                    }
                    types.containsKey(UnitType.CHARACTER) -> {
                        types[UnitType.CHARACTER]?.first()?.also {
                            drawDisplayableWithCount(it.image!!, it.controllingPlayer, canvas, 1)
                        }
                    }
                    else -> {
                        TODO("Don't know how to draw something!")
                    }
                }
            } else {
                unitsPresent[0].also {
                    drawDisplayableWithCount(it.image!!, it.controllingPlayer, canvas, types[it.type]!!.size)
                }
            }
        }
    }

    private fun drawDisplayableWithCount(image: Int, player: PlayerInstance, canvas: Canvas, count: Int, rect: Rect = this.rect!!) {
        drawDisplayable(image, canvas)
        if(count > 1) {
            getTextPaint(player).first.also {
                if(unitTextSize == null) setTextSize(it)
                it.textSize = unitTextSize!!
                canvas.drawText(
                        count.toString(),
                        rect.centerX().toFloat(),
                        rect.centerY().toFloat() + unitTextYOffset!!,
                        it
                )
            }
        }
    }

    private fun drawDiamondWithDoubleCount(image: Int, player: PlayerInstance, canvas: Canvas, firstCount: Int, secondCount: Int, rect: Rect = this.rect!!) {
        drawDisplayable(image, canvas)
        getTextPaint(player).second.also {
            if(unitTextSize == null) setTextSize(it)
            it.textSize = unitTextSize!!
            canvas.drawText(
                    "$firstCount/$secondCount",
                    rect.centerX().toFloat(),
                    rect.centerY().toFloat() + unitTextYOffset!!,
                    it
            )
        }
    }

    private fun setTextSize(paint: Paint){
        paint.textSize = 16f
        val bounds = Rect()
        paint.getTextBounds("8/8",0,3, bounds)

        unitTextSize = (paint.textSize * measuredTileSize) / (2.5f * bounds.width())

        paint.textSize = unitTextSize!!
        paint.getTextBounds("1", 0, 1, bounds)

        unitTextYOffset = bounds.height() / 2.0f
        println(unitTextYOffset!!)
    }
}