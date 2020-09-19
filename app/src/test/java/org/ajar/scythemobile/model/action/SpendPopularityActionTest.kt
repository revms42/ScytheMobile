package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.ScytheTestDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.faction.StandardFactionMat
import org.ajar.scythemobile.model.player.StandardPlayerMat
import org.ajar.scythemobile.turn.TurnHolder
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SpendPopularityActionTest {

    lateinit var playerInstance: PlayerInstance

    @Before
    fun setup() {
        ScytheTestDatabase.setTestingDatabase()
        playerInstance = PlayerInstance.makePlayer("testPlayer", StandardPlayerMat.MECHANICAL.id, StandardFactionMat.CRIMEA.id)
    }

    @After
    fun cleanUp() {
        TurnHolder.commitChanges()
        ScytheDatabase.playerDao()?.removePlayer(playerInstance.playerData)
    }

    @Test
    fun testSpendPopularityAction() {
        val initialPop = playerInstance.popularity
        assertTrue(ScytheAction.SpendPopularityAction(playerInstance, 2).perform())
        assertEquals(initialPop - 2, playerInstance.popularity)
        assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testSpendAllPopularityAction() {
        val initialPop = playerInstance.popularity
        assertTrue(ScytheAction.SpendPopularityAction(playerInstance, initialPop).perform())
        assertEquals(0, playerInstance.popularity)
        assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testSpendExcessPopularityAction() {
        val initialPop = playerInstance.popularity
        assertFalse(ScytheAction.SpendPopularityAction(playerInstance, initialPop+1).perform())
        assertEquals(initialPop, playerInstance.popularity)
        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testSpendNegativePopularityAction() {
        val initialPop = playerInstance.popularity
        assertFalse(ScytheAction.SpendPopularityAction(playerInstance, -1).perform())
        assertEquals(initialPop, playerInstance.popularity)
        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testSpendNoPopularityAction() {
        playerInstance.popularity = 0
        assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))
        TurnHolder.commitChanges()
        assertFalse(ScytheAction.SpendPopularityAction(playerInstance, 1).perform())
        assertEquals(0, playerInstance.popularity)
        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }
}