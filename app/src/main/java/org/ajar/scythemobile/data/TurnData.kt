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
        val attackingPlayer: Int,
        val defendingPlayer: Int,
        var attackingUnits: List<Int>,
        val defendingUnits: List<Int>,
        var attackerPower: Int? = null,
        var attackerCards: List<Int>? = null,
        var attackerAbilities: List<String>? = null,
        var defenderPower: Int? = null,
        var defenderCards: List<Int>? = null,
        var defenderAbilities: List<String>? = null,
        var combatResolved: Boolean = false
)