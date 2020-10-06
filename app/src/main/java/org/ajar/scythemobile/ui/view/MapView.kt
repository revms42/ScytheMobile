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
import androidx.core.content.res.ResourcesCompat
import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.Resource
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import kotlin.math.pow


class MapView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet), ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnGestureListener {

    private val minX: Float by lazy { minimumXPoint() }
    private val minY: Float by lazy { minimumYPoint() }
    private val maxX: Float by lazy { maximumXPoint() }
    private val maxY: Float by lazy { maximumYPoint() }

    private val unitWidth: Float by lazy { maxX - minX }
    private val unitHeight: Float by lazy { maxY - minY }

    private var rawWidth: Int = 0
    private var rawHeight: Int = 0

    private var unitTextSize: Float? = null
    private var unitTextYOffset: Float? = null

    val paintUnit: (GameUnit) -> Boolean = fun(_): Boolean = true
    val paintResource: (Resource) -> Boolean = fun(_): Boolean = true
    val selectable = true

    private val noHighlight: (MapHex) -> Boolean = fun(_): Boolean = true
    private var _highlight: (MapHex) -> Boolean = noHighlight
    var hexHighLight: ((MapHex) -> Boolean)?
        get() = _highlight
        set(value){
            _highlight = value ?: noHighlight
        }
    private val muteFilter = ColorMatrixColorFilter(Desaturate.makeMatrix())

    private var unitTextPaint = SparseArrayCompat<Pair<Paint,Paint>>()
    private fun getTextPaint(playerInstance: PlayerInstance?) : Pair<Paint,Paint> {
        if(playerInstance != null) {
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
        } else {
            unitTextPaint[-1] = Pair(
                    Paint().also {
                        it.color = Color.WHITE
                        it.textSize = measuredTileSize / 2.0f
                        it.textAlign = Paint.Align.CENTER
                    },
                    Paint().also {
                        it.color = Color.BLACK
                        it.textSize = measuredTileSize / 2.0f
                        it.textAlign = Paint.Align.CENTER
                    }
            )
        }

        return unitTextPaint[playerInstance?.playerId?: -1]!!
    }

    private val map: List<MapHex> by lazy { GameMap.currentMap.mapHexes }
    val selected: MutableList<MapHex> = ArrayList()
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
    private val resourceGroupings: SparseArrayCompat<out List<ResourceData>>
        get() {
            val map = SparseArrayCompat<MutableList<ResourceData>>()

            GameMap.currentMap.mapHexes.forEach { mapHex ->
                map[mapHex.loc] = ArrayList()
            }
            ScytheDatabase.resourceDao()?.getResources()?.filter { it.loc > 0 }?.forEach { resourceData ->
                map[resourceData.loc]!!.add(resourceData)
            }

            return map
        }

    private var measuredTileSize: Float = 0.0F
    private var rect: Rect? = null
    private var filter: Boolean = false

