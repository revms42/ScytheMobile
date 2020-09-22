package org.ajar.scythemobile.ui.trade

import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.player.Bank
import org.ajar.scythemobile.model.player.TopRowAction
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.TopRowViewModel

class TradeViewModel : TopRowViewModel<TopRowAction.Trade>() {

    private var _action: TopRowAction.Trade? = null
    override val action: TopRowAction.Trade
        get() {
            if(_action == null) {
                _action = TurnHolder.currentPlayer.playerMat.findTopRowAction(TopRowAction.Trade::class.java)
            }
            return _action!!
        }
    val cost: Int
        get() = action.cost.count()
    val popGain: Int
        get() = action.popularityGain
    val resourceGain: Int
        get() = action.resourceGain

    private var _resourceDistribution: HashMap<GameUnit, ArrayList<NaturalResourceType>>? = null
    val resourceDistrubution: Map<GameUnit, ArrayList<NaturalResourceType>>
        get() {
            if(_resourceDistribution == null) {
                _resourceDistribution = HashMap()
            }
            if(_resourceDistribution!!.isEmpty()) {
                _resourceDistribution!!.putAll(
                        ScytheDatabase.unitDao()?.getUnitsForPlayer(TurnHolder.currentPlayer.playerId, UnitType.WORKER.ordinal)?.filter { it.loc != -1 }?.map {
                            Pair(GameUnit(it, TurnHolder.currentPlayer), ArrayList<NaturalResourceType>())
                        }?.toMap()!!
                )
            }
            return _resourceDistribution!!
        }

    val choicesLeft: Int
        get() = resourceGain - resourceDistrubution.values.sumBy { it.size }

    var gainResources: Boolean = true

    val hasArmory: Boolean
            get() = TurnHolder.currentPlayer.selectUnits(UnitType.ARMORY)?.isNotEmpty()?:false

    fun performTrade() {
        if(TurnHolder.currentPlayer.coins >= cost){
            Bank.removeCoins(TurnHolder.currentPlayer.playerData, cost)
            if(gainResources) {
                resourceDistrubution.forEach { (unit, list) ->  list.forEach { resource -> ScytheAction.GiveNaturalResource(unit.pos, resource, 1) } }
            } else {
                ScytheAction.GiveCapitalResourceAction(TurnHolder.currentPlayer, CapitalResourceType.POPULARITY, popGain)
            }
            if(hasArmory) {
                ScytheAction.GiveCapitalResourceAction(TurnHolder.currentPlayer, CapitalResourceType.POWER, 1)
            }
        }
    }

    fun reset() {
        _resourceDistribution = null
        gainResources = false
    }
}