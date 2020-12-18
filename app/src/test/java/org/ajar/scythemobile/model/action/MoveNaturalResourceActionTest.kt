package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.model.NaturalResourceType
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.ScytheTestDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.faction.StandardFactionMat
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.objective.ObjectiveCardDeck
import org.ajar.scythemobile.model.player.StandardPlayerMat
import org.ajar.scythemobile.turn.TurnHolder
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MoveNaturalResourceActionTest {
    private lateinit var playerInstance: PlayerInstance
    private var resourceData: ResourceData? = null

    @Before
    fun setup() {
        ScytheTestDatabase.setTestingDatabase()
        GameMap.currentMap
        playerInstance = PlayerInstance.makePlayer("testPlayer", StandardPlayerMat.MECHANICAL.id, StandardFactionMat.CRIMEA.id)
        repeat(20) {
            resourceData = ResourceData(0, -1, -1, NaturalResourceType.WOOD.id, 1)
            ScytheDatabase.resourceDao()?.addResource(resourceData!!)
        }
        TurnHolder.commitChanges()
    }

    @After
    fun cleanUp() {
        TurnHolder.commitChanges()
        ScytheDatabase.playerDao()?.removePlayer(playerInstance.playerData)
        ObjectiveCardDeck.resetDeck()
    }

    @Test
    fun testMoveNaturalResourceAction() {
        resourceData!!.loc = 8

        TurnHolder.updateResource(resourceData!!)
        TurnHolder.commitChanges()

        assertTrue(ScytheAction.MoveNaturalResourceAction(resourceData!!, GameMap.currentMap.findHexAtIndex(9)!!).perform())

        assertEquals(1, ScytheDatabase.resourceDao()?.getResourcesAt(9)?.size?: 0)
        assertEquals(0, ScytheDatabase.resourceDao()?.getResourcesAt(8)?.size?: 0)
        assertTrue(TurnHolder.isUpdateQueued(resourceData!!))
    }

    @Test
    fun testMoveNaturalResourceActionToStartingHex() {
        resourceData!!.loc = 8

        TurnHolder.updateResource(resourceData!!)
        TurnHolder.commitChanges()

        assertFalse(ScytheAction.MoveNaturalResourceAction(resourceData!!, GameMap.currentMap.findHexAtIndex(1)!!).perform())

        assertEquals(0, ScytheDatabase.resourceDao()?.getResourcesAt(1)?.size?: 0)
        assertEquals(1, ScytheDatabase.resourceDao()?.getResourcesAt(8)?.size?: 0)
        assertFalse(TurnHolder.isUpdateQueued(resourceData!!))
    }
}