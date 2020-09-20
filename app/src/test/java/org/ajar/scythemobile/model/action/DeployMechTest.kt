package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.ScytheTestDatabase
import org.ajar.scythemobile.data.UnitData
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.StandardFactionMat
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.objective.ObjectiveCardDeck
import org.ajar.scythemobile.model.player.StandardPlayerMat
import org.ajar.scythemobile.turn.TurnHolder
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeployMechTest {
    private lateinit var playerInstance: PlayerInstance
    private val mechs: List<GameUnit> by lazy { playerInstance.selectUnits(UnitType.MECH)!! }

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
        ScytheDatabase.unitDao()?.removeUnit(*mechs.map { it.unitData }.toTypedArray())
        ObjectiveCardDeck.resetDeck()
    }

    @Test
    fun testDeployMechAction() {
        val mapHex = GameMap.currentMap.findHexAtIndex(8)!!
        val ability = playerInstance.factionMat.lockedFactionAbilities.first()
        assertTrue(ScytheAction.DeployMech(playerInstance, mapHex, ability).perform())
        val mech = playerInstance.selectUnits(UnitType.MECH)?.firstOrNull { it.pos == 8 }
        assertNotNull(mech)
        assertTrue(TurnHolder.isUpdateQueued(mech!!.unitData))
        assertTrue(TurnHolder.isUpdateQueued(playerInstance.playerData))
        assertTrue(playerInstance.factionMat.unlockedFactionAbilities.contains(ability))
    }

    @Test
    fun testDeployMechActionBadHex() {
        val mapHex = GameMap.currentMap.findHexAtIndex(1)!!
        val ability = playerInstance.factionMat.lockedFactionAbilities.first()
        assertFalse(ScytheAction.DeployMech(playerInstance, mapHex, ability).perform())
        val mech = playerInstance.selectUnits(UnitType.MECH)?.firstOrNull { it.pos == 1 }
        assertNull(mech)
        assertTrue(mechs.none { it.pos > 0 } )
        assertFalse(TurnHolder.isAnyUpdateQueued(UnitData::class.java))
        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
        assertFalse(playerInstance.factionMat.unlockedFactionAbilities.contains(ability))
    }

    @Test
    fun testDeployMechActionAbilityUnlocked() {
        val mapHex = GameMap.currentMap.findHexAtIndex(8)!!
        val ability = playerInstance.factionMat.lockedFactionAbilities.first()
        playerInstance.factionMat.unlockFactionAbility(ability)
        assertFalse(ScytheAction.DeployMech(playerInstance, mapHex, ability).perform())
        val mech = playerInstance.selectUnits(UnitType.MECH)?.firstOrNull { it.pos == 8 }
        assertNull(mech)
        assertFalse(TurnHolder.isAnyUpdateQueued(UnitData::class.java))
        assertTrue(mechs.none { it.pos > 0 } )
        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
    }

    @Test
    fun testDeployMechActionAlreadyDeployed() {
        val mapHex = GameMap.currentMap.findHexAtIndex(8)!!
        val ability = playerInstance.factionMat.lockedFactionAbilities.first()

        mechs.forEach { it.pos = 9 }

        assertFalse(ScytheAction.DeployMech(playerInstance, mapHex, ability).perform())
        val mech = playerInstance.selectUnits(UnitType.MECH)?.firstOrNull { it.pos == 8 }
        assertNull(mech)
        assertTrue(mechs.none { it.pos == 8 } )
        assertFalse(TurnHolder.isAnyUpdateQueued(UnitData::class.java))
        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
        assertFalse(playerInstance.factionMat.unlockedFactionAbilities.contains(ability))
    }

    @Test
    fun testDeployMechActionBadAbility() {
        val mapHex = GameMap.currentMap.findHexAtIndex(8)!!
        val ability = StandardFactionMat.ALBION.factionAbility
        assertFalse(ScytheAction.DeployMech(playerInstance, mapHex, ability).perform())
        val mech = playerInstance.selectUnits(UnitType.MECH)?.firstOrNull { it.pos == 8 }
        assertNull(mech)
        assertTrue(mechs.none { it.pos > 0 } )
        assertFalse(TurnHolder.isAnyUpdateQueued(UnitData::class.java))
        assertFalse(TurnHolder.isUpdateQueued(playerInstance.playerData))
        assertFalse(playerInstance.factionMat.unlockedFactionAbilities.contains(ability))
    }
}