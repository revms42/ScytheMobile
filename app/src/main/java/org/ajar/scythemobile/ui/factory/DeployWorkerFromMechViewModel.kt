package org.ajar.scythemobile.ui.factory

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.turn.TurnHolder

class DeployWorkerFromMechViewModel : ViewModel() {

    val hexChoices: Set<MapHex>
            get() = ScytheDatabase.unitDao()?.getUnitsForPlayer(TurnHolder.currentPlayer.playerId, UnitType.MECH.ordinal)?.mapNotNull {
                GameMap.currentMap.findHexAtIndex(it.loc)
            }?.toSet()?: emptySet()

    var chosenHex: MapHex? = null
    val hasWorkersToDeploy: Boolean
            get() = ScytheDatabase.unitDao()?.getUnitsForPlayer(TurnHolder.currentPlayer.playerId, UnitType.WORKER.ordinal)?.any { it.loc == -1 }?: false

    fun perform() {
        ScytheAction.DeployWorker(TurnHolder.currentPlayer, chosenHex!!).perform()
    }

    fun reset() {
        chosenHex == null
    }
}
