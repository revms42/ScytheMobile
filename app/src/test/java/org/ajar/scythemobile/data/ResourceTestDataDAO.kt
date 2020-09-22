package org.ajar.scythemobile.data

class ResourceTestDataDAO : ResourceDataDAO {
    private val resourceData = ArrayList<ResourceData>()

    override fun getResources(): List<ResourceData>? {
        return resourceData
    }

    override fun getResource(id: Int): ResourceData? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getResourcesAt(loc: Int): List<ResourceData>? {
        return this.resourceData.filter { it.loc == loc }
    }

    override fun getResourcesOfType(type: Int): List<ResourceData>? {
        return this.resourceData.filter {it.type == type}
    }

    override fun getOwnedResourcesOfType(owner: Int, type: List<Int>): List<ResourceData>? {
        return this.resourceData.filter { it.owner == owner && type.contains(it.type) }
    }

    override fun getUnclaimedResourcesOfType(type: Int): List<ResourceData>? {
        return this.resourceData.filter { it.owner == -1 && it.type == type }
    }

    override fun getResourcesAtPosOfType(loc: Int, type: Int): List<ResourceData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addResource(vararg resourceData: ResourceData) {
        resourceData.forEach { newResource -> if(newResource.id <= 0) newResource.id = this.resourceData.size; this.resourceData.add(newResource) }
    }

    override fun removeResource(vararg setting: ResourceData) {
        setting.forEach { update -> this.resourceData.removeIf { it.id == update.id } }

    }

    override fun updateResource(vararg setting: ResourceData) {
        setting.forEach { update -> this.resourceData.removeIf { it.id == update.id }; this.resourceData.add(update) }
    }
}