package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.entity.ResourceHolder
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.production.Resource

class FactionHomeHex(desc: MapHexDesc, val player: Player?) : MapHex(desc) {
}

open class MapHex(val desc: MapHexDesc) : ResourceHolder {

    var unitsPresent: ArrayList<GameUnit> = ArrayList()
    override var heldResources: ArrayList<Resource> = ArrayList()
    var encounterCard: EncounterCard? = null

    init {
        if(desc.mapFeature.contains(SpecialFeature.ENCOUNTER)) {
            encounterCard = EncounterDeck.currentDeck.drawCard()
        }
    }

    val playerInControl : Player?
        get() {
            return if(unitsPresent.isEmpty()) {
                null
            } else {
                findControllingUnit()
            } //TODO: Flesh this out when airships come into play.
        }

    private fun findControllingUnit() : Player? =
            unitsPresent.firstOrNull { it.type == UnitType.CHARACTER || it.type == UnitType.MECH || it.type == UnitType.WORKER }?.controllingPlayer?:
            unitsPresent.firstOrNull { it.type == UnitType.STRUCTURE}?.controllingPlayer

    fun canUnitOccupy(unit:GameUnit) : Boolean {
        return if(playerInControl != null) unit.controllingPlayer == playerInControl else true
    }

    fun willMoveProvokeFight() : Boolean {
        return unitsPresent.firstOrNull { it.type == UnitType.CHARACTER || it.type == UnitType.MECH } != null
    }

    fun dropResource(unit: GameUnit, resource: Resource) {
        if (unit.heldResources.remove(resource)) heldResources.add(resource)
    }

    fun dropAll(unit: GameUnit) {
        heldResources.addAll(unit.heldResources)
        unit.heldResources.clear()
    }

    fun loadResource(unit: GameUnit, resource: Resource) {
        if(heldResources.remove(resource)) unit.heldResources.add(resource)
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
