package org.ajar.scythemobile.data

import org.ajar.scythemobile.data.MapHexSerializationTest.Companion.isHexIdentical
import org.ajar.scythemobile.data.PlayerDataSerializationTest.Companion.isPlayerIdentical
import org.ajar.scythemobile.data.ResourceDataSerializationTest.Companion.isResourceIdentical
import org.ajar.scythemobile.data.UnitDataSerializationTest.Companion.isUnitIdentical
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.faction.StandardFactionMat
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.objective.ObjectiveCardDeck
import org.ajar.scythemobile.model.player.StandardPlayerMat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class VersionedTest(private val versioned: Versioned) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Versioned> {
            ScytheTestDatabase.setTestingDatabase()
            var int = 0
            return StandardPlayerMat.values().flatMap { playerMat ->
                StandardFactionMat.values().map { factionMat ->
                    ObjectiveCardDeck.resetDeck()
                    PlayerInstance.makePlayer("Player ${int++}", playerMat.id, factionMat.id).playerData
                }
            } + GameMap.currentMap.mapHexes.map { it.data } + ScytheDatabase.resourceDao()!!.getResources()!! + ScytheDatabase.unitDao()!!.getUnits()!!
        }

        fun Versioned.isIdentical(other: Versioned) : Boolean {
            return when(this) {
                is PlayerData -> this.isPlayerIdentical(other as PlayerData)
                is MapHexData -> this.isHexIdentical(other as MapHexData)
                is ResourceData -> this.isResourceIdentical(other as ResourceData)
                is UnitData -> this.isUnitIdentical(other as UnitData)
                else -> false
            }
        }
    }

    @Test
    fun testSerializeVersioned() {
        val serialized = versioned.toString()

        var deserialized = Versioned.fromString(serialized)!!

        Assert.assertTrue("Versioned does not match: $versioned vs. $deserialized", versioned.isIdentical(deserialized))

        val short = versioned.toStringCompressed()

        deserialized = Versioned.fromString(short)!!

        if(deserialized is PlayerData) {
            Assert.assertTrue("Shortened description does not match: $versioned vs. $deserialized", versioned.isIdentical(deserialized))
        } else {
            Assert.assertTrue("Shortened description does not deserialize to existing object", deserialized === versioned)
        }
    }
}