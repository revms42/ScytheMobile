package org.ajar.scythemobile.data

class SnapshotTestDataDAO : SnapshotDataDAO {
    private val snapshotData = ArrayList<SnapshotData>()

    override fun getSnapshots(): List<SnapshotData>? {
        return snapshotData
    }

    override fun findSnapshots(type: Int, objId: Int): SnapshotData? {
        return snapshotData.firstOrNull { it.type == type && it.objId == objId }
    }

    override fun insertSnapshot(vararg snapshotData: SnapshotData) {
        snapshotData.forEach { data -> data.id = snapshotData.size ; this.snapshotData.add(data) }
    }

    override fun deleteSnapshot(vararg snapshotData: SnapshotData) {
        snapshotData.forEach { data -> this.snapshotData.removeIf { data.id == it.id } }
    }

}