package org.ajar.scythemobile.data

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import org.mockito.Mockito

class ScytheTestDatabase : ScytheDatabase() {
    private val playerDataDAO: PlayerDataDAO by lazy { PlayerTestDataDAO() }
    private val unitDataDAO: UnitDataDAO by lazy { UnitTestDataDAO() }
    private val resourceDataDAO: ResourceDataDAO by lazy { ResourceTestDataDAO() }
    private val mapHexDAO: MapHexDAO by lazy { MapHexTestDAO() }
    private val turnDataDAO: TurnDataDAO by lazy { TurnTestDataDAO() }

    override fun playerDao(): PlayerDataDAO = playerDataDAO

    override fun unitDao(): UnitDataDAO = unitDataDAO

    override fun resourceDao(): ResourceDataDAO = resourceDataDAO

    override fun mapDao(): MapHexDAO = mapHexDAO

    override fun turnDao(): TurnDataDAO = turnDataDAO

    // Room Database
    override fun createOpenHelper(config: DatabaseConfiguration?): SupportSQLiteOpenHelper { TODO("not implemented") }
    override fun createInvalidationTracker(): InvalidationTracker {
        return Mockito.mock(InvalidationTracker::class.java)
    }
    override fun clearAllTables() { TODO("not implemented") }

    companion object {
        fun setTestingDatabase() {
            setDatabaseForTesting(ScytheTestDatabase())
        }
    }
}