package org.ajar.scythemobile.ui.choose

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.faction.FactionAbility
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.turn.TurnHolder

class MechDeployChoiceViewModel : ViewModel() {

    fun getSelectableAbilities() = TurnHolder.currentPlayer.factionMat.lockedFactionAbilities

    fun getValidLocations(limitToUnitsOf: Int): List<MapHex>? {
        return ScytheDatabase.unitDao()?.getUnitsForPlayer(TurnHolder.currentPlayer.playerId, limitToUnitsOf)?.map { unit -> unit.loc }?.filter { it != -1 }?.mapNotNull { GameMap.currentMap.findHexAtIndex(it) }
    }

    var selectedAbility: FactionAbility? = null
    var selectedHex: MapHex? = null

    fun performDeploy(): Boolean {
        return if(selectedAbility != null && selectedHex != null) {
            ScytheAction.DeployMech(TurnHolder.currentPlayer, selectedHex!!, selectedAbility!!).perform()
        } else {
            false
        }
    }
}