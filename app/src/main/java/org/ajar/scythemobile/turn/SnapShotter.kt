package org.ajar.scythemobile.turn

import org.ajar.scythemobile.data.*

object SnapShotter {

    private fun <A: Versioned> snapShotDatabase(type: Class<out A>) {
        SnapshotType.getAllFor(type)?.forEach { SnapshotDataDAO.snapshot(it) }
    }

    private fun <A: Versioned> checkForUpdated(type: Class<out A>) : List<A> {
        return SnapshotType.getAllFor(type)?.filter { SnapshotDataDAO.isUpdated(it) }?: emptyList()
    }

    private fun <A: Versioned> dumpDatabase(type: Class<out A>) : List<A> {
        return SnapshotType.getAllFor(type)?: emptyList()
    }

    private fun updateToSnapshot(values: List<Versioned>) {
        values.forEach { value ->
            if(!SnapshotDataDAO.isUpdated(value)) {
                SnapshotType.update(value)
            }
        }
    }

    private fun overwrite(values: List<Versioned>) {
        values.forEach { value ->
            SnapshotType.update(value)
        }
    }

    fun deleteSnapshot() {
        with(ScytheDatabase.snapshotDao()) {
            this?.getSnapshots()?.also { this.deleteSnapshot(*it.toTypedArray()) }
        }
    }

    fun createSnapshot() {
        deleteSnapshot()
        SnapshotType.values().forEach { snapShotDatabase(it.dataType) }
    }

    fun determineChanged(): List<Versioned> {
        return if(ScytheDatabase.snapshotDao()?.getSnapshots()?.isNotEmpty() == true) {
            SnapshotType.values().flatMap { checkForUpdated(it.dataType) }
        } else {
            emptyList()
        }
    }

    fun createSharableList(): List<Versioned> {
        return SnapshotType.values().flatMap { dumpDatabase(it.dataType) }
    }

    fun createDiff(): List<String> {
        return determineChanged().map { it.toStringCompressed() }
    }

    fun readDiff(diff: List<String>) {
        updateToSnapshot(diff.mapNotNull { Versioned.fromString(it) } )
    }

    fun createSharableDatabase(): List<String> {
        return createSharableDatabase().map { it }
    }

    fun importDatabase(db: List<String>) {
        db.mapNotNull { Versioned.fromString(it) }.also { overwrite(it) }
    }
}