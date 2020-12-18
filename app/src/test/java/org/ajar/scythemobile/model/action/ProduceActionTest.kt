package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.model.NaturalResourceType
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.ScytheTestDatabase
import org.ajar.scythemobile.data.UnitData
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.StandardFactionMat
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.objective.ObjectiveCardDeck
import org.ajar.scythemobile.model.player.StandardPlayerMat
import org.ajar.scythemobile.turn.TurnHolder
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ProduceActionTest {
    private lateinit var playerInstance: PlayerInstance

    @Before
    fun setup() {
        ScytheTestDatabase.setTestingDatabase()
        GameMap.currentMap
        playerInstance = PlayerInstance.makePlayer("testPlayer", StandardPlayerMat.INNOVATIVE.id, StandardFactionMat.CRIMEA.id)
        repeat(20) {
            val resourceData = ResourceData(0, -1, -1, NaturalResourceType.METAL.id, 1)
            ScytheDatabase.resourceDao()?.addResource(resourceData)
        }
        TurnHolder.commitChanges()
    }

    @After
    fun cleanUp() {
        TurnHolder.commitChanges()
        ScytheDatabase.playerDao()?.removePlayer(playerInstance.playerData)
        ScytheDatabase.unitDao()?.getUnits()?.toTypedArray()?.also { ScytheDatabase.unitDao()?.removeUnit(*it) }
        ScytheDatabase.resourceDao()?.getResources()?.toTypedArray()?.also { ScytheDatabase.resourceDao()?.removeResource(*it) }
        ObjectiveCardDeck.resetDeck()
    }

    @Test
    fun testProduceAction() {
        val worker = ScytheDatabase.unitDao()?.getUnitsForPlayer(playerInstance.playerId, UnitType.WORKER.ordinal)?.first()
        worker!!.loc = 8
        TurnHolder.updateMove(worker)
        TurnHolder.commitChanges()

        val hex = GameMap.currentMap.findHexAtIndex(8)
        assertTrue(ScytheAction.ProduceAction(playerInstance, hex!!).perform())

        val resourcesAtHex = ScytheDatabase.resourceDao()?.getResourcesAt(8)
        assertEquals(1, resourcesAtHex?.size?: 0)
        assertEquals(NaturalResourceType.METAL.id, resourcesAtHex!!.first().type)
        assertTrue(TurnHolder.isUpdateQueued(resourcesAtHex.first()))
    }

    @Test
    fun testProduceActionWithThreeWorkers() {
        val worker = ScytheDatabase.unitDao()?.getUnitsForPlayer(playerInstance.playerId, UnitType.WORKER.ordinal)?.subList(0, 3)
        worker!!.forEach { it.loc = 8 }
        TurnHolder.updateMove(*worker.toTypedArray())
        TurnHolder.commitChanges()

        val hex = GameMap.currentMap.findHexAtIndex(8)
        assertTrue(ScytheAction.ProduceAction(playerInstance, hex!!).perform())

        val resourcesAtHex = ScytheDatabase.resourceDao()?.getResourcesAt(8)
        assertEquals(3, resourcesAtHex?.size?: 0)

        resourcesAtHex!!.forEach {
            assertEquals(NaturalResourceType.METAL.id, it.type)
            assertTrue(TurnHolder.isUpdateQueued(it))
        }
    }

    @Test
    fun testProduceActionWithMill() {
        val worker = ScytheDatabase.unitDao()?.getUnitsForPlayer(playerInstance.playerId, UnitType.WORKER.ordinal)?.first()
        worker!!.loc = 8
        TurnHolder.updateMove(worker)

        val mill = ScytheDatabase.unitDao()?.getUnitsForPlayer(playerInstance.playerId, UnitType.MILL.ordinal)?.first()
        mill!!.loc = 8
        TurnHolder.updateMove(mill)
        TurnHolder.commitChanges()

        val hex = GameMap.currentMap.findHexAtIndex(8)
        assertTrue(ScytheAction.ProduceAction(playerInstance, hex!!).perform())

        val resourcesAtHex = ScytheDatabase.resourceDao()?.getResourcesAt(8)
        assertEquals(2, resourcesAtHex?.size?: 0)
        resourcesAtHex!!.forEach {
            assertEquals(NaturalResourceType.METAL.id, it.type)
            assertTrue(TurnHolder.isUpdateQueued(it))
        }
    }

    @Test
    fun testProduceActionNoMoreResources() {
        TurnHolder.updateResource(
            *ScytheDatabase.resourceDao()?.getResourcesOfType(NaturalResourceType.METAL.id)?.map {
                it.loc = 9
                it
            }?.toTypedArray()!!
        )
        val worker = ScytheDatabase.unitDao()?.getUnitsForPlayer(playerInstance.playerId, UnitType.WORKER.ordinal)?.first()
        worker!!.loc = 8
        TurnHolder.updateMove(worker)
        TurnHolder.commitChanges()

        val hex = GameMap.currentMap.findHexAtIndex(8)
        assertFalse(ScytheAction.ProduceAction(playerInstance, hex!!).perform())

        val resourcesAtHex = ScytheDatabase.resourceDao()?.getResourcesAt(8)
        assertEquals(0, resourcesAtHex?.size?: 0)
        assertFalse(TurnHolder.isAnyUpdateQueued(ResourceData::class.java))
    }

    @Test
    fun testProduceActionProduceWorker() {
        val workers = ScytheDatabase.unitDao()?.getUnitsForPlayer(playerInstance.playerId, UnitType.WORKER.ordinal)
        val worker = workers!!.first()
        worker.loc = 10
        TurnHolder.updateMove(worker)
        TurnHolder.commitChanges()

        val hex = GameMap.currentMap.findHexAtIndex(10)
        assertTrue(ScytheAction.ProduceAction(playerInstance, hex!!).perform())

        val resourcesAtHex = ScytheDatabase.resourceDao()?.getResourcesAt(10)
        assertEquals(0, resourcesAtHex?.size?: 0)
        val unitsAtHex = playerInstance.selectUnits(UnitType.WORKER)?.filter { it.pos == 10 }
        assertEquals(2, unitsAtHex?.size?: 0)
        val newWorker = unitsAtHex?.first { it.id != worker.id }
        assertTrue(TurnHolder.isUpdateQueued(newWorker!!.unitData))
        assertFalse(TurnHolder.isUpdateQueued(worker))
        assertFalse(TurnHolder.isAnyUpdateQueued(ResourceData::class.java))
    }

    @Test
    fun testProduceActionProduceWorkerLimit() {
        val workers = ScytheDatabase.unitDao()?.getUnitsForPlayer(playerInstance.playerId, UnitType.WORKER.ordinal)!!
        repeat(5) {
            workers[it].loc = 10
            TurnHolder.updateMove(workers[it])
        }
        TurnHolder.commitChanges()

        val hex = GameMap.currentMap.findHexAtIndex(10)
        assertTrue(ScytheAction.ProduceAction(playerInstance, hex!!).perform())

        val resourcesAtHex = ScytheDatabase.resourceDao()?.getResourcesAt(10)
        assertEquals(0, resourcesAtHex?.size?: 0)
        val unitsAtHex = playerInstance.selectUnits(UnitType.WORKER)?.filter { it.pos == 10 }
        assertEquals(8, unitsAtHex?.size?: 0)
        assertTrue(TurnHolder.isAnyUpdateQueued(UnitData::class.java))
        assertFalse(TurnHolder.isAnyUpdateQueued(ResourceData::class.java))
    }

}