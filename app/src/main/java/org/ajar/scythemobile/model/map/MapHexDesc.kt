package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.data.MapHexData
import org.ajar.scythemobile.data.Neighbors
import org.ajar.scythemobile.data.Rivers

class MapHexDesc(val location: Int, val neighbors: Neighbors, vararg val mapFeature: MapFeature) {
    fun createMapData(): MapHexData {
        val builder = MapHexBuilder()
        builder.loc = location
        builder.neighbors = neighbors

        mapFeature.forEach { it.applyToData(builder) }

        return builder.makeData()
    }
}

class MapHexBuilder {
    var loc: Int = 0
    var terrain: Int = 0

    var neighbors: Neighbors? = null
    var rivers = HashMap<Direction,Boolean>()

    var encounter: Int? = null
    var tunnel: Boolean = false
    var faction: Int? = null

    fun makeData(): MapHexData {
        val riversObject = Rivers(
                rivers[Direction.NW]?: false,
                rivers[Direction.NE]?: false,
                rivers[Direction.E]?: false,
                rivers[Direction.SE]?: false,
                rivers[Direction.SW]?: false,
                rivers[Direction.W]?: false
        )
        return MapHexData(loc, terrain, neighbors!!, riversObject, encounter, tunnel, faction)
    }

    fun addRiver(direction: Direction) {
        rivers[direction] = true
    }
}
