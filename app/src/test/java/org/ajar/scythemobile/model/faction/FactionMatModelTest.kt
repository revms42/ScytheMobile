package org.ajar.scythemobile.model.faction

import org.ajar.scythemobile.model.StarType
import org.ajar.scythemobile.model.TestPlayer
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.map.*
import org.ajar.scythemobile.model.production.ResourceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FactionMatModelTest {

    @Test
    fun testHeroCharacter() {
        assertEquals(CharacterDescription.GUNTER, FactionMat.SAXONY.heroCharacter)
        assertEquals(CharacterDescription.AKIKO, FactionMat.TOGAWA.heroCharacter)
        assertEquals(CharacterDescription.CONNER, FactionMat.ALBION.heroCharacter)
        assertEquals(CharacterDescription.OLGA, FactionMat.RUSVIET.heroCharacter)
        assertEquals(CharacterDescription.ANNA, FactionMat.POLONIA.heroCharacter)
        assertEquals(CharacterDescription.ZERHA, FactionMat.CRIMEA.heroCharacter)
        assertEquals(CharacterDescription.BJORN, FactionMat.NORDIC.heroCharacter)
    }

    @Test
    fun testAbilitySaxony() {
        val mat = FactionMat.SAXONY

        assertEquals(DefaultFactionAbility.DOMINATE, mat.factionAbility)

        TestPlayer.player = TestPlayer(FactionMat.SAXONY)

        mat.addStar(StarType.COMBAT, TestPlayer.player)
        mat.addStar(StarType.COMBAT, TestPlayer.player)
        mat.addStar(StarType.COMBAT, TestPlayer.player)
        mat.addStar(StarType.COMBAT, TestPlayer.player)
        mat.addStar(StarType.COMBAT, TestPlayer.player)
        mat.addStar(StarType.COMBAT, TestPlayer.player)
        mat.addStar(StarType.COMBAT, TestPlayer.player)
        mat.addStar(StarType.COMBAT, TestPlayer.player)
        mat.addStar(StarType.COMBAT, TestPlayer.player)

        assertEquals(8, TestPlayer.player.stars[StarType.COMBAT])

        mat.addStar(StarType.POPULARITY, TestPlayer.player)
        mat.addStar(StarType.POPULARITY, TestPlayer.player)

        assertEquals(1, TestPlayer.player.stars[StarType.POPULARITY])

        mat.addStar(StarType.POWER, TestPlayer.player)
        mat.addStar(StarType.POWER, TestPlayer.player)

        assertEquals(1, TestPlayer.player.stars[StarType.POWER])

        mat.addStar(StarType.BUILD, TestPlayer.player)
        mat.addStar(StarType.BUILD, TestPlayer.player)

        assertEquals(1, TestPlayer.player.stars[StarType.BUILD])

        mat.addStar(StarType.DEPLOY, TestPlayer.player)
        mat.addStar(StarType.DEPLOY, TestPlayer.player)

        assertEquals(1, TestPlayer.player.stars[StarType.DEPLOY])

        mat.addStar(StarType.ENLIST, TestPlayer.player)
        mat.addStar(StarType.ENLIST, TestPlayer.player)

        assertEquals(1, TestPlayer.player.stars[StarType.ENLIST])

        mat.addStar(StarType.OBJECTIVE, TestPlayer.player)
        mat.addStar(StarType.OBJECTIVE, TestPlayer.player)

        assertEquals(1, TestPlayer.player.stars[StarType.OBJECTIVE])

        mat.addStar(StarType.UPGRADE, TestPlayer.player)
        mat.addStar(StarType.UPGRADE, TestPlayer.player)

        assertEquals(1, TestPlayer.player.stars[StarType.UPGRADE])

        mat.addStar(StarType.WORKERS, TestPlayer.player)
        mat.addStar(StarType.WORKERS, TestPlayer.player)

        assertEquals(1, TestPlayer.player.stars[StarType.WORKERS])
    }

    @Test
    fun testNordicAbility() {
        val movementRules = FactionMat.NORDIC.getFactionMovementRules()

        assertEquals(1, movementRules.size)
        assertTrue(movementRules[0]::class == Swim::class)
    }

    @Test
    fun testCrimeaAbility() {
        TestPlayer.player = TestPlayer(FactionMat.CRIMEA)

        TestPlayer.player.combatCards.add(CombatCardDeck.currentDeck.drawCard())
        val playerBaseDesc = MapHexDesc(1, HexNeigbors(), HomeBase(TestPlayer.player))
        val mapDesc = MapDesc(playerBaseDesc)

        val map = GameMap(mapDesc)
        GameMap.currentMap = map

        val target = TestPlayer.player.combatCards.size - 1

        assertTrue(TestPlayer.player.factionMat.collectPayment(listOf(ResourceType.METAL), TestPlayer.player))
        assertEquals(target, TestPlayer.player.combatCards.size)
    }
}