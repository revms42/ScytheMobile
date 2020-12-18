package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.model.NaturalResourceType
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.turn.TurnHolder
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SpendResourceActionTest {

    private lateinit var resourceData: ResourceData

    @Before
    fun setup() {
        resourceData = ResourceData(1, 1, 1, NaturalResourceType.FOOD.id, 1)
    }

    @After
    fun cleanUp() {
        TurnHolder.commitChanges()
        ScytheDatabase.resourceDao()?.removeResource(resourceData)
    }

    @Test
    fun testSpendResourceAction() {
        assertTrue(ScytheAction.SpendResourceAction(resourceData).perform())
        assertEquals(-1, resourceData.owner)
        assertEquals(-1, resourceData.loc)
        assertEquals(NaturalResourceType.FOOD.id, resourceData.type)
        assertEquals(1, resourceData.value)
        assertTrue(TurnHolder.isUpdateQueued(resourceData))
    }

    @Test
    fun testSpendResourceActionNoLoc() {
        resourceData.loc = -1
        assertTrue(ScytheAction.SpendResourceAction(resourceData).perform())
        assertEquals(-1, resourceData.owner)
        assertEquals(-1, resourceData.loc)
        assertEquals(NaturalResourceType.FOOD.id, resourceData.type)
        assertEquals(1, resourceData.value)
        assertTrue(TurnHolder.isUpdateQueued(resourceData))
    }

    @Test
    fun testSpendResourceActionNoOwner() {
        resourceData.owner = -1
        assertTrue(ScytheAction.SpendResourceAction(resourceData).perform())
        assertEquals(-1, resourceData.owner)
        assertEquals(-1, resourceData.loc)
        assertEquals(NaturalResourceType.FOOD.id, resourceData.type)
        assertEquals(1, resourceData.value)
        assertTrue(TurnHolder.isUpdateQueued(resourceData))
    }

    @Test
    fun testSpendResourceActionAlreadySpent() {
        resourceData.loc = -1
        resourceData.owner = -1
        assertFalse(ScytheAction.SpendResourceAction(resourceData).perform())
        assertEquals(-1, resourceData.owner)
        assertEquals(-1, resourceData.loc)
        assertEquals(NaturalResourceType.FOOD.id, resourceData.type)
        assertEquals(1, resourceData.value)
        assertFalse(TurnHolder.isUpdateQueued(resourceData))
    }
}