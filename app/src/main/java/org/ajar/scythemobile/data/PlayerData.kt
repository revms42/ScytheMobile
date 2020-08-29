package org.ajar.scythemobile.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlayerData(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = COLUMN_INDEX) var id: Int,
        @ColumnInfo(name = COLUMN_NAME) var name: String,
        @ColumnInfo(name = COLUMN_COINS) var coins: Int,
        @ColumnInfo(name = COLUMN_POWER) var power: Int,
        @ColumnInfo(name = COLUMN_POPULARITY) var popularity: Int,
        @ColumnInfo(name = COLUMN_OBJECTIVE_1) var objectiveOne: Int,
        @ColumnInfo(name = COLUMN_OBJECTIVE_2) var objectiveTwo: Int,
        @ColumnInfo(name = COLUMN_FACTION_MAT) @Embedded var factionMat: FactionMatData,
        @ColumnInfo(name = COLUMN_PLAYER_MAT) @Embedded var playerMat: PlayerMatData,
        @ColumnInfo(name = COLUMN_STAR_UPGRADES) var starUpgrades: Int,
        @ColumnInfo(name = COLUMN_STAR_MECHS) var starMechs: Int,
        @ColumnInfo(name = COLUMN_STAR_STRUCTURES) var starStructures: Int,
        @ColumnInfo(name = COLUMN_STAR_RECRUITS) var starRecruits: Int,
        @ColumnInfo(name = COLUMN_STAR_WORKERS) var starWorkers: Int,
        @ColumnInfo(name = COLUMN_STAR_OBJECTIVES) var starObjectives: Int,
        @ColumnInfo(name = COLUMN_STAR_COMBAT) var starCombat: Int,
        @ColumnInfo(name = COLUMN_STAR_POPULARITY) var starPopularity: Int,
        @ColumnInfo(name = COLUMN_STAR_POWER) var starPower: Int,
        @ColumnInfo(name = COLUMN_FLAG_FORCED_RETREAT) var flagRetreat: Boolean,
        @ColumnInfo(name = COLUMN_FLAG_USED_COERCION) var flagCoercion: Boolean,
        @ColumnInfo(name = COLUMN_FLAG_USED_TOKA) var flagToka: Boolean,
        @ColumnInfo(name = COLUMN_FACTORY_CARD) var factoryCard: Int?
) {
    companion object {
        const val TABLE_NAME = "PlayerTable"

        const val COLUMN_NAME = "name"
        const val COLUMN_INDEX = "ID"
        const val COLUMN_COINS = "coins"
        const val COLUMN_POWER = "power"
        const val COLUMN_POPULARITY = "popularity"

        const val COLUMN_OBJECTIVE_1 = "objective_1"
        const val COLUMN_OBJECTIVE_2 = "objective_2"

        const val COLUMN_FACTION_MAT = "factionMat"
        const val COLUMN_PLAYER_MAT = "playerMat"

        const val COLUMN_STAR_UPGRADES = "star_upgrade"
        const val COLUMN_STAR_MECHS = "star_mech"
        const val COLUMN_STAR_STRUCTURES = "star_structure"
        const val COLUMN_STAR_RECRUITS = "star_recruit"
        const val COLUMN_STAR_WORKERS = "star_worker"
        const val COLUMN_STAR_OBJECTIVES = "star_objective"
        const val COLUMN_STAR_COMBAT = "star_combat"
        const val COLUMN_STAR_POPULARITY = "star_popularity"
        const val COLUMN_STAR_POWER = "star_power"

        const val COLUMN_FLAG_FORCED_RETREAT = "flag_retreat"
        const val COLUMN_FLAG_USED_COERCION = "flag_coercion"
        const val COLUMN_FLAG_USED_TOKA = "flag_toka"

        const val COLUMN_FACTORY_CARD = "factory_card"
    }
}

data class FactionMatData(
        @ColumnInfo(name = COLUMN_MAT_ID) var matId: Int,
        @ColumnInfo(name = COLUMN_UPGRADE_ONE) var upgradeOne: Boolean,
        @ColumnInfo(name = COLUMN_UPGRADE_TWO) var upgradeTwo: Boolean,
        @ColumnInfo(name = COLUMN_UPGRADE_THREE) var upgradeThree: Boolean,
        @ColumnInfo(name = COLUMN_UPGRADE_FOUR) var upgradeFour: Boolean
) {

    companion object {
        const val COLUMN_MAT_ID = "faction_mat_id"
        const val COLUMN_UPGRADE_ONE = "upgrade_one"
        const val COLUMN_UPGRADE_TWO = "upgrade_two"
        const val COLUMN_UPGRADE_THREE = "upgrade_three"
        const val COLUMN_UPGRADE_FOUR = "upgrade_four"
    }
}

data class PlayerMatData(
        @ColumnInfo(name = COLUMN_MAT_ID) var matId: Int,
        @ColumnInfo(name = COLUMN_LAST_SECTION) var lastSection: Int,
        @ColumnInfo(name = COLUMN_TRADE) @Embedded var tradeSection: TradeSectionData,
        @ColumnInfo(name = COLUMN_PRODUCE) @Embedded var produceSection: ProduceSectionData,
        @ColumnInfo(name = COLUMN_BOLSTER) @Embedded var bolsterSection: BolsterSectionData,
        @ColumnInfo(name = COLUMN_MOVE_GAIN) @Embedded var moveGainSection: MoveGainSectionData,
        @ColumnInfo(name = COLUMN_UPGRADE) @Embedded var upgradeSection: UpgradeSectionData,
        @ColumnInfo(name = COLUMN_DEPLOY) @Embedded var deploySection: DeploySectionData,
        @ColumnInfo(name = COLUMN_BUILD) @Embedded var buildSection: BuildSectionData,
        @ColumnInfo(name = COLUMN_ENLIST) @Embedded var enlistSection: EnlistSectionData
) {

    companion object {
        const val COLUMN_MAT_ID = "player_mat_id"
        const val COLUMN_LAST_SECTION = "last_section"
        const val COLUMN_TRADE = "trade_section"
        const val COLUMN_PRODUCE = "produce_section"
        const val COLUMN_BOLSTER = "bolster_section"
        const val COLUMN_MOVE_GAIN = "move_gain_section"
        const val COLUMN_UPGRADE = "upgrade_section"
        const val COLUMN_DEPLOY = "deploy_section"
        const val COLUMN_BUILD = "build_section"
        const val COLUMN_ENLIST = "enlist_section"
    }
}

data class TradeSectionData(var resourceGain: Int, var popularityGain: Int)
data class ProduceSectionData(var territories: Int)
data class BolsterSectionData(var powerGain: Int, var cardsGain: Int)
data class MoveGainSectionData(var coinsGained: Int, var unitsMoved: Int)

data class UpgradeSectionData(var oilCost: Int, val recruited: Boolean)
data class DeploySectionData(var metalCost: Int, val recruited: Boolean)
data class BuildSectionData(var woodCost: Int, val recruited: Boolean)
data class EnlistSectionData(var foodCost: Int, val recruited: Boolean)