package org.ajar.scythemobile.model.player

import androidx.navigation.NavDirections
import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.R
import org.ajar.scythemobile.Resource
import org.ajar.scythemobile.data.PlayerMatData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.StartTurnFragmentDirections
import org.ajar.scythemobile.ui.build.BuildFragmentDirections
import org.ajar.scythemobile.ui.deploy.DeployFragmentDirections
import org.ajar.scythemobile.ui.enlist.EnlistFragmentDirections
import org.ajar.scythemobile.ui.upgrade.UpgradeFragmentDirections

class Section(val topRowAction: TopRowAction, val bottomRowAction: BottomRowAction, val moveTopToBottom: NavDirections)

interface PlayerMatAction {
    val fragmentNav: Int
    val playerInstance: PlayerInstance
    val playerMatData: PlayerMatData
        get() = playerInstance.playerData.playerMat
    val upgrades: Int
    val canUpgrade: Boolean
    val cost: List<Resource>
}

sealed class TopRowAction(override val playerInstance: PlayerInstance) : PlayerMatAction {
    class MoveOrGain(playerInstance: PlayerInstance) : TopRowAction(playerInstance) {
        override val fragmentNav: Int = R.id.nav_move
        override val actionInto: NavDirections = StartTurnFragmentDirections.actionNavStartToNavMove()

        private val data = playerMatData.moveGainSection

        override val upgrades: Int
            get() = (data.unitsMoved - 2) + (data.coinsGained - 1)

        override val cost: List<Resource> = emptyList()

        override val canUpgrade: Boolean
            get() = upgrades < 2

        override fun upgradeLeading(): Boolean {
            return if(data.unitsMoved < 3) {
                data.unitsMoved = 3
                true
            } else{
                false
            }
        }

        override fun upgradeFollowing(): Boolean {
            return if(data.coinsGained < 2) {
                data.coinsGained = 2
                true
            } else{
                false
            }
        }
    }
    class Trade(playerInstance: PlayerInstance) : TopRowAction(playerInstance) {
        override val fragmentNav: Int = R.id.nav_trade
        override val actionInto: NavDirections = StartTurnFragmentDirections.actionNavStartToNavTrade()

        private val data = playerMatData.tradeSection

        override val upgrades: Int
            get() = (data.popularityGain - 1)

        override val cost: List<Resource> = listOf(CapitalResourceType.COINS)

        override val canUpgrade: Boolean
            get() = upgrades < 1

        val popularityGain: Int
            get() = data.popularityGain

        val resourceGain: Int
            get() = data.resourceGain

        override fun upgradeLeading(): Boolean {
            return false
        }

        override fun upgradeFollowing(): Boolean {
            return if(data.popularityGain < 2) {
                data.popularityGain = 2
                true
            } else{
                false
            }
        }
    }
    class Produce(playerInstance: PlayerInstance) : TopRowAction(playerInstance) {
        override val fragmentNav: Int = R.id.nav_produce
        override val actionInto: NavDirections = StartTurnFragmentDirections.actionNavStartToNavProduce()

        private val data = playerMatData.produceSection

        override val upgrades: Int
            get() = (data.territories - 2)

        override val canUpgrade: Boolean
            get() = upgrades < 1

        override val cost: List<Resource>
            get() {
                return when(ScytheDatabase.unitDao()!!.getUnitsForPlayer(playerInstance.playerId, UnitType.WORKER.ordinal)?.size?: 0) {
                    in 0..3 -> emptyList()
                    in 4..5 -> listOf(CapitalResourceType.POWER)
                    in 6..7 -> listOf(CapitalResourceType.POWER, CapitalResourceType.POPULARITY)
                    else -> listOf(CapitalResourceType.POWER, CapitalResourceType.POPULARITY, CapitalResourceType.COINS)
                }
            }

        override fun upgradeLeading(): Boolean {
            return false
        }

        override fun upgradeFollowing(): Boolean {
            return if(data.territories < 2) {
                data.territories = 2
                true
            } else{
                false
            }
        }
    }
    class Bolster(playerInstance: PlayerInstance) : TopRowAction(playerInstance) {
        override val fragmentNav: Int = R.id.nav_bolster
        override val actionInto: NavDirections = StartTurnFragmentDirections.actionNavStartToNavBolster()

        private val data = playerMatData.bolsterSection

        override val upgrades: Int
            get() = (data.cardsGain - 1) + (data.powerGain - 2)

        override val canUpgrade: Boolean
            get() = upgrades < 2

        override val cost: List<Resource> = listOf(CapitalResourceType.COINS)

        val cardsGain: Int
            get() = data.cardsGain

        val powerGain: Int
            get() = data.powerGain

