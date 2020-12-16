package org.ajar.scythemobile.data

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ResourceDataSerializationTest(private val resourceData: ResourceData) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): List<ResourceData> {
            ScytheTestDatabase.setTestingDatabase()
            return ScytheDatabase.resourceDao()!!.getResources()!!
        }

        fun ResourceData.isResourceIdentical(other: ResourceData) : Boolean {
            return listOf(
                    this.id == other.id,
                    this.loc == other.loc,
                    this.type == other.type,
                    this.value == other.value,
                    this.owner == other.owner,
                    this.version == other.version
            ).all { it }
        }
    }

    @Test
    fun testSerializeResource() {
        val serialized = resourceData.toString()

        var deserialized = ResourceData.fromString(serialized)!!

        assertTrue("ResourceData does not match: $resourceData vs. $deserialized", resourceData.isResourceIdentical(deserialized))

        val short = resourceData.toStringCompressed()

        deserialized = ResourceData.fromString(short)!!

        assertTrue("Shortened description does not deserialize to existing object", deserialized === resourceData)
    }
}