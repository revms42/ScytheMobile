package org.ajar.scythemobile.model.player

import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.R
import org.ajar.scythemobile.Resource
import org.ajar.scythemobile.data.PlayerMatData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.UnitType

class Section(val topRowAction: TopRowAction, val bottomRowAction: BottomRowAction)

interface PlayerMatAction {
    val fragmentNav: Int
    val playerInstance: PlayerInstance
    val playerMatData: PlayerMatData
        get() = playerInstance.playerData.playerMat
    val upgrades: Int
    val cost: List<Resource>
}

sealed class TopRowAction(override val playerInstance: PlayerInstance) : PlayerMatAction {
    class MoveOrGain(playerInstance: PlayerInstance) : TopRowAction(playerInstance) {
        override val fragmentNav: Int = R.id.nav_move

        private val data = playerMatData.moveGainSection

        override val upgrades: Int
            get() = (data.unitsMoved - 2) + (data.coinsGained - 1)

        override val cost: List<Resource> = emptyList()
    }
    class Trade(playerInstance: PlayerInstance) : TopRowAction(playerInstance) {
        override val fragmentNav: Int = R.id.nav_trade

        private val data = playerMatData.tradeSection

        override val upgrades: Int
            get() = (data.popularityGain - 1)

        override val cost: List<Resource> = listOf(CapitalResourceType.COINS)
    }
    class Produce(playerInstance: PlayerInstance) : TopRowAction(playerInstance) {
        override val fragmentNav: Int = R.id.nav_produce

        private val data = playerMatData.produceSection

        override val upgrades: Int
            get() = (data.territories - 2)

        override val cost: List<Resource>
            get() {
                return when(ScytheDatabase.unitDao()!!.getUnitsForPlayer(playerInstance.playerId, UnitType.WORKER.ordinal)?.size?: 0) {
                    in 0..3 -> emptyList()
                    in 4..5 -> listOf(CapitalResourceType.POWER)
                    in 6..7 -> listOf(CapitalResourceType.POWER, CapitalResourceType.POPULARITY)
                    else -> listOf(CapitalResourceType.POWER, CapitalResourceType.POPULARITY, CapitalResourceType.COINS)
                }
            }
    }
    class Bolster(playerInstance: PlayerInstance) : TopRowAction(playerInstance) {
        override val fragmentNav: Int = R.id.nav_bolster

        private val data = playerMatData.bolsterSection

        override val upgrades: Int
            get() = (data.cardsGain - 1) + (data.powerGain - 2)

        override val cost: List<Resource> = listOf(CapitalResourceType.COINS)
    }
}

sealed class BottomRowAction(override val playerInstance: PlayerInstance, val startingCost: Int, val costBottom: Int, val coins: Int) : PlayerMatAction {
    class Upgrade(playerInstance: PlayerInstance, startingCost: Int, costBottom: Int, coins: Int) : BottomRowAction(playerInstance, startingCost, costBottom, coins) {
        override val fragmentNav: Int = R.id.nav_upgrade

        private val data = playerMatData.upgradeSection

        override val upgrades: Int
            get() = (startingCost - data.oilCost)

        override val recruitResource: Resource = CapitalResourceType.POWER
        override val recruited: Boolean
            get() = data.recruited

        override val cost: List<Resource>
            get() = (1..data.oilCost).map { NaturalResourceType.OIL }
    }
    class Deploy(playerInstance: PlayerInstance, startingCost: Int, costBottom: Int, coins: Int) : BottomRowAction(playerInstance, startingCost, costBottom, coins) {
        override val fragmentNav: Int = R.id.nav_deploy

        private val data = playerMatData.deploySection

        override val upgrades: Int
            get() = (startingCost - data.metalCost)

        override val recruitResource: Resource = CapitalResourceType.COINS
        override val recruited: Boolean
            get() = data.recruited

        override val cost: List<Resource>
            get() = (1..data.metalCost).map { NaturalResourceType.METAL }
    }
    class Build(playerInstance: PlayerInstance, startingCost: Int, costBottom: Int, coins: Int) : BottomRowAction(playerInstance, startingCost, costBottom, coins) {
        override val fragmentNav: Int = R.id.nav_build

        private val data = playerMatData.buildSection

        override val upgrades: Int
            get() = (startingCost - data.woodCost)

        override val recruitResource: Resource = CapitalResourceType.POPULARITY
        override val recruited: Boolean
            get() = data.recruited

        override val cost: List<Resource>
            get() = (1..data.woodCost).map { NaturalResourceType.WOOD }
    }
    class Enlist(playerInstance: PlayerInstance, startingCost: Int, costBottom: Int, coins: Int) : BottomRowAction(playerInstance, startingCost, costBottom, coins) {
        override val fragmentNav: Int = R.id.nav_enlist

        private val data = playerMatData.enlistSection

        override val upgrades: Int
            get() = (startingCost - data.foodCost)

        override val recruitResource: Resource = CapitalResourceType.CARDS
        override val recruited: Boolean
            get() = data.recruited

        override val cost: List<Resource>
            get() = (1..data.foodCost).map { NaturalResourceType.FOOD }
    }

    abstract val recruitResource: Resource
    abstract val recruited: Boolean
}