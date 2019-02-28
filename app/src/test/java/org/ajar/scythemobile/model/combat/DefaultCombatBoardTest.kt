package org.ajar.scythemobile.model.combat

import org.ajar.scythemobile.model.TestPlayer
import org.ajar.scythemobile.model.TestUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.*
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

        TestPlayer.enemy.power = 3
        TestPlayer.enemy.popularity = 2
        TestPlayer.enemy.combatCards.clear()
        TestPlayer.enemy.combatCards.add(CombatCardDeck.currentDeck.drawCard())

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
    fun testGetOpponentBoard() {
        Assert.fail("NYI")
    }

    @Test
    fun testDetermineResults() {
        Assert.fail("NYI")
    }

    @Test
    fun testResolveCombatAttackerWins() {
        Assert.fail("NYI")
    }

    @Test
    fun testResolveCombatAttackerWinsDrivesOffWorkers() {
        Assert.fail("NYI")
    }

    @Test
    fun testResolveCombatAttackerWinsTakesResources() {
        Assert.fail("NYI")
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
    fun testResolveCombatDefenderWinsTakesResources() {
        Assert.fail("NYI")
    }

    @Test
    fun testResolveCombatDefenderWinsAgainstSeaworthy() {
        Assert.fail("NYI")
    }

    @Test
    fun testTieFavorsAttacker() {
        Assert.fail("NYI")
    }
}