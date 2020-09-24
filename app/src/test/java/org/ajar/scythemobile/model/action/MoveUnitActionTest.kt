package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.ScytheTestDatabase
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

class MoveUnitActionTest {
    private lateinit var playerInstance: PlayerInstance
    private lateinit var enemyInstance: PlayerInstance

    @Before
    fun setup() {
        ScytheTestDatabase.setTestingDatabase()
        GameMap.currentMap
        playerInstance = PlayerInstance.makePlayer("testPlayer", StandardPlayerMat.INNOVATIVE.id, StandardFactionMat.CRIMEA.id)
        enemyInstance = PlayerInstance.makePlayer("enemyPlayer", StandardPlayerMat.MECHANICAL.id, StandardFactionMat.RUSVIET.id)
        TurnHolder.commitChanges()
    }

    @After
    fun cleanUp() {
        TurnHolder.resetTurn()
        ScytheDatabase.playerDao()?.removePlayer(playerInstance.playerData)
        ScytheDatabase.playerDao()?.removePlayer(enemyInstance.playerData)
        ScytheDatabase.unitDao()?.getUnits()?.toTypedArray()?.also { ScytheDatabase.unitDao()?.removeUnit(*it) }
        ObjectiveCardDeck.resetDeck()
    }

    @Test
    fun testMoveUnitAction() {
        val unit = playerInstance.selectUnits(UnitType.CHARACTER)!!.first()
        val hex = GameMap.currentMap.findHexAtIndex(8)!!
        assertTrue(ScytheAction.MoveUnitAction(listOf(unit), hex).perform())

        assertEquals(8, unit.pos)
        assertNull(TurnHolder.currentTurn.combatOne)
        assertTrue(TurnHolder.isUpdateQueued(unit.unitData))
    }

    @Test
    fun testMoveUnitActionMechWithWorker() {
        val unit = playerInstance.selectUnits(UnitType.MECH)!!.first()
        val worker = playerInstance.selectUnits(UnitType.WORKER)!!.first()
        val hex = GameMap.currentMap.findHexAtIndex(8)!!
        assertTrue(ScytheAction.MoveUnitAction(listOf(unit, worker), hex).perform())

        assertEquals(8, unit.pos)
        assertNull(TurnHolder.currentTurn.combatOne)
        assertTrue(TurnHolder.isUpdateQueued(unit.unitData))
        assertTrue(TurnHolder.isUpdateQueued(worker.unitData))
    }

    @Test
    fun testMoveUnitActionProvokeFight() {
        val enemy = enemyInstance.selectUnits(UnitType.CHARACTER)!!.first()
        enemy.pos = 8
        TurnHolder.updateMove(enemy.unitData)
        TurnHolder.commitChanges()

        val unit = playerInstance.selectUnits(UnitType.CHARACTER)!!.first()
        val hex = GameMap.currentMap.findHexAtIndex(8)!!
        assertTrue(ScytheAction.MoveUnitAction(listOf(unit), hex).perform())

        assertEquals(8, unit.pos)
        val currentCombat = TurnHolder.currentTurn.combatOne
        assertNotNull(currentCombat)
        assertEquals(1, currentCombat!!.attackingUnits.size)
        assertEquals(unit.id, currentCombat.attackingUnits.first())

        assertEquals(1, currentCombat.defendingUnits.size)
        assertEquals(enemy.id, currentCombat.defendingUnits.first())

        assertTrue(TurnHolder.isUpdateQueued(unit.unitData))
        assertFalse(TurnHolder.isUpdateQueued(enemy.unitData))
    }

    @Test
    fun testMoveUnitActionProvokeFightWithWorkers() {
        val enemy = enemyInstance.selectUnits(UnitType.CHARACTER)!!.first()
        val enemyWorker = enemyInstance.selectUnits(UnitType.WORKER)!!.first()
        enemy.pos = 8
        enemyWorker.pos = 8
        TurnHolder.updateMove(enemy.unitData, enemyWorker.unitData)
        TurnHolder.commitChanges()

        val unit = playerInstance.selectUnits(UnitType.CHARACTER)!!.first()
        val worker = playerInstance.selectUnits(UnitType.WORKER)!!.first()
        val hex = GameMap.currentMap.findHexAtIndex(8)!!
        assertTrue(ScytheAction.MoveUnitAction(listOf(unit, worker), hex).perform())

        assertEquals(8, unit.pos)
        val currentCombat = TurnHolder.currentTurn.combatOne
        assertNotNull(currentCombat)
        assertEquals(2, currentCombat!!.attackingUnits.size)
        assertTrue(currentCombat.attackingUnits.contains(unit.id))
        assertTrue(currentCombat.attackingUnits.contains(worker.id))

        assertEquals(2, currentCombat.defendingUnits.size)
        assertTrue(currentCombat.defendingUnits.contains(enemy.id))
        assertTrue(currentCombat.defendingUnits.contains(enemyWorker.id))

        assertTrue(TurnHolder.isUpdateQueued(unit.unitData))
        assertTrue(TurnHolder.isUpdateQueued(worker.unitData))
        assertFalse(TurnHolder.isUpdateQueued(enemy.unitData))
        assertFalse(TurnHolder.isUpdateQueued(enemyWorker.unitData))
    }

    @Test
    fun testMoveUnitActionBuildingsDoNotProvokeFight() {
        val enemy = enemyInstance.selectUnits(UnitType.MILL)!!.first()
        enemy.pos = 8
        TurnHolder.updateMove(enemy.unitData)
        TurnHolder.commitChanges()

        val unit = playerInstance.selectUnits(UnitType.CHARACTER)!!.first()
        val hex = GameMap.currentMap.findHexAtIndex(8)!!
        assertTrue(ScytheAction.MoveUnitAction(listOf(unit), hex).perform())

        assertEquals(8, unit.pos)
        val currentCombat = TurnHolder.currentTurn.combatOne
        assertNull(currentCombat)

        assertTrue(TurnHolder.isUpdateQueued(unit.unitData))
    }
}