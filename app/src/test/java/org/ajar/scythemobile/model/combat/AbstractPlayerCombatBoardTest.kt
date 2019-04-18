package org.ajar.scythemobile.model.combat

import org.ajar.scythemobile.model.TestPlayer
import org.ajar.scythemobile.model.TestPlayerCombatBoard
import org.ajar.scythemobile.model.TestUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AbstractPlayerCombatBoardTest {

    @Before
    fun setupPlayer() {
        TestPlayer.player.power = 0
        TestPlayer.player.combatCards.clear()
    }

    @Test
    fun testPowerSelectionBounds() {
        TestPlayer.player.power = 16

        val playerBoard = TestPlayerCombatBoard(TestPlayer.player, ArrayList())
        assertEquals(16, playerBoard.power)

        playerBoard.powerSelected = 16

        assertEquals(7, playerBoard.powerSelected)

        playerBoard.powerSelected = -1

        assertEquals(0, playerBoard.powerSelected)
    }

    @Test
    fun testCardLimitBounds() {
        val mech = TestUnit(TestPlayer.player, UnitType.MECH)
        val character = TestUnit(TestPlayer.player, UnitType.CHARACTER)
        val worker = TestUnit(TestPlayer.player, UnitType.WORKER)
        val trap = TestUnit(TestPlayer.player, UnitType.TRAP)
        val flag = TestUnit(TestPlayer.player, UnitType.FLAG)

        var playerBoard = TestPlayerCombatBoard(TestPlayer.player, ArrayList())
        assertEquals(0, playerBoard.cardLimit)

        playerBoard = TestPlayerCombatBoard(TestPlayer.player, mutableListOf(flag))
        assertEquals(0, playerBoard.cardLimit)

        playerBoard = TestPlayerCombatBoard(TestPlayer.player, mutableListOf(trap))
        assertEquals(0, playerBoard.cardLimit)

        playerBoard = TestPlayerCombatBoard(TestPlayer.player, mutableListOf(worker))
        assertEquals(0, playerBoard.cardLimit)

        playerBoard = TestPlayerCombatBoard(TestPlayer.player, mutableListOf(flag, trap, worker))
        assertEquals(0, playerBoard.cardLimit)

        playerBoard = TestPlayerCombatBoard(TestPlayer.player, mutableListOf(mech))
        assertEquals(1, playerBoard.cardLimit)

        playerBoard = TestPlayerCombatBoard(TestPlayer.player, mutableListOf(character))
        assertEquals(1, playerBoard.cardLimit)

        playerBoard = TestPlayerCombatBoard(TestPlayer.player, mutableListOf(character, mech))
        assertEquals(2, playerBoard.cardLimit)

        playerBoard = TestPlayerCombatBoard(TestPlayer.player, mutableListOf(character, mech, worker))
        assertEquals(2, playerBoard.cardLimit)
    }

    @Test
    fun testFinalPower() {
        val mech = TestUnit(TestPlayer.player, UnitType.MECH)
        val mech2 = TestUnit(TestPlayer.player, UnitType.MECH)

        TestPlayer.player.power = 2

        var playerBoard = TestPlayerCombatBoard(TestPlayer.player, ArrayList())
        assertEquals(0, playerBoard.finalPower())

        assertEquals(0, playerBoard.cardsAvailable.size)

        playerBoard.powerSelected = 2
        assertEquals(2, playerBoard.finalPower())

        playerBoard.powerSelected = 7
        assertEquals(2, playerBoard.finalPower())

        TestPlayer.player.combatCards.add(CombatCardDeck.currentDeck.drawCard())
        playerBoard = TestPlayerCombatBoard(TestPlayer.player, listOf(mech))
        val card1 = TestPlayer.player.combatCards[0]

        assertTrue(playerBoard.cardsAvailable.contains(card1))
        assertEquals(1, playerBoard.cardsAvailable.size)

        playerBoard.selectCard(card1)

        playerBoard.powerSelected = 0
        assertEquals(card1.power, playerBoard.finalPower())

        playerBoard.powerSelected = 1
        assertEquals(card1.power + 1, playerBoard.finalPower())

        playerBoard.powerSelected = 7
        assertEquals(card1.power + 2, playerBoard.finalPower())

        TestPlayer.player.combatCards.add(CombatCardDeck.currentDeck.drawCard())
        playerBoard = TestPlayerCombatBoard(TestPlayer.player, listOf(mech, mech2))
        val card2 = TestPlayer.player.combatCards[1]

        assertTrue(playerBoard.cardsAvailable.contains(card1))
        assertTrue(playerBoard.cardsAvailable.contains(card2))
        assertEquals(2, playerBoard.cardsAvailable.size)

        playerBoard.selectCard(card1)
        playerBoard.selectCard(card2)

        playerBoard.powerSelected = 0
        assertEquals(card1.power + card2.power, playerBoard.finalPower())

        playerBoard.powerSelected = 1
        assertEquals(card1.power + card2.power + 1, playerBoard.finalPower())

        playerBoard.powerSelected = 7
        assertEquals(card1.power + card2.power + 2, playerBoard.finalPower())

        playerBoard.deselectCard(card1)

        playerBoard.powerSelected = 0
        assertEquals(card2.power, playerBoard.finalPower())

        playerBoard.powerSelected = 1
        assertEquals(card2.power + 1, playerBoard.finalPower())

        playerBoard.powerSelected = 7
        assertEquals(card2.power + 2, playerBoard.finalPower())

        playerBoard = TestPlayerCombatBoard(TestPlayer.player, listOf(mech))

        playerBoard.selectCard(card1)
        playerBoard.selectCard(card2)

        playerBoard.powerSelected = 0
        assertEquals(card1.power, playerBoard.finalPower())

        playerBoard.powerSelected = 1
        assertEquals(card1.power + 1, playerBoard.finalPower())

        playerBoard.powerSelected = 7
        assertEquals(card1.power + 2, playerBoard.finalPower())

        val foreignCard = CombatCardDeck.currentDeck.drawCard()

        playerBoard.deselectCard(card1)
        playerBoard.selectCard(foreignCard)

        playerBoard.powerSelected = 0
        assertEquals(0, playerBoard.finalPower())

        playerBoard.powerSelected = 1
        assertEquals(1, playerBoard.finalPower())

        playerBoard.powerSelected = 7
        assertEquals(2, playerBoard.finalPower())
    }

    @Test
    fun testConcludeCombatPowerReduction() {
        TestPlayer.player.power = 2

        val playerBoard = TestPlayerCombatBoard(TestPlayer.player, ArrayList())
        playerBoard.powerSelected = 0

        playerBoard.concludeCombat(true, 0)
        assertEquals(2, TestPlayer.player.power)

        playerBoard.powerSelected = 2
        playerBoard.concludeCombat(true, 0)
        assertEquals(0, TestPlayer.player.power)
    }

    @Test
    fun testConcludeCombatCardsRemoved() {
        val mech = TestUnit(TestPlayer.player, UnitType.MECH)
        val mech2 = TestUnit(TestPlayer.player, UnitType.MECH)

        TestPlayer.player.combatCards.add(CombatCardDeck.currentDeck.drawCard())
        TestPlayer.player.combatCards.add(CombatCardDeck.currentDeck.drawCard())

        val card1 = TestPlayer.player.combatCards[0]
        val card2 = TestPlayer.player.combatCards[1]

        val playerBoard = TestPlayerCombatBoard(TestPlayer.player, listOf(mech, mech2))

        playerBoard.concludeCombat(true, 0)
        assertEquals(2, TestPlayer.player.combatCards.size)

        playerBoard.selectCard(card1)
        playerBoard.selectCard(card2)

        playerBoard.concludeCombat(true, 0)
        assertEquals(0, TestPlayer.player.combatCards.size)
    }

    @Test
    fun testConcludeCombatLossCardGained() {
        TestPlayer.player.power = 1

        TestPlayer.player.combatCards.clear()
        assertEquals(0, TestPlayer.player.combatCards.size)

        val playerBoard = TestPlayerCombatBoard(TestPlayer.player, ArrayList())
        playerBoard.powerSelected = 1

        playerBoard.concludeCombat(false, 0)

        assertEquals(0, TestPlayer.player.power)
        assertEquals(1, TestPlayer.player.combatCards.size)
    }

    @Test
    fun testConcludeCombatPopularityLoss() {
        TestPlayer.player.popularity = 2

        val playerBoard = TestPlayerCombatBoard(TestPlayer.player, ArrayList())

        playerBoard.concludeCombat(true, 0)
        assertEquals(2, TestPlayer.player.popularity)

        playerBoard.concludeCombat(false, 2)
        assertEquals(2, TestPlayer.player.popularity)

        playerBoard.concludeCombat(true, 2)
        assertEquals(0, TestPlayer.player.popularity)
    }

    @Test
    fun testConcludeCombatCamaraderie() {
        TestPlayer.player.popularity = 2

        val playerBoard = TestPlayerCombatBoard(TestPlayer.player, ArrayList())

        playerBoard.camaraderie = true

        playerBoard.concludeCombat(true, 2)
        assertEquals(2, TestPlayer.player.popularity)
    }

    @Test
    fun testRetreatUnits() {
        val mech = TestUnit(TestPlayer.player, UnitType.MECH)
        val worker = TestUnit(TestPlayer.player, UnitType.WORKER)

        val homeBaseDesc = MapHexDesc(1, HexNeigbors(), HomeBase(TestPlayer.player))
        val combatHexDesc = MapHexDesc(2, HexNeigbors())
        val mapDesc = MapDesc(homeBaseDesc, combatHexDesc)
        val map = GameMap(mapDesc)

        val combatHex = map.findHexAtIndex(combatHexDesc.location)!!
        val homeBase = map.findHexAtIndex(homeBaseDesc.location)!!

        combatHex.unitsPresent.addAll(listOf(mech, worker))

        val playerBoard = TestPlayerCombatBoard(TestPlayer.player, combatHex.unitsPresent)
        assertEquals(2, combatHex.unitsPresent.size)
        playerBoard.retreatUnits(combatHex)

        assertEquals(0, combatHex.unitsPresent.size)
        assertEquals(2, homeBase.unitsPresent.size)
        assertTrue(homeBase.unitsPresent.containsAll(listOf(mech, worker)))
    }
}