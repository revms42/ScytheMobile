package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.ScytheTestDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.faction.StandardFactionMat
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.objective.ObjectiveCardDeck
import org.ajar.scythemobile.model.player.BottomRowAction
import org.ajar.scythemobile.model.player.StandardPlayerMat
import org.ajar.scythemobile.model.player.TopRowAction
import org.ajar.scythemobile.turn.TurnHolder
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UpgradeSectionTest {
    private lateinit var playerInstance: PlayerInstance

    @Before
    fun setup() {
        ScytheTestDatabase.setTestingDatabase()
        GameMap.currentMap
        playerInstance = PlayerInstance.makePlayer("testPlayer", StandardPlayerMat.INNOVATIVE.id, StandardFactionMat.CRIMEA.id)
        TurnHolder.commitChanges()
    }

    @After
    fun cleanUp() {
        TurnHolder.commitChanges()
        ScytheDatabase.playerDao()?.removePlayer(playerInstance.playerData)
        ObjectiveCardDeck.resetDeck()
    }

    @Test
    fun testUpgradeSectionActionLeading() {
        val topRowAction = playerInstance.playerMat.findTopRowAction(TopRowAction.MoveOrGain::class.java)
        val bottomRowAction = playerInstance.playerMat.findBottomRowAction(BottomRowAction.Deploy::class.java)

        val topUpgrades = topRowAction!!.upgrades
        val bottomUpgrades = bottomRowAction!!.upgrades

        val topMoves = topRowAction.unitsMoved
        val deployCost = bottomRowAction.cost.size

        assertTrue(ScytheAction.UpgradeSection(topRowAction, true, bottomRowAction).perform())

        assertEquals(topUpgrades + 1, topRowAction.upgrades)
        assertEquals(bottomUpgrades + 1, bottomRowAction.upgrades)

        assertEquals(topMoves + 1, topRowAction.unitsMoved)
        assertEquals(deployCost - 1, bottomRowAction.cost.size)

        assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testUpgradeSectionAction() {
        val topRowAction = playerInstance.playerMat.findTopRowAction(TopRowAction.MoveOrGain::class.java)
        val bottomRowAction = playerInstance.playerMat.findBottomRowAction(BottomRowAction.Deploy::class.java)

        val topUpgrades = topRowAction!!.upgrades
        val bottomUpgrades = bottomRowAction!!.upgrades

        val topGain = topRowAction.coinsGained
        val deployCost = bottomRowAction.cost.size

        assertTrue(ScytheAction.UpgradeSection(topRowAction, false, bottomRowAction).perform())

        assertEquals(topUpgrades + 1, topRowAction.upgrades)
        assertEquals(bottomUpgrades + 1, bottomRowAction.upgrades)

        assertEquals(topGain + 1, topRowAction.coinsGained)
        assertEquals(deployCost - 1, bottomRowAction.cost.size)

        assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testUpgradeSectionActionTopMaxed() {
        val topRowAction = playerInstance.playerMat.findTopRowAction(TopRowAction.MoveOrGain::class.java)
        topRowAction!!.upgradeFollowing()

        TurnHolder.commitChanges()

        val bottomRowAction = playerInstance.playerMat.findBottomRowAction(BottomRowAction.Deploy::class.java)

        val topUpgrades = topRowAction.upgrades
        val bottomUpgrades = bottomRowAction!!.upgrades

        val topGain = topRowAction.coinsGained
        val deployCost = bottomRowAction.cost.size

        assertFalse(ScytheAction.UpgradeSection(topRowAction, false, bottomRowAction).perform())

        assertEquals(topUpgrades, topRowAction.upgrades)
        assertEquals(bottomUpgrades, bottomRowAction.upgrades)

        assertEquals(topGain, topRowAction.coinsGained)
        assertEquals(deployCost, bottomRowAction.cost.size)

        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testUpgradeSectionActionBottomMaxed() {
        val topRowAction = playerInstance.playerMat.findTopRowAction(TopRowAction.MoveOrGain::class.java)
        val bottomRowAction = playerInstance.playerMat.findBottomRowAction(BottomRowAction.Deploy::class.java)
        bottomRowAction!!.upgrade()
        TurnHolder.commitChanges()

        val topUpgrades = topRowAction!!.upgrades
        val bottomUpgrades = bottomRowAction.upgrades

        val topGain = topRowAction.coinsGained
        val deployCost = bottomRowAction.cost.size

        assertFalse(ScytheAction.UpgradeSection(topRowAction, false, bottomRowAction).perform())

        assertEquals(topUpgrades, topRowAction.upgrades)
        assertEquals(bottomUpgrades, bottomRowAction.upgrades)

        assertEquals(topGain, topRowAction.coinsGained)
        assertEquals(deployCost, bottomRowAction.cost.size)

        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testUpgradeSectionActionBottomNotUpgradable() {
        val topRowAction = playerInstance.playerMat.findTopRowAction(TopRowAction.MoveOrGain::class.java)
        val bottomRowAction = playerInstance.playerMat.findBottomRowAction(BottomRowAction.Upgrade::class.java)

        val topUpgrades = topRowAction!!.upgrades
        val bottomUpgrades = bottomRowAction!!.upgrades

        val topGain = topRowAction.coinsGained
        val deployCost = bottomRowAction.cost.size

        assertFalse(ScytheAction.UpgradeSection(topRowAction, false, bottomRowAction).perform())

        assertEquals(topUpgrades, topRowAction.upgrades)
        assertEquals(bottomUpgrades, bottomRowAction.upgrades)

        assertEquals(topGain, topRowAction.coinsGained)
        assertEquals(deployCost, bottomRowAction.cost.size)

        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testUpgradeSectionActionTopUpgradePossibleOnOtherElement() {
        val topRowAction = playerInstance.playerMat.findTopRowAction(TopRowAction.MoveOrGain::class.java)
        topRowAction!!.upgradeFollowing()

        TurnHolder.commitChanges()

        val bottomRowAction = playerInstance.playerMat.findBottomRowAction(BottomRowAction.Deploy::class.java)

        val topUpgrades = topRowAction.upgrades
        val bottomUpgrades = bottomRowAction!!.upgrades

        val topMoves = topRowAction.unitsMoved
        val deployCost = bottomRowAction.cost.size

        assertTrue(ScytheAction.UpgradeSection(topRowAction, true, bottomRowAction).perform())

        assertEquals(topUpgrades + 1, topRowAction.upgrades)
        assertEquals(bottomUpgrades + 1, bottomRowAction.upgrades)

        assertEquals(topMoves + 1, topRowAction.unitsMoved)
        assertEquals(deployCost - 1, bottomRowAction.cost.size)

        assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }
}