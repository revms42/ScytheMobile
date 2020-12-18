package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.model.CapitalResourceType
import org.ajar.scythemobile.data.PlayerData
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class GiveCapitalResourceActionTest(private val testAid: CapitalResourceAid) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
                CapitalResourceAid(false, 18, CapitalResourceType.POPULARITY, fun(playerInstance: PlayerInstance) : Int { return playerInstance.popularity} ),
                CapitalResourceAid(false, 16, CapitalResourceType.POWER, fun(playerInstance: PlayerInstance) : Int { return playerInstance.power} ),
                CapitalResourceAid(false, 495, CapitalResourceType.COINS, fun(playerInstance: PlayerInstance) : Int { return playerInstance.coins} ),
                CapitalResourceAid(true, 32, CapitalResourceType.CARDS, fun(playerInstance: PlayerInstance) : Int { return playerInstance.combatCards?.size?: 0} )
        )
    }

    class CapitalResourceAid(resource: Boolean, val upperLimit: Int, val resourceType: CapitalResourceType, val count: (PlayerInstance) -> Int) {
        private val updateClass = if(resource) ResourceData::class.java else PlayerData::class.java
        fun isUpdateQueued(): Boolean = TurnHolder.isAnyUpdateQueued(updateClass)
    }

    private lateinit var playerInstance: PlayerInstance

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
        ScytheDatabase.resourceDao()?.getResources()?.map { it.owner = -1 ; it.loc = -1; it }?.toTypedArray()?.also { ScytheDatabase.resourceDao()?.updateResource(*it) }
        ObjectiveCardDeck.resetDeck()
    }

    @Test
    fun testGiveCapitalResourceAction() {
        val preCount = testAid.count(playerInstance)
        assertTrue(ScytheAction.GiveCapitalResourceAction(playerInstance, testAid.resourceType, 1).perform())
        assertTrue(testAid.isUpdateQueued())

        TurnHolder.commitChanges()
        val postCount = testAid.count(playerInstance)

        assertEquals(preCount + 1, postCount)

    }

    @Test
    fun testGiveCapitalResourceActionLimit() {
        assertTrue(ScytheAction.GiveCapitalResourceAction(playerInstance, testAid.resourceType, testAid.upperLimit).perform())
        assertTrue(testAid.isUpdateQueued())

        TurnHolder.commitChanges()
        val postCount = testAid.count(playerInstance)

        assertEquals(testAid.upperLimit, postCount)

    }
}