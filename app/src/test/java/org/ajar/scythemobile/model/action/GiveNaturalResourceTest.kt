package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.ScytheTestDatabase
import org.ajar.scythemobile.turn.TurnHolder
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GiveNaturalResourceTest {
    private var resourceData: ResourceData? = null

    @Before
    fun setup() {
        ScytheTestDatabase.setTestingDatabase()
        repeat(20) {
            resourceData = ResourceData(0, -1, -1, NaturalResourceType.WOOD.id, 1)
            ScytheDatabase.resourceDao()?.addResource(resourceData!!)
        }
    }

    @After
    fun cleanUp() {
        TurnHolder.commitChanges()
        resourceData?.also { ScytheDatabase.resourceDao()?.removeResource(it) }
    }

    @Test
    fun testGiveNaturalResourceAction() {
        assertTrue(ScytheAction.GiveNaturalResource(8, NaturalResourceType.WOOD, 1).perform())
        resourceData = ScytheDatabase.resourceDao()?.getResourcesAt(8)?.get(0)!!

        assertNotNull(resourceData)
        assertEquals(8, resourceData?.loc)
        assertEquals(NaturalResourceType.WOOD.id, resourceData?.type)
        assertEquals(-1, resourceData?.owner)
        assertEquals(1, resourceData?.value)
    }

    @Test
    fun testGiveNaturalResourceActionAboveLimit() {
        assertTrue(ScytheAction.GiveNaturalResource(8, NaturalResourceType.WOOD, 21).perform())
        val resourceData = ScytheDatabase.resourceDao()?.getResourcesAt(8)

        assertNotNull(resourceData)
        assertEquals(20, resourceData!!.size)
        resourceData.forEach {
            assertEquals(8, it.loc)
            assertEquals(NaturalResourceType.WOOD.id, it.type)
            assertEquals(-1, it.owner)
            assertEquals(1, it.value)
        }
    }

    @Test
    fun testGiveNaturalResourceActionBadLocation() {
        assertFalse(ScytheAction.GiveNaturalResource(0, NaturalResourceType.WOOD, 21).perform())
        assertTrue(ScytheDatabase.resourceDao()?.getResourcesAt(0).isNullOrEmpty())
    }

    @Test
    fun testGiveNaturalResourceActionIllegalLocation() {
        assertFalse(ScytheAction.GiveNaturalResource(1, NaturalResourceType.WOOD, 21).perform())
        assertTrue(ScytheDatabase.resourceDao()?.getResourcesAt(1).isNullOrEmpty())
    }
}