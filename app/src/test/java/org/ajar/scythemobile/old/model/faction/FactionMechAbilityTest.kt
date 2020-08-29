package org.ajar.scythemobile.old.model.faction

import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.old.model.TestCombatBoard
import org.ajar.scythemobile.old.model.TestPlayer
import org.ajar.scythemobile.old.model.TestUnit
import org.ajar.scythemobile.old.model.map.GameMap
import org.ajar.scythemobile.old.model.map.MapHex
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.*
import org.ajar.scythemobile.old.model.map.*
import org.ajar.scythemobile.old.model.turn.MoveTurnAction

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class FactionMechAbilityTest {

    private val villageDesc: MapHexDesc = MapHexDesc(1, HexNeighbors(e = 2, se = 4, sw = 3), ResourceFeature.VILLAGE, RiverFeature(direction = Direction.SE))
    private val farmDesc: MapHexDesc = MapHexDesc(2, HexNeighbors(w = 1, se = 5, sw = 4), ResourceFeature.FARM, RiverFeature(direction = Direction.SW))
    private val mountainDesc: MapHexDesc = MapHexDesc(3, HexNeighbors(e = 4, ne = 1, nw = 6), ResourceFeature.MOUNTAIN, RiverFeature(direction = Direction.E))
    private val tundraDesc: MapHexDesc = MapHexDesc(5, HexNeighbors(w = 4, nw = 2, sw = 7), ResourceFeature.TUNDRA, RiverFeature(direction = Direction.W))
    private val forestDesc: MapHexDesc = MapHexDesc(6, HexNeighbors(nw = 3, ne = 4, e = 7), ResourceFeature.FOREST, RiverFeature(direction = Direction.NE))
    private val lakeDesc: MapHexDesc = MapHexDesc(7, HexNeighbors(w = 6, nw = 4, ne = 5), SpecialFeature.LAKE)

    private val tunnelDesc: MapHexDesc = MapHexDesc(4, HexNeighbors(1,2,5,7,6,3), SpecialFeature.TUNNEL,
            RiverFeature(direction = Direction.SW),
            RiverFeature(direction = Direction.W),
            RiverFeature(direction = Direction.NW),
            RiverFeature(direction = Direction.NE),
            RiverFeature(direction = Direction.E)
    )

    private val lakeDesc2: MapHexDesc = MapHexDesc(14, HexNeighbors(), SpecialFeature.LAKE)
    private val factoryDesc: MapHexDesc = MapHexDesc(15, HexNeighbors(), SpecialFeature.FACTORY)
    private val tunnelDesc2: MapHexDesc = MapHexDesc(16, HexNeighbors(), SpecialFeature.TUNNEL)

    private val mapDesc = MapDesc(villageDesc, farmDesc, mountainDesc, tundraDesc, forestDesc, lakeDesc, tunnelDesc, lakeDesc2, tunnelDesc2, factoryDesc)

    private lateinit var testMap: GameMap
    private lateinit var centerHex: MapHex

    private lateinit var player: TestPlayer
    private lateinit var enemy: TestPlayer

    @Before
    fun setup() {
        testMap = GameMap(mapDesc)
        centerHex = testMap.findHexAtIndex(4)!!

        player = TestPlayer()
        enemy = TestPlayer()
    }

    @Test
    fun testStandardMove() {
        val hex1 = testMap.findHexAtIndex(1)!!
        val hex6 = testMap.findHexAtIndex(6)!!
        val hex7 = testMap.findHexAtIndex(7)!!
        val move = StandardMove()
        assertTrue(move.validStartingHex(hex1))

        var validDest = move.validEndingHexes(hex1)
        assertEquals(2, validDest!!.size)
        assertEquals(2, validDest.filter { it!!.data.location == mountainDesc.location || it.data.location == farmDesc.location }.size)
        assertNotSame(validDest[0], validDest[1])

        assertTrue(move.validStartingHex(hex6))
        validDest = move.validEndingHexes(hex6)
        assertEquals(1, validDest!!.size)
        assertEquals(mountainDesc.location, validDest[0]!!.data.location)

        assertFalse(move.validStartingHex(hex7))
    }

    @Test
    fun testTunnelMove() {
        val tunnel2 = testMap.findHexAtIndex(tunnelDesc2.location)!!

        val tunnelMove = TunnelMove()
        assertTrue(tunnelMove.validStartingHex(centerHex))
        assertTrue(tunnelMove.validStartingHex(tunnel2))
        assertFalse(tunnelMove.validStartingHex(testMap.findHexAtIndex(factoryDesc.location)!!))

        var validDest = tunnelMove.validEndingHexes(centerHex)
        assertEquals(1, validDest!!.size)
        assertEquals(tunnelDesc2.location, validDest[0]!!.data.location)

        validDest = tunnelMove.validEndingHexes(tunnel2)
        assertEquals(1, validDest!!.size)
        assertEquals(tunnelDesc.location, validDest[0]!!.data.location)
    }

    @Test
    fun testRiverWalkForestMountain() {
        val riverWalk = RiverWalk.FOREST_MOUNTAIN
        assertTrue(riverWalk.validStartingHex(centerHex))

        assertTrue(riverWalk.validUnitType(UnitType.CHARACTER))
        assertTrue(riverWalk.validUnitType(UnitType.MECH))
        assertFalse(riverWalk.validUnitType(UnitType.WORKER))
        assertFalse(riverWalk.validUnitType(UnitType.AIRSHIP))

        val validDest = riverWalk.validEndingHexes(centerHex)
        assertEquals(2, validDest!!.size)
        assertEquals(2, validDest.filter { it!!.data.location == mountainDesc.location || it.data.location == forestDesc.location }.size)
        assertNotSame(validDest[0], validDest[1])
    }

    @Test
    fun testRiverWalkFarmTundra() {
        val riverWalk = RiverWalk.FARM_TUNDRA
        assertTrue(riverWalk.validStartingHex(centerHex))

        assertTrue(riverWalk.validUnitType(UnitType.CHARACTER))
        assertTrue(riverWalk.validUnitType(UnitType.MECH))
        assertFalse(riverWalk.validUnitType(UnitType.WORKER))
        assertFalse(riverWalk.validUnitType(UnitType.AIRSHIP))

        val validDest = riverWalk.validEndingHexes(centerHex)
        assertEquals(2, validDest!!.size)
        assertEquals(2, validDest.filter { it!!.data.location == farmDesc.location || it.data.location == tundraDesc.location }.size)
        assertNotSame(validDest[0], validDest[1])
    }

    @Test
    fun testRiverWalkFarmVillage() {
        val riverWalk = RiverWalk.FARM_VILLAGE
        assertTrue(riverWalk.validStartingHex(centerHex))

        assertTrue(riverWalk.validUnitType(UnitType.CHARACTER))
        assertTrue(riverWalk.validUnitType(UnitType.MECH))
        assertFalse(riverWalk.validUnitType(UnitType.WORKER))
        assertFalse(riverWalk.validUnitType(UnitType.AIRSHIP))

        val validDest = riverWalk.validEndingHexes(centerHex)
        assertEquals(2, validDest!!.size)
        assertEquals(2,validDest.filter { it!!.data.location == farmDesc.location || it.data.location == villageDesc.location }.size)
        assertNotSame(validDest[0], validDest[1])
    }

    @Test
    fun testRiverWalkVillageMountain() {
        val riverWalk = RiverWalk.VILLAGE_MOUNTAIN
        assertTrue(riverWalk.validStartingHex(centerHex))

        assertTrue(riverWalk.validUnitType(UnitType.CHARACTER))
        assertTrue(riverWalk.validUnitType(UnitType.MECH))
        assertFalse(riverWalk.validUnitType(UnitType.WORKER))
        assertFalse(riverWalk.validUnitType(UnitType.AIRSHIP))

        val validDest = riverWalk.validEndingHexes(centerHex)
        assertEquals(2, validDest!!.size)
        assertEquals(2, validDest.filter { it!!.data.location == mountainDesc.location || it.data.location == villageDesc.location }.size)
        assertNotSame(validDest[0], validDest[1])
    }

    @Test
    fun testUnderpass() {
        val mountain1 = testMap.findHexAtIndex(mountainDesc.location)!!
        val tunnel1 = testMap.findHexAtIndex(tunnelDesc.location)!!
        val village1 = testMap.findHexAtIndex(villageDesc.location)!!

        val underpass = Underpass()
        assertTrue(underpass.validStartingHex(mountain1))
        assertTrue(underpass.validStartingHex(tunnel1))
        assertFalse(underpass.validStartingHex(village1))

        assertTrue(underpass.validUnitType(UnitType.CHARACTER))
        assertTrue(underpass.validUnitType(UnitType.MECH))
        assertFalse(underpass.validUnitType(UnitType.WORKER))
        assertFalse(underpass.validUnitType(UnitType.AIRSHIP))

        var validDest = underpass.validEndingHexes(mountain1)
        assertEquals(2, validDest!!.size)
        assertTrue(validDest.filter { it!!.data.location == tunnelDesc.location || it.data.location == tunnelDesc2.location}.size == 2)

        val startingUnit = TestUnit(player, UnitType.MECH)
        val endingUnit = TestUnit(player, UnitType.WORKER)

        centerHex.unitsPresent.add(startingUnit)

        validDest = underpass.validEndingHexes(centerHex)
        assertEquals(0, validDest!!.size)

        mountain1.unitsPresent.add(endingUnit)

        validDest = underpass.validEndingHexes(centerHex)
        assertEquals(1, validDest!!.size)
        assertTrue(validDest.filter { it!!.data.location == mountainDesc.location }.size == 1)
    }

    @Test
    fun testTownship() {
        val village = testMap.findHexAtIndex(villageDesc.location)!!
        val factory = testMap.findHexAtIndex(factoryDesc.location)!!
        val mountain = testMap.findHexAtIndex(mountainDesc.location)!!

        val township = Township()
        assertTrue(township.validStartingHex(village))
        assertTrue(township.validStartingHex(factory))
        assertFalse(township.validStartingHex(mountain))

        assertTrue(township.validUnitType(UnitType.CHARACTER))
        assertTrue(township.validUnitType(UnitType.MECH))
        assertFalse(township.validUnitType(UnitType.WORKER))
        assertFalse(township.validUnitType(UnitType.AIRSHIP))

        var validDest = township.validEndingHexes(village)
        assertEquals(1, validDest!!.size)
        assertTrue(validDest.filter { it!!.data.location == factoryDesc.location }.size == 1)

        val startingUnit = TestUnit(player, UnitType.MECH)
        val endingUnit = TestUnit(player, UnitType.WORKER)

        factory.unitsPresent.add(startingUnit)

        validDest = township.validEndingHexes(factory)
        assertEquals(0, validDest!!.size)

        village.unitsPresent.add(endingUnit)

        validDest = township.validEndingHexes(factory)
        assertEquals(1, validDest!!.size)
        assertTrue(validDest.filter { it!!.data.location == villageDesc.location }.size == 1)
    }

    @Test
    fun testSeaworthy() {
        val lake1 = testMap.findHexAtIndex(lakeDesc.location)!!

        val seaworthy = Seaworthy()
        assertTrue(seaworthy.validStartingHex(lake1))

        assertTrue(seaworthy.validUnitType(UnitType.CHARACTER))
        assertTrue(seaworthy.validUnitType(UnitType.MECH))
        assertFalse(seaworthy.validUnitType(UnitType.WORKER))
        assertFalse(seaworthy.validUnitType(UnitType.AIRSHIP))

        var validDest = seaworthy.validEndingHexes(lake1)
        assertEquals(3, validDest!!.size)
        assertTrue(validDest.filter { it!!.data.location == tunnelDesc.location || it.data.location == forestDesc.location || it.data.location == tundraDesc.location}.size == 3)

        validDest = seaworthy.validEndingHexes(centerHex)
        assertEquals(1, validDest!!.size)
        assertTrue(validDest.filter { it!!.data.location == lakeDesc.location }.size == 1)
    }

    @Test
    fun testWayfare() {
        testMap.addHomeBase(MapHexDesc(-42, HexNeighbors(sw = 1, se = 2), HomeBase(player)), player)
        testMap.addHomeBase(MapHexDesc(-43, HexNeighbors(nw = 6, ne = 7), HomeBase(enemy)), enemy)
        testMap.addHomeBase(MapHexDesc(-44, HexNeighbors(e = 1, se = 3), HomeBase(TestPlayer())))

        val wayfare = Wayfare()
        assertTrue(wayfare.validStartingHex(centerHex))

        assertTrue(wayfare.validUnitType(UnitType.CHARACTER))
        assertTrue(wayfare.validUnitType(UnitType.MECH))
        assertFalse(wayfare.validUnitType(UnitType.WORKER))
        assertFalse(wayfare.validUnitType(UnitType.AIRSHIP))

        val unit = TestUnit(player, UnitType.MECH)
        centerHex.unitsPresent.add(unit)

        val validDest = wayfare.validEndingHexes(centerHex)
        assertEquals(2, validDest!!.size)
        assertTrue(validDest.filter { it!!.data.location == -42 || it.data.location == -44 }.size == 2)
    }

    @Test
    fun testRally() {
        val rally = Rally()
        assertTrue(rally.validStartingHex(centerHex))

        assertTrue(rally.validUnitType(UnitType.CHARACTER))
        assertTrue(rally.validUnitType(UnitType.MECH))
        assertFalse(rally.validUnitType(UnitType.WORKER))
        assertFalse(rally.validUnitType(UnitType.AIRSHIP))

        val worker = TestUnit(player, UnitType.WORKER)
        val mech = TestUnit(player, UnitType.MECH)
        val flag = TestUnit(player, UnitType.FLAG)
        val enemyWorker = TestUnit(enemy, UnitType.WORKER)
        val enemyMech = TestUnit(enemy, UnitType.MECH)

        centerHex.unitsPresent.add(mech)
        testMap.findHexAtIndex(villageDesc.location)!!.unitsPresent.add(worker)
        testMap.findHexAtIndex(forestDesc.location)!!.unitsPresent.add(flag)
        testMap.findHexAtIndex(forestDesc.location)!!.unitsPresent.add(enemyMech)
        testMap.findHexAtIndex(tundraDesc.location)!!.unitsPresent.add(enemyWorker)

        val validDest = rally.validEndingHexes(centerHex)
        assertEquals(2, validDest!!.size)
        assertTrue(validDest.filter { it!!.data.location == villageDesc.location || it.data.location == forestDesc.location }.size == 2)
    }

    @Test
    fun testShinobi() {
        val shinobi = Shinobi()
        assertTrue(shinobi.validStartingHex(centerHex))

        assertTrue(shinobi.validUnitType(UnitType.CHARACTER))
        assertTrue(shinobi.validUnitType(UnitType.MECH))
        assertFalse(shinobi.validUnitType(UnitType.WORKER))
        assertFalse(shinobi.validUnitType(UnitType.AIRSHIP))

        val trap1 = TestUnit(player, UnitType.TRAP)
        val trap2 = TestUnit(player, UnitType.TRAP)
        val mech = TestUnit(player, UnitType.MECH)
        val enemyMech = TestUnit(enemy, UnitType.MECH)

        centerHex.unitsPresent.add(mech)
        testMap.findHexAtIndex(villageDesc.location)!!.unitsPresent.add(trap1)
        testMap.findHexAtIndex(forestDesc.location)!!.unitsPresent.add(trap2)
        testMap.findHexAtIndex(forestDesc.location)!!.unitsPresent.add(enemyMech)

        val validDest = shinobi.validEndingHexes(centerHex)
        assertEquals(2, validDest!!.size)
        assertTrue(validDest.filter { it!!.data.location == villageDesc.location || it.data.location == forestDesc.location }.size == 2)
    }

    @Test
    fun testSubmerge() {
        val lake1 = testMap.findHexAtIndex(7)!!

        val submerge = Submerge()
        assertTrue(submerge.validStartingHex(lake1))

        assertTrue(submerge.validUnitType(UnitType.CHARACTER))
        assertTrue(submerge.validUnitType(UnitType.MECH))
        assertFalse(submerge.validUnitType(UnitType.WORKER))
        assertFalse(submerge.validUnitType(UnitType.AIRSHIP))

        var validDest = submerge.validEndingHexes(lake1)
        assertEquals(1, validDest!!.size)
        assertTrue(validDest.filter { it!!.data.location == lakeDesc2.location }.size == 1)

        validDest = submerge.validEndingHexes(centerHex)
        assertEquals(1, validDest!!.size)
        assertTrue(validDest.filter { it!!.data.location == lakeDesc.location }.size == 1)
    }

    @Test
    fun testBurrow() {
        val riverWalk = Burrow()
        assertTrue(riverWalk.validStartingHex(centerHex))

        assertTrue(riverWalk.validUnitType(UnitType.CHARACTER))
        assertTrue(riverWalk.validUnitType(UnitType.MECH))
        assertFalse(riverWalk.validUnitType(UnitType.WORKER))
        assertFalse(riverWalk.validUnitType(UnitType.AIRSHIP))

        var validDest = riverWalk.validEndingHexes(centerHex)
        assertEquals(5, validDest!!.size)
        assertTrue(validDest.filter {
            it!!.data.location == mountainDesc.location ||
            it.data.location == villageDesc.location ||
            it.data.location == forestDesc.location ||
            it.data.location == farmDesc.location ||
            it.data.location == tundraDesc.location
        }.size == 5)

        val otherHex = testMap.findHexAtIndex(1)!!
        assertTrue(riverWalk.validStartingHex(otherHex))

        validDest = riverWalk.validEndingHexes(otherHex)
        assertEquals(1, validDest!!.size)
        assertTrue(validDest[0]!!.data.location == tunnelDesc.location)
    }

    @Test
    fun testToka() {
        val riverWalk = Toka()

        assertTrue(riverWalk.canUse(player))

        assertTrue(riverWalk.validStartingHex(centerHex))

        assertTrue(riverWalk.validUnitType(UnitType.CHARACTER))
        assertTrue(riverWalk.validUnitType(UnitType.MECH))
        assertFalse(riverWalk.validUnitType(UnitType.WORKER))
        assertFalse(riverWalk.validUnitType(UnitType.AIRSHIP))

        var validDest = riverWalk.validEndingHexes(centerHex)
        assertEquals(5, validDest!!.size)
        assertTrue(validDest.filter {
            it!!.data.location == mountainDesc.location ||
                    it.data.location == villageDesc.location ||
                    it.data.location == forestDesc.location ||
                    it.data.location == farmDesc.location ||
                    it.data.location == tundraDesc.location
        }.size == 5)

        val otherHex = testMap.findHexAtIndex(1)!!
        assertTrue(riverWalk.validStartingHex(otherHex))

        validDest = riverWalk.validEndingHexes(otherHex)
        assertEquals(3, validDest!!.size)

        player.turn.performAction(MoveTurnAction(TestUnit(player, UnitType.CHARACTER), centerHex, otherHex, rule = riverWalk))

        assertFalse(riverWalk.canUse(player))
    }

    @Test
    fun testDisarm() {
        val mech = TestUnit(player, UnitType.MECH)
        val enemyMech = TestUnit(enemy, UnitType.MECH)

        centerHex.unitsPresent.add(mech)
        centerHex.unitsPresent.add(enemyMech)

        player.power = 2
        enemy.power = 2

        val disarm = Disarm()

        assertTrue(disarm.appliesForAttack)
        var testCombatBoard = TestCombatBoard(centerHex, player, enemy)

        assertTrue(disarm.validCombatHex(player, testCombatBoard))
        assertEquals(2, testCombatBoard.defendingPlayer.power)

        disarm.applyEffect(player, testCombatBoard)

        assertEquals(0, testCombatBoard.defendingPlayer.power)

        player.power = 2
        enemy.power = 2

        assertTrue(disarm.appliesForDefense)
        testCombatBoard = TestCombatBoard(centerHex, enemy, player)

        assertTrue(disarm.validCombatHex(player, testCombatBoard))
        assertEquals(2, testCombatBoard.attackingPlayer.power)

        disarm.applyEffect(player, testCombatBoard)

        assertEquals(0, testCombatBoard.attackingPlayer.power)

        assertFalse(disarm.appliesDuringUncontested)
    }

    @Test
    fun testArtillery() {
        val mech = TestUnit(player, UnitType.MECH)
        val enemyMech = TestUnit(enemy, UnitType.MECH)

        centerHex.unitsPresent.add(mech)
        centerHex.unitsPresent.add(enemyMech)

        val artillery = Artillery()

        player.power = 2
        enemy.power = 2

        assertTrue(artillery.appliesForAttack)
        var testCombatBoard = TestCombatBoard(centerHex, player, enemy)

        assertTrue(artillery.validCombatHex(player, testCombatBoard))
        assertEquals(2, testCombatBoard.defendingPlayer.power)
        assertEquals(2, testCombatBoard.attackingPlayer.power)

        artillery.applyEffect(player, testCombatBoard)

        assertEquals(1, testCombatBoard.attackingPlayer.power)
        assertEquals(0, testCombatBoard.defendingPlayer.power)

        player.power = 2
        enemy.power = 2

        assertTrue(artillery.appliesForDefense)
        testCombatBoard = TestCombatBoard(centerHex, enemy, player)

        assertTrue(artillery.validCombatHex(player, testCombatBoard))
        assertEquals(2, testCombatBoard.defendingPlayer.power)
        assertEquals(2, testCombatBoard.attackingPlayer.power)

        artillery.applyEffect(player, testCombatBoard)

        assertEquals(0, testCombatBoard.attackingPlayer.power)
        assertEquals(1, testCombatBoard.defendingPlayer.power)

        assertFalse(artillery.appliesDuringUncontested)
    }

    @Test
    fun testPeoplesArmy() {
        val mech = TestUnit(player, UnitType.MECH)
        val worker = TestUnit(player, UnitType.WORKER)
        val enemyMech = TestUnit(enemy, UnitType.MECH)

        centerHex.unitsPresent.add(mech)
        centerHex.unitsPresent.add(worker)
        centerHex.unitsPresent.add(enemyMech)

        val peoplesArmy = PeoplesArmy()

        assertTrue(peoplesArmy.appliesForAttack)
        var testCombatBoard = TestCombatBoard(centerHex, player, enemy)

        assertTrue(peoplesArmy.validCombatHex(player, testCombatBoard))
        assertEquals(1, testCombatBoard.attackingPlayer.cardLimit)

        peoplesArmy.applyEffect(player, testCombatBoard)

        assertEquals(2, testCombatBoard.attackingPlayer.cardLimit)

        assertTrue(peoplesArmy.appliesForDefense)
        testCombatBoard = TestCombatBoard(centerHex, enemy, player)

        assertTrue(peoplesArmy.validCombatHex(player, testCombatBoard))
        assertEquals(1, testCombatBoard.defendingPlayer.cardLimit)

        peoplesArmy.applyEffect(player, testCombatBoard)

        assertEquals(2, testCombatBoard.defendingPlayer.cardLimit)

        assertFalse(peoplesArmy.appliesDuringUncontested)
    }

    @Test
    fun testScout() {
        val mech = TestUnit(player, UnitType.MECH)
        val enemyMech = TestUnit(enemy, UnitType.MECH)

        centerHex.unitsPresent.add(mech)
        centerHex.unitsPresent.add(enemyMech)

        player.combatCards.clear()
        enemy.combatCards.clear()

        player.combatCards.add(CombatCardDeck.currentDeck.drawCard())
        player.combatCards.add(CombatCardDeck.currentDeck.drawCard())

        enemy.combatCards.add(CombatCardDeck.currentDeck.drawCard())
        enemy.combatCards.add(CombatCardDeck.currentDeck.drawCard())

        val scout = Scout()

        assertTrue(scout.appliesForAttack)
        var testCombatBoard = TestCombatBoard(centerHex, player, enemy)

        assertTrue(scout.validCombatHex(player, testCombatBoard))
        assertEquals(2, testCombatBoard.attackingPlayer.cardsAvailable.size)
        assertEquals(2, testCombatBoard.defendingPlayer.cardsAvailable.size)

        scout.applyEffect(player, testCombatBoard)

        assertEquals(3, testCombatBoard.attackingPlayer.cardsAvailable.size)
        assertEquals(1, testCombatBoard.defendingPlayer.cardsAvailable.size)

        assertTrue(scout.appliesForDefense)
        testCombatBoard = TestCombatBoard(centerHex, enemy, player)

        assertEquals(2, testCombatBoard.attackingPlayer.cardsAvailable.size)
        assertEquals(2, testCombatBoard.defendingPlayer.cardsAvailable.size)

        scout.applyEffect(player, testCombatBoard)

        assertEquals(1, testCombatBoard.attackingPlayer.cardsAvailable.size)
        assertEquals(3, testCombatBoard.defendingPlayer.cardsAvailable.size)

        assertFalse(scout.appliesDuringUncontested)
    }

    @Test
    fun testSword() {
        val mech = TestUnit(player, UnitType.MECH)
        val enemyMech = TestUnit(enemy, UnitType.MECH)

        centerHex.unitsPresent.add(mech)
        centerHex.unitsPresent.add(enemyMech)

        player.power = 2
        enemy.power = 2

        val sword = Sword()

        assertTrue(sword.appliesForAttack)
        val testCombatBoard = TestCombatBoard(centerHex, player, enemy)

        assertTrue(sword.validCombatHex(player, testCombatBoard))
        assertEquals(2, testCombatBoard.defendingPlayer.power)

        sword.applyEffect(player, testCombatBoard)

        assertEquals(0, testCombatBoard.defendingPlayer.power)

        assertFalse(sword.appliesForDefense)
        assertFalse(sword.appliesDuringUncontested)
    }

    @Test
    fun testShield() {
        val mech = TestUnit(player, UnitType.MECH)
        val enemyMech = TestUnit(enemy, UnitType.MECH)

        centerHex.unitsPresent.add(mech)
        centerHex.unitsPresent.add(enemyMech)

        player.power = 2
        enemy.power = 2

        val shield = Shield()

        assertFalse(shield.appliesForAttack)

        assertTrue(shield.appliesForDefense)
        val testCombatBoard = TestCombatBoard(centerHex, enemy, player)

        assertTrue(shield.validCombatHex(player, testCombatBoard))
        assertEquals(2, testCombatBoard.defendingPlayer.power)

        shield.applyEffect(player, testCombatBoard)

        assertEquals(4, testCombatBoard.defendingPlayer.power)

        assertFalse(shield.appliesDuringUncontested)
    }

    @Test
    fun testRonin() {
        val mech = TestUnit(player, UnitType.MECH)
        val enemyMech = TestUnit(enemy, UnitType.MECH)

        centerHex.unitsPresent.add(mech)
        centerHex.unitsPresent.add(enemyMech)

        player.power = 2
        enemy.power = 2

        val ronin = Ronin()

        assertTrue(ronin.appliesForAttack)
        var testCombatBoard = TestCombatBoard(centerHex, player, enemy)

        assertTrue(ronin.validCombatHex(player, testCombatBoard))
        assertEquals(2, testCombatBoard.attackingPlayer.power)

        ronin.applyEffect(player, testCombatBoard)

        assertEquals(4, testCombatBoard.attackingPlayer.power)

        player.power = 2
        enemy.power = 2

        assertTrue(ronin.appliesForDefense)
        testCombatBoard = TestCombatBoard(centerHex, enemy, player)

        assertTrue(ronin.validCombatHex(player, testCombatBoard))
        assertEquals(2, testCombatBoard.defendingPlayer.power)

        ronin.applyEffect(player, testCombatBoard)

        assertEquals(4, testCombatBoard.defendingPlayer.power)

        //Shouldn't apply with two units present.
        val mech2 = TestUnit(player, UnitType.MECH)

        centerHex.unitsPresent.add(mech2)

        testCombatBoard = TestCombatBoard(centerHex, player, enemy)

        assertFalse(ronin.validCombatHex(player, testCombatBoard))

        assertFalse(ronin.appliesDuringUncontested)
    }

    @Test
    fun testCamaraderie() {
        val mech = TestUnit(player, UnitType.MECH)
        val enemyMech = TestUnit(enemy, UnitType.MECH)

        centerHex.unitsPresent.add(mech)
        centerHex.unitsPresent.add(enemyMech)

        val camaraderie = Camaraderie()

        assertTrue(camaraderie.appliesForAttack)
        assertFalse(camaraderie.appliesForDefense)
        assertTrue(camaraderie.appliesDuringUncontested)

        val testCombatBoard = TestCombatBoard(centerHex, player, enemy)

        assertTrue(camaraderie.validCombatHex(player, testCombatBoard))
        assertFalse(testCombatBoard.attackingPlayer.camaraderie)

        camaraderie.applyEffect(player, testCombatBoard)

        //Testing the effect of Camaraderie will be the job of the combat board tests
        assertTrue(testCombatBoard.attackingPlayer.camaraderie)
    }

    @Test
    fun testSuiton() {
        val suiton = Suiton()

        assertTrue(suiton.canUse(player))

        assertTrue(suiton.validUnitType(UnitType.CHARACTER))
        assertTrue(suiton.validUnitType(UnitType.MECH))
        assertFalse(suiton.validUnitType(UnitType.WORKER))
        assertFalse(suiton.validUnitType(UnitType.AIRSHIP))
        assertFalse(suiton.allowsRetreat)

        assertTrue(suiton.validStartingHex(centerHex))

        val validDest = suiton.validEndingHexes(centerHex)
        assertEquals(1, validDest!!.size)
        assertEquals(lakeDesc.location, validDest[0]!!.data.location)

        val mech = TestUnit(player, UnitType.MECH)
        val enemyMech = TestUnit(enemy, UnitType.MECH)

        val lakeHex = testMap.findHexAtIndex(lakeDesc.location)!!

        lakeHex.unitsPresent.add(mech)
        lakeHex.unitsPresent.add(enemyMech)

        assertTrue(suiton.appliesForAttack)
        var testCombatBoard = TestCombatBoard(lakeHex, player, enemy)

        assertTrue(suiton.validCombatHex(player, testCombatBoard))
        assertEquals(1, testCombatBoard.attackingPlayer.cardLimit)

        suiton.applyEffect(player, testCombatBoard)

        assertEquals(2, testCombatBoard.attackingPlayer.cardLimit)

        assertTrue(suiton.appliesForDefense)
        testCombatBoard = TestCombatBoard(lakeHex, enemy, player)

        assertTrue(suiton.validCombatHex(player, testCombatBoard))
        assertEquals(1, testCombatBoard.defendingPlayer.cardLimit)

        suiton.applyEffect(player, testCombatBoard)

        assertEquals(2, testCombatBoard.defendingPlayer.cardLimit)

        assertFalse(suiton.appliesDuringUncontested)
    }
}
