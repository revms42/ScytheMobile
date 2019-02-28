package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.production.Resource

class FactionHomeHex(desc: MapHexDesc, val player: Player?) : MapHex(desc) {
}

open class MapHex(val desc: MapHexDesc) {

    var unitsPresent: ArrayList<GameUnit> = ArrayList()
    var resourcesPresent: ArrayList<Resource> = ArrayList()

    val playerInControl : Player?
        get() {
            return if(unitsPresent.isEmpty()) {
                null
            } else {
                unitsPresent.firstOrNull { it.type == UnitType.CHARACTER || it.type == UnitType.MECH || it.type == UnitType.WORKER }?.controllingPlayer
            } //TODO: Flesh this out when airships come into play.
        }

    fun canUnitOccupy(unit:GameUnit) : Boolean {
        return if(playerInControl != null) unit.controllingPlayer == playerInControl else true
    }

    fun willMoveProvokeFight() : Boolean {
        return unitsPresent.firstOrNull { it.type == UnitType.CHARACTER || it.type == UnitType.MECH } != null
    }

    fun dropResource(unit: GameUnit, resource: Resource) {
        if (unit.heldResources?.remove(resource) == true) resourcesPresent.add(resource)
    }

    fun loadResource(unit: GameUnit, resource: Resource) {
        if(unit.heldResources != null && resourcesPresent.remove(resource)) unit.heldResources?.add(resource)
    }

    private fun matchingNeighbors(riversBlock: Boolean, predicate: (MapFeature) -> Boolean) : List<MapHex?> {
        return Direction.values().map { direction ->  neighbor(direction, riversBlock) }.filter { neighbor -> neighbor?.desc?.mapFeature?.firstOrNull { feature -> predicate(feature) } != null }
    }

    private fun nonMatchingNeighbors(riversBlock: Boolean, predicate: (MapFeature) -> Boolean) : List<MapHex?> {
        return Direction.values().map { direction ->  neighbor(direction, riversBlock)}.filter { neighbor -> neighbor?.desc?.mapFeature?.none { feature -> predicate(feature) }?: false }
    }

    fun matchingNeighborsNoRivers(predicate: (MapFeature) -> Boolean) : List<MapHex?> = matchingNeighbors(true, predicate)
    fun matchingNeighborsIncludeRivers(predicate: (MapFeature) -> Boolean) : List<MapHex?> = matchingNeighbors(false, predicate)

    fun nonMatchingNeighborsNoRivers(predicate: (MapFeature) -> Boolean) : List<MapHex?> = nonMatchingNeighbors(true, predicate)
    fun nonMatchingNeighborsIncludeRivers(predicate: (MapFeature) -> Boolean) : List<MapHex?> = nonMatchingNeighbors(false, predicate)

    fun neighbor(direction: Direction, riversBlock: Boolean = true) : MapHex? {
        if(riversBlock && desc.mapFeature.find { it is RiverFeature && it.direction == direction } != null) return null

        val index = when(direction) {
            Direction.NW -> desc.hexNeigbors.nw
            Direction.NE -> desc.hexNeigbors.ne
            Direction.E -> desc.hexNeigbors.e
            Direction.SE -> desc.hexNeigbors.se
            Direction.SW -> desc.hexNeigbors.sw
            Direction.W -> desc.hexNeigbors.w
        }

        return GameMap.currentMap?.findHexAtIndex(index)
    }
}
