package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.model.PlayerInstance

class GameMap (desc: MapDesc) {

    private val mapHexes: ArrayList<MapHex> = ArrayList(desc.mapHexDescs.map { MapHex(it) })

    val homeBases: List<FactionHomeHex>
        get() {
            return mapHexes.filterIsInstance<FactionHomeHex>()
        }

    init {
        currentMap = this
    }

    fun findHexAtIndex(index: Int): MapHex? {
        return mapHexes.find { it.desc.location == index }
    }

    fun findAllMatching(predicate: (MapFeature) -> Boolean) : List<MapHex> {
        return mapHexes.filter { mapHex ->  mapHex.desc.mapFeature.firstOrNull { mapFeature ->  predicate(mapFeature)} != null}
    }

    fun findHomeBase(player: PlayerInstance) : MapHex? {
        return mapHexes.firstOrNull { mapHex -> mapHex.desc.mapFeature.firstOrNull { mapFeature -> mapFeature is HomeBase && mapFeature.player == player} != null }
    }

    fun addHomeBase(desc: MapHexDesc, player: PlayerInstance? = null) {
        mapHexes.add(FactionHomeHex(desc, player))
    }

    companion object {
        var currentMap: GameMap? = null
    }
}