package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.data.MapHexData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.entity.UnitType

open class MapHex(val data: MapHexData) {

    val playerInControl : Int?
        get() {
            return ScytheDatabase.unitDao()?.getUnitsAtLocation(data.loc)?.firstOrNull { UnitType.controlUnits.contains(UnitType.valueOf(it.type)) }?.owner
        }

    private fun matchingNeighbors(riversBlock: Boolean, predicate: (MapHexData?) -> Boolean) : List<MapHex?> {
        return Direction.values().map { direction ->  neighbor(direction, riversBlock) }.filter { predicate(it?.data) }
    }

    private fun nonMatchingNeighbors(riversBlock: Boolean, predicate: (MapHexData?) -> Boolean) : List<MapHex?> {
        return Direction.values().map { direction ->  neighbor(direction, riversBlock)}.filter { !predicate(it?.data) }
    }

    fun matchingNeighborsNoRivers(predicate: (MapHexData?) -> Boolean) : List<MapHex?> = matchingNeighbors(true, predicate)
    fun matchingNeighborsIncludeRivers(predicate: (MapHexData?) -> Boolean) : List<MapHex?> = matchingNeighbors(false, predicate)

    fun nonMatchingNeighborsNoRivers(predicate: (MapHexData?) -> Boolean) : List<MapHex?> = nonMatchingNeighbors(true, predicate)
    fun nonMatchingNeighborsIncludeRivers(predicate: (MapHexData?) -> Boolean) : List<MapHex?> = nonMatchingNeighbors(false, predicate)

    private fun neighbor(direction: Direction, riversBlock: Boolean = true) : MapHex? {
        if(riversBlock && data.rivers.getDirection(direction)) return null

        val index = data.neighbors.getDirection(direction)

        return GameMap.currentMap.findHexAtIndex(index)
    }

    override fun toString(): String {
        return "${TerrainFeature.valueOf(data.terrain).displayName} ${data.loc}"
    }
}
