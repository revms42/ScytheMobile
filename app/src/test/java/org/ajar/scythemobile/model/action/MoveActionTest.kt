package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.model.TestPlayer
import org.ajar.scythemobile.model.TestUnit
import org.ajar.scythemobile.model.TestUser
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.FactionMat
import org.ajar.scythemobile.model.map.*
import org.ajar.scythemobile.model.playermat.MoveOrGainAction
import org.ajar.scythemobile.model.turn.MoveTurnAction
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

        assertEquals("Not enough units at location $hex2",2, hex2.unitsPresent.size)

        verifyLocation(mech1, hex2)
        verifyLocation(mech2, hex2)
    }

}