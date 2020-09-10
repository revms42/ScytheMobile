package org.ajar.scythemobile.ui.build

import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.player.BottomRowAction
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.BottomRowViewModel

class BuildViewModel : BottomRowViewModel<BottomRowAction.Build>() {
    private var _action: BottomRowAction.Build? = null
    override val action: BottomRowAction.Build
        get() {
            if(_action == null) {
                _action = TurnHolder.currentPlayer.playerMat.findBottomRowAction(BottomRowAction.Build::class.java)
            }
            return _action!!
        }
    var returnNav: Int? = null
    var unitType: Int? = null

    var selectedStructure: GameUnit? = null
    var selectedHex: MapHex? = null

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
        selectedHex = null
        selectedStructure = null
    }
}