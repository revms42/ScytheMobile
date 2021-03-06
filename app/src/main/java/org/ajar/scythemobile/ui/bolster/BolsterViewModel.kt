package org.ajar.scythemobile.ui.bolster

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.model.CapitalResourceType
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.player.Bank
import org.ajar.scythemobile.model.player.TopRowAction
import org.ajar.scythemobile.turn.TurnHolder

class BolsterViewModel : ViewModel() {
    private var _action: TopRowAction.Bolster? = null
    val action: TopRowAction.Bolster
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
        if(TurnHolder.currentPlayer.coins >= cost) {
            Bank.removeCoins(TurnHolder.currentPlayer.playerData, cost)
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