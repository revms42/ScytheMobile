package org.ajar.scythemobile.old.model.map

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class DefaultMapDescTest(private val hex: MapHexDesc) {

    companion object {
        @JvmStatic
        val mapDesc = MapDesc()
        private val gameMap = GameMap(mapDesc)

        @JvmStatic
        @Parameterized.Parameters
        fun data() = mapDesc.mapHexDescs
    }

    private val neighbors = hex.hexNeighbors

    @Test
    fun testReciprocalNeighbors() {
        if(hex.mapFeature.any { mapFeature ->  mapFeature is HomeBase}) {
            if(neighbors.e != -1) assertEquals("${hex.location} is a base with a neighbor, ${neighbors.e}, to the E that lists ${hex.location} as a neighbor", -1, gameMap.findHexAtIndex(neighbors.e)!!.desc.hexNeighbors.w)
            if(neighbors.ne != -1) assertEquals("${hex.location} is a base with a neighbor, ${neighbors.ne}, to the NE that lists ${hex.location} as a neighbor",-1, gameMap.findHexAtIndex(neighbors.ne)!!.desc.hexNeighbors.sw)
            if(neighbors.nw != -1) assertEquals("${hex.location} is a base with a neighbor, ${neighbors.nw}, to the NW that lists ${hex.location} as a neighbor",-1, gameMap.findHexAtIndex(neighbors.nw)!!.desc.hexNeighbors.se)
            if(neighbors.w != -1) assertEquals("${hex.location} is a base with a neighbor, ${neighbors.w}, to the W that lists ${hex.location} as a neighbor",-1, gameMap.findHexAtIndex(neighbors.w)!!.desc.hexNeighbors.e)
            if(neighbors.sw != -1) assertEquals("${hex.location} is a base with a neighbor, ${neighbors.sw}, to the SW that lists ${hex.location} as a neighbor",-1, gameMap.findHexAtIndex(neighbors.sw)!!.desc.hexNeighbors.ne)
            if(neighbors.se != -1) assertEquals("${hex.location} is a base with a neighbor, ${neighbors.se}, to the SE that lists ${hex.location} as a neighbor",-1, gameMap.findHexAtIndex(neighbors.se)!!.desc.hexNeighbors.nw)
        } else {
            if(neighbors.e != -1) assertEquals("${hex.location} has a neighbor, ${neighbors.e}, to the E that does not have ${hex.location} as a neighbor", hex.location, gameMap.findHexAtIndex(neighbors.e)!!.desc.hexNeighbors.w)
            if(neighbors.ne != -1) assertEquals("${hex.location} has a neighbor, ${neighbors.ne}, to the NE that does not have ${hex.location} as a neighbor", hex.location, gameMap.findHexAtIndex(neighbors.ne)!!.desc.hexNeighbors.sw)
            if(neighbors.nw != -1) assertEquals("${hex.location} has a neighbor, ${neighbors.nw}, to the NW that does not have ${hex.location} as a neighbor", hex.location, gameMap.findHexAtIndex(neighbors.nw)!!.desc.hexNeighbors.se)
            if(neighbors.w != -1) assertEquals("${hex.location} has a neighbor, ${neighbors.w}, to the W that does not have ${hex.location} as a neighbor", hex.location, gameMap.findHexAtIndex(neighbors.w)!!.desc.hexNeighbors.e)
            if(neighbors.sw != -1) assertEquals("${hex.location} has a neighbor, ${neighbors.sw}, to the SW that does not have ${hex.location} as a neighbor", hex.location, gameMap.findHexAtIndex(neighbors.sw)!!.desc.hexNeighbors.ne)
            if(neighbors.se != -1) assertEquals("${hex.location} has a neighbor, ${neighbors.se}, to the SE that does not have ${hex.location} as a neighbor", hex.location, gameMap.findHexAtIndex(neighbors.se)!!.desc.hexNeighbors.nw)
        }
    }

    @Test
    fun testReciprocalRivers() {
        hex.mapFeature.filter { mapFeature -> mapFeature is RiverFeature }.map { it as RiverFeature }.forEach { riverFeature ->
            // Note: Due to things like home base hexes you can have a river that is on your edge but doesn't have a neighbor, i.e. the rusviet home base.
            when(riverFeature.direction){
                Direction.NW -> {
                    val target = gameMap.findHexAtIndex(hex.hexNeighbors.nw)

                    if(target != null) {
                        assertTrue(
                                "${hex.location} as a neighbor across a river NW, ${neighbors.nw}, that does not have river to the SE",
                                target.desc.mapFeature.filter { mapFeature -> mapFeature is RiverFeature }.map { it as RiverFeature}.any { it.direction == Direction.SE }
                        )
                    }
                }
                Direction.NE -> {
                    val target = gameMap.findHexAtIndex(hex.hexNeighbors.ne)

                    if(target != null) {
                        assertTrue(
                                "${hex.location} as a neighbor across a river NE, ${neighbors.ne}, that does not have river to the SW",
                                target.desc.mapFeature.filter { mapFeature -> mapFeature is RiverFeature }.map { it as RiverFeature}.any { it.direction == Direction.SW }
                        )
                    }
                }
                Direction.E -> {
                    val target = gameMap.findHexAtIndex(hex.hexNeighbors.e)

                    if(target != null) {
                        assertTrue(
                                "${hex.location} as a neighbor across a river E, ${neighbors.e}, that does not have river to the W",
                                target.desc.mapFeature.filter { mapFeature -> mapFeature is RiverFeature }.map { it as RiverFeature }.any { it.direction == Direction.W }
                        )
                    }
                }
                Direction.SE -> {
                    val target = gameMap.findHexAtIndex(hex.hexNeighbors.se)

                    if(target != null) {
                        assertTrue(
                                "${hex.location} as a neighbor across a river SE, ${neighbors.se}, that does not have river to the NW",
                                target.desc.mapFeature.filter { mapFeature -> mapFeature is RiverFeature }.map { it as RiverFeature }.any { it.direction == Direction.NW }
                        )
                    }
                }
                Direction.SW -> {
                    val target = gameMap.findHexAtIndex(hex.hexNeighbors.sw)

                    if(target != null) {
                        assertTrue(
                                "${hex.location} as a neighbor across a river SW, ${neighbors.sw}, that does not have river to the NE",
                                target.desc.mapFeature.filter { mapFeature -> mapFeature is RiverFeature }.map { it as RiverFeature }.any { it.direction == Direction.NE }
                        )
                    }
                }
                Direction.W -> {
                    val target = gameMap.findHexAtIndex(hex.hexNeighbors.w)

                    if(target != null) {
                        assertTrue(
                                "${hex.location} as a neighbor across a river W, ${neighbors.w}, that does not have river to the E",
                                target.desc.mapFeature.filter { mapFeature -> mapFeature is RiverFeature }.map { it as RiverFeature }.any { it.direction == Direction.E }
                        )
                    }
                }
            }
        }
    }

    @Test
    fun testNoLakeRivers() {
        if(hex.mapFeature.any { it == SpecialFeature.LAKE }) assertTrue("${hex.location} is a lake that has a river feature", hex.mapFeature.none { it is RiverFeature })
    }

    @Test
    fun testOneSpecialFeature() {
        assertTrue("${hex.location} has more than one special feature", hex.mapFeature.count { it is SpecialFeature } <= 1)
    }
}
