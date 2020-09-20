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

class SpendPowerActionTest {

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
    }

    @Test
    fun testSpendPowerAction() {
        val initialPower = playerInstance.power
        assertTrue(ScytheAction.SpendPowerAction(playerInstance, 2).perform())
        assertEquals(initialPower - 2, playerInstance.power)
        assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testSpendAllPowerAction() {
        val initialPower = playerInstance.power
        assertTrue(ScytheAction.SpendPowerAction(playerInstance, initialPower).perform())
        assertEquals(0, playerInstance.power)
        assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testSpendExcessPowerAction() {
        val initialPower = playerInstance.power
        assertFalse(ScytheAction.SpendPowerAction(playerInstance, initialPower+1).perform())
        assertEquals(initialPower, playerInstance.power)
        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testSpendNegativePowerAction() {
        val initialPower = playerInstance.power
        assertFalse(ScytheAction.SpendPowerAction(playerInstance, -1).perform())
        assertEquals(initialPower, playerInstance.power)
        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testSpendNoPowerAction() {
        playerInstance.power = 0
        assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))
        TurnHolder.commitChanges()
        assertFalse(ScytheAction.SpendPowerAction(playerInstance, 1).perform())
        assertEquals(0, playerInstance.power)
        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }
}