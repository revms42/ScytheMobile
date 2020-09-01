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
        @Embedded(prefix = PREFIX_FACTION_MAT) var factionMat: FactionMatData,
        @Embedded(prefix = PREFIX_PLAYER_MAT) var playerMat: PlayerMatData,
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
        const val TABLE_NAME = "PlayerData"

        const val COLUMN_NAME = "name"
        const val COLUMN_INDEX = "ID"
        const val COLUMN_COINS = "coins"
        const val COLUMN_POWER = "power"
        const val COLUMN_POPULARITY = "popularity"

        const val COLUMN_OBJECTIVE_1 = "objective_1"
        const val COLUMN_OBJECTIVE_2 = "objective_2"

        const val PREFIX_FACTION_MAT = "fm_"
        const val PREFIX_PLAYER_MAT = "pm"

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
        @Embedded(prefix = PREFIX_TRADE) var tradeSection: TradeSectionData,
        @Embedded(prefix =  PREFIX_PRODUCE) var produceSection: ProduceSectionData,
        @Embedded(prefix =  PREFIX_BOLSTER) var bolsterSection: BolsterSectionData,
        @Embedded(prefix =  PREFIX_MOVE_GAIN) var moveGainSection: MoveGainSectionData,
        @Embedded(prefix =  PREFIX_UPGRADE) var upgradeSection: UpgradeSectionData,
        @Embedded(prefix =  PREFIX_DEPLOY) var deploySection: DeploySectionData,
        @Embedded(prefix =  PREFIX_BUILD) var buildSection: BuildSectionData,
        @Embedded(prefix =  PREFIX_ENLIST) var enlistSection: EnlistSectionData
) {

    companion object {
        const val COLUMN_MAT_ID = "player_mat_id"
        const val COLUMN_LAST_SECTION = "last_section"
        const val PREFIX_TRADE = "trade_section"
        const val PREFIX_PRODUCE = "produce_section"
        const val PREFIX_BOLSTER = "bolster_section"
        const val PREFIX_MOVE_GAIN = "move_gain_section"
        const val PREFIX_UPGRADE = "upgrade_section"
        const val PREFIX_DEPLOY = "deploy_section"
        const val PREFIX_BUILD = "build_section"
        const val PREFIX_ENLIST = "enlist_section"
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