package org.ajar.scythemobile.model.playermat

import org.ajar.scythemobile.model.Mat
import org.ajar.scythemobile.model.PredefinedBinaryChoice
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.production.PlayerResourceType
import org.ajar.scythemobile.model.production.ResourceType
import org.ajar.scythemobile.model.turn.*


class PlayerMatInstance(val playerMatModel: PlayerMatModel) {

    val sections: Set<SectionInstance> = playerMatModel.sections.map { SectionInstance(it) }.toSet()

    var currentSection: SectionInstance? = null
}

class SectionInstance(val sectionDef: SectionDef) {

}

enum class PlayerMat(
        override val matName: String,
        override val matImage: Int,
        override val initialPopularity: Int,
        override val initialCoins: Int,
        override val initialObjectives: Int) : PlayerMatModel
{
    MECHANICAL("Mechanical", 0, 3, 6, 2) {
        override val sections: Set<SectionDef>
            get() = setOf() //TODO: Put stuff here.
    };
}

data class UpgradeDef(val name: String, val check: () -> Boolean, val perform: () -> Unit)

interface SectionDef {
    val topRowAction: TopRowAction
    val bottomRowAction: BottomRowAction

    val sectionSelectable: (player: Player) -> Boolean

    fun isSectionSelectable(player: Player) : Boolean {
        val all = ArrayList(topRowAction.actionClassTypes)
        all.addAll(bottomRowAction.actionClassTypes)

        return player.turn.hasAnyOfTupes(all)
    }
}

interface PlayerMatAction {
    val name: String
    val image:  Int
    val upgradeActions: Collection<UpgradeDef>

    var canPerform: (player: Player) -> Boolean
    var performAction: (player: Player) -> Unit

    val actionClassTypes: Collection<Class<out TurnAction>>
}

interface TopRowAction : PlayerMatAction {
}

interface BottomRowAction : PlayerMatAction {
    val coinsGained: Int
    val canUpgradeCost: Boolean
    fun upgradeCost()
}

