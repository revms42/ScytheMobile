package org.ajar.scythemobile.turn

import org.ajar.scythemobile.data.*
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.faction.StandardFactionMat
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.objective.ObjectiveCardDeck
import org.ajar.scythemobile.model.player.StandardPlayerMat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TurnUpdateTest {

    private lateinit var allData: List<Versioned>

    fun Versioned.updateTurn() {
        when(this) {
            is UnitData -> TurnHolder.updateMove(this)
            is PlayerData -> TurnHolder.updatePlayer(this)
            is MapHexData -> TurnHolder.updateEncounter(this)
            is ResourceData -> TurnHolder.updateResource(this)
        }
    }

    fun Versioned.index() : Int {
        return when(this) {
            is UnitData -> this.id
            is PlayerData -> this.id
            is MapHexData -> this.loc
            is ResourceData -> this.id
            else -> 0
        }
    }

    @Before
    fun setup() {
        ScytheTestDatabase.setTestingDatabase()
        allData = createData()
        TurnHolder.commitChanges()
    }

    private fun createData(): List<Versioned> {
        var int = 0
        return StandardPlayerMat.values().flatMap { playerMat ->
            StandardFactionMat.values().map { factionMat ->
                ObjectiveCardDeck.resetDeck()
                PlayerInstance.makePlayer("Player ${int++}", playerMat.id, factionMat.id).playerData
            }
        } + GameMap.currentMap.mapHexes.map { it.data } + ScytheDatabase.resourceDao()!!.getResources()!! + ScytheDatabase.unitDao()!!.getUnits()!!
    }

    private fun readData(): List<Versioned> {
        return ScytheDatabase.unitDao()?.getUnits()!! +
        ScytheDatabase.playerDao()?.getPlayers()!! +
        ScytheDatabase.resourceDao()?.getResources()!! +
        ScytheDatabase.unitDao()?.getUnits()!!
    }

    @Test
    fun testSnapshotData() {
        assertEquals("Database is not clear of snapshot!", 0, ScytheDatabase.snapshotDao()?.getSnapshots()?.size)
        SnapShotter.createSnapshot()

        assertEquals("Not every entry was snapshot!", allData.size, ScytheDatabase.snapshotDao()?.getSnapshots()?.size)
        var updateCount = 0
        allData.filter { it.index() % 2 == 1 }.forEach { it.updateTurn() ; updateCount++ }
        TurnHolder.commitChanges()

        val changed = SnapShotter.determineChanged()

        assertEquals("Changed list does not contain all the elements that it should!", updateCount, changed.size)
        changed.forEach { assertTrue("Snapshot for $it should not exist because it is not a odd id'd object!", it.index() % 2 == 1) }

        val diff = SnapShotter.createDiff()
        assertEquals("Diff list does not contain all the elements that it should!", updateCount, diff.size)

        ScytheDatabase.reset()
        val startData = readData()
        startData.forEach { assertEquals("Value $it did not reset!", 0, it.version) }

        SnapShotter.readDiff(diff)
        val updatedData = readData()
        updatedData.forEach {
            if(it.index() % 2 == 1) {
                assertTrue("Updated data for $it does not have updated version!", it.version > 0)
            } else {
                assertEquals("Updated data for $it is not version 0!", 0, it.version)
            }
        }
    }
}