package org.ajar.scythemobile.model.factory

import androidx.collection.SparseArrayCompat
import androidx.collection.set
import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.R
import org.ajar.scythemobile.Resource
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.player.PlayerMatAction
import org.ajar.scythemobile.ui.factory.FactoryFragmentDirections

interface FactoryCard : PlayerMatAction {

    companion object {
        private val factoryCards = SparseArrayCompat<(PlayerInstance) -> FactoryCard>()

        operator fun set(id: Int, mat: (PlayerInstance) -> FactoryCard) {
            factoryCards[id] = mat
        }

        operator fun get(id: Int): ((PlayerInstance) -> FactoryCard)? = factoryCards[id]

        fun create(playerInstance: PlayerInstance) : FactoryCard? {
            return playerInstance.playerData.factoryCard?.let { FactoryCard[it]?.invoke(playerInstance) }
        }
    }
}

class FactoryMoveAction(override val playerInstance: PlayerInstance) : PlayerMatAction {
    override val fragmentNav: Int = R.id.nav_factory_move
    override val upgrades: Int = 0
    override val canUpgrade: Boolean = false
    override val cost: List<Resource> = emptyList()
}

sealed class StandardGainFactoryCard(override val playerInstance: PlayerInstance) : FactoryCard {
    class ProduceOnTwoHexes(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf("Produce On Two Hexes" to ::perform)
        override val cost: List<Resource> = listOf(CapitalResourceType.CARDS, CapitalResourceType.COINS)

        private fun perform() {
            FactoryFragmentDirections.actionNavFactoryToNavProduce(
                    numberOfHexes = 2,
                    cost = cost.map { it.id }.toIntArray(),
                    paid = false,
                    returnNav = R.id.action_nav_produce_to_nav_factory_move,
                    ignoreMill = true
            )
        }
    }
    class UpgradeOrEnlistForPop(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Enlist" to ::performFirst,
                "Upgrade" to ::performSecond
        )
        override val cost: List<Resource> = listOf(CapitalResourceType.POPULARITY)

        private fun performFirst() {
            if(ScytheAction.SpendPopularityAction(playerInstance, cost.size).perform()) {
                performEnlist(true)
            } else {
                FactoryFragmentDirections.actionNavFactoryToNavFactoryMove()
            }
        }

