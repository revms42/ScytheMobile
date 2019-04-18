package org.ajar.scythemobile.model.playermat

import org.ajar.scythemobile.model.Mat
import org.ajar.scythemobile.model.PredefinedBinaryChoice
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.production.MapResourceType
import org.ajar.scythemobile.model.production.PlayerResourceType
import org.ajar.scythemobile.model.production.ResourceType
import org.ajar.scythemobile.model.turn.*


class PlayerMatInstance(playerMatModel: PlayerMatModel) {
    val sections: Set<SectionInstance> = playerMatModel.sections.map { SectionInstance(it) }.toSet()
}

data class SectionInstance(val sectionDef: SectionDef)

enum class PlayerMat(
        override val matName: String,
        override val matImage: Int,
        override val initialPopularity: Int,
        override val initialCoins: Int,
        override val initialObjectives: Int = 2) : PlayerMatModel
{
    AGRICULTURAL("Agricultural", -1, 4, 7) {
        override val sections: Set<SectionDef>
            get() = setOf(
                    SectionDef(
                            MoveOrGainAction(),
                            UpgradeAction(startingCost = 2, costBottom = 2, coinsGained = 1)
                    ),
                    SectionDef(
                            TradeAction(),
                            DeployAction(costStarting = 4, costBottom = 2, coinsGained = 0)
                    ),
                    SectionDef(
                            ProduceAction(),
                            BuildAction(costStarting = 4, costBottom = 2, coinsGained = 2)

                    ),
                    SectionDef(
                            BolsterAction(),
                            EnlistAction(costStarting = 3, costBottom = 1, coinsGained = 3)
                    )
            )

    },
    ENGINEERING("Engineering", -1, 2, 5) {
        override val sections: Set<SectionDef>
            get() = setOf(
                    SectionDef(
                            ProduceAction(),
                            UpgradeAction(startingCost = 3, costBottom = 2, coinsGained = 2)
                    ),
                    SectionDef(
                            TradeAction(),
                            DeployAction(costStarting = 4, costBottom = 2, coinsGained = 0)
                    ),
                    SectionDef(
                            BolsterAction(),
                            BuildAction(costStarting = 3, costBottom = 1, coinsGained = 3)

                    ),
                    SectionDef(
                            MoveOrGainAction(),
                            EnlistAction(costStarting = 3, costBottom = 2, coinsGained = 1)
                    )
            )

    },
    INDUSTRIAL("Industrial", -1, 2, 4) {
        override val sections: Set<SectionDef>
            get() = setOf(
                    SectionDef(
                            BolsterAction(),
                            UpgradeAction(startingCost = 3, costBottom = 2, coinsGained = 3)
                    ),
                    SectionDef(
                            ProduceAction(),
                            DeployAction(costStarting = 3, costBottom = 1, coinsGained = 2)
                    ),
                    SectionDef(
                            MoveOrGainAction(),
                            BuildAction(costStarting = 3, costBottom = 2, coinsGained = 1)

                    ),
                    SectionDef(
                            TradeAction(),
                            EnlistAction(costStarting = 4, costBottom = 2, coinsGained = 0)
                    )
            )

    },
    MECHANICAL("Mechanical", -1, 3, 6) {
        override val sections: Set<SectionDef>
            get() = setOf(
                    SectionDef(
                            TradeAction(),
                            UpgradeAction(startingCost = 3, costBottom = 2, coinsGained = 0)
                    ),
                    SectionDef(
                            BolsterAction(),
                            DeployAction(costStarting = 3, costBottom = 1, coinsGained = 2)
                    ),
                    SectionDef(
                            MoveOrGainAction(),
                            BuildAction(costStarting = 3, costBottom = 2, coinsGained = 2)

                    ),
                    SectionDef(
                            ProduceAction(),
                            EnlistAction(costStarting = 4, costBottom = 2, coinsGained = 2)
                    )
            )

    },
    PATRIOTIC("Patriotic", -1, 2, 6) {
        override val sections: Set<SectionDef>
            get() = setOf(
                    SectionDef(
                            MoveOrGainAction(),
                            UpgradeAction(startingCost = 2, costBottom = 2, coinsGained = 1)
                    ),
                    SectionDef(
                            BolsterAction(),
                            DeployAction(costStarting = 4, costBottom = 1, coinsGained = 3)
                    ),
                    SectionDef(
                            TradeAction(),
                            BuildAction(costStarting = 4, costBottom = 2, coinsGained = 0)

                    ),
                    SectionDef(
                            ProduceAction(),
                            EnlistAction(costStarting = 3, costBottom = 2, coinsGained = 0)
                    )
            )

    },
    MILITANT("Militant", -1, 3, 4) {
        override val sections: Set<SectionDef>
            get() = setOf(
                    SectionDef(
                            BolsterAction(),
                            UpgradeAction(startingCost = 3, costBottom = 1, coinsGained = 0)
                    ),
                    SectionDef(
                            MoveOrGainAction(),
                            DeployAction(costStarting = 3, costBottom = 2, coinsGained = 3)
                    ),
                    SectionDef(
                            ProduceAction(),
                            BuildAction(costStarting = 4, costBottom = 3, coinsGained = 1)

                    ),
                    SectionDef(
                            TradeAction(),
                            EnlistAction(costStarting = 3, costBottom = 1, coinsGained = 2)
                    )
            )

    },
    INNOVATIVE("Innovative", -1, 3, 5) {
        override val sections: Set<SectionDef>
            get() = setOf(
                    SectionDef(
                            TradeAction(),
                            UpgradeAction(startingCost = 3, costBottom = 3, coinsGained = 3)
                    ),
                    SectionDef(
                            ProduceAction(),
                            DeployAction(costStarting = 3, costBottom = 2, coinsGained = 1)
                    ),
                    SectionDef(
                            BolsterAction(),
                            BuildAction(costStarting = 4, costBottom = 1, coinsGained = 2)

                    ),
                    SectionDef(
                            MoveOrGainAction(),
                            EnlistAction(costStarting = 3, costBottom = 1, coinsGained = 0)
                    )
            )

    };
}

