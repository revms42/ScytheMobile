package org.ajar.scythemobile.model.player

import android.util.SparseArray
import androidx.core.util.set
import org.ajar.scythemobile.data.*
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.ui.bolster.BolsterFragmentDirections
import org.ajar.scythemobile.ui.move.MoveFragmentDirections
import org.ajar.scythemobile.ui.produce.ProduceFragmentDirections
import org.ajar.scythemobile.ui.trade.TradeFragmentDirections

interface PlayerMat {

    val matName: String
    val matImage: Int
    val initialPopularity: Int
    val initialCoins: Int
    val initialObjectives: Int

    val upgradeStart: Int
    val upgradeBottom: Int
    val upgradeCoins: Int

    val deployStart: Int
    val deployBottom: Int
    val deployCoins: Int

    val buildStart: Int
    val buildBottom: Int
    val buildCoins: Int

    val enlistStart: Int
    val enlistBottom: Int
    val enlistCoins: Int

    val id: Int
        get() = playerMats.indexOfValue(this)

    fun createData(): PlayerMatData {
        val tradeSectionData = TradeSectionData(2, 1)
        val produceSectionData = ProduceSectionData(2)
        val bolsterSectionData = BolsterSectionData(2, 1)
        val moveGainSectionData = MoveGainSectionData(1, 2)

        val upgradeSectionData = UpgradeSectionData(upgradeStart, false)
        val deploySectionData = DeploySectionData(deployStart, false)
        val buildSectionData = BuildSectionData(buildStart, false)
        val enlistSectionData = EnlistSectionData(enlistStart, false)

        return PlayerMatData(id, -1,
                tradeSectionData,
                produceSectionData,
                bolsterSectionData,
                moveGainSectionData,

                upgradeSectionData,
                deploySectionData,
                buildSectionData,
                enlistSectionData
        )
    }
    
    fun makeSections(playerInstance: PlayerInstance): List<Section>

    companion object {
        private val playerMats = SparseArray<PlayerMat>()

        operator fun set(id: Int, mat: PlayerMat) {
            playerMats[id] = mat
        }

        operator fun get(id: Int): PlayerMat? = playerMats[id]
    }
}

