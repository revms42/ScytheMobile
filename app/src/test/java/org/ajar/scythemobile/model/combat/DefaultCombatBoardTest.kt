package org.ajar.scythemobile.model.combat

import org.ajar.scythemobile.model.StarType
import org.ajar.scythemobile.model.TestPlayer
import org.ajar.scythemobile.model.TestUnit
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.FactionMat
import org.ajar.scythemobile.model.map.*
import org.ajar.scythemobile.model.production.MapResource
import org.ajar.scythemobile.model.production.MapResourceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class DefaultCombatBoardTest {

    lateinit var player: TestPlayer
    lateinit var enemy: TestPlayer

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
        player = TestPlayer(FactionMat.NORDIC)
        enemy = TestPlayer(FactionMat.NORDIC)

        val lakeHexDesc = MapHexDesc(7, HexNeighbors(nw = 3, w = 8), SpecialFeature.LAKE)
        val tunnelHexDesc = MapHexDesc(8, HexNeighbors(ne = 3, e = 7), SpecialFeature.TUNNEL)
        val tundraHexDesc = MapHexDesc( 9, HexNeighbors(w = 3, sw = 7), ResourceFeature.TUNDRA)

        val playerBaseDesc = MapHexDesc(1, HexNeighbors(), HomeBase(player))
        val enemyBaseDesc = MapHexDesc(2, HexNeighbors(), HomeBase(enemy))
        val combatHexDesc = MapHexDesc(3, HexNeighbors(sw = 8, se = 7, e = 9))

        val mapDesc = MapDesc(playerBaseDesc, enemyBaseDesc, combatHexDesc, lakeHexDesc, tundraHexDesc, tunnelHexDesc)

        playerMech = TestUnit(player, UnitType.MECH)
        playerWorker = TestUnit(player, UnitType.WORKER)

        enemyMech = TestUnit(enemy, UnitType.MECH)
        enemyWorker = TestUnit(enemy, UnitType.WORKER)

        map = GameMap(mapDesc)

        combatHex = map.findHexAtIndex(combatHexDesc.location)!!
        lakeHex = map.findHexAtIndex(lakeHexDesc.location)!!
        combatHex.unitsPresent.addAll(listOf(playerMech, playerWorker, enemyMech, enemyWorker))

        playerBase = map.findHomeBase(player)!!

        player.power = 2
        player.popularity = 3
        player.combatCards.clear()
        player.combatCards.add(CombatCardDeck.currentDeck.drawCard())
        player.stars.clear()

        enemyBase = map.findHomeBase(enemy)!!

        enemy.power = 3
        enemy.popularity = 2
        enemy.combatCards.clear()
        enemy.combatCards.add(CombatCardDeck.currentDeck.drawCard())
        enemy.stars.clear()

        combatBoard = DefaultCombatBoard(combatHex, player, enemy)
    }

    @Test
    fun testGetPlayerBoard() {
        val playerBoard = combatBoard.getPlayerBoard(player)
        assertEquals(2, playerBoard.unitsPresent.size)
        assertTrue(playerBoard.unitsPresent.containsAll(listOf(playerWorker, playerMech)))
        assertEquals(2, playerBoard.power)
        assertEquals(1, playerBoard.cardLimit)
        assertEquals(1, playerBoard.cardsAvailable.size)
    }

    @Test
    fun testGetOpposingBoard() {
        val enemyBoard = combatBoard.getOpposingBoard(player)
        assertEquals(2, enemyBoard.unitsPresent.size)
        assertTrue(enemyBoard.unitsPresent.containsAll(listOf(enemyWorker, enemyMech)))
        assertEquals(3, enemyBoard.power)
        assertEquals(1, enemyBoard.cardLimit)
        assertEquals(1, enemyBoard.cardsAvailable.size)
    }

    @Test
    fun testDetermineResults() {
        combatBoard.getPlayerBoard(player).powerSelected = 2
        combatBoard.getOpposingBoard(player).powerSelected = 3

        var combatResults = combatBoard.determineResults()
        assertEquals(2, combatResults.attackerResult)
        assertEquals(3, combatResults.defenderResult)

        combatBoard.getOpposingBoard(player).powerSelected = 3

        // You can't take it back.
        combatResults = combatBoard.determineResults()
        assertEquals(2, combatResults.attackerResult)
        assertEquals(3, combatResults.defenderResult)
    }

    @Test
    fun testResolveCombatAttackerWins() {
        combatBoard.getPlayerBoard(player).powerSelected = 2
        combatBoard.getOpposingBoard(player).powerSelected = 2

        enemyWorker.heldMapResources.add(MapResource(MapResourceType.FOOD))

        val combatResults = combatBoard.determineResults()
        assertEquals(2, combatResults.attackerResult)
        assertEquals(2, combatResults.defenderResult)

        combatBoard.resolveCombat()

        assertEquals(2, combatHex.unitsPresent.size)
        assertTrue(combatHex.unitsPresent.containsAll(listOf(playerMech, playerWorker)))
        assertTrue(enemyBase.unitsPresent.containsAll(listOf(enemyMech, enemyWorker)))
        assertEquals(0, enemyWorker.heldMapResources.size)
        assertEquals(1, player.getStarCount(StarType.COMBAT))

        assertEquals(2, player.popularity)
        assertEquals(0, player.power)

        assertEquals(2, enemy.popularity)
        assertEquals(1, enemy.power)
        assertEquals(2, enemy.combatCards.size)
        assertEquals(0, enemy.getStarCount(StarType.COMBAT))

        assertEquals(1, combatHex.heldMapResources.size)
    }

    @Test
    fun testResolveCombatAttackerWinsAgainstSeaworthy() {
        enemy.factionMat.unlockMechAbility("Seaworthy")

        combatBoard.getPlayerBoard(player).powerSelected = 2
        combatBoard.getOpposingBoard(player).powerSelected = 2

        enemyWorker.heldMapResources.add(MapResource(MapResourceType.FOOD))

        val combatResults = combatBoard.determineResults()
        assertEquals(2, combatResults.attackerResult)
        assertEquals(2, combatResults.defenderResult)

        combatBoard.resolveCombat()

        assertEquals(2, combatHex.unitsPresent.size)
        assertTrue(combatHex.unitsPresent.containsAll(listOf(playerMech, playerWorker)))
        assertTrue(lakeHex.unitsPresent.containsAll(listOf(enemyMech, enemyWorker)))
        assertEquals(0, enemyWorker.heldMapResources.size)
        assertEquals(1, player.getStarCount(StarType.COMBAT))

        assertEquals(2, player.popularity)
        assertEquals(0, player.power)

        assertEquals(2, enemy.popularity)
        assertEquals(1, enemy.power)
        assertEquals(2, enemy.combatCards.size)
        assertEquals(0, enemy.getStarCount(StarType.COMBAT))

        assertEquals(1, combatHex.heldMapResources.size)
    }

    @Test
    fun testResolveCombatDefenderWins() {
        combatBoard.getPlayerBoard(player).powerSelected = 2
        combatBoard.getOpposingBoard(player).powerSelected = 3

        playerWorker.heldMapResources.add(MapResource(MapResourceType.FOOD))

        val combatResults = combatBoard.determineResults()
        assertEquals(2, combatResults.attackerResult)
        assertEquals(3, combatResults.defenderResult)

        combatBoard.resolveCombat()

        assertEquals(2, combatHex.unitsPresent.size)
        assertTrue(combatHex.unitsPresent.containsAll(listOf(enemyMech, enemyWorker)))
        assertTrue(playerBase.unitsPresent.containsAll(listOf(playerMech, playerWorker)))
        assertEquals(0, playerWorker.heldMapResources.size)
        assertEquals(0, player.getStarCount(StarType.COMBAT))

        assertEquals(3, player.popularity)
        assertEquals(0, player.power)
        assertEquals(2, player.combatCards.size)

        assertEquals(2, enemy.popularity)
        assertEquals(0, enemy.power)
        assertEquals(1, enemy.getStarCount(StarType.COMBAT))

        assertEquals(1, combatHex.heldMapResources.size)
    }

    @Test
    fun testResolveCombatDefenderWinsAgainstSeaworthy() {
        player.factionMat.unlockMechAbility("Seaworthy")

        combatBoard.getPlayerBoard(player).powerSelected = 2
        combatBoard.getOpposingBoard(player).powerSelected = 3

        playerWorker.heldMapResources.add(MapResource(MapResourceType.FOOD))

        val combatResults = combatBoard.determineResults()
        assertEquals(2, combatResults.attackerResult)
        assertEquals(3, combatResults.defenderResult)

        combatBoard.resolveCombat()

        assertEquals(2, combatHex.unitsPresent.size)
        assertTrue(combatHex.unitsPresent.containsAll(listOf(enemyMech, enemyWorker)))
        assertTrue(lakeHex.unitsPresent.containsAll(listOf(playerMech, playerWorker)))
        assertEquals(0, playerWorker.heldMapResources.size)
        assertEquals(0, player.getStarCount(StarType.COMBAT))

        assertEquals(3, player.popularity)
        assertEquals(0, player.power)
        assertEquals(2, player.combatCards.size)

        assertEquals(2, enemy.popularity)
        assertEquals(0, enemy.power)
        assertEquals(1, enemy.getStarCount(StarType.COMBAT))

        assertEquals(1, combatHex.heldMapResources.size)
    }
}