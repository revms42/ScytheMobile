package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.entity.UnitType

class GameMap (desc: MapDesc) {

    private val mapHexes: ArrayList<MapHex> = ArrayList(desc.mapHexDescs.map { MapHex(it) })

    val homeBases: List<FactionHomeHex>
        get() {
            return mapHexes.filter { it is FactionHomeHex }.map { it as FactionHomeHex }
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

    fun findHomeBase(player: Player) : MapHex? {
        return mapHexes.firstOrNull { mapHex -> mapHex.desc.mapFeature.firstOrNull { mapFeature -> mapFeature is HomeBase && mapFeature.player == player} != null }
    }

    fun findUnit(type: UnitType? = null, player: Player? = null): List<GameUnit> {
        var allUnits = mapHexes.flatMap { mapHex -> mapHex.unitsPresent }

        return if(type != null) {
            allUnits = allUnits.filter { gameUnit -> gameUnit.type == type }

            if (player != null) {
                allUnits.filter { gameUnit -> gameUnit.controllingPlayer == player }
            } else {
                allUnits
            }
        } else {
            allUnits
        }
    }

    fun locateUnit(unit: GameUnit): MapHex? {
        return mapHexes.firstOrNull { mapHex -> mapHex.unitsPresent.contains(unit) }
    }

    fun addHomeBase(desc: MapHexDesc, player: Player? = null) {
        mapHexes.add(FactionHomeHex(desc, player))
    }

    companion object {
        var currentMap: GameMap? = null
    }
}