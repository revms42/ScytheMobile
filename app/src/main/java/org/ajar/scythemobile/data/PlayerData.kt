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
        @ColumnInfo(name = COLUMN_FACTORY_CARD) var factoryCard: Int?,
        @ColumnInfo(name = Versioned.COLUMN_VERSION) override var version: Int = 0
) : Versioned {

    override fun toString(): String {
        return "P:$id,$name,$coins,$power,$popularity,$objectiveOne,$objectiveTwo,$factionMat,$playerMat,$starUpgrades," +
                "$starMechs,$starStructures,$starRecruits,$starWorkers,$starObjectives,$starCombat,$starPopularity,$starPower," +
                "$flagRetreat,$flagCoercion,$flagToka,$factoryCard,$version"
    }
    
    override fun toStringCompressed(): String {
        return toString()
    }

    companion object {
        init {
            Versioned.addVersionedDeserializer(::fromString)
        }

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

        fun fromString(str: String): PlayerData? {
            return if(str.startsWith("P:")) {
                val parts = str.subSequence(2 until str.length).split(",")

                if(parts.size == 23) {
                    PlayerData(parts[0].toInt(), parts[1], parts[2].toInt(), parts[3].toInt(), parts[4].toInt(), parts[5].toInt(),
                            parts[6].toInt(), FactionMatData.fromString(parts[7]), PlayerMatData.fromString(parts[8]), parts[9].toInt(), parts[10].toInt(), parts[11].toInt(),
                            parts[12].toInt(), parts[13].toInt(), parts[14].toInt(), parts[15].toInt(), parts[16].toInt(),
                            parts[17].toInt(), parts[18].toBoolean(), parts[19].toBoolean(), parts[20].toBoolean(), parts[21].let { if(it == "null") null else it.toInt() },
                            parts[22].toInt())
                } else {
                    null
                }
            } else null
        }
    }
}

data class FactionMatData(
        @ColumnInfo(name = COLUMN_MAT_ID) var matId: Int,
        @ColumnInfo(name = COLUMN_ENLIST_POWER) var enlistPower: Boolean = false,
        @ColumnInfo(name = COLUMN_ENLIST_COINS) var enlistCoins: Boolean = false,
        @ColumnInfo(name = COLUMN_ENLIST_POP) var enlistPop: Boolean = false,
        @ColumnInfo(name = COLUMN_ENLIST_CARDS) var enlistCards: Boolean = false,
        @ColumnInfo(name = COLUMN_UPGRADE_ONE) var upgradeOne: Boolean = false,
        @ColumnInfo(name = COLUMN_UPGRADE_TWO) var upgradeTwo: Boolean = false,
        @ColumnInfo(name = COLUMN_UPGRADE_THREE) var upgradeThree: Boolean = false,
        @ColumnInfo(name = COLUMN_UPGRADE_FOUR) var upgradeFour: Boolean = false
) {

    override fun toString(): String {
        return "$matId:$enlistPower:$enlistCoins:$enlistPop:$enlistCards:$enlistCards:$upgradeOne:$upgradeTwo:$upgradeThree:$upgradeFour"
    }

    companion object {
        const val COLUMN_MAT_ID = "faction_mat_id"
        const val COLUMN_ENLIST_POWER = "enlist_pow"
        const val COLUMN_ENLIST_COINS = "enlist_coins"
        const val COLUMN_ENLIST_POP = "enlist_pop"
        const val COLUMN_ENLIST_CARDS = "enlist_cards"
        const val COLUMN_UPGRADE_ONE = "upgrade_one"
        const val COLUMN_UPGRADE_TWO = "upgrade_two"
        const val COLUMN_UPGRADE_THREE = "upgrade_three"
        const val COLUMN_UPGRADE_FOUR = "upgrade_four"

        fun fromString(str: String) : FactionMatData {
            val parts = str.split(":")
            return FactionMatData(
                    parts[0].toInt(),
                    parts[1].toBoolean(),
                    parts[2].toBoolean(),
                    parts[3].toBoolean(),
                    parts[4].toBoolean(),
                    parts[5].toBoolean(),
                    parts[6].toBoolean(),
                    parts[7].toBoolean(),
                    parts[8].toBoolean()
            )
        }
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

    override fun toString(): String {
        return "$matId:$lastSection:" +
                "${tradeSection.resourceGain}&${tradeSection.popularityGain}:" +
                "${produceSection.territories}:" +
                "${bolsterSection.powerGain}&${bolsterSection.cardsGain}:" +
                "${moveGainSection.coinsGained}&${moveGainSection.unitsMoved}:" +
                "${upgradeSection.oilCost}&${upgradeSection.enlisted}:" +
                "${deploySection.metalCost}&${deploySection.enlisted}:" +
                "${buildSection.woodCost}&${buildSection.enlisted}:" +
                "${enlistSection.foodCost}&${enlistSection.enlisted}"
    }

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

        fun fromString(str: String) : PlayerMatData {
            val parts = str.split(":")

            return PlayerMatData(
                    parts[0].toInt(),
                    parts[1].toInt(),
                    parts[2].split("&").let { TradeSectionData(it[0].toInt(), it[1].toInt()) },
                    ProduceSectionData(parts[3].toInt()),
                    parts[4].split("&").let { BolsterSectionData(it[0].toInt(), it[1].toInt()) },
                    parts[5].split("&").let { MoveGainSectionData(it[0].toInt(), it[1].toInt()) },
                    parts[6].split("&").let { UpgradeSectionData(it[0].toInt(), it[1].toBoolean()) },
                    parts[7].split("&").let { DeploySectionData(it[0].toInt(), it[1].toBoolean()) },
                    parts[8].split("&").let { BuildSectionData(it[0].toInt(), it[1].toBoolean()) },
                    parts[9].split("&").let { EnlistSectionData(it[0].toInt(), it[1].toBoolean()) }
            )
        }
    }
}

data class TradeSectionData(var resourceGain: Int, var popularityGain: Int)
data class ProduceSectionData(var territories: Int)
data class BolsterSectionData(var powerGain: Int, var cardsGain: Int)
data class MoveGainSectionData(var coinsGained: Int, var unitsMoved: Int)

data class UpgradeSectionData(var oilCost: Int, var enlisted: Boolean)
data class DeploySectionData(var metalCost: Int, var enlisted: Boolean)
data class BuildSectionData(var woodCost: Int, var enlisted: Boolean)
data class EnlistSectionData(var foodCost: Int, var enlisted: Boolean)