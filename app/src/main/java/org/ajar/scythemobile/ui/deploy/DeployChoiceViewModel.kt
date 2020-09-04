package org.ajar.scythemobile.ui.deploy

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.faction.FactionAbility
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.turn.TurnHolder

class DeployChoiceViewModel : ViewModel() {
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