package org.ajar.scythemobile.model.map

data class HexNeigbors(val nw: Int = -1, val ne: Int = -1, val e: Int = -1, val se: Int = -1, val sw: Int = -1, val w: Int = -1)

class MapHexDesc(val location: Int, val hexNeigbors: HexNeigbors, vararg val mapFeature: MapFeature) {
    fun <T: MapFeature> getFeature(feature: Class<T>): List<T> {
        return mapFeature.filter { mapFeature -> feature.isAssignableFrom(mapFeature.javaClass) }.map { mapFeature -> mapFeature as T }
    }
}
