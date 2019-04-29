package org.ajar.scythemobile.model.faction

import org.ajar.scythemobile.model.StarType
import org.ajar.scythemobile.model.TestPlayer
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.map.*
import org.ajar.scythemobile.model.playermat.MoveOrGainAction
import org.ajar.scythemobile.model.production.MapResourceType
import org.ajar.scythemobile.model.turn.GainTurnAction
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FactionMatModelTest {
    
    private lateinit var player: TestPlayer
    
    @Before
    fun setup() {
        player = TestPlayer()
    }

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

        player = TestPlayer(FactionMat.SAXONY)

        player.addStar(StarType.COMBAT)
        player.addStar(StarType.COMBAT)
        player.addStar(StarType.COMBAT)
        player.addStar(StarType.COMBAT)
        player.addStar(StarType.COMBAT)
        player.addStar(StarType.COMBAT)
        player.addStar(StarType.COMBAT)
        player.addStar(StarType.COMBAT)
        player.addStar(StarType.COMBAT)

        assertEquals(8, player.stars[StarType.COMBAT])

        player.addStar(StarType.POPULARITY)
        player.addStar(StarType.POPULARITY)

        assertEquals(1, player.stars[StarType.POPULARITY])

        player.addStar(StarType.POWER)
        player.addStar(StarType.POWER)

        assertEquals(1, player.stars[StarType.POWER])

        player.addStar(StarType.BUILD)
        player.addStar(StarType.BUILD)

        assertEquals(1, player.stars[StarType.BUILD])

        player.addStar(StarType.DEPLOY)
        player.addStar(StarType.DEPLOY)

        assertEquals(1, player.stars[StarType.DEPLOY])

        player.addStar(StarType.ENLIST)
        player.addStar(StarType.ENLIST)

        assertEquals(1, player.stars[StarType.ENLIST])

        player.addStar(StarType.OBJECTIVE)
        player.addStar(StarType.OBJECTIVE)

        assertEquals(1, player.stars[StarType.OBJECTIVE])

        player.addStar(StarType.UPGRADE)
        player.addStar(StarType.UPGRADE)

        assertEquals(1, player.stars[StarType.UPGRADE])

        player.addStar(StarType.WORKERS)
        player.addStar(StarType.WORKERS)

        assertEquals(1, player.stars[StarType.WORKERS])
    }

    @Test
    fun testAbilityNordic() {
        val movementRules = FactionMat.NORDIC.getFactionMovementRules()

        assertEquals(1, movementRules.size)
        assertTrue(movementRules[0]::class == Swim::class)
    }

    @Test
    fun testAbilityCrimea() {
        player = TestPlayer(FactionMat.CRIMEA)

        player.combatCards.add(CombatCardDeck.currentDeck.drawCard())
        val playerBaseDesc = MapHexDesc(1, HexNeigbors(), HomeBase(player))
        val mapDesc = MapDesc(playerBaseDesc)

        val map = GameMap(mapDesc)
        GameMap.currentMap = map

        val target = player.combatCards.size - 1

        assertTrue(player.canPay(listOf(MapResourceType.METAL)))
        assertTrue(player.payResources(listOf(MapResourceType.METAL)))
        assertEquals(target, player.combatCards.size)
    }

    @Test
    fun testAbilityRusviet() {
        player = TestPlayer(FactionMat.RUSVIET)

        player.turn.performAction(GainTurnAction(1))
        player.newTurn()

        assertNotNull(player.selectableSections().firstOrNull { it.sectionDef.topRowAction is MoveOrGainAction })
    }
}