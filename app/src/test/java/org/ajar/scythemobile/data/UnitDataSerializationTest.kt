package org.ajar.scythemobile.data

import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.faction.StandardFactionMat
import org.ajar.scythemobile.model.player.StandardPlayerMat
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class UnitDataSerializationTest(private val unitData: UnitData) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): List<UnitData> {
            ScytheTestDatabase.setTestingDatabase()

            var id = 0
            StandardFactionMat.values().forEach { mat ->
                PlayerInstance.makePlayer("Player ${id++}", StandardPlayerMat.INDUSTRIAL.id, mat.id)
            }

            return ScytheDatabase.unitDao()!!.getUnits()!!
        }

        fun UnitData.isUnitIdentical(other: UnitData) : Boolean {
            return listOf(
                    this.id == other.id,
                    this.owner == other.owner,
                    this.loc == other.loc,
                    this.type == other.type,
                    this.state == other.state,
                    this.subType == other.subType,
                    this.version == other.version
            ).all { it }
        }
    }

    @Test
    fun testSerializeUnitData() {
        val serialized = unitData.toString()

        var deserialized = UnitData.fromString(serialized)!!

        assertTrue("ResourceData does not match: $unitData vs. $deserialized", unitData.isUnitIdentical(deserialized))

        val short = unitData.toStringCompressed()

        deserialized = UnitData.fromString(short)!!

        assertTrue("Shortened description does not deserialize to existing object", deserialized === unitData)
    }
}