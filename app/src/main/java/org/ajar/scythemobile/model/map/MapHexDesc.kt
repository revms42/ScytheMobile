package org.ajar.scythemobile.model.map

data class HexNeighbors(val nw: Int = -1, val ne: Int = -1, val e: Int = -1, val se: Int = -1, val sw: Int = -1, val w: Int = -1) {
    fun asArray(): Array<Int> = arrayOf(nw, ne, e, se, sw, w)
}

class MapHexDesc(val location: Int, val hexNeighbors: HexNeighbors, vararg val mapFeature: MapFeature)
