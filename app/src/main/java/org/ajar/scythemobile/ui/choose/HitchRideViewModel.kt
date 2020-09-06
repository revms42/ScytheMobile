package org.ajar.scythemobile.ui.choose

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.Resource
import org.ajar.scythemobile.ScytheMoble
import org.ajar.scythemobile.data.PlayerData
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.FactionAbility
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.turn.TurnHolder

class HitchRideViewModel : ViewModel() {

    val hex: Int
        get() = ScytheDatabase.unitDao()?.getUnitsForPlayer(TurnHolder.currentPlayer.playerId, UnitType.CHARACTER.ordinal)!!.first().loc

    var chosenHex: MapHex? = null
    val chosenUnits = ArrayList<GameUnit>()

    val destinationChoices: List<MapHex>
        get() = GameMap.currentMap.findHexAtIndex(hex!!)?.matchingNeighborsIncludeRivers { neighbor ->
            neighbor?.let{ (ScytheDatabase.unitDao()?.getUnitsAtLocation(it.loc))?.isEmpty() == true }?: false
        }?.filterNotNull()?: emptyList<MapHex>() + MapHex(ScytheDatabase.mapDao()?.getMapHex(hex!!)!!)

    val unitChoices: List<GameUnit>
        get() = ScytheDatabase.unitDao()?.getUnitsAtLocation(hex!!)?.filter {
            UnitType.provokeUnits.contains(UnitType.valueOf(it.type))
        }?.map { GameUnit(it, TurnHolder.currentPlayer) }!!

    fun performRide() {
        TurnHolder.updateMove(*chosenUnits.map { it.pos = chosenHex!!.loc ; it.unitData }.toTypedArray() )
    }

    fun reset() {
        chosenHex = null
        chosenUnits.clear()
    }
}