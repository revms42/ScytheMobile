package org.ajar.scythemobile.model.combat

import org.ajar.scythemobile.model.StarType
import org.ajar.scythemobile.model.TestPlayer
import org.ajar.scythemobile.model.TestUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.*
import org.ajar.scythemobile.model.production.Resource
import org.ajar.scythemobile.model.production.ResourceType
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class DefaultCombatBoardTest {

    val playerBaseDesc = MapHexDesc(1, HexNeigbors(), HomeBase(TestPlayer.player))
    val enemyBaseDesc = MapHexDesc(2, HexNeigbors(), HomeBase(TestPlayer.enemy))
    val combatHexDesc = MapHexDesc(3, HexNeigbors())

    val mapDesc = MapDesc(playerBaseDesc, enemyBaseDesc, combatHexDesc)

    val playerMech = TestUnit(TestPlayer.player, UnitType.MECH)
    val playerWorker = TestUnit(TestPlayer.player, UnitType.WORKER)

    val enemyMech = TestUnit(TestPlayer.enemy, UnitType.MECH)
    val enemyWorker = TestUnit(TestPlayer.enemy, UnitType.WORKER)

    lateinit var map: GameMap
    lateinit var playerBase: MapHex
    lateinit var enemyBase: MapHex
    lateinit var combatHex: MapHex

    lateinit var combatBoard: DefaultCombatBoard

    @Before
    fun setup() {
        map = GameMap(mapDesc)
        playerBase = map.findHomeBase(TestPlayer.player)!!
        enemyBase = map.findHomeBase(TestPlayer.enemy)!!
        combatHex = map.findHexAtIndex(combatHexDesc.location)!!

        combatHex.unitsPresent.addAll(listOf(playerMech, playerWorker, enemyMech, enemyWorker))

        TestPlayer.player.power = 2
        TestPlayer.player.popularity = 3
        TestPlayer.player.combatCards.clear()
        TestPlayer.player.combatCards.add(CombatCardDeck.currentDeck.drawCard())
        TestPlayer.player.stars.clear()

        TestPlayer.enemy.power = 3
        TestPlayer.enemy.popularity = 2
        TestPlayer.enemy.combatCards.clear()
        TestPlayer.enemy.combatCards.add(CombatCardDeck.currentDeck.drawCard())
        TestPlayer.enemy.stars.clear()

        combatBoard = DefaultCombatBoard(combatHex, TestPlayer.player, TestPlayer.enemy)
    }

    @Test
    fun testGetPlayerBoard() {
        val playerBoard = combatBoard.getPlayerBoard(TestPlayer.player)
        assertEquals(2, playerBoard.unitsPresent.size)
        assertTrue(playerBoard.unitsPresent.containsAll(listOf(playerWorker, playerMech)))
        assertEquals(2, playerBoard.power)
        assertEquals(1, playerBoard.cardLimit)
        assertEquals(1, playerBoard.cardsAvailable.size)
    }

    @Test
    fun testGetOpposingBoard() {
        val enemyBoard = combatBoard.getOpposingBoard(TestPlayer.player)
        assertEquals(2, enemyBoard.unitsPresent.size)
        assertTrue(enemyBoard.unitsPresent.containsAll(listOf(enemyWorker, enemyMech)))
        assertEquals(3, enemyBoard.power)
        assertEquals(1, enemyBoard.cardLimit)
        assertEquals(1, enemyBoard.cardsAvailable.size)
    }

    @Test
    fun testDetermineResults() {
        combatBoard.getPlayerBoard(TestPlayer.player).powerSelected = 2
        combatBoard.getOpposingBoard(TestPlayer.player).powerSelected = 3

        var combatResults = combatBoard.determineResults()
        assertEquals(2, combatResults.attackerResult)
        assertEquals(3, combatResults.defenderResult)

        combatBoard.getOpposingBoard(TestPlayer.player).powerSelected = 3

        // You can't take it back.
        combatResults = combatBoard.determineResults()
        assertEquals(2, combatResults.attackerResult)
        assertEquals(3, combatResults.defenderResult)
    }

    @Test
    fun testResolveCombatAttackerWins() {
        combatBoard.getPlayerBoard(TestPlayer.player).powerSelected = 2
        combatBoard.getOpposingBoard(TestPlayer.player).powerSelected = 2

        enemyWorker.heldResources?.add(Resource(ResourceType.FOOD))

        val combatResults = combatBoard.determineResults()
        assertEquals(2, combatResults.attackerResult)
        assertEquals(2, combatResults.defenderResult)

        combatBoard.resolveCombat()

        assertEquals(2, combatHex.unitsPresent.size)
        assertTrue(combatHex.unitsPresent.containsAll(listOf(playerMech, playerWorker)))
        assertTrue(enemyBase.unitsPresent.containsAll(listOf(enemyMech, enemyWorker)))
        assertEquals(0, enemyWorker.heldResources?.size)
        assertEquals(1, TestPlayer.player.getStarCount(StarType.COMBAT))

        assertEquals(2, TestPlayer.player.popularity)
        assertEquals(0, TestPlayer.player.power)

        assertEquals(2, TestPlayer.enemy.popularity)
        assertEquals(1, TestPlayer.enemy.power)
        assertEquals(2, TestPlayer.enemy.combatCards.size)
        assertEquals(0, TestPlayer.enemy.getStarCount(StarType.COMBAT))

        assertEquals(1, combatHex.resourcesPresent.size)
    }

    @Test
    fun testResolveCombatAttackerWinsAgainstSeaworthy() {
        Assert.fail("NYI")
    }

    @Test
    fun testResolveCombatDefenderWins() {
        Assert.fail("NYI")
    }

    @Test
    fun testResolveCombatDefenderWinsAgainstSeaworthy() {
        Assert.fail("NYI")
    }
}