enum class StandardPlayerMat(
        override val matName: String,
        override val matImage: Int,
        override val initialPopularity: Int,
        override val initialCoins: Int,
        override val initialObjectives: Int = 2
) : PlayerMat {
    AGRICULTURAL("Agricultural", -1, 4, 7) {
        override val upgradeStart = 2
        override val upgradeBottom = 2
        override val upgradeCoins = 2

        override val deployStart = 4
        override val deployBottom = 2
        override val deployCoins = 0

        override val buildStart = 4
        override val buildBottom = 2
        override val buildCoins = 2

        override val enlistStart = 3
        override val enlistBottom = 1
        override val enlistCoins = 3

        override fun makeSections(playerInstance: PlayerInstance): List<Section> {
            return listOf(
                    Section(TopRowAction.MoveOrGain(playerInstance), BottomRowAction.Upgrade(playerInstance, upgradeStart, upgradeBottom, upgradeCoins), MoveFragmentDirections.actionNavMoveToNavUpgrade()),
                    Section(TopRowAction.Trade(playerInstance), BottomRowAction.Deploy(playerInstance, deployStart, deployBottom, deployCoins), TradeFragmentDirections.actionNavTradeToNavDeploy()),
                    Section(TopRowAction.Produce(playerInstance), BottomRowAction.Build(playerInstance, buildStart, buildBottom, buildCoins), ProduceFragmentDirections.actionNavProduceToNavBuild()),
                    Section(TopRowAction.Bolster(playerInstance), BottomRowAction.Enlist(playerInstance, enlistStart, enlistBottom, enlistCoins), BolsterFragmentDirections.actionNavBolsterToNavEnlist())
            )
        }

    },
    ENGINEERING("Engineering", -1, 2, 5) {
        override val upgradeStart = 3
        override val upgradeBottom = 2
        override val upgradeCoins = 2

        override val deployStart = 4
        override val deployBottom = 2
        override val deployCoins = 0

        override val buildStart = 3
        override val buildBottom = 1
        override val buildCoins = 3

        override val enlistStart = 3
        override val enlistBottom = 2
        override val enlistCoins = 1

        override fun makeSections(playerInstance: PlayerInstance): List<Section> {
            return listOf(
                    Section(TopRowAction.Produce(playerInstance), BottomRowAction.Upgrade(playerInstance, upgradeStart, upgradeBottom, upgradeCoins), ProduceFragmentDirections.actionNavProduceToNavUpgrade()),
                    Section(TopRowAction.Trade(playerInstance), BottomRowAction.Deploy(playerInstance, deployStart, deployBottom, deployCoins), TradeFragmentDirections.actionNavTradeToNavDeploy()),
                    Section(TopRowAction.Bolster(playerInstance), BottomRowAction.Build(playerInstance, buildStart, buildBottom, buildCoins), BolsterFragmentDirections.actionNavBolsterToNavBuild()),
                    Section(TopRowAction.MoveOrGain(playerInstance), BottomRowAction.Enlist(playerInstance, enlistStart, enlistBottom, enlistCoins), MoveFragmentDirections.actionNavMoveToNavEnlist())
            )
        }
    },
    INDUSTRIAL("Industrial", -1, 2, 4) {
        override val upgradeStart = 3
        override val upgradeBottom = 2
        override val upgradeCoins = 3

        override val deployStart = 3
        override val deployBottom = 1
        override val deployCoins = 2

        override val buildStart = 3
        override val buildBottom = 2
        override val buildCoins = 1

        override val enlistStart = 4
        override val enlistBottom = 2
        override val enlistCoins = 0

        override fun makeSections(playerInstance: PlayerInstance): List<Section> {
            return listOf(
                    Section(TopRowAction.Bolster(playerInstance), BottomRowAction.Upgrade(playerInstance, upgradeStart, upgradeBottom, upgradeCoins), BolsterFragmentDirections.actionNavBolsterToNavUpgrade()),
                    Section(TopRowAction.Produce(playerInstance), BottomRowAction.Deploy(playerInstance, deployStart, deployBottom, deployCoins), ProduceFragmentDirections.actionNavProduceToNavDeploy()),
                    Section(TopRowAction.MoveOrGain(playerInstance), BottomRowAction.Build(playerInstance, buildStart, buildBottom, buildCoins), MoveFragmentDirections.actionNavMoveToNavBuild()),
                    Section(TopRowAction.Trade(playerInstance), BottomRowAction.Enlist(playerInstance, enlistStart, enlistBottom, enlistCoins), TradeFragmentDirections.actionNavTradeToNavEnlist())
            )
        }

    },
    MECHANICAL("Mechanical", -1, 3, 6) {
        override val upgradeStart = 3
        override val upgradeBottom = 2
        override val upgradeCoins = 0

        override val deployStart = 3
        override val deployBottom = 1
        override val deployCoins = 2

        override val buildStart = 3
        override val buildBottom = 2
        override val buildCoins = 2

        override val enlistStart = 4
        override val enlistBottom = 2
        override val enlistCoins = 2

        override fun makeSections(playerInstance: PlayerInstance): List<Section> {
            return listOf(
                    Section(TopRowAction.Trade(playerInstance), BottomRowAction.Upgrade(playerInstance, upgradeStart, upgradeBottom, upgradeCoins), TradeFragmentDirections.actionNavTradeToNavUpgrade()),
                    Section(TopRowAction.Bolster(playerInstance), BottomRowAction.Deploy(playerInstance, deployStart, deployBottom, deployCoins), BolsterFragmentDirections.actionNavBolsterToNavDeploy()),
                    Section(TopRowAction.MoveOrGain(playerInstance), BottomRowAction.Build(playerInstance, buildStart, buildBottom, buildCoins), MoveFragmentDirections.actionNavMoveToNavBuild()),
                    Section(TopRowAction.Produce(playerInstance), BottomRowAction.Enlist(playerInstance, enlistStart, enlistBottom, enlistCoins), ProduceFragmentDirections.actionNavProduceToNavEnlist())
            )
        }
    },
    PATRIOTIC("Patriotic", -1, 2, 6) {
        override val upgradeStart = 2
        override val upgradeBottom = 2
        override val upgradeCoins = 1

        override val deployStart = 4
        override val deployBottom = 1
        override val deployCoins = 3

        override val buildStart = 4
        override val buildBottom = 2
        override val buildCoins = 0

        override val enlistStart = 3
        override val enlistBottom = 2
        override val enlistCoins = 0

        override fun makeSections(playerInstance: PlayerInstance): List<Section> {
            return listOf(
                    Section(TopRowAction.MoveOrGain(playerInstance), BottomRowAction.Upgrade(playerInstance, upgradeStart, upgradeBottom, upgradeCoins), MoveFragmentDirections.actionNavMoveToNavUpgrade()),
                    Section(TopRowAction.Bolster(playerInstance), BottomRowAction.Deploy(playerInstance, deployStart, deployBottom, deployCoins), BolsterFragmentDirections.actionNavBolsterToNavDeploy()),
                    Section(TopRowAction.Trade(playerInstance), BottomRowAction.Build(playerInstance, buildStart, buildBottom, buildCoins), TradeFragmentDirections.actionNavTradeToNavBuild()),
                    Section(TopRowAction.Produce(playerInstance), BottomRowAction.Enlist(playerInstance, enlistStart, enlistBottom, enlistCoins), ProduceFragmentDirections.actionNavProduceToNavEnlist())
            )
        }
    },
    MILITANT("Militant", -1, 3, 4) {
        override val upgradeStart = 3
        override val upgradeBottom = 1
        override val upgradeCoins = 0

        override val deployStart = 3
        override val deployBottom = 2
        override val deployCoins = 3

        override val buildStart = 4
        override val buildBottom = 3
        override val buildCoins = 1

        override val enlistStart = 3
        override val enlistBottom = 1
        override val enlistCoins = 2

        override fun makeSections(playerInstance: PlayerInstance): List<Section> {
            return listOf(
                    Section(TopRowAction.Bolster(playerInstance), BottomRowAction.Upgrade(playerInstance, upgradeStart, upgradeBottom, upgradeCoins), BolsterFragmentDirections.actionNavBolsterToNavUpgrade()),
                    Section(TopRowAction.MoveOrGain(playerInstance), BottomRowAction.Deploy(playerInstance, deployStart, deployBottom, deployCoins), MoveFragmentDirections.actionNavMoveToNavDeploy()),
                    Section(TopRowAction.Produce(playerInstance), BottomRowAction.Build(playerInstance, buildStart, buildBottom, buildCoins), ProduceFragmentDirections.actionNavProduceToNavBuild()),
                    Section(TopRowAction.Trade(playerInstance), BottomRowAction.Enlist(playerInstance, enlistStart, enlistBottom, enlistCoins), TradeFragmentDirections.actionNavTradeToNavEnlist())
            )
        }
    },
    INNOVATIVE("Innovative", -1, 3, 5) {
        override val upgradeStart = 3
        override val upgradeBottom = 3
        override val upgradeCoins = 3

        override val deployStart = 3
        override val deployBottom = 2
        override val deployCoins = 1

        override val buildStart = 4
        override val buildBottom = 1
        override val buildCoins = 2

        override val enlistStart = 3
        override val enlistBottom = 1
        override val enlistCoins = 0

        override fun makeSections(playerInstance: PlayerInstance): List<Section> {
            return listOf(
                    Section(TopRowAction.Trade(playerInstance), BottomRowAction.Upgrade(playerInstance, upgradeStart, upgradeBottom, upgradeCoins), TradeFragmentDirections.actionNavTradeToNavUpgrade()),
                    Section(TopRowAction.Produce(playerInstance), BottomRowAction.Deploy(playerInstance, deployStart, deployBottom, deployCoins), ProduceFragmentDirections.actionNavProduceToNavDeploy()),
                    Section(TopRowAction.Bolster(playerInstance), BottomRowAction.Build(playerInstance, buildStart, buildBottom, buildCoins), BolsterFragmentDirections.actionNavBolsterToNavBuild()),
                    Section(TopRowAction.MoveOrGain(playerInstance), BottomRowAction.Enlist(playerInstance, enlistStart, enlistBottom, enlistCoins), MoveFragmentDirections.actionNavMoveToNavEnlist())
            )
        }
    };

    companion object {
        init {
            load()
        }

        fun load() {
            values().forEach {
                PlayerMat[it.ordinal] = it
            }
        }
    }
}

class PlayerMatInstance(val playerMat: PlayerMat, val playerInstance: PlayerInstance) {
    constructor(playerInstance: PlayerInstance) : this(PlayerMat[playerInstance.playerData.playerMat.matId]!!, playerInstance)
    
    val sections: List<Section> = playerMat.makeSections(playerInstance)

    fun findSection(action: Class<out PlayerMatAction>): Section? {
        return sections.firstOrNull { section ->
            section.topRowAction::class.java == action || section.bottomRowAction::class.java == action
        }
    }

    fun <A: TopRowAction> findTopRowAction(action: Class<A>): A? {
        return findSection(action)?.topRowAction as A?
    }

    fun <A: BottomRowAction> findBottomRowAction(action: Class<A>): A? {
        return findSection(action)?.bottomRowAction as A?
    }

    fun initialize(playerInstance: PlayerInstance) {
        playerInstance.drawCoins(playerMat.initialCoins)
        playerInstance.popularity = playerMat.initialPopularity
    }
}