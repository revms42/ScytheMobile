package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.data.MapHexData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.entity.UnitType

open class MapHex(val data: MapHexData) {

    val playerInControl : Int?
        get() {
            return if(provokesCombat) {
                ScytheDatabase.unitDao()?.getUnitsAtLocation(data.loc)?.firstOrNull { UnitType.provokeUnits.contains(UnitType.valueOf(it.type)) }?.owner
            } else {
                ScytheDatabase.unitDao()?.getUnitsAtLocation(data.loc)?.firstOrNull { UnitType.controlUnits.contains(UnitType.valueOf(it.type)) }?.owner
            }
        }

    val provokesCombat : Boolean
        get() {
            return ScytheDatabase.unitDao()?.getUnitsAtLocation(data.loc)?.any { UnitType.provokeUnits.contains(UnitType.valueOf(it.type)) }?: false
        }

    val loc: Int
        get() = data.loc

    val terrain: TerrainFeature
        get() = TerrainFeature.valueOf(data.terrain)

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
