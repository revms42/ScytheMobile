package org.ajar.scythemobile.model.playermat

import org.ajar.scythemobile.model.PredefinedBinaryChoice
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.production.PlayerResourceType
import org.ajar.scythemobile.model.production.ResourceType
import org.ajar.scythemobile.model.turn.BolsterTurnAction
import org.ajar.scythemobile.model.turn.TurnAction

class BolsterAction(
        override val name: String = "Bolster",
        override val image: Int = -1,
        override val cost: List<ResourceType> = listOf(PlayerResourceType.COIN),
        powerGainStart: Int = 2,
        cardGainStart: Int = 1,
        private val powerGainTop: Int = 3,
        private val cardGainTop: Int = 2
) : TopRowAction {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(BolsterTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(
                UpgradeDef("Upgrade Power Gained", canUpgradePower, upgradePowerGain),
                UpgradeDef("Upgrade Cards Gained", canUpgradeCard, upgradeCardGain)
        )

    private var _powerGain: Int = powerGainStart
    private val powerGain: Int
        get() = _powerGain

    private var _cardGain: Int = cardGainStart
    private val cardGain: Int
        get() = _cardGain

    private val canUpgradePower: () -> Boolean = {powerGain < powerGainTop}
    private val canUpgradeCard: () -> Boolean = {cardGain < cardGainTop}

    private val upgradePowerGain: () -> Unit = {_powerGain++}
    private val upgradeCardGain: () -> Unit = {_cardGain++}

    private fun performBolster(player: Player) {
        player.payResources(cost)

        if(player.user.requester?.requestBinaryChoice(PredefinedBinaryChoice.BOLSTER_SELECTION) == true) {
            for(i in 0..cardGain) {
                player.combatCards.add(CombatCardDeck.currentDeck.drawCard())
            }
            player.turn.performAction(BolsterTurnAction(cardGain, true))
        } else {
            player.power += powerGain
            player.turn.performAction(BolsterTurnAction(powerGain, false))
        }
    }

    private fun canPerformBolster(player: Player) : Boolean {
        return player.canPay(cost)
    }

    override var canPerform: (player: Player) -> Boolean = { player: Player -> canPerformBolster(player)}
    override var performAction: (player: Player) -> Unit = { player: Player -> performBolster(player)}
}