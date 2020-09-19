package org.ajar.scythemobile.data

class ResourceTestDataDAO : ResourceDataDAO {
    private val resourceData = ArrayList<ResourceData>()

    override fun getResources(): List<ResourceData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getResource(id: Int): ResourceData? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getResourcesAt(loc: Int): List<ResourceData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getResourcesOfType(type: Int): List<ResourceData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOwnedResourcesOfType(owner: Int, type: List<Int>): List<ResourceData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUnclaimedResourcesOfType(type: Int): List<ResourceData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getResourcesAtPosOfType(loc: Int, type: Int): List<ResourceData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addResource(vararg resourceData: ResourceData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeResource(vararg setting: ResourceData) {
        setting.forEach { update -> this.resourceData.removeIf { it.id == update.id } }

    }

    override fun updateResource(vararg setting: ResourceData) {
        setting.forEach { update -> this.resourceData.removeIf { it.id == update.id }; this.resourceData.add(update) }
    }
}