package org.ajar.scythemobile.ui.produce

import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.Resource
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.player.TopRowAction
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.TopRowViewModel
import java.lang.IllegalArgumentException

class ProduceViewModel : TopRowViewModel<TopRowAction.Produce>() {
    private var _action: TopRowAction.Produce? = null
    override val action: TopRowAction.Produce
        get() {
            if(_action == null) {
                _action = TopRowAction.Produce(TurnHolder.currentPlayer)
            }
            return _action!!
        }

    val cost: List<Resource>
        get()= action.cost

    val availableHexes: Set<MapHex>
        get() = TurnHolder.currentPlayer.selectUnits(UnitType.WORKER)?.map { GameMap.currentMap.findHexAtIndex(it.pos) }?.filterNotNull()!!.filter { it.producesResource }.toSet()

    val hexSelection = HashSet<MapHex>()

    private fun getMillHex(): MapHex? {
        return ScytheDatabase.unitDao()?.getUnitsForPlayer(TurnHolder.currentPlayer.playerId, UnitType.MILL.ordinal)?.first().let { unitData ->
            GameMap.currentMap.findHexAtIndex(unitData!!.loc)?.let { hex ->
                if (hex.playerInControl == unitData.owner) hex else null
            }
        }
    }

    fun performProduce() {
        if(cost.all { resource ->
            when(resource) {
                CapitalResourceType.COINS -> TurnHolder.currentPlayer.takeCoins(1, true)?.all { coin ->
                    ScytheAction.SpendResourceAction(coin.resourceData).perform()
                    true
                } == true
                CapitalResourceType.POWER -> ScytheAction.SpendPowerAction(TurnHolder.currentPlayer, 1).perform()
                CapitalResourceType.POPULARITY -> ScytheAction.SpendPopularityAction(TurnHolder.currentPlayer, 1).perform()
                else -> throw IllegalArgumentException("Should not have a produce cost that is not either coins, power, or popularity: $resource")
            }
        }) {
            getMillHex()?.also { hexSelection.add(it) }

            hexSelection.forEach { hex -> ScytheAction.ProduceAction(TurnHolder.currentPlayer, hex).perform() }
        }
    }

    fun reset() {
        _action = null
        hexSelection.clear()
    }
}