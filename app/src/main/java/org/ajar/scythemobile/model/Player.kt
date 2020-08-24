package org.ajar.scythemobile.model

import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.data.FactionMatData
import org.ajar.scythemobile.data.PlayerData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.faction.CombatRule
import org.ajar.scythemobile.model.faction.FactionMatInstance
import org.ajar.scythemobile.model.faction.MovementRule
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.FactionMat
import org.ajar.scythemobile.model.player.*
import org.ajar.scythemobile.old.model.objective.Objective
import org.ajar.scythemobile.old.model.objective.ObjectiveCardDeck

class PlayerInstance private constructor(
        val playerData: PlayerData
) {

    val factionMat: FactionMatInstance by lazy {
        FactionMatInstance(playerData.factionMat)
    }

    val playerMat: PlayerMatInstance by lazy {
        PlayerMatInstance(this)
    }

    var popularity: Int
        get() = playerData.popularity
        set(value) { playerData.popularity = value }

    var power: Int
        get() = playerData.power
        set(value) { playerData.power = value }

    var playerId: Int = playerData.id

    val recruits: Int
        get() = playerMat.sections.count { section -> section.bottomRowAction.recruited }

    val upgrades: Int
        get() = playerMat.sections.sumBy { section -> section.bottomRowAction.upgrades + section.topRowAction.upgrades }

    val combatCards: List<CombatCard>?
        get() = ScytheDatabase.instance!!.resourceDao().getOwnedResourcesOfType(CapitalResourceType.CARDS.id, playerId)?.map { CombatCard(it) }

    val coins: List<Coin>?
        get() = ScytheDatabase.instance!!.resourceDao().getOwnedResourcesOfType(CapitalResourceType.COINS.id, playerId)?.map { Coin(it) }

    val stars: Int
        get() = playerData.starCombat +
                playerData.starMechs +
                playerData.starObjectives +
                playerData.starPopularity +
                playerData.starPower +
                playerData.starRecruits +
                playerData.starStructures +
                playerData.starUpgrades +
                playerData.starWorkers

    val objectives: List<Objective?>
        get() = listOf(Objective[playerData.objectiveOne], Objective[playerData.objectiveTwo])

    fun getMovementRules(unitType: UnitType) : Collection<MovementRule> = factionMat.getMovementAbilities(unitType)
    fun getCombatRules() : Collection<CombatRule> = factionMat.getCombatAbilities()

    fun addStar(starType: StarType) = factionMat.addStar(starType, playerData)

    fun selectableSections(): List<Section> {
        val selectable = factionMat.selectableSections(playerData)
        return playerMat.sections.filterIndexed { index, _ -> selectable.contains(index) }
    }

    fun selectUnits(unitType: UnitType) : List<GameUnit>? {
        return ScytheDatabase.instance?.unitDao()?.getUnitsForPlayer(playerData.id, unitType.ordinal)?.map {
            GameUnit.get(it, this)
        }
    }

    fun initialize() {
        factionMat.initializePlayer(this)
        TODO("Initialize Units")
    }

    companion object {
        fun makePlayer(playerName: String, playerMatId: Int, factionMatId: Int): PlayerInstance {
            val playerMat = PlayerMat[playerMatId]
            val factionMat = FactionMat[factionMatId]
            val playerData = PlayerData(0, playerName, playerMat!!.initialCoins, factionMat!!.initialPower, playerMat.initialPopularity,
                    ObjectiveCardDeck.currentDeck.drawCard().id,
                    ObjectiveCardDeck.currentDeck.drawCard().id,
                    FactionMatData(factionMat.id, upgradeOne = false, upgradeTwo = false, upgradeThree = false, upgradeFour = false),
                    playerMat.createData(),
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    false,
                    null
            )
            val playerInstance = PlayerInstance(playerData)
            playerInstance.initialize()

            ScytheDatabase.instance!!.playerDao().addPlayer(playerData)

            return playerInstance
        }

        fun loadPlayer(playerName: String): PlayerInstance {
            return PlayerInstance(ScytheDatabase.instance!!.playerDao().getPlayer(playerName)!!)
        }
    }
}