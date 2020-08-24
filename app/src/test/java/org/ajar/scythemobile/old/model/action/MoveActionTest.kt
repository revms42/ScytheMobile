package org.ajar.scythemobile.old.model.action

import org.ajar.scythemobile.old.model.TestPlayer
import org.ajar.scythemobile.old.model.TestUnit
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.old.model.faction.FactionMat
import org.ajar.scythemobile.old.model.map.*
import org.ajar.scythemobile.old.model.playermat.MoveOrGainAction
import org.ajar.scythemobile.old.model.turn.MoveTurnAction
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MoveActionTest {

    private val hexDesc0 = MapHexDesc(0, HexNeighbors(w = 1), ResourceFeature.VILLAGE)
    private val hexDesc1 = MapHexDesc(1, HexNeighbors(w = 2, e = 0), ResourceFeature.VILLAGE)
    private val hexDesc2 = MapHexDesc(2, HexNeighbors(e = 1), ResourceFeature.VILLAGE)

    private val mapDesc = MapDesc(hexDesc0, hexDesc1, hexDesc2)
    private val map = GameMap(mapDesc)

    private val moveAction = MoveOrGainAction()

    private lateinit var hex0: MapHex
    private lateinit var hex1: MapHex
    private lateinit var hex2: MapHex

    private lateinit var player: TestPlayer

    private lateinit var mech1: TestUnit
    private lateinit var mech2: TestUnit
    private lateinit var mech3: TestUnit

    private lateinit var worker1: TestUnit
    private lateinit var worker2: TestUnit

    @Before
    fun setup() {
        hex0 = map.findHexAtIndex(0)!!
        hex1 = map.findHexAtIndex(1)!!
        hex2 = map.findHexAtIndex(2)!!
    }

    @After
    fun tearDown() {
        hex0.unitsPresent.clear()
        hex1.unitsPresent.clear()
        hex2.unitsPresent.clear()
    }

    private fun setupPlayer(faction: FactionMat) {
        player = TestPlayer(faction)
        setupUnits(player)
    }

    private fun setupUnits(player: TestPlayer) {
        mech1 = TestUnit(player, UnitType.MECH)
        mech2 = TestUnit(player, UnitType.MECH)
        mech3 = TestUnit(player, UnitType.MECH)

        worker1 = TestUnit(player, UnitType.WORKER)
        worker2 = TestUnit(player, UnitType.WORKER)
    }

    private fun deployUnits(hex: MapHex, vararg unit: TestUnit) {
        hex.moveUnitsInto(listOf(*unit))
        player.deployedUnits.addAll(listOf(*unit))
    }

    private fun verifyLocation(unit: GameUnit, hex: MapHex) {
        assertTrue(hex.unitsPresent.contains(unit))
        when(hex) {
            hex0 -> {
                assertFalse(hex1.unitsPresent.contains(unit))
                assertFalse(hex2.unitsPresent.contains(unit))
            }
            hex1 -> {
                assertFalse(hex0.unitsPresent.contains(unit))
                assertFalse(hex2.unitsPresent.contains(unit))
            }
            hex2 -> {
                assertFalse(hex1.unitsPresent.contains(unit))
                assertFalse(hex0.unitsPresent.contains(unit))
            }
        }
    }

    @Test
    fun testMoveBasic() {
        setupPlayer(FactionMat.CRIMEA)
        deployUnits(hex0, mech1, mech2)

        assertTrue(moveAction.canPerform.invoke(player))

        moveAction.performAction(player)

        assertEquals("Units at ${map.locateUnit(mech1)?.desc?.location} and ${map.locateUnit(mech2)?.desc?.location}",2, hex1.unitsPresent.size)

        verifyLocation(mech1, hex1)
        verifyLocation(mech2, hex1)
    }

    @Test
    fun testMoveSpeed() {
        setupPlayer(FactionMat.CRIMEA)
        deployUnits(hex0, mech1, mech2)

        player.factionMat.unlockMechAbility("Speed")

        // Need to change how selections are made because it will choose the first hex, then go back to the original hex.
        moveAction.performAction(player)

        assertTrue(moveAction.canPerform.invoke(player))

        val turnActions = player.turn.findActionOfType(MoveTurnAction::class.java)
        assertEquals("There should be 4 turn actions", 4, turnActions.size)

        val mech1TurnActions = turnActions.filter { it.unit === mech1 }
        assertEquals("$mech1 should have two turn actions", 2, mech1TurnActions.size)

        val mech2TurnActions = turnActions.filter { it.unit === mech2 }
        assertEquals("$mech2 should have two turn actions", 2, mech2TurnActions.size)

        assertTrue("$mech1 did not move to $hex1", mech1TurnActions.firstOrNull { it.to === hex1} != null)
        assertTrue("$mech2 did not move to $hex1", mech2TurnActions.firstOrNull { it.to === hex1} != null)

        assertTrue("$mech1 did not move to $hex2 or $hex0", mech1TurnActions.firstOrNull { it.to === hex2 || it.to === hex0 } != null)
        assertTrue("$mech2 did not move to $hex2 or $hex0", mech2TurnActions.firstOrNull { it.to === hex2 || it.to === hex0 } != null)
    }

    @Test
    fun testMoveSpeedCombat() {
        setupPlayer(FactionMat.CRIMEA)
        deployUnits(hex0, mech1, mech2)

        val enemy = TestPlayer(FactionMat.POLONIA)
        val enemyMech = TestUnit(enemy, UnitType.MECH)

        hex1.moveUnitsInto(listOf(enemyMech))

        player.factionMat.unlockMechAbility("Speed")

        // Need to change how selections are made because it will choose the first hex, then go back to the original hex.
        moveAction.performAction(player)

        assertTrue(moveAction.canPerform.invoke(player))

        assertEquals("Too many units at location $hex2",0, hex2.unitsPresent.size)
        assertEquals("Incorrect units at $hex1: ${hex1.unitsPresent}",3, hex1.unitsPresent.size)

        verifyLocation(mech1, hex1)
        verifyLocation(mech2, hex1)

        assertEquals("An incorrect number of combat boards are queued",1, player.queuedCombatBoards.size)
        assertEquals("Incorrect number of units in the combat board for the player", 2, player.queuedCombatBoards[0].attackingPlayer.unitsPresent.size)
    }

    @Test
    fun testMoveWorkers() {
        setupPlayer(FactionMat.CRIMEA)
        deployUnits(hex1, worker1, worker2)

        val enemy = TestPlayer(FactionMat.POLONIA)
        val enemyMech = TestUnit(enemy, UnitType.MECH)

        hex0.moveUnitsInto(listOf(enemyMech))

        // Need to change how selections are made because it will choose the first hex, then go back to the original hex.
        moveAction.performAction(player)

        assertTrue(moveAction.canPerform.invoke(player))

        assertEquals("Too many units at location $hex0: ${hex0.unitsPresent}",1, hex0.unitsPresent.size)
        assertEquals("Incorrect units at $hex2: ${hex2.unitsPresent}",2, hex2.unitsPresent.size)

        verifyLocation(worker1, hex2)
        verifyLocation(worker2, hex2)

        assertEquals("An incorrect number of combat boards are queued",0, player.queuedCombatBoards.size)
    }

    // We need both of these to make sure there is no inherent bias for choosing one direction over the other which would invalidate the result of the previous test.
    @Test
    fun testMoveWorkers2() {
        setupPlayer(FactionMat.CRIMEA)
        deployUnits(hex1, worker1, worker2)

        val enemy = TestPlayer(FactionMat.POLONIA)
        val enemyMech = TestUnit(enemy, UnitType.MECH)

        hex2.moveUnitsInto(listOf(enemyMech))

        // Need to change how selections are made because it will choose the first hex, then go back to the original hex.
        moveAction.performAction(player)

        assertTrue(moveAction.canPerform.invoke(player))

        assertEquals("Too many units at location $hex2: ${hex2.unitsPresent}",1, hex2.unitsPresent.size)
        assertEquals("Incorrect units at $hex0: ${hex0.unitsPresent}",2, hex0.unitsPresent.size)

        verifyLocation(worker1, hex0)
        verifyLocation(worker2, hex0)

        assertEquals("An incorrect number of combat boards are queued",0, player.queuedCombatBoards.size)
    }
}