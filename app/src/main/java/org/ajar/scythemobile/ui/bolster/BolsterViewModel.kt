package org.ajar.scythemobile.ui.bolster

import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.player.TopRowAction
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.TopRowViewModel

class BolsterViewModel : TopRowViewModel<TopRowAction.Bolster>() {
    private var _action: TopRowAction.Bolster? = null
    override val action: TopRowAction.Bolster
        get() {
            if(_action == null) {
                _action = TurnHolder.currentPlayer.playerMat.findTopRowAction(TopRowAction.Bolster::class.java)
            }
            return _action!!
        }

    val cardsGain: Int
        get() = action.cardsGain

    val powerGain: Int
        get() = action.powerGain

    var obtainCards: Boolean = false

    val cost: Int
        get() = action.cost.size


    fun performBolster() {
        TurnHolder.currentPlayer.takeCoins(cost, true)?.also {
            it.forEach { resource -> ScytheAction.SpendResourceAction(resource.resourceData).perform() }
            if(obtainCards) {
                repeat(cardsGain) { CombatCardDeck.currentDeck.drawCard(TurnHolder.currentPlayer) }
            } else {
                ScytheAction.GiveCapitalResourceAction(TurnHolder.currentPlayer, CapitalResourceType.POWER, powerGain).perform()
            }
        }
    }

    fun reset() {
        obtainCards = false
    }
}