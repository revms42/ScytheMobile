package org.ajar.scythemobile.ui.deploy

import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.faction.FactionAbility
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.player.BottomRowAction
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.BottomRowViewModel

class DeployViewModel : BottomRowViewModel<BottomRowAction.Deploy>() {
    private var _action: BottomRowAction.Deploy? = null
    override val action: BottomRowAction.Deploy
        get() {
            if(_action == null) {
                _action = TurnHolder.currentPlayer.playerMat.findBottomRowAction(BottomRowAction.Deploy::class.java)
            }
            return _action!!
        }
    var unitType: Int? = null

    var selectedAbility: FactionAbility? = null
    var selectedHex: MapHex? = null

    var returnNav: Int? = null

    fun getSelectableAbilities() = TurnHolder.currentPlayer.factionMat.lockedFactionAbilities

    fun getValidLocations(): List<MapHex>? {
        return ScytheDatabase.unitDao()?.getUnitsForPlayer(TurnHolder.currentPlayer.playerId, unitType!!)?.map { unit -> unit.loc }?.filter { it != -1 }?.mapNotNull { GameMap.currentMap.findHexAtIndex(it) }
    }

    fun performDeploy(): Boolean {
        return if(selectedAbility != null && selectedHex != null) {
            ScytheAction.DeployMech(TurnHolder.currentPlayer, selectedHex!!, selectedAbility!!).perform()
        } else {
            false
        }
    }

    fun reset() {
        unitType = null
        selectedAbility = null
        selectedHex = null
        returnNav = null
    }
}