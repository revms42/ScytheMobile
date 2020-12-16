package org.ajar.scythemobile.data

import org.ajar.scythemobile.model.map.GameMap
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class MapHexSerializationTest(private val mapHexData: MapHexData) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): List<MapHexData> {
            ScytheTestDatabase.setTestingDatabase()
            return GameMap.currentMap.mapHexes.map { it.data }
        }

        fun MapHexData.isHexIdentical(other: MapHexData) : Boolean {
            return listOf(
                    this.loc == other.loc,
                    this.tunnel == other.tunnel,
                    this.encounter == other.encounter,
                    this.faction == other.faction,
                    this.terrain == other.terrain,
                    this.version == other.version,
                    this.neighbors.let { neighbors ->
                        listOf(
                                neighbors.e == other.neighbors.e,
                                neighbors.ne == other.neighbors.ne,
                                neighbors.nw == other.neighbors.nw,
                                neighbors.se == other.neighbors.se,
                                neighbors.sw == other.neighbors.sw,
                                neighbors.w == other.neighbors.w
                        ).all { it }
                    },
                    this.rivers.let {rivers ->
                        listOf(
                                rivers.riverE == other.rivers.riverE,
                                rivers.riverSE == other.rivers.riverSE,
                                rivers.riverSW == other.rivers.riverSW,
                                rivers.riverW == other.rivers.riverW,
                                rivers.riverNW == other.rivers.riverNW,
                                rivers.riverNE == other.rivers.riverNE
                        ).all { it }
                    }
            ).all { it }
        }
    }

    @Test
    fun testSerializeMap() {
        val serialized = mapHexData.toString()

        var deserialized = MapHexData.fromString(serialized)!!

        assertTrue("MapHexData does not match: $mapHexData vs. $deserialized", mapHexData.isHexIdentical(deserialized))

        val short = mapHexData.toStringCompressed()

        deserialized = MapHexData.fromString(short)!!

        if(mapHexData.encounter != null) {
            assertTrue("Encounters do not match!", deserialized.encounter == mapHexData.encounter)
        }

        assertTrue("Shortened description does not deserialize to existing object", deserialized === mapHexData)
    }
}