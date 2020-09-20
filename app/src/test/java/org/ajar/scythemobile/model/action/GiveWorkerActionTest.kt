package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.data.PlayerData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.ScytheTestDatabase
import org.ajar.scythemobile.data.UnitData
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.StandardFactionMat
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.player.StandardPlayerMat
import org.ajar.scythemobile.turn.TurnHolder
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GiveWorkerActionTest {

    private lateinit var playerInstance: PlayerInstance
    private val workers = ArrayList<UnitData>()

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
        ScytheDatabase.unitDao()?.removeUnit(*workers.toTypedArray())
    }

    @Test
    fun testGiveWorkerAction() {
        assertTrue(ScytheAction.GiveWorkerAction(8, playerInstance, 1).perform())

        val unit: GameUnit? = playerInstance.selectUnits(UnitType.WORKER)?.firstOrNull { it.pos == 8 }?.also { workers.add(it.unitData) }
        assertNotNull(unit)
        assertTrue(TurnHolder.isUpdateQueued(unit!!.unitData))
        assertEquals(playerInstance, unit.controllingPlayer)
    }

    @Test
    fun testGiveWorkerActionAllWorkersOut() {
        ScytheDatabase.unitDao()?.getUnitsForPlayer(playerInstance.playerId, UnitType.WORKER.ordinal)?.forEach { worker ->
            worker.loc = 8
            ScytheDatabase.unitDao()!!.updateUnit(worker)
        }

        assertFalse(ScytheAction.GiveWorkerAction(8, playerInstance, 1).perform())
        assertFalse(TurnHolder.isAnyUpdateQueued(UnitData::class.java))
        assertFalse(TurnHolder.isAnyUpdateQueued(PlayerData::class.java))
    }

    @Test
    fun testGiveWorkerActionBadHex() {
        assertFalse(ScytheAction.GiveWorkerAction(-1, playerInstance, 1).perform())
        assertFalse(TurnHolder.isAnyUpdateQueued(UnitData::class.java))
        assertFalse(TurnHolder.isAnyUpdateQueued(PlayerData::class.java))
    }

    @Test
    fun testGiveWorkerActionHomeBase() {
        assertFalse(ScytheAction.GiveWorkerAction(1, playerInstance, 1).perform())
        assertFalse(TurnHolder.isAnyUpdateQueued(UnitData::class.java))
        assertFalse(TurnHolder.isAnyUpdateQueued(PlayerData::class.java))
    }
}