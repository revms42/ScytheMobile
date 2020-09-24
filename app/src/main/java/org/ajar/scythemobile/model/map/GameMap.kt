package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.data.MapHexData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.UnitData
import org.ajar.scythemobile.model.PlayerInstance

class GameMap(list: List<MapHexData>) {

    constructor(desc: MapDesc) : this(desc.mapHexDesc.map { it.createMapData() })

    val mapHexes: List<MapHex> = list.map { MapHex(it) }

    val startingHexes: List<MapHex>
        get() = mapHexes.filter { it.data.faction?: -1 >= 0 }

    val homeBases: List<MapHex>
        get() = startingHexes.filter { PlayerInstance.activeFactions?.contains(it.data.faction)?: false }

    val vacantBases: List<MapHex>
        get()  = startingHexes.filter { !homeBases.contains(it) }

    fun findHexAtIndex(index: Int): MapHex? {
        return mapHexes.find { it.data.loc == index }
    }

    fun findAllMatching(predicate: (MapHexData?) -> Boolean) : List<MapHex>? {
        return mapHexes.filter { mapHex ->  predicate(mapHex.data)}
    }

    fun findHomeBase(player: PlayerInstance) : MapHex? {
        return homeBases.firstOrNull { it.data.faction == player.factionMat.factionMatData.matId }
    }

    fun findFactionBase(faction: Int) : MapHex? {
        return homeBases.firstOrNull { it.data.faction == faction }
    }

    fun findHomeBase(id: Int) : MapHex? {
        return ScytheDatabase.playerDao()?.getPlayer(id)?.let { findFactionBase(it.factionMat.matId) }
    }

    fun unitsAtHex(hex: Int) : List<UnitData> {
        return ScytheDatabase.unitDao()?.getUnitsAtLocation(hex)?: emptyList()
    }

    companion object {
        private var _currentMap: GameMap? = null
        var currentMap: GameMap
            get() {
                if(_currentMap == null) {
                    val saved = ScytheDatabase.mapDao()?.getMap()
                    _currentMap = if(saved.isNullOrEmpty()) {
                        val map = GameMap(MapDesc())
                        map.mapHexes.map { it.data }.toTypedArray().also { ScytheDatabase.mapDao()?.addMapHex(*it) }
                        map
                    } else {
                        GameMap(saved)
                    }
                }
                return _currentMap!!
            }
            set(value) {
                _currentMap?.mapHexes?.map { it.data }?.toTypedArray()?.also { ScytheDatabase.mapDao()?.removeMapHex(*it) }
                _currentMap = value
                _currentMap?.mapHexes?.map { it.data }?.toTypedArray()?.also { ScytheDatabase.mapDao()?.addMapHex(*it) }
            }
    }
}