        override fun upgradeLeading(): Boolean {
            return if(data.powerGain < 3) {
                data.powerGain = 3
                true
            } else{
                false
            }
        }

        override fun upgradeFollowing(): Boolean {
            return if(data.cardsGain < 2) {
                data.cardsGain = 2
                true
            } else{
                false
            }
        }
    }

    abstract fun upgradeLeading() : Boolean
    abstract fun upgradeFollowing() : Boolean
    abstract val actionInto: NavDirections
}

sealed class BottomRowAction(override val playerInstance: PlayerInstance, val startingCost: Int, val costBottom: Int, val coins: Int) : PlayerMatAction {
    class Upgrade(playerInstance: PlayerInstance, startingCost: Int, costBottom: Int, coins: Int) : BottomRowAction(playerInstance, startingCost, costBottom, coins) {
        override val fragmentNav: Int = R.id.nav_upgrade
        override val actionOutOf: Int = R.id.action_nav_upgrade_to_nav_end

        private val data = playerMatData.upgradeSection

        override val upgrades: Int
            get() = (startingCost - data.oilCost)

        override val canUpgrade: Boolean
            get() = data.oilCost > costBottom

        override val recruitResource: Resource = CapitalResourceType.POWER
        override var recruited: Boolean
            get() = data.recruited
            set(value) {
                data.recruited = value
            }

        override val cost: List<Resource>
            get() = (1..data.oilCost).map { NaturalResourceType.OIL }

        override fun upgrade() {
            data.oilCost -= 1
        }
    }
    class Deploy(playerInstance: PlayerInstance, startingCost: Int, costBottom: Int, coins: Int) : BottomRowAction(playerInstance, startingCost, costBottom, coins) {
        override val fragmentNav: Int = R.id.nav_deploy
        override val actionOutOf: Int = R.id.action_nav_deploy_to_nav_end

        private val data = playerMatData.deploySection

        override val upgrades: Int
            get() = (startingCost - data.metalCost)

        override val canUpgrade: Boolean
            get() = data.metalCost > costBottom

        override val recruitResource: Resource = CapitalResourceType.COINS
        override var recruited: Boolean
            get() = data.recruited
            set(value) {
                data.recruited = value
            }

        override val cost: List<Resource>
            get() = (1..data.metalCost).map { NaturalResourceType.METAL }

        override fun upgrade() {
            data.metalCost -= 1
        }
    }
    class Build(playerInstance: PlayerInstance, startingCost: Int, costBottom: Int, coins: Int) : BottomRowAction(playerInstance, startingCost, costBottom, coins) {
        override val fragmentNav: Int = R.id.nav_build
        override val actionOutOf: Int = R.id.action_nav_build_to_nav_end

        private val data = playerMatData.buildSection

        override val upgrades: Int
            get() = (startingCost - data.woodCost)

        override val canUpgrade: Boolean
            get() = data.woodCost > costBottom

        override val recruitResource: Resource = CapitalResourceType.POPULARITY
        override var recruited: Boolean
            get() = data.recruited
            set(value) {
                data.recruited = value
            }

        override val cost: List<Resource>
            get() = (1..data.woodCost).map { NaturalResourceType.WOOD }

        fun getBuildableStructures() : List<GameUnit>? {
            return listOf(UnitType.MILL, UnitType.ARMORY, UnitType.MONUMENT, UnitType.MINE).flatMap { unitType ->
                ScytheDatabase.unitDao()?.getUnitsForPlayer(playerInstance.playerId, unitType.ordinal)?.map { unitData ->
                    GameUnit(unitData, playerInstance)
                }?: emptyList()
            }.filter { it.pos == -1 }
        }

        override fun upgrade() {
            data.woodCost -= 1
        }
    }
    class Enlist(playerInstance: PlayerInstance, startingCost: Int, costBottom: Int, coins: Int) : BottomRowAction(playerInstance, startingCost, costBottom, coins) {
        override val fragmentNav: Int = R.id.nav_enlist
        override val actionOutOf: Int = R.id.action_nav_enlist_to_nav_end

        private val data = playerMatData.enlistSection

        override val upgrades: Int
            get() = (startingCost - data.foodCost)

        override val canUpgrade: Boolean
            get() = data.foodCost > costBottom

        override val recruitResource: Resource = CapitalResourceType.CARDS
        override var recruited: Boolean
            get() = data.recruited
            set(value) {
                data.recruited = value
            }

        override val cost: List<Resource>
            get() = (1..data.foodCost).map { NaturalResourceType.FOOD }

        override fun upgrade() {
            data.foodCost -= 1
        }
    }

    abstract val recruitResource: Resource
    abstract val actionOutOf: Int
    abstract var recruited: Boolean
    abstract fun upgrade()
}