    private val selectionMapping = HashMap<PointF, MapHex>()
    private val selectedHexes = ArrayList<MapHex>()

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

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return if(selectable) e?.let {
            selectionMapping.entries.filter { hexHighLight?.invoke(it.value) != false }.firstOrNull { entry ->
                (e.x - entry.key.x).pow(2) + (e.y - entry.key.y).pow(2) <= (measuredTileSize/2).pow(2)
            }?.let {
                if(selectedHexes.contains(it.value)) {
                    selectedHexes.remove(it.value)
                } else {
                    selectedHexes.add(it.value)
                }
                invalidate()
                true
            }?: false
        }?: false
        else false
    }

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

        rect = Rect(x, y, x + measuredTileSize.toInt(), y + measuredTileSize.toInt()).also {
            selectionMapping[PointF(it.exactCenterX(), it.exactCenterY())] = mapHex
        }

        filter = !hexHighLight!!.invoke(mapHex) || selectedHexes.contains(mapHex)
    }

    override fun onDraw(canvas: Canvas) {
        if(displayMapping.isNotEmpty()) {
            val unitGroupings = unitGroupings
            val resourceGroupings = resourceGroupings
            canvas.save()
            canvas.translate(translate.x, translate.y)
            canvas.scale(scaleFactor, scaleFactor)

            map.forEach { hex ->
                setRectBounds(hex)
                drawBaseTerrain(hex, canvas)
                drawRivers(hex, canvas)
                drawOtherFeatures(hex, canvas)
                unitGroupings[hex.loc]?.also { if(it.isNotEmpty()) drawUnits(it, canvas) }
                resourceGroupings[hex.loc]?.also { if(it.isNotEmpty()) drawResources(it, canvas) }
            }

            canvas.restore()
            //TODO: Draw units, resources if required.
        } else {
            super.onDraw(canvas)
        }
    }

    private fun drawDisplayable(id: Int, canvas: Canvas, rect: Rect = this.rect!!) {
        val drawable = ResourcesCompat.getDrawable(resources, id, null)?.mutate()

        drawable?.also {
            it.bounds.set(rect)
            if(filter) it.colorFilter = muteFilter
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

                types.forEach { (type, list) ->
                    when {
                        UnitType.CHARACTER == type -> conflictCount += list.size
                        UnitType.MECH == type -> conflictCount += list.size
                        UnitType.WORKER == type -> workerCount = list.size
                        UnitType.structures.contains(type) -> building = list.first()
                    }
                }

                building?.also {
                    if(paintUnit(it)) it.image?.also {image -> drawDisplayable(image, canvas) }
                }

                when {
                    (conflictCount > 0 && workerCount > 0) -> {
                        val paintWorker = paintUnit(types[UnitType.WORKER]?.first()!!)
                        val paintMech = types[UnitType.MECH]?.first()?.let { paintUnit(it) }?: false
                        val paintCharacter = types[UnitType.CHARACTER]?.first()?.let { paintUnit(it) }?: false
                        when {
                            (paintWorker && paintMech) || (paintCharacter && paintWorker) -> {
                                types[UnitType.WORKER]?.first()!!.also { drawDiamondWithDoubleCount(it.controllingPlayer.resources.diamondRes, it.controllingPlayer, canvas, conflictCount, workerCount) }
                            }
                            paintMech && paintCharacter -> {
                                types[UnitType.CHARACTER]?.first()!!.also { unit ->
                                    unit.image?.also { drawDisplayableWithCount(it, unit.controllingPlayer, canvas, conflictCount) }
                                }
                            }
                            paintWorker -> {
                                types[UnitType.WORKER]?.first()?.also {
                                    drawDisplayableWithCount(it.image!!, it.controllingPlayer, canvas, workerCount)
                                }
                            }
                            paintMech -> {
                                types[UnitType.MECH]?.first()?.also {
                                    drawDisplayableWithCount(it.image!!, it.controllingPlayer, canvas, conflictCount)
                                }
                            }
                            paintCharacter -> {
                                types[UnitType.CHARACTER]?.first()?.also {
                                    drawDisplayableWithCount(it.image!!, it.controllingPlayer, canvas, 1)
                                }
                            }
                        }
                    }
                    workerCount > 0 -> {
                        types[UnitType.WORKER]?.first()?.also {
                            if(paintUnit(it)) drawDisplayableWithCount(it.image!!, it.controllingPlayer, canvas, workerCount)
                        }
                    }
                    types.containsKey(UnitType.CHARACTER) && types.containsKey(UnitType.MECH) -> {
                        types[UnitType.CHARACTER]?.first()?.also {
                            if(paintUnit(it)) drawDisplayableWithCount(it.controllingPlayer.resources.diamondRes, it.controllingPlayer, canvas, conflictCount)
                        }
                    }
                    types.containsKey(UnitType.MECH) -> {
                        types[UnitType.MECH]?.first()?.also {
                            if(paintUnit(it)) drawDisplayableWithCount(it.image!!, it.controllingPlayer, canvas, conflictCount)
                        }
                    }
                    types.containsKey(UnitType.CHARACTER) -> {
                        types[UnitType.CHARACTER]?.first()?.also {
                            if(paintUnit(it)) drawDisplayableWithCount(it.image!!, it.controllingPlayer, canvas, 1)
                        }
                    }
                    else -> {
                        TODO("Don't know how to draw something!")
                    }
                }
            } else {
                unitsPresent[0].also {
                    if(paintUnit(it)) drawDisplayableWithCount(it.image!!, it.controllingPlayer, canvas, types[it.type]!!.size)
                }
            }
        }
    }

    private fun drawResources(resourcesPresent: List<ResourceData>, canvas: Canvas) {
        val resourceMap = HashMap<Resource, Int>()

        resourcesPresent.forEach {
            val type = Resource.valueOf(it.type)!!
            if(!resourceMap.containsKey(type)) {
                resourceMap[type] = 0
            }
            if(paintResource(type)) resourceMap[type] = resourceMap[type]!! + 1
        }

        val rectList = ArrayList<Rect>()
        val rawWidth = this.rect!!.width()
        val rawHeight = this.rect!!.height()
        val centerX = this.rect!!.centerX()
        val centerY = this.rect!!.centerY()

        val scaleFactor = (3 * rawWidth / 8)
        when(resourceMap.size) {
            4 -> {
                rectList.add(Rect(centerX - scaleFactor, centerY - scaleFactor, centerX, centerY)) // Top-Left
                rectList.add(Rect(centerX, centerY - scaleFactor, centerX + scaleFactor, centerY)) // Top-Right
                rectList.add(Rect(centerX - scaleFactor, centerY, centerX, centerY + scaleFactor)) // Bottom-Left
                rectList.add(Rect(centerX, centerY, centerX + scaleFactor, centerY + scaleFactor)) // Bottom-Right
            }
            3 -> {
                rectList.add(Rect(centerX, centerY, centerX + scaleFactor, centerY + scaleFactor))
                rectList.add(Rect(centerX - scaleFactor, centerY, centerX, centerY + scaleFactor))
                rectList.add(Rect(centerX - (scaleFactor /2), centerY + (scaleFactor/2), centerX + (scaleFactor / 2), centerY + (3 * scaleFactor/2)))
            }
            2 -> {
                rectList.add(Rect(centerX, centerY, centerX + scaleFactor, centerY + scaleFactor))
                rectList.add(Rect(centerX - scaleFactor, centerY, centerX, centerY + scaleFactor))

            }
            1 -> {
                rectList.add(Rect(centerX - (scaleFactor/2), centerY, centerX + (scaleFactor/2), centerY + scaleFactor))
            }
        }

        val rectIterator = rectList.iterator()
        resourceMap.forEach {
            val rect = rectIterator.next()
            val forceSecondary = it.key.id == NaturalResourceType.FOOD.id
            drawDisplayableWithCount(it.key.image, null, canvas, it.value, rect, forceSecondary)
        }
    }

    private fun drawDisplayableWithCount(image: Int, player: PlayerInstance?, canvas: Canvas, count: Int, rect: Rect = this.rect!!, forceSecondary: Boolean = false) {
        drawDisplayable(image, canvas, rect)
        if(count > 1) {
            getTextPaint(player).let { (if(forceSecondary) it.second else it.first) }.also {
                val filter = it.colorFilter
                if(this.filter) it.colorFilter = muteFilter
                if(unitTextSize == null) setTextSize(it)
                it.textSize = unitTextSize!!
                canvas.drawText(
                        count.toString(),
                        rect.centerX().toFloat(),
                        rect.centerY().toFloat() + unitTextYOffset!!,
                        it
                )
                it.colorFilter = filter
            }
        }
    }

    private fun drawDiamondWithDoubleCount(image: Int, player: PlayerInstance, canvas: Canvas, firstCount: Int, secondCount: Int, rect: Rect = this.rect!!) {
        drawDisplayable(image, canvas)
        getTextPaint(player).second.also {
            val filter = it.colorFilter
            if(this.filter) it.colorFilter = muteFilter
            if(unitTextSize == null) setTextSize(it)
            it.textSize = unitTextSize!!
            canvas.drawText(
                    "$firstCount/$secondCount",
                    rect.centerX().toFloat(),
                    rect.centerY().toFloat() + unitTextYOffset!!,
                    it
            )
            it.colorFilter = filter
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