package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.data.MapHexData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.PlayerInstance

class GameMap(list: List<MapHexData>) {

    constructor(desc: MapDesc) : this(desc.mapHexDesc.map { it.createMapData() })

    private val mapHexes: List<MapHex> = list.map { MapHex(it) }

    private var _startingHexes: List<MapHex>? = null
    val startingHexes: List<MapHex>
        get() {
            if(_startingHexes == null) {
                _startingHexes = mapHexes.filter { it.data.faction?: -1 >= 0 }
            }
            return _startingHexes!!
        }

    private var _homeBases: List<MapHex>? = null
    val homeBases: List<MapHex>
        get() {
            if(_homeBases == null) {
                _homeBases = startingHexes.filter { PlayerInstance.activeFactions()?.contains(it.data.faction)?: false }
            }
            return _homeBases!!
        }

    private var _vacantBases: List<MapHex>? = null
    val vacantBases: List<MapHex>
        get() {
            if(_vacantBases == null) {
                _vacantBases = startingHexes.filter { !homeBases.contains(it) }
            }
            return _vacantBases!!
        }

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

    companion object {
        private var _currentMap: GameMap? = null
        var currentMap: GameMap
            get() {
                if(_currentMap == null) {
                    val saved = ScytheDatabase.mapDao()?.getMap()
                    _currentMap = if(saved == null) {
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