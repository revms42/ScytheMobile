package org.ajar.scythemobile.data

import android.content.Context
import androidx.room.*
import org.ajar.scythemobile.NaturalResourceType

private class Converters {
    @TypeConverter
    fun intListToString(value: List<Int>): String? {
        val builder = StringBuilder()
        value.forEach {
            if(builder.isNotEmpty()) {
                builder.append(",")
            }
            builder.append(it)
        }
        return builder.toString()
    }


    @TypeConverter
    fun stringToIntList(value: String): List<Int>? {
        return value.split(",").map { it.toInt() }
    }

    @TypeConverter
    fun stringListToString(value: List<String>): String? {
        val builder = StringBuilder()
        value.forEach {
            if(builder.isNotEmpty()) {
                builder.append(",")
            }
            builder.append(it)
        }
        return builder.toString()
    }

    @TypeConverter
    fun stringToStringList(value: String): List<String>? {
        return value.split(",")
    }
}

@Database(entities = [PlayerData::class, UnitData::class, ResourceData::class, MapHexData::class, TurnData::class], version = 1)
@TypeConverters(Converters::class)
abstract class ScytheDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDataDAO
    abstract fun unitDao(): UnitDataDAO
    abstract fun resourceDao(): ResourceDataDAO
    abstract fun mapDao(): MapHexDAO
    abstract fun turnDao(): TurnDataDAO

    companion object {
        const val DATABASE_NAME = "ScytheDatabase"

        private var database: ScytheDatabase? = null
        private val instance: ScytheDatabase?
            get() = database

        internal fun setDatabaseForTesting(database: ScytheDatabase) {
            ScytheDatabase.database = database
        }

        fun playerDao(): PlayerDataDAO? = instance?.playerDao()
        fun unitDao(): UnitDataDAO? = instance?.unitDao()
        fun resourceDao(): ResourceDataDAO? = instance?.resourceDao()
        fun mapDao(): MapHexDAO? = instance?.mapDao()
        fun turnDao(): TurnDataDAO? = instance?.turnDao()

        fun init(context: Context, name: String = DATABASE_NAME): Boolean {
            if(database == null) {
                //TODO: This should take considerably more thought as this builder has a lot of options. Main Thread queries may need to come off.
                database = Room.databaseBuilder(context, ScytheDatabase::class.java, name).enableMultiInstanceInvalidation().allowMainThreadQueries().build()

                NaturalResourceType.values().forEach { type ->
                    resourceDao()?.addResource( *(1..20).map { ResourceData(0, -1, -1, type.id) }.toTypedArray() )
                }
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
                    this.getResources()?.map { resource ->
                        resource.loc = -1
                        resource.owner = -1
                        resource
                    }?.toTypedArray()?.also { this.updateResource(*it) }
                }
                with(db.mapDao()) {
                    this.getMap()?.also { this.removeMapHex(*it.toTypedArray()) }
                }
                with(db.turnDao()) {
                    this.getTurns()?.also { this.removeTurn(*it.toTypedArray()) }
                }
            }
        }
    }
}