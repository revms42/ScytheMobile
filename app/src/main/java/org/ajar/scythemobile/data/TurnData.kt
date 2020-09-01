package org.ajar.scythemobile.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TurnData.TABLE_NAME)
data class TurnData(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COLUMN_INDEX) var turn: Int,
        @ColumnInfo(name = COLUMN_PLAYER) val playerId: Int,
        @ColumnInfo(name = COLUMN_SELECTION) var selection: Int? = null,
        @ColumnInfo(name = COLUMN_PERFORMED_TOP) var performedTop: Boolean = false,
        @Embedded(prefix = PREFIX_COMBAT_ONE) var combatOne: CombatRecord? = null,
        @Embedded(prefix = PREFIX_COMBAT_TWO) var combatTwo: CombatRecord? = null,
        @Embedded(prefix = PREFIX_COMBAT_THREE) var combatThree: CombatRecord? = null,
        @ColumnInfo(name = COLUMN_PERFORMED_BOTTOM) var performedBottom: Boolean = false
) {
    companion object {
        const val TABLE_NAME = "Turn"
        const val COLUMN_INDEX = "id"
        const val COLUMN_PLAYER = "player_id"
        const val COLUMN_SELECTION = "selection"
        const val COLUMN_PERFORMED_TOP = "top_performed"
        const val PREFIX_COMBAT_ONE = "cbt_one_"
        const val PREFIX_COMBAT_TWO = "cbt_two_"
        const val PREFIX_COMBAT_THREE = "cbt_three_"
        const val COLUMN_PERFORMED_BOTTOM = "bottom_performed"
    }
}

data class CombatRecord(
        val hex: Int,
        val attackingUnits: List<Int>,
        val defendingUnits: List<Int>,
        val attackerPower: Int? = null,
        val attackerCards: List<Int>? = null,
        val defenderPower: Int? = null,
        val defenderCards: List<Int>? = null,
        val combatResolved: Boolean
)