class BolsterAction(
        override val name: String = "Bolster",
        override val image: Int = -1,
        val cost: List<ResourceType> = listOf(PlayerResourceType.COIN),
        powerGainStart: Int = 1,
        cardGainStart: Int = 1,
        private val powerGainTop: Int = 2,
        private val cardGainTop: Int = 2
) : TopRowAction {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(BolsterTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(
                UpgradeDef("Upgrade Power Gained", canUpgradePower, upgradePowerGain),
                UpgradeDef("Upgrade Cards Gained", canUpgradeCard, upgradeCardGain)
        )

    private var _powerGain: Int = powerGainStart
    val powerGain: Int
        get() = _powerGain

    private var _cardGain: Int = cardGainStart
    val cardGain: Int
        get() = _cardGain

    var canUpgradePower: () -> Boolean = {powerGain < powerGainTop}
    var canUpgradeCard: () -> Boolean = {cardGain < cardGainTop}

    var upgradePowerGain: () -> Unit = {_powerGain++}
    var upgradeCardGain: () -> Unit = {_cardGain++}

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

    override var canPerform: (player: Player) -> Boolean = {player: Player -> canPerformBolster(player)}
    override var performAction: (player: Player) -> Unit = {player: Player -> performBolster(player)}
}

class MoveOrGainAction(
        override val name: String = "Move/Gain Action",
        override val image: Int = -1,
        val cost: List<ResourceType> = emptyList(),
        numberOfUnitsStart: Int = 2,
        numberOfCoinsStart: Int = 1,
        private val numberOfUnitsTop: Int = 3,
        private val numberOfCoinsTop: Int = 2

) : TopRowAction {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(MoveTurnAction::class.java, GainTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(
                UpgradeDef("Upgrade Number of Units to Move", canUpgradeUnits, upgradeNumberOfUnits),
                UpgradeDef("Upgrade Number of Coins to Gain", canUpgradeCoins, upgradeNumberOfCoins)
        )
    private var _numberOfUnits: Int = numberOfUnitsStart
    val numberOfUnits: Int
        get() = _numberOfUnits

    private var _numberOfCoins: Int = numberOfCoinsStart
    val numberOfCoins: Int
        get() = _numberOfCoins

    var canUpgradeUnits: () -> Boolean = {numberOfUnits < numberOfUnitsTop}
    var canUpgradeCoins: () -> Boolean = {numberOfCoins < numberOfCoinsTop}

    var upgradeNumberOfUnits: () -> Unit = {_numberOfUnits++}
    var upgradeNumberOfCoins: () -> Unit = {_numberOfCoins++}

    private fun canPerformMoveOrGain(player: Player): Boolean {
        return true
    }

    private fun performMoveOrGain(player: Player) {
        TODO("PERFORM MOVE OR GAIN")
    }

    override var canPerform: (player: Player) -> Boolean = { player: Player -> canPerformMoveOrGain(player) }
    override var performAction: (player: Player) -> Unit = { player: Player -> performMoveOrGain(player) }
}

class TradeAction (
    override val name: String = "Trade Action",
    override val image: Int = -1,
    val cost: List<ResourceType> = listOf(PlayerResourceType.COIN),
    resourceGainStart: Int = 2,
    popularityGainStart: Int = 1,
    private val resourceGainTop: Int = 2,
    private val popularityGainTop: Int = 2
) : TopRowAction {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(TradeTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(
                UpgradeDef("Upgrade Resources Gained", canUpgradeResource, upgradeResources),
                UpgradeDef("Upgrade Popularity Gained", canUpgradePopularity, upgradePopularity)
        )

    private var _resourceGain: Int = resourceGainStart
    val resourceGain: Int
        get() = _resourceGain

    private var _popularityGain: Int = popularityGainStart
    val popularityGain: Int
        get() = _popularityGain

    var canUpgradeResource: () -> Boolean = {resourceGain < resourceGainTop}
    var canUpgradePopularity: () -> Boolean = {popularityGain < popularityGainTop}

    var upgradeResources: () -> Unit = {_resourceGain++}
    var upgradePopularity: () -> Unit = {_popularityGain++}

    private fun canPerformTrade(player: Player) : Boolean {
        return player.coins > 0
    }

    private fun performTrade(player: Player) {
        TODO("TRADE")
    }

    override var canPerform: (player: Player) -> Boolean = {player: Player ->  canPerformTrade(player)}
    override var performAction: (player: Player) -> Unit = {player: Player -> performTrade(player) }
}

class ProduceAction (
        override val name: String = "Produce Action",
        override val image: Int = -1,
        numberofTerritoriesStart: Int = 2,
        private val numberOfTerritoriesTop: Int = 2
) : TopRowAction {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(ProduceTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(
                UpgradeDef("Upgrade Territories Harvested", canUpgradeTerritories, upgradeTerritories)
        )

    private val numberOfWorkersTop: Int = 6
    var _numberOfWorkersProduced: Int = 0
    val numberOfWorkersProduced: Int
        get() = _numberOfWorkersProduced

    val cost: List<ResourceType>
        get() {
            return (0..numberOfWorkersProduced).filter { it > 2 && it % 2 == 0 }.map {
                when (it) {
                    2 -> PlayerResourceType.POWER
                    4 -> PlayerResourceType.POPULARITY
                    else -> PlayerResourceType.COIN
                }
            }
        }


    private var _numberOfTerritories: Int = numberofTerritoriesStart
    val numberOfTerritories: Int
        get() = _numberOfTerritories

    var canProduceWorker: () -> Boolean = { numberOfWorkersProduced < numberOfTerritoriesTop }
    var canUpgradeTerritories: () -> Boolean = { numberOfTerritories < numberOfTerritoriesTop }


    var upgradeTerritories: () -> Unit = { _numberOfTerritories++ }

    private fun canProduce(player: Player): Boolean {
        return player.canPay(cost)
    }

    private fun performProduce(player: Player) {
        TODO("PRODUCE")
    }

    private fun produceWorker(player: Player) {
        TODO("PRODUCE WORKER")
    }

    override var canPerform: (player: Player) -> Boolean = { player: Player -> canProduce(player) }
    override var performAction: (player: Player) -> Unit = { player: Player -> performProduce(player) }
}

interface UpgradeAction : BottomRowAction {
    val cost: List<ResourceType>

    fun performUpgrade(player: Player)
}

interface DeployAction : BottomRowAction {
    val cost: List<ResourceType>

    fun performDeploy(player: Player)
}

interface BuildAction : BottomRowAction {
    val cost: List<ResourceType>

    fun performBuild(player: Player)
}

interface Enlist : BottomRowAction {
    val cost: List<ResourceType>

    fun performEnlist(player: Player)
}

interface PlayerMatModel : Mat {
    val initialPopularity: Int
    val initialCoins: Int
    val initialObjectives: Int
    val sections: Set<SectionDef>

}