        private fun performSecond() {
            if(ScytheAction.SpendPopularityAction(playerInstance, cost.size).perform()) {
                performUpgrade(true)
            } else {
                FactoryFragmentDirections.actionNavFactoryToNavFactoryMove()
            }
        }
    }
    class CombatCardForPop(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Gain Two Popularity" to ::perform
        )
        override val cost: List<Resource> = listOf(CapitalResourceType.CARDS)

        private fun perform() {
            performCapitalForCapitalTrade(CapitalResourceType.POPULARITY, 2)
        }
    }
    class CombatCardForCash(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Gain Three Coins" to ::perform
        )
        override val cost: List<Resource> = listOf(CapitalResourceType.CARDS)

        private fun perform() {
            performCapitalForCapitalTrade(CapitalResourceType.COINS, 2)
        }
    }
    class PowerForPopularity(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Gain Two Popularity" to ::perform
        )
        override val cost: List<Resource> = listOf(CapitalResourceType.POWER)

        private fun perform() {
            performCapitalForCapitalTrade(CapitalResourceType.POPULARITY, 2)
        }
    }
    class PowerForCoins(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Gain Three Coins" to ::perform
        )
        override val cost: List<Resource> = listOf(CapitalResourceType.POWER)

        private fun perform() {
            performCapitalForCapitalTrade(CapitalResourceType.COINS, 3)
        }
    }
    class PopularityForDeployOrBuild(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Deploy a Mech" to ::performOne,
                "Build a Structure" to ::performTwo
        )
        override val cost: List<Resource> = listOf(CapitalResourceType.POPULARITY)

        private fun performOne() {
            if(ScytheAction.SpendPopularityAction(playerInstance, cost.size).perform()) {
                performDeploy(true)
            } else {
                FactoryFragmentDirections.actionNavFactoryToNavFactoryMove()
            }
        }

        private fun performTwo() {
            if(ScytheAction.SpendPopularityAction(playerInstance, cost.size).perform()) {
                performBuild(true)
            } else {
                FactoryFragmentDirections.actionNavFactoryToNavFactoryMove()
            }
        }
    }
    class CombatCardForResourceChoice(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Gain Three Resources" to ::perform
        )
        override val cost: List<Resource> = listOf(CapitalResourceType.CARDS)

        private fun perform() {
            if(payCapital()) {
                FactoryFragmentDirections.actionNavFactoryToNavChooseResources(
                        amount = 3,
                        deployFromUnit = UnitType.WORKER.ordinal,
                        returnNav = R.id.action_nav_choose_resources_to_nav_factory_move
                )
            } else {
                FactoryFragmentDirections.actionNavFactoryToNavFactoryMove()
            }
        }
    }
    class CoinsForUpgradeAndPopularity(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Upgrade and Gain Popularity" to ::perform
        )
        override val cost: List<Resource> = listOf(CapitalResourceType.COINS, CapitalResourceType.COINS)

        private fun perform() {
            if(payCapital()) {
                ScytheAction.GiveCapitalResourceAction(playerInstance, CapitalResourceType.POPULARITY, 1)
                performUpgrade(true)
            } else {
                FactoryFragmentDirections.actionNavFactoryToNavFactoryMove()
            }
        }
    }
    class CoinsForDeployAndPower(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Deploy and Gain Power" to ::perform
        )
        override val cost: List<Resource> = listOf(CapitalResourceType.COINS, CapitalResourceType.COINS)

        private fun perform() {
            if(payCapital()) {
                ScytheAction.GiveCapitalResourceAction(playerInstance, CapitalResourceType.POWER, 1)
                performDeploy(true)
            } else {
                FactoryFragmentDirections.actionNavFactoryToNavFactoryMove()
            }
        }
    }
    class CoinsForBuildAndPopularity(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Build and Gain Popularity" to ::perform
        )
        override val cost: List<Resource> = listOf(CapitalResourceType.COINS, CapitalResourceType.COINS)

        private fun perform() {
            if(payCapital()) {
                ScytheAction.GiveCapitalResourceAction(playerInstance, CapitalResourceType.POPULARITY, 1)
                performBuild(true)
            } else {
                FactoryFragmentDirections.actionNavFactoryToNavFactoryMove()
            }
        }
    }
    class CoinsForEnlistAndPower(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Enlist and Gain Power" to ::perform
        )
        override val cost: List<Resource> = listOf(CapitalResourceType.COINS, CapitalResourceType.COINS)

        private fun perform() {
            if(payCapital()) {
                ScytheAction.GiveCapitalResourceAction(playerInstance, CapitalResourceType.POWER, 1)
                performEnlist(true)
            } else {
                FactoryFragmentDirections.actionNavFactoryToNavFactoryMove()
            }
        }
    }
    class DissimilarForBuildOrDeploy(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Deploy a Mech" to ::performOne,
                "Build a Structure" to ::performTwo
        )
        override val cost: List<Resource> = listOf(NaturalResourceType.ANY_DISSIMILAR, NaturalResourceType.ANY_DISSIMILAR)

        private fun performOne() {
            performDeploy(false)
        }

        private fun performTwo() {
            performBuild(false)
        }
    }
    class CardForWorkerAndCoins(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Conscript Worker and Gain Coins" to ::perform
        )
        override val cost: List<Resource> = listOf(CapitalResourceType.CARDS)

        private fun perform() {
            if(payCapital()) {
                ScytheAction.GiveCapitalResourceAction(playerInstance, CapitalResourceType.COINS, 2)
                performDeployWorkerFromMech()
            } else {
                FactoryFragmentDirections.actionNavFactoryToNavFactoryMove()
            }
        }
    }
    class AnyResourceForCardAndPower(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Gain a Combat Card and Power" to ::perform
        )
        override val cost: List<Resource> = listOf(NaturalResourceType.ANY)

        private fun perform() {
            performChooseAnyResourceForBenefit(listOf(CapitalResourceType.CARDS, CapitalResourceType.POWER))
        }
    }
    class AnyResourceForPowerCoinAndPopularity(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Gain a Power, Popularity, and a Coin" to ::perform
        )
        override val cost: List<Resource> = listOf(NaturalResourceType.ANY)

        private fun perform() {
            performChooseAnyResourceForBenefit(listOf(CapitalResourceType.COINS, CapitalResourceType.POPULARITY))
        }
    }
    class CombatCardForPowerAndUpgrade(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Upgrade and Gain Power" to ::perform
        )
        override val cost: List<Resource> = listOf(CapitalResourceType.CARDS)

        private fun perform() {
            if(payCapital()) {
                ScytheAction.GiveCapitalResourceAction(playerInstance, CapitalResourceType.POWER, 1)
                performUpgrade(true)
            } else {
                FactoryFragmentDirections.actionNavFactoryToNavFactoryMove()
            }
        }
    }
    class DissimilarForEnlistOrUpgrade(playerInstance: PlayerInstance) : StandardGainFactoryCard(playerInstance) {
        override val choices: Map<String, () -> Unit> = mapOf(
                "Enlist a Recruit" to ::performOne,
                "Upgrade a Section" to ::performTwo
        )
        override val cost: List<Resource> = listOf(NaturalResourceType.ANY_DISSIMILAR, NaturalResourceType.ANY_DISSIMILAR)

        private fun performOne() {
            performEnlist(false)
        }

        private fun performTwo() {
            performUpgrade(false)
        }
    }

    abstract val choices: Map<String, () -> Unit>

    override val upgrades: Int = 0
    override val canUpgrade: Boolean = false
    override val fragmentNav: Int = R.id.nav_factory

    protected fun performEnlist(paid: Boolean) {
        FactoryFragmentDirections.actionNavFactoryToNavEnlist(
                paid = paid,
                cost = cost.map { it.id }.toIntArray(),
                returnNav = R.id.action_nav_enlist_to_nav_factory_move
        )
    }

    protected fun performUpgrade(paid: Boolean) {
        FactoryFragmentDirections.actionNavFactoryToNavUpgrade(
                paid = paid,
                cost = cost.map { it.id }.toIntArray(),
                returnNav = R.id.action_nav_upgrade_to_nav_factory_move
        )
    }

    protected fun performBuild(paid: Boolean) {
        FactoryFragmentDirections.actionNavFactoryToNavBuild(
                paid = paid,
                cost = cost.map { it.id }.toIntArray(),
                deployFromUnit = UnitType.WORKER.ordinal,
                returnNav = R.id.action_nav_build_to_nav_factory_move
        )
    }

    protected fun performDeploy(paid: Boolean) {
        FactoryFragmentDirections.actionNavFactoryToNavDeploy(
                paid = paid,
                cost = cost.map { it.id }.toIntArray(),
                deployFromUnit = UnitType.WORKER.ordinal,
                returnNav = R.id.action_nav_deploy_to_nav_factory_move
        )
    }

    protected fun performChooseAnyResourceForBenefit(benefit: List<Resource>) {
        FactoryFragmentDirections.actionNavFactoryToNavResourceChoicePayment(
                cost = cost.map { it.id }.toIntArray(),
                reward = benefit.map { it.id }.toIntArray()
        )
    }

    protected fun performDeployWorkerFromMech() {
        FactoryFragmentDirections.actionNavFactoryToNavDeployWorkerFromMech()
    }

    protected fun payCapital(): Boolean {
        return cost.all {
            if(it is CapitalResourceType) {
                when(it) {
                    CapitalResourceType.CARDS -> {
                        playerInstance.takeCombatCards(1, true)?.also { cards ->
                            cards.forEach { card -> CombatCardDeck.currentDeck.returnCard(card) }
                        }?.isNotEmpty()?:false
                    }
                    CapitalResourceType.POPULARITY -> {
                        ScytheAction.SpendPopularityAction(playerInstance, 1).perform()
                    }
                    CapitalResourceType.POWER -> {
                        ScytheAction.SpendPowerAction(playerInstance, 1).perform()
                    }
                    CapitalResourceType.COINS -> {
                        playerInstance.takeCoins(1, true)?.isNotEmpty()?: false
                    }
                }
            } else {
                false
            }
        }
    }

    protected fun performCapitalForCapitalTrade(type: CapitalResourceType, amount: Int) {
        if(payCapital()) {
            ScytheAction.GiveCapitalResourceAction(playerInstance, type, amount)
        }
        FactoryFragmentDirections.actionNavFactoryToNavFactoryMove()
    }

    companion object {
        init {
            FactoryCard[1]  = StandardGainFactoryCard::ProduceOnTwoHexes
            FactoryCard[2]  = StandardGainFactoryCard::UpgradeOrEnlistForPop
            FactoryCard[3]  = StandardGainFactoryCard::CombatCardForPop
            FactoryCard[4]  = StandardGainFactoryCard::CombatCardForCash
            FactoryCard[5]  = StandardGainFactoryCard::PowerForPopularity
            FactoryCard[6]  = StandardGainFactoryCard::PowerForCoins
            FactoryCard[7]  = StandardGainFactoryCard::PopularityForDeployOrBuild
            FactoryCard[8]  = StandardGainFactoryCard::CombatCardForResourceChoice
            FactoryCard[9]  = StandardGainFactoryCard::CoinsForUpgradeAndPopularity
            FactoryCard[10] = StandardGainFactoryCard::CoinsForDeployAndPower
            FactoryCard[11] = StandardGainFactoryCard::CoinsForBuildAndPopularity
            FactoryCard[12] = StandardGainFactoryCard::CoinsForEnlistAndPower
            FactoryCard[13] = StandardGainFactoryCard::DissimilarForBuildOrDeploy
            FactoryCard[14] = StandardGainFactoryCard::CardForWorkerAndCoins
            FactoryCard[15] = StandardGainFactoryCard::AnyResourceForCardAndPower
            FactoryCard[16] = StandardGainFactoryCard::AnyResourceForPowerCoinAndPopularity
            FactoryCard[17] = StandardGainFactoryCard::CombatCardForPowerAndUpgrade
            FactoryCard[18] = StandardGainFactoryCard::DissimilarForEnlistOrUpgrade
        }
    }
}