package org.ajar.scythemobile.ui.build

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.player.BottomRowAction
import org.ajar.scythemobile.turn.TurnHolder

class BuildChoiceViewModel : ViewModel() {
    var returnNav: Int? = null
    var unitType: Int? = null

    var selectedStructure: GameUnit? = null
    var selectedHex: MapHex? = null

    fun getSelectableStructures() : List<GameUnit>? {
        val player = TurnHolder.currentPlayer
        return listOf(UnitType.MILL, UnitType.ARMORY, UnitType.MONUMENT, UnitType.MINE).flatMap {
            ScytheDatabase.unitDao()?.getUnitsForPlayer(player.playerId, it.ordinal)?.map {
                GameUnit(it, player)
            }?: emptyList()
        }.filter { it.pos == -1 }
    }

    fun getValidLocations(): List<MapHex>? {
        return ScytheDatabase.unitDao()?.getUnitsForPlayer(TurnHolder.currentPlayer.playerId, unitType!!)?.map { unit -> unit.loc }?.filter { it != -1 }?.mapNotNull { GameMap.currentMap.findHexAtIndex(it) }
    }

    fun performBuild(): Boolean {
        return if(selectedStructure != null && selectedHex != null) {
            ScytheAction.BuildStructure(selectedHex!!, selectedStructure!!)
            return true
        } else {
            false
        }
    }

    fun reset() {
        returnNav = null
        unitType = null
    }
}