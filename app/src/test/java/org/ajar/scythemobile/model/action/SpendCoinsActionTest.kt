package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.ScytheTestDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.faction.StandardFactionMat
import org.ajar.scythemobile.model.objective.ObjectiveCardDeck
import org.ajar.scythemobile.model.player.StandardPlayerMat
import org.ajar.scythemobile.turn.TurnHolder
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SpendCoinsActionTest {

    lateinit var playerInstance: PlayerInstance

    @Before
    fun setup() {
        ScytheTestDatabase.setTestingDatabase()
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
    fun testSpendCoinsAction() {
        val initialCoins = playerInstance.coins
        Assert.assertTrue(ScytheAction.SpendCoinsAction(playerInstance, 2).perform())
        Assert.assertEquals(initialCoins - 2, playerInstance.coins)
        Assert.assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testSpendAllCoinsAction() {
        val initialCoins = playerInstance.coins
        Assert.assertTrue(ScytheAction.SpendCoinsAction(playerInstance, initialCoins).perform())
        Assert.assertEquals(0, playerInstance.coins)
        Assert.assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testSpendExcessCoinsAction() {
        val initialCoins = playerInstance.coins
        Assert.assertFalse(ScytheAction.SpendCoinsAction(playerInstance, initialCoins + 1).perform())
        Assert.assertEquals(initialCoins, playerInstance.coins)
        Assert.assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testSpendNegativeCoinsAction() {
        val initialCoins = playerInstance.coins
        Assert.assertFalse(ScytheAction.SpendCoinsAction(playerInstance, -1).perform())
        Assert.assertEquals(initialCoins, playerInstance.coins)
        Assert.assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testSpendNoCoinsAction() {
        playerInstance.coins = 0
        Assert.assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))
        TurnHolder.commitChanges()
        Assert.assertFalse(ScytheAction.SpendCoinsAction(playerInstance, 1).perform())
        Assert.assertEquals(0, playerInstance.coins)
        Assert.assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }
}