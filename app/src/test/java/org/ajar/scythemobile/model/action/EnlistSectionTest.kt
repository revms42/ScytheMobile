package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.model.CapitalResourceType
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.ScytheTestDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.faction.StandardFactionMat
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.objective.ObjectiveCardDeck
import org.ajar.scythemobile.model.player.BottomRowAction
import org.ajar.scythemobile.model.player.StandardPlayerMat
import org.ajar.scythemobile.turn.TurnHolder
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EnlistSectionTest {
    private lateinit var playerInstance: PlayerInstance

    @Before
    fun setup() {
        ScytheTestDatabase.setTestingDatabase()
        GameMap.currentMap
        playerInstance = PlayerInstance.makePlayer("testPlayer", StandardPlayerMat.MECHANICAL.id, StandardFactionMat.CRIMEA.id)
        TurnHolder.commitChanges()
    }

    @After
    fun cleanUp() {
        TurnHolder.commitChanges()
        ScytheDatabase.playerDao()?.removePlayer(playerInstance.playerData)
        ObjectiveCardDeck.resetDeck()
    }

    @Test
    fun testEnlistSectionAction() {
        val bottomRowAction = playerInstance.playerMat.findBottomRowAction(BottomRowAction.Deploy::class.java)
        val resourceType = CapitalResourceType.COINS
        val previousCoins = playerInstance.coins

        assertTrue(ScytheAction.EnlistSection(playerInstance, bottomRowAction!!, resourceType).perform())

        assertEquals(previousCoins + 2, playerInstance.coins)
        assertTrue(playerInstance.playerMat.findBottomRowAction(bottomRowAction::class.java)!!.enlisted)
        assertFalse(playerInstance.factionMat.getEnlistmentBonusesAvailabe().contains(CapitalResourceType.COINS))
        assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testEnlistSectionActionTwice() {
        val bottomRowAction = playerInstance.playerMat.findBottomRowAction(BottomRowAction.Deploy::class.java)
        val resourceType = CapitalResourceType.COINS
        val previousCoins = playerInstance.coins

        assertTrue(ScytheAction.EnlistSection(playerInstance, bottomRowAction!!, resourceType).perform())

        assertEquals(previousCoins + 2, playerInstance.coins)
        assertTrue(playerInstance.playerMat.findBottomRowAction(bottomRowAction::class.java)!!.enlisted)
        assertFalse(playerInstance.factionMat.getEnlistmentBonusesAvailabe().contains(CapitalResourceType.COINS))
        assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))

        TurnHolder.commitChanges()

        assertFalse(ScytheAction.EnlistSection(playerInstance, bottomRowAction, resourceType).perform())
        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testEnlistSectionActionRewardTwice() {
        var bottomRowAction: BottomRowAction? = playerInstance.playerMat.findBottomRowAction(BottomRowAction.Deploy::class.java)
        val resourceType = CapitalResourceType.COINS
        val previousCoins = playerInstance.coins

        assertTrue(ScytheAction.EnlistSection(playerInstance, bottomRowAction!!, resourceType).perform())

        assertEquals(previousCoins + 2, playerInstance.coins)
        assertTrue(playerInstance.playerMat.findBottomRowAction(bottomRowAction::class.java)!!.enlisted)
        assertFalse(playerInstance.factionMat.getEnlistmentBonusesAvailabe().contains(CapitalResourceType.COINS))
        assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))

        TurnHolder.commitChanges()

        bottomRowAction = playerInstance.playerMat.findBottomRowAction(BottomRowAction.Upgrade::class.java)
        assertFalse(ScytheAction.EnlistSection(playerInstance, bottomRowAction!!, resourceType).perform())
        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testEnlistSectionActionSectionTwice() {
        val bottomRowAction: BottomRowAction? = playerInstance.playerMat.findBottomRowAction(BottomRowAction.Deploy::class.java)
        var resourceType = CapitalResourceType.COINS
        val previousCoins = playerInstance.coins

        assertTrue(ScytheAction.EnlistSection(playerInstance, bottomRowAction!!, resourceType).perform())

        assertEquals(previousCoins + 2, playerInstance.coins)
        assertTrue(playerInstance.playerMat.findBottomRowAction(bottomRowAction::class.java)!!.enlisted)
        assertFalse(playerInstance.factionMat.getEnlistmentBonusesAvailabe().contains(CapitalResourceType.COINS))
        assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))

        TurnHolder.commitChanges()

        resourceType = CapitalResourceType.POPULARITY
        assertFalse(ScytheAction.EnlistSection(playerInstance, bottomRowAction, resourceType).perform())
        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testEnlistSectionActionAllEnlisted() {
        val bottomRowAction: BottomRowAction? = playerInstance.playerMat.findBottomRowAction(BottomRowAction.Deploy::class.java)
        val resourceType = CapitalResourceType.COINS

        playerInstance.playerData.playerMat.enlistSection.enlisted = true
        playerInstance.playerData.playerMat.buildSection.enlisted = true
        playerInstance.playerData.playerMat.deploySection.enlisted = true
        playerInstance.playerData.playerMat.upgradeSection.enlisted = true

        assertFalse(ScytheAction.EnlistSection(playerInstance, bottomRowAction!!, resourceType).perform())
        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testEnlistSectionActionAllRewardsHandedOut() {
        val bottomRowAction: BottomRowAction? = playerInstance.playerMat.findBottomRowAction(BottomRowAction.Deploy::class.java)
        val resourceType = CapitalResourceType.COINS

        playerInstance.playerData.factionMat.enlistCoins = true
        playerInstance.playerData.factionMat.enlistCards = true
        playerInstance.playerData.factionMat.enlistPop = true
        playerInstance.playerData.factionMat.enlistPower = true

        assertFalse(ScytheAction.EnlistSection(playerInstance, bottomRowAction!!, resourceType).perform())
        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }
}