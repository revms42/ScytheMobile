package org.ajar.scythemobile.model

import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.entity.ResourceHolder
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.FactionMat
import org.ajar.scythemobile.model.map.*
import org.ajar.scythemobile.model.production.MapResource
import org.ajar.scythemobile.model.production.MapResourceType
import org.ajar.scythemobile.model.production.PlayerResourceType
import org.ajar.scythemobile.model.production.ResourceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AbstractPlayerTest {

    lateinit var player: TestPlayer
    lateinit var hex: MapHex
    lateinit var worker: TestUnit
    lateinit var map: GameMap

    @Before
    fun setup() {
        player = TestPlayer(FactionMat.POLONIA)

        val mapDesc = MapDesc(MapHexDesc(1, HexNeighbors(), SpecialFeature.ANY))

        map = GameMap(mapDesc)
        GameMap.currentMap = map

        worker = TestUnit(player, UnitType.WORKER)
        player.deployedUnits.add(worker)

        hex = map.findHexAtIndex(1)!!

        hex.moveUnitsInto(listOf(worker))
    }

    private fun setupResources(type: ResourceType, amount: Int, holder: ResourceHolder? = null) {
        if(type is MapResourceType) {
            holder!!.heldMapResources.addAll( (0..(amount-1)).map { MapResource(type) } )
        } else {
            when(type as PlayerResourceType) {
                PlayerResourceType.COMBAT_CARD -> player.combatCards.addAll( (0..(amount-1)).map { CombatCardDeck.currentDeck.drawCard() } )
                PlayerResourceType.POPULARITY -> player.popularity += amount
                PlayerResourceType.COIN -> player.coins += amount
                PlayerResourceType.POWER -> player.power += amount
            }
        }
    }

    @Test
    fun testPayMapResources() {
        setupResources(MapResourceType.METAL, 1, hex)
        setupResources(MapResourceType.FOOD, 1, hex)
        setupResources(MapResourceType.WOOD, 1, hex)
        setupResources(MapResourceType.OIL, 1, hex)

        val list = listOf(
                MapResourceType.METAL,
                MapResourceType.WOOD,
                MapResourceType.FOOD,
                MapResourceType.OIL
        )

        assertTrue(player.canPay(list))

        assertTrue(player.payResources(list))

        assertEquals(0, hex.heldMapResources.size)
    }

    @Test
    fun testPayUnitResources() {
        setupResources(MapResourceType.METAL, 1, worker)
        setupResources(MapResourceType.FOOD, 1, worker)
        setupResources(MapResourceType.WOOD, 1, worker)
        setupResources(MapResourceType.OIL, 1, worker)

        val list = listOf(
                MapResourceType.METAL,
                MapResourceType.WOOD,
                MapResourceType.FOOD,
                MapResourceType.OIL
        )

        assertTrue(player.canPay(list))

        assertTrue(player.payResources(list))

        assertEquals(0, hex.heldMapResources.size)
    }

    @Test
    fun testPayPlayerResources() {
        val startingCards = player.combatCards.size
        val startingPop = player.popularity
        val startingPower = player.power
        val startingCoins = player.coins

        setupResources(PlayerResourceType.COMBAT_CARD, 1)
        setupResources(PlayerResourceType.POPULARITY, 1)
        setupResources(PlayerResourceType.POWER, 1)
        setupResources(PlayerResourceType.COIN, 1)

        val list = listOf(
                PlayerResourceType.COMBAT_CARD,
                PlayerResourceType.POPULARITY,
                PlayerResourceType.POWER,
                PlayerResourceType.COIN
        )

        assertTrue(player.canPay(list))

        assertTrue(player.payResources(list))

        assertEquals(startingCards, player.combatCards.size)
        assertEquals(startingPop, player.popularity)
        assertEquals(startingPower, player.power)
        assertEquals(startingCoins, player.coins)
    }

    @Test
    fun testSelectUnits() {
        val mech = TestUnit(player, UnitType.MECH)
        val character = TestUnit(player, UnitType.CHARACTER)
        val trap = TestUnit(player, UnitType.TRAP)
        val flag = TestUnit(player, UnitType.FLAG)
        val structure = TestUnit(player, UnitType.STRUCTURE)
        val airship = TestUnit(player, UnitType.AIRSHIP)
        val worker2 = TestUnit(player, UnitType.WORKER)

        player.deployedUnits.addAll(listOf(mech, character, flag, trap, structure, airship, worker2))

        assertEquals(mech, player.selectUnits(UnitType.MECH)[0])
        assertEquals(character, player.selectUnits(UnitType.CHARACTER)[0])
        assertEquals(trap, player.selectUnits(UnitType.TRAP)[0])
        assertEquals(flag, player.selectUnits(UnitType.FLAG)[0])
        assertEquals(structure, player.selectUnits(UnitType.STRUCTURE)[0])
        assertEquals(airship, player.selectUnits(UnitType.AIRSHIP)[0])
        assertEquals(worker, player.selectUnits(UnitType.WORKER)[0])
        assertEquals(worker2, player.selectUnits(UnitType.WORKER)[1])
    }
}