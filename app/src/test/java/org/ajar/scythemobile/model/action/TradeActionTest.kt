package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.model.TestPlayer
import org.ajar.scythemobile.model.TestUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.*
import org.ajar.scythemobile.model.playermat.TradeAction
import org.ajar.scythemobile.model.production.MapResource
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class TradeActionTest {
    private val hexDesc0 = MapHexDesc(0, HexNeighbors(w = 1), ResourceFeature.VILLAGE)
    private val hexDesc1 = MapHexDesc(1, HexNeighbors(w = 2, e = 0), ResourceFeature.VILLAGE)
    private val hexDesc2 = MapHexDesc(2, HexNeighbors(e = 1), ResourceFeature.VILLAGE)

    private lateinit var player: TestPlayer

    private lateinit var homeBase: MapHexDesc

    private lateinit var mapDesc: MapDesc
    private lateinit var map: GameMap

    private lateinit var hex0: MapHex
    private lateinit var hex1: MapHex
    private lateinit var hex2: MapHex
    private lateinit var home: MapHex

    private lateinit var worker1: TestUnit
    private lateinit var worker2: TestUnit

    private val tradeAction = TradeAction()

    @Before
    fun setup() {
        player = TestPlayer()

        homeBase = MapHexDesc(3, HexNeighbors(sw = 1, se = 0), HomeBase(player))

        mapDesc = MapDesc(hexDesc0, hexDesc1, hexDesc2, homeBase)
        map = GameMap(mapDesc)

        hex0 = map.findHexAtIndex(0)!!
        hex1 = map.findHexAtIndex(1)!!
        hex2 = map.findHexAtIndex(2)!!

        home = map.findHomeBase(player)!!

        setupUnits(player)
    }

    @After
    fun tearDown() {
        hex0.unitsPresent.clear()
        hex1.unitsPresent.clear()
        hex2.unitsPresent.clear()

        worker2.heldMapResources.clear()
        worker1.heldMapResources.clear()

        home.unitsPresent.clear()
    }

    private fun setupUnits(player: TestPlayer) {
        worker1 = TestUnit(player, UnitType.WORKER)
        worker2 = TestUnit(player, UnitType.WORKER)
    }

    private fun deployUnits(hex: MapHex, vararg unit: TestUnit) {
        hex.moveUnitsInto(listOf(*unit))
        player.deployedUnits.addAll(listOf(*unit))
    }

    private fun verifyCarried(unit: TestUnit, vararg resources: MapResource) {
        assertTrue(unit.heldMapResources.containsAll(listOf(*resources)))
        when(unit) {
            worker1 -> {
                resources.forEach {
                    assertFalse(worker2.heldMapResources.contains(it))
                }
            }
            worker2 -> {
                resources.forEach {
                    assertFalse(worker1.heldMapResources.contains(it))
                }
            }
            else -> throw RuntimeException("Unknown unit $unit")
        }
    }

    @Test
    fun testTradePopularity() {
        val initPop = player.popularity
        val initCoins = player.coins

        tradeAction.performAction(player)

        assertEquals("Popularity did not increase!", initPop + 1, player.popularity)
        assertEquals("Cost was not subtracted!", initCoins - 1, player.coins)
    }

    @Ignore("Need to mock out the calls to place resources on the map and select trading for resources.")
    @Test
    fun testTradeResources() {
    }
}