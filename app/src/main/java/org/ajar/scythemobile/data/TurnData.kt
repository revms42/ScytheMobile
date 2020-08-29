package org.ajar.scythemobile.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TurnData(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COLUMN_INDEX) var turn: Int,
        @ColumnInfo(name = COLUMN_PLAYER) val playerId: Int,
        @ColumnInfo(name = COLUMN_SELECTION) var selection: Int? = null,
        @ColumnInfo(name = COLUMN_PERFORMED_TOP) var performedTop: Boolean = false,
        @ColumnInfo(name = COLUMN_COMBAT_ONE) var combatOne: CombatRecord? = null,
        @ColumnInfo(name = COLUMN_COMBAT_TWO) var combatTwo: CombatRecord? = null,
        @ColumnInfo(name = COLUMN_COMBAT_THREE) var combatThree: CombatRecord? = null,
        @ColumnInfo(name = COLUMN_PERFORMED_BOTTOM) var performedBottom: Boolean = false
) {
    companion object {
        const val TABLE_NAME = "turn"
        const val COLUMN_INDEX = "id"
        const val COLUMN_PLAYER = "player_id"
        const val COLUMN_SELECTION = "selection"
        const val COLUMN_PERFORMED_TOP = "top_performed"
        const val COLUMN_COMBAT_ONE = "first_combat"
        const val COLUMN_COMBAT_TWO = "second_combat"
        const val COLUMN_COMBAT_THREE = "third_combat"
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