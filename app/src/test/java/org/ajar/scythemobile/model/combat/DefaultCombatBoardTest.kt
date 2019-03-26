package org.ajar.scythemobile.model.combat

import org.ajar.scythemobile.model.StarType
import org.ajar.scythemobile.model.TestPlayer
import org.ajar.scythemobile.model.TestUnit
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.FactionMat
import org.ajar.scythemobile.model.map.*
import org.ajar.scythemobile.model.production.Resource
import org.ajar.scythemobile.model.production.ResourceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class DefaultCombatBoardTest {

    lateinit var map: GameMap
    lateinit var playerBase: MapHex
    lateinit var enemyBase: MapHex
    lateinit var combatHex: MapHex
    lateinit var lakeHex: MapHex

    lateinit var playerMech: GameUnit
    lateinit var playerWorker: GameUnit
    lateinit var enemyMech: GameUnit
    lateinit var enemyWorker: GameUnit

    lateinit var combatBoard: DefaultCombatBoard

    @Before
    fun setup() {
        TestPlayer.player = TestPlayer(FactionMat.NORDIC)
        TestPlayer.enemy = TestPlayer(FactionMat.NORDIC)

        val lakeHexDesc = MapHexDesc(7, HexNeigbors(nw = 3, w = 8), SpecialFeature.LAKE)
        val tunnelHexDesc = MapHexDesc(8, HexNeigbors(ne = 3, e = 7), SpecialFeature.TUNNEL)
        val tundraHexDesc = MapHexDesc( 9, HexNeigbors(w = 3, sw = 7), ResourceFeature.TUNDRA)

        val playerBaseDesc = MapHexDesc(1, HexNeigbors(), HomeBase(TestPlayer.player))
        val enemyBaseDesc = MapHexDesc(2, HexNeigbors(), HomeBase(TestPlayer.enemy))
        val combatHexDesc = MapHexDesc(3, HexNeigbors(sw = 8, se = 7, e = 9))

        val mapDesc = MapDesc(playerBaseDesc, enemyBaseDesc, combatHexDesc, lakeHexDesc, tundraHexDesc, tunnelHexDesc)

        playerMech = TestUnit(TestPlayer.player, UnitType.MECH)
        playerWorker = TestUnit(TestPlayer.player, UnitType.WORKER)

        enemyMech = TestUnit(TestPlayer.enemy, UnitType.MECH)
        enemyWorker = TestUnit(TestPlayer.enemy, UnitType.WORKER)

        map = GameMap(mapDesc)

        combatHex = map.findHexAtIndex(combatHexDesc.location)!!
        lakeHex = map.findHexAtIndex(lakeHexDesc.location)!!
        combatHex.unitsPresent.addAll(listOf(playerMech, playerWorker, enemyMech, enemyWorker))

        playerBase = map.findHomeBase(TestPlayer.player)!!

        TestPlayer.player.power = 2
        TestPlayer.player.popularity = 3
        TestPlayer.player.combatCards.clear()
        TestPlayer.player.combatCards.add(CombatCardDeck.currentDeck.drawCard())
        TestPlayer.player.stars.clear()

        enemyBase = map.findHomeBase(TestPlayer.enemy)!!

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

        assertEquals(1, combatHex.heldResources.size)
    }

    @Test
    fun testResolveCombatAttackerWinsAgainstSeaworthy() {
        TestPlayer.enemy.factionMat.unlockMechAbility("Seaworthy")

        combatBoard.getPlayerBoard(TestPlayer.player).powerSelected = 2
        combatBoard.getOpposingBoard(TestPlayer.player).powerSelected = 2

        enemyWorker.heldResources.add(Resource(ResourceType.FOOD))

        val combatResults = combatBoard.determineResults()
        assertEquals(2, combatResults.attackerResult)
        assertEquals(2, combatResults.defenderResult)

        combatBoard.resolveCombat()

        assertEquals(2, combatHex.unitsPresent.size)
        assertTrue(combatHex.unitsPresent.containsAll(listOf(playerMech, playerWorker)))
        assertTrue(lakeHex.unitsPresent.containsAll(listOf(enemyMech, enemyWorker)))
        assertEquals(0, enemyWorker.heldResources.size)
        assertEquals(1, TestPlayer.player.getStarCount(StarType.COMBAT))

        assertEquals(2, TestPlayer.player.popularity)
        assertEquals(0, TestPlayer.player.power)

        assertEquals(2, TestPlayer.enemy.popularity)
        assertEquals(1, TestPlayer.enemy.power)
        assertEquals(2, TestPlayer.enemy.combatCards.size)
        assertEquals(0, TestPlayer.enemy.getStarCount(StarType.COMBAT))

        assertEquals(1, combatHex.heldResources.size)
    }

    @Test
    fun testResolveCombatDefenderWins() {
        combatBoard.getPlayerBoard(TestPlayer.player).powerSelected = 2
        combatBoard.getOpposingBoard(TestPlayer.player).powerSelected = 3

        playerWorker.heldResources?.add(Resource(ResourceType.FOOD))

        val combatResults = combatBoard.determineResults()
        assertEquals(2, combatResults.attackerResult)
        assertEquals(3, combatResults.defenderResult)

        combatBoard.resolveCombat()

        assertEquals(2, combatHex.unitsPresent.size)
        assertTrue(combatHex.unitsPresent.containsAll(listOf(enemyMech, enemyWorker)))
        assertTrue(playerBase.unitsPresent.containsAll(listOf(playerMech, playerWorker)))
        assertEquals(0, playerWorker.heldResources?.size)
        assertEquals(0, TestPlayer.player.getStarCount(StarType.COMBAT))

        assertEquals(3, TestPlayer.player.popularity)
        assertEquals(0, TestPlayer.player.power)
        assertEquals(2, TestPlayer.player.combatCards.size)

        assertEquals(2, TestPlayer.enemy.popularity)
        assertEquals(0, TestPlayer.enemy.power)
        assertEquals(1, TestPlayer.enemy.getStarCount(StarType.COMBAT))

        assertEquals(1, combatHex.heldResources.size)
    }

    @Test
    fun testResolveCombatDefenderWinsAgainstSeaworthy() {
        TestPlayer.player.factionMat.unlockMechAbility("Seaworthy")

        combatBoard.getPlayerBoard(TestPlayer.player).powerSelected = 2
        combatBoard.getOpposingBoard(TestPlayer.player).powerSelected = 3

        playerWorker.heldResources.add(Resource(ResourceType.FOOD))

        val combatResults = combatBoard.determineResults()
        assertEquals(2, combatResults.attackerResult)
        assertEquals(3, combatResults.defenderResult)

        combatBoard.resolveCombat()

        assertEquals(2, combatHex.unitsPresent.size)
        assertTrue(combatHex.unitsPresent.containsAll(listOf(enemyMech, enemyWorker)))
        assertTrue(lakeHex.unitsPresent.containsAll(listOf(playerMech, playerWorker)))
        assertEquals(0, playerWorker.heldResources.size)
        assertEquals(0, TestPlayer.player.getStarCount(StarType.COMBAT))

        assertEquals(3, TestPlayer.player.popularity)
        assertEquals(0, TestPlayer.player.power)
        assertEquals(2, TestPlayer.player.combatCards.size)

        assertEquals(2, TestPlayer.enemy.popularity)
        assertEquals(0, TestPlayer.enemy.power)
        assertEquals(1, TestPlayer.enemy.getStarCount(StarType.COMBAT))

        assertEquals(1, combatHex.heldResources.size)
    }
}