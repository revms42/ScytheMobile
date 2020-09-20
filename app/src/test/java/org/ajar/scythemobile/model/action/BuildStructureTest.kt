package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.ScytheTestDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.StandardFactionMat
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.objective.ObjectiveCardDeck
import org.ajar.scythemobile.model.player.StandardPlayerMat
import org.ajar.scythemobile.turn.TurnHolder
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class BuildStructureTest(private val type: UnitType) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = UnitType.structures
    }

    private lateinit var playerInstance: PlayerInstance
    private val structure: GameUnit by lazy { playerInstance.selectUnits(type)!!.first() }

    @Before
    fun setup() {
        ScytheTestDatabase.setTestingDatabase()
        GameMap.currentMap
        playerInstance = PlayerInstance.makePlayer("testPlayer", StandardPlayerMat.MECHANICAL.id, StandardFactionMat.CRIMEA.id)
        TurnHolder.commitChanges()
    }

    @After
    fun cleanUp() {
        TurnHolder.commitChanges()
        ScytheDatabase.playerDao()?.removePlayer(playerInstance.playerData)
        ScytheDatabase.unitDao()?.removeUnit(structure.unitData)
        ObjectiveCardDeck.resetDeck()
    }

    @Test
    fun testBuildStructure() {
        assertTrue(ScytheAction.BuildStructure(GameMap.currentMap.findHexAtIndex(8)!!, structure).perform())
        assertTrue(TurnHolder.isUpdateQueued(structure.unitData))
    }

    @Test
    fun testBuildStructureAlreadyOut() {
        structure.unitData.loc = 9
        TurnHolder.updateMove(structure.unitData)
        TurnHolder.commitChanges()
        assertFalse(ScytheAction.BuildStructure(GameMap.currentMap.findHexAtIndex(8)!!, structure).perform())
        assertFalse(TurnHolder.isUpdateQueued(structure.unitData))
    }

    @Test
    fun testBuildStructureHomeBase() {
        assertFalse(ScytheAction.BuildStructure(GameMap.currentMap.findHexAtIndex(1)!!, structure).perform())
        assertFalse(TurnHolder.isUpdateQueued(structure.unitData))
    }

    @Test
    fun testBuildStructureLake() {
        assertFalse(ScytheAction.BuildStructure(GameMap.currentMap.findHexAtIndex(16)!!, structure).perform())
        assertFalse(TurnHolder.isUpdateQueued(structure.unitData))
    }

    @Test
    fun testBuildStructureFactory() {
        assertFalse(ScytheAction.BuildStructure(GameMap.currentMap.findHexAtIndex(30)!!, structure).perform())
        assertFalse(TurnHolder.isUpdateQueued(structure.unitData))
    }
}