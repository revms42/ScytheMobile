package org.ajar.scythemobile.model

import androidx.collection.SparseArrayCompat
import org.ajar.scythemobile.data.FactionMatData
import org.ajar.scythemobile.data.PlayerData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.UnitData
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.*
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.player.*
import org.ajar.scythemobile.model.objective.Objective
import org.ajar.scythemobile.model.objective.ObjectiveCardDeck
import org.ajar.scythemobile.turn.TurnHolder

class PlayerInstance private constructor(
        val playerData: PlayerData
) {

    val factionMat: FactionMatInstance by lazy {
        FactionMatInstance(playerData.factionMat)
    }

    val playerMat: PlayerMatInstance by lazy {
        PlayerMatInstance(this)
    }

    val resources: FactionResourcePack
        get() = factionMat.factionMat.resourcePack

    var popularity: Int
        get() = playerData.popularity
        set(value) {
            playerData.popularity = when {
                value > 18 -> 18
                value < 0 -> 0
                else -> value
            }
            TurnHolder.updatePlayer(playerData)
        }

    var power: Int
        get() = playerData.power
        set(value) {
            playerData.power = when {
                value > 16 -> 16
                value < 0 -> 0
                else -> value
            }
            TurnHolder.updatePlayer(playerData)
        }

    var coins: Int
        get() = playerData.coins
        set(value) {
            when {
                value > playerData.coins -> Bank.addCoins(playerData,value - playerData.coins)
                value < playerData.coins -> Bank.removeCoins(playerData, playerData.coins - value)
            }
            TurnHolder.updatePlayer(playerData)
        }

    val playerId: Int
        get() = playerData.id

    val recruits: Int
        get() = playerMat.sections.count { section -> section.bottomRowAction.enlisted }

    val upgrades: Int
        get() = playerMat.sections.sumBy { section -> section.bottomRowAction.upgrades + section.topRowAction.upgrades }

    val combatCards: List<CombatCard>?
        //TODO: This may cause issues if it's not in sync with the turn holder.
        get() = ScytheDatabase.resourceDao()?.getOwnedResourcesOfType(playerId, listOf(CapitalResourceType.CARDS.id))?.map { CombatCard(it) }

    fun takeCombatCards(number: Int, requireExactChange: Boolean = false): List<CombatCard>? {
        val cards = this.combatCards
        return if(!cards.isNullOrEmpty()) {
            if(requireExactChange) {
                if(cards.size >= number) {
                    val sublist = cards.subList(0, number)
                    TurnHolder.updateResource(*sublist.map { it.resourceData.owner = -1; it.resourceData }.toTypedArray())
                    sublist
                } else {
                    null
                }
            } else {
                val total = if(number > cards.size) cards.size else number
                val sublist = cards.subList(0, total)
                TurnHolder.updateResource(*sublist.map { it.resourceData.owner = -1; it.resourceData }.toTypedArray())
                sublist
            }
        }else {
            null
        }
    }

    fun giveCombatCards(vararg combatCard: CombatCard) {
        TurnHolder.updateResource(*combatCard.map { it.resourceData.owner = playerId; it.resourceData }.toTypedArray())
    }

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
        return ScytheDatabase.unitDao()?.getUnitsForPlayer(playerData.id, unitType.ordinal)?.map {
            GameUnit(it, this)
        }
    }

    private fun initializePlayer() {
        playerMat.initialize(this)
    }

    private fun initializeUnits() {
        ScytheDatabase.unitDao()!!.addUnit(UnitData(0, this.playerId, GameMap.currentMap.findHomeBase(this)!!.loc, UnitType.CHARACTER.ordinal))
        repeat(8) {
            val pos = if(it < 2) GameMap.currentMap.findHomeBase(this)!!.loc else -1
            ScytheDatabase.unitDao()!!.addUnit(UnitData(0, this.playerId, pos, UnitType.WORKER.ordinal))
        }
        repeat(4) {
            ScytheDatabase.unitDao()!!.addUnit(UnitData(0, this.playerId, -1, UnitType.MECH.ordinal))
        }
        UnitType.structures.forEach {
            ScytheDatabase.unitDao()!!.addUnit(UnitData(0, this.playerId, -1, it.ordinal))
        }
        factionMat.initialize(this)
    }

    companion object {
        private val loadedPlayers = SparseArrayCompat<PlayerInstance>()

        fun makePlayer(playerName: String, playerMatId: Int, factionMatId: Int): PlayerInstance {
            val playerMat = PlayerMat[playerMatId]
            val factionMat = FactionMat[factionMatId]
            val id= ScytheDatabase.playerDao()?.getPlayers()?.size?: 0
            val playerData = PlayerData(id, playerName, playerMat!!.initialCoins, factionMat!!.initialPower, playerMat.initialPopularity,
                    ObjectiveCardDeck.currentDeck.drawCard().id,
                    ObjectiveCardDeck.currentDeck.drawCard().id,
                    FactionMatData(factionMat.id),
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
                    flagRetreat = false,
                    flagCoercion = false,
                    flagToka = false,
                    factoryCard = null
            )
            val playerInstance = PlayerInstance(playerData)
            playerInstance.initializePlayer()

            ScytheDatabase.playerDao()!!.addPlayer(playerData)
            playerInstance.playerData.id = ScytheDatabase.playerDao()!!.getPlayer(playerName)!!.id
            playerInstance.initializeUnits()

            return playerInstance
        }

        fun loadPlayer(playerId: Int): PlayerInstance {
            return if(loadedPlayers.containsKey(playerId)) loadedPlayers[playerId]!! else PlayerInstance(ScytheDatabase.playerDao()!!.getPlayer(playerId)!!).also {
                loadedPlayers.put(playerId, it)
            }
        }

        val activeFactions: List<Int>?
            get() = ScytheDatabase.playerDao()?.getPlayers()?.map { it.factionMat.matId }
    }
}