package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.model.PlayerInstance

class FactionHomeHex(desc: MapHexDesc, val player: PlayerInstance?) : MapHex(desc)

open class MapHex(val desc: MapHexDesc) {

    var encounterCard: EncounterCard? = null

    init {
        if(desc.mapFeature.contains(SpecialFeature.ENCOUNTER)) {
            encounterCard = EncounterDeck.currentDeck.drawCard()
        }
    }

    val playerInControl : Int?
        get() {
            TODO("NYI")
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

    private fun neighbor(direction: Direction, riversBlock: Boolean = true) : MapHex? {
        if(riversBlock && desc.mapFeature.find { it is RiverFeature && it.direction == direction } != null) return null

        val index = when(direction) {
            Direction.NW -> desc.hexNeighbors.nw
            Direction.NE -> desc.hexNeighbors.ne
            Direction.E -> desc.hexNeighbors.e
            Direction.SE -> desc.hexNeighbors.se
            Direction.SW -> desc.hexNeighbors.sw
            Direction.W -> desc.hexNeighbors.w
        }

        return GameMap.currentMap?.findHexAtIndex(index)
    }

    override fun toString(): String {
        return "${desc.mapFeature[0]} ${desc.location}"
    }
}
