package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.model.TestPlayer
import org.ajar.scythemobile.model.TestUnit
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.entity.TrapUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.FactionMat
import org.ajar.scythemobile.model.turn.DeployTokenTurnAction
import org.ajar.scythemobile.model.turn.ResetTrapAction
import org.ajar.scythemobile.model.turn.TrapSprungAction
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MapHexTest {

    private lateinit var player: TestPlayer
    private lateinit var hex: MapHex

    @Before
    fun setup() {
        player = TestPlayer()
        hex = MapHex(MapHexDesc(0, HexNeighbors(), SpecialFeature.ANY))
    }

    @Test
    fun testMoveUnitsInto() {
        val character = TestUnit(player, UnitType.CHARACTER)
        hex.moveUnitsInto(listOf(character))
        assertEquals(1, hex.unitsPresent.size)

        val mech = TestUnit(player, UnitType.MECH)
        hex.moveUnitsInto(listOf(mech))
        assertEquals(2, hex.unitsPresent.size)

        val worker = TestUnit(player, UnitType.WORKER)
        hex.moveUnitsInto(listOf(worker))
        assertEquals(3, hex.unitsPresent.size)

        val structure = TestUnit(player, UnitType.STRUCTURE)
        hex.moveUnitsInto(listOf(structure))
        assertEquals(4, hex.unitsPresent.size)

        val flag = TestUnit(player, UnitType.FLAG)
        hex.moveUnitsInto(listOf(flag))
        assertEquals(5, hex.unitsPresent.size)

        val trap = TestUnit(player, UnitType.TRAP)
        hex.moveUnitsInto(listOf(trap))
        assertEquals(6, hex.unitsPresent.size)

        val airship = TestUnit(player, UnitType.AIRSHIP)
        hex.moveUnitsInto(listOf(airship))
        assertEquals(7, hex.unitsPresent.size)

        hex.unitsPresent.clear()

        hex.moveUnitsInto(listOf(character, mech, worker, structure, flag, trap, airship))
        assertEquals(7, hex.unitsPresent.size)
    }

    @Test
    fun testProvokeFight() {
        val character = TestUnit(player, UnitType.CHARACTER)

        val enemy = TestPlayer()
        val enemyCharacter = TestUnit(enemy, UnitType.CHARACTER)
        hex.moveUnitsInto(listOf(enemyCharacter))

        hex.moveUnitsInto(listOf(character))

        assertEquals(1, player.queuedCombatCards.size)

        val board = player.queuedCombatCards[0]

        assertEquals(hex, board.combatHex)
        assertEquals(player, board.attackingPlayer.player)
        assertEquals(enemy, board.defendingPlayer.player)

        assertEquals(1, board.getPlayerBoard(player).unitsPresent.size)
        assertEquals(character, board.getPlayerBoard(player).unitsPresent[0])

        assertEquals(1, board.getPlayerBoard(enemy).unitsPresent.size)
        assertEquals(enemyCharacter, board.getPlayerBoard(enemy).unitsPresent[0])
    }

    @Test
    fun testEncounterTrigger() {
        hex = MapHex(MapHexDesc(0, HexNeighbors(), SpecialFeature.ENCOUNTER))

        var outcome = false
        hex.encounterCard = object : EncounterCard {
            override val outcomes: List<EncounterOutcome> = listOf(
                    object : EncounterOutcome {
                        override val title: String = "Test Encounter Card"
                        override val description: String = ""

                        override fun applyOutcome(unit: GameUnit) {
                            outcome = true
                        }

                        override fun canMeetCost(player: Player): Boolean = true
                    }
            )

        }

        val character = TestUnit(player, UnitType.CHARACTER)

        var triggered = false
        val previousDoEncounter = player.doEncounter
        player.doEncounter = { encounter, unit ->
            assertEquals(hex.encounterCard, encounter)
            assertEquals(character, unit)
            triggered = true
            previousDoEncounter.invoke(encounter, unit)
        }

        hex.moveUnitsInto(listOf(character))
        assertEquals(1, hex.unitsPresent.size)
        assertTrue(triggered)
        assertTrue(outcome)
    }

    @Test
    fun testControllingUnitPriority() {
        val building =  TestUnit(player, UnitType.STRUCTURE)
        hex.moveUnitsInto(listOf(building))

        val enemy = TestPlayer()
        val enemyMech = TestUnit(enemy, UnitType.MECH)

        assertEquals(player, hex.playerInControl)
        assertTrue(hex.canUnitOccupy(enemyMech))

        hex.moveUnitsInto(listOf(enemyMech))

        assertEquals(enemy, hex.playerInControl)
        assertEquals(0, enemy.queuedCombatCards.size)

        val mech = TestUnit(player, UnitType.MECH)

        assertFalse(hex.canUnitOccupy(mech))
    }

    @Test
    fun testPlaceTrap() {
        player = TestPlayer(FactionMat.TOGAWA)

        val trap = player.tokens!![0]

        val character = TestUnit(player, UnitType.CHARACTER)
        hex.moveUnitsInto(listOf(character))

        assertTrue(hex.unitsPresent.contains(trap))
        assertNotNull(player.turn.findFirstOfType(DeployTokenTurnAction::class.java))
    }

    @Test
    fun testTriggerTrap() {
        val enemy = TestPlayer(FactionMat.TOGAWA)

        hex.moveUnitsInto(listOf(enemy.tokens!![1]))
        player.coins += 4
        val initialCoins = player.coins

        val character = TestUnit(player, UnitType.CHARACTER)
        hex.moveUnitsInto(listOf(character))

        assertEquals(initialCoins - 4, player.coins)
        assertNotNull(player.turn.findFirstOfType(TrapSprungAction::class.java))
    }

    @Test
    fun testResetTrap() {
        player = TestPlayer(FactionMat.TOGAWA)

        val trap = player.tokens!![1] as TrapUnit

        val enemy = TestPlayer()
        val enemyCharacter = TestUnit(enemy, UnitType.CHARACTER)

        trap.springTrap(enemyCharacter)

        hex.moveUnitsInto(listOf(trap))

        val character = TestUnit(player, UnitType.CHARACTER)
        hex.moveUnitsInto(listOf(character))

        assertFalse(trap.sprung)
        assertNotNull(player.turn.findFirstOfType(ResetTrapAction::class.java))
    }

    @Test
    fun testPlaceFlag() {
        player = TestPlayer(FactionMat.ALBION)

        val flag = player.tokens!![0]

        val character = TestUnit(player, UnitType.CHARACTER)
        hex.moveUnitsInto(listOf(character))

        assertTrue(hex.unitsPresent.contains(flag))
        assertNotNull(player.turn.findFirstOfType(DeployTokenTurnAction::class.java))
    }
}