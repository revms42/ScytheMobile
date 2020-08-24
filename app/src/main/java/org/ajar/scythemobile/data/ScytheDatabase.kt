package org.ajar.scythemobile.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlayerData::class, UnitData::class, ResourceData::class], version = 1)
abstract class ScytheDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDataDAO
    abstract fun unitDao(): UnitDataDAO
    abstract fun resourceDao(): ResourceDataDAO

    companion object {
        const val DATABASE_NAME = "ScytheDatabase"

        private var database: ScytheDatabase? = null
        val instance: ScytheDatabase?
            get() = database

        internal fun setDatabaseForTesting(database: ScytheDatabase) {
            ScytheDatabase.database = database
        }

        fun init(context: Context, name: String = DATABASE_NAME): Boolean {
            if(database == null) {
                //TODO: This should take considerably more thought as this builder has a lot of options.
                database = Room.databaseBuilder(context, ScytheDatabase::class.java, name).enableMultiInstanceInvalidation().build()

                TODO("Init Resources")
            }
            return database != null
        }

        fun reset() {
            database?.also {db ->
                with(db.playerDao()) {
                    this.getPlayers()?.also { this.removePlayer(*it.toTypedArray()) }
                }
                with(db.unitDao()) {
                    this.getUnits()?.also { this.removeUnit(*it.toTypedArray()) }
                }
                with(db.resourceDao()) {
                    this.getResources()?.map {
                        resource -> resource.pos = -1
                        resource
                    }?.toTypedArray()?.also { this.updateResource(*it) }
                }
            }
        }
    }
}