package org.ajar.scythemobile.data

import androidx.room.*

@Dao
interface SnapshotDataDAO {

    @Query("SELECT * FROM ${SnapshotData.TABLE_NAME}")
    fun getSnapshots(): List<SnapshotData>?

    @Query("SELECT * FROM ${SnapshotData.TABLE_NAME} WHERE ${SnapshotData.COLUMN_TYPE} = :type AND ${SnapshotData.COLUMN_OBJECT_ID} = :objId LIMIT 1")
    fun findSnapshots(type: Int, objId: Int): SnapshotData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSnapshot(vararg snapshotData: SnapshotData)

    @Delete
    fun deleteSnapshot(vararg snapshotData: SnapshotData)

    companion object {
        fun <A: Versioned> snapshot(obj: A) {
            with(ScytheDatabase.snapshotDao()){
                SnapshotType.typeFor(obj).takeIf { it >= 0 }?.let {
                    val objId = SnapshotType.getIdFor(obj)
                    this?.findSnapshots(it, objId)?.let { snapshot -> snapshot.version = obj.version ; snapshot }?: SnapshotData(0, it, objId, obj.version)
                }?.let { data -> this?.insertSnapshot(data) }
            }
        }

        fun <A: Versioned> isUpdated(obj: A) : Boolean {
            return with(ScytheDatabase.snapshotDao()) {
                SnapshotType.typeFor(obj).takeIf { it >= 0 }?.let {
                    val objId = SnapshotType.getIdFor(obj)
                    this?.findSnapshots(it, objId)?.let { data -> data.version < obj.version }?: true
                }?: false
            }
        }
    }
}