data class UpgradeDef(val name: String, val check: () -> Boolean, val perform: () -> Unit)

class SectionDef(val topRowAction: TopRowAction, val bottomRowAction: BottomRowAction) {

    val sectionSelectable: (player: Player) -> Boolean = { player: Player ->  isSectionSelectable(player) }

    private fun isSectionSelectable(player: Player) : Boolean {
        val all = ArrayList(topRowAction.actionClassTypes)
        all.addAll(bottomRowAction.actionClassTypes)

        return player.turn.hasAnyOfTypes(all)
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
    val cost: List<ResourceType>
}

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

    override var canPerform: (player: Player) -> Boolean = {player: Player -> canPerformBolster(player)}
    override var performAction: (player: Player) -> Unit = {player: Player -> performBolster(player)}
}

class MoveOrGainAction(
        override val name: String = "Move/Gain Action",
        override val image: Int = -1,
        override val cost: List<ResourceType> = emptyList(),
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
    private val numberOfUnits: Int
        get() = _numberOfUnits

    private var _numberOfCoins: Int = numberOfCoinsStart
    private val numberOfCoins: Int
        get() = _numberOfCoins

    private val canUpgradeUnits: () -> Boolean = {numberOfUnits < numberOfUnitsTop}
    private val canUpgradeCoins: () -> Boolean = {numberOfCoins < numberOfCoinsTop}

    private val upgradeNumberOfUnits: () -> Unit = {_numberOfUnits++}
    private val upgradeNumberOfCoins: () -> Unit = {_numberOfCoins++}

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
    override val cost: List<ResourceType> = listOf(PlayerResourceType.COIN),
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
    private val resourceGain: Int
        get() = _resourceGain

    private var _popularityGain: Int = popularityGainStart
    private val popularityGain: Int
        get() = _popularityGain

    private val canUpgradeResource: () -> Boolean = {resourceGain < resourceGainTop}
    private val canUpgradePopularity: () -> Boolean = {popularityGain < popularityGainTop}

    private val upgradeResources: () -> Unit = {_resourceGain++}
    private val upgradePopularity: () -> Unit = {_popularityGain++}

    private fun canPerformTrade(player: Player) : Boolean {
        return player.canPay(cost)
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
        private val numberOfTerritoriesTop: Int = 3
) : TopRowAction {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(ProduceTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(
                UpgradeDef("Upgrade Territories Harvested", canUpgradeTerritories, upgradeTerritories)
        )

    private val numberOfWorkersTop: Int = 6
    private val _numberOfWorkersProduced: Int = 0
    private val numberOfWorkersProduced: Int
        get() = _numberOfWorkersProduced

    override val cost: List<ResourceType>
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
    private val numberOfTerritories: Int
        get() = _numberOfTerritories

    private val canProduceWorker: () -> Boolean = { numberOfWorkersProduced < numberOfTerritoriesTop }
    private val canUpgradeTerritories: () -> Boolean = { numberOfTerritories < numberOfTerritoriesTop }


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

interface BottomRowAction : PlayerMatAction {
    val coinsGained: Int
    val cost: List<ResourceType>

    val isEnlisted: Boolean
    fun enlist(player: Player)
}

abstract class AbstractBottomRowAction(
        private val enlistBonus: PlayerResourceType,
        costStarting: Int,
        costBottom: Int,
        private val resourceType: MapResourceType
) : BottomRowAction {
    private var _enlisted: Boolean = false

    var canUpgrade: () -> Boolean = { _cost > costBottom }
    var upgrade: () -> Unit = {_cost--}

    override val isEnlisted: Boolean
        get() = _enlisted
    override fun enlist(player: Player) {
        _enlisted = true
        when(enlistBonus) {
            PlayerResourceType.COIN -> player.coins += 2
            PlayerResourceType.POPULARITY -> player.popularity += 2
            PlayerResourceType.POWER -> player.power += 2
            PlayerResourceType.COMBAT_CARD -> {
                player.combatCards.addAll(listOf(CombatCardDeck.currentDeck.drawCard(), CombatCardDeck.currentDeck.drawCard()))
            }
        }
    }

    private var _cost: Int = costStarting
    override val cost: List<ResourceType>
        get() = (0.._cost).map { resourceType }
}

class UpgradeAction(
        override val name: String = "Upgrade",
        override val image: Int = -1,
        startingCost: Int,
        costBottom: Int,
        override val coinsGained: Int,
        enlistBonus: PlayerResourceType = PlayerResourceType.POWER
) : AbstractBottomRowAction(enlistBonus, startingCost, costBottom, MapResourceType.OIL) {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(UpgradeTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
            get() = listOf(UpgradeDef("Improve Upgrade", canUpgrade, upgrade))

    private fun canPerformUpgrade(player: Player) : Boolean{
        return player.canPay(cost) && player.playerMat.sections.firstOrNull { sectionDef ->
            sectionDef.sectionDef.topRowAction.upgradeActions.firstOrNull { it.check.invoke() } != null ||
            sectionDef.sectionDef.bottomRowAction.upgradeActions.firstOrNull { it.check.invoke() } != null
        } != null
    }

    private fun performUpgrade(player: Player) {
        TODO("PERFORM UPGRADE")
    }

    override var canPerform: (player: Player) -> Boolean = { player: Player -> canPerformUpgrade(player) }
    override var performAction: (player: Player) -> Unit = { player: Player -> performUpgrade(player) }
}

class DeployAction(
        override val name: String = "Deploy",
        override val image: Int = -1,
        costStarting: Int,
        costBottom: Int,
        override val coinsGained: Int,
        enlistBonus: PlayerResourceType = PlayerResourceType.COIN
) : AbstractBottomRowAction(enlistBonus, costStarting, costBottom, MapResourceType.METAL) {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(DeployTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(UpgradeDef("Improve Deploy", canUpgrade, upgrade))

    private fun canPerformDeploy(player: Player) : Boolean {
        return player.canPay(cost) && player.factionMat.unlockedMechAbility.size < 4
    }

    private fun performDeploy(player: Player) {
        TODO("PERFORM DEPLOY")
    }

    override var canPerform: (player: Player) -> Boolean = { player: Player -> canPerformDeploy(player) }
    override var performAction: (player: Player) -> Unit = { player: Player -> performDeploy(player) }

}

class BuildAction(
        override val name: String = "Build",
        override val image: Int = -1,
        costStarting: Int,
        costBottom: Int,
        override val coinsGained: Int,
        enlistBonus: PlayerResourceType = PlayerResourceType.POPULARITY
) : AbstractBottomRowAction(enlistBonus, costStarting, costBottom, MapResourceType.WOOD) {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(BuildTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(UpgradeDef("Improve Deploy", canUpgrade, upgrade))

    private fun canPerformBuild(player: Player) : Boolean {
        return player.canPay(cost) && player.selectUnits(UnitType.STRUCTURE).size < 4
    }

    private fun performBuild(player: Player) {
        TODO("PERFORM BUILD")
    }

    override var canPerform: (player: Player) -> Boolean = { player: Player -> canPerformBuild(player) }
    override var performAction: (player: Player) -> Unit = { player: Player -> performBuild(player) }

}

class EnlistAction(
        override val name: String = "Enlist",
        override val image: Int = -1,
        costStarting: Int,
        costBottom: Int,
        override val coinsGained: Int,
        enlistBonus: PlayerResourceType = PlayerResourceType.COMBAT_CARD
) : AbstractBottomRowAction(enlistBonus, costStarting, costBottom, MapResourceType.FOOD) {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(EnlistTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(UpgradeDef("Improve Enlist", canUpgrade, upgrade))

    private fun canPerformEnlist(player: Player) : Boolean {
        return player.canPay(cost) && player.playerMat.sections.any { !it.sectionDef.bottomRowAction.isEnlisted }
    }

    private fun performEnlist(player: Player) {
        TODO("PERFORM ENLIST")
    }

    override var canPerform: (player: Player) -> Boolean = { player: Player -> canPerformEnlist(player) }
    override var performAction: (player: Player) -> Unit = { player: Player -> performEnlist(player) }

}

interface PlayerMatModel : Mat {
    val initialPopularity: Int
    val initialCoins: Int
    val initialObjectives: Int
    val sections: Set<SectionDef>

}
