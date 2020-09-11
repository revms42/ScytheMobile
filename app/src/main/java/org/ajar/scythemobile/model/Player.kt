package org.ajar.scythemobile.model

import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.data.FactionMatData
import org.ajar.scythemobile.data.PlayerData
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.faction.CombatRule
import org.ajar.scythemobile.model.faction.FactionMatInstance
import org.ajar.scythemobile.model.faction.MovementRule
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.FactionMat
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

    var playerId: Int = playerData.id

    val recruits: Int
        get() = playerMat.sections.count { section -> section.bottomRowAction.recruited }

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

    val coins: List<Coin>?
        get() = ScytheDatabase.resourceDao()?.getOwnedResourcesOfType(playerId, listOf(CapitalResourceType.COINS.id))?.map { Coin(it) }

    fun takeCoins(number: Int, requireExactChange: Boolean = false): List<Coin>? {
        val coins = this.coins
        return if(!coins.isNullOrEmpty()) {
            if(requireExactChange) {
                if(coins.size >= number) {
                    val sublist = coins.subList(0, number)
                    TurnHolder.updateResource(*sublist.map { it.resourceData.owner = -1; it.resourceData }.toTypedArray())
                    sublist
                } else {
                    null
                }
            } else {
                val total = if(number > coins.size) coins.size else number
                val sublist = coins.subList(0, total)
                TurnHolder.updateResource(*sublist.map { it.resourceData.owner = -1; it.resourceData }.toTypedArray())
                sublist
            }
        } else {
            null
        }
    }

    fun giveCoins(vararg coin: Coin) {
        TurnHolder.updateResource(*coin.map { it.resourceData.owner = playerId; it.resourceData }.toTypedArray())
    }

    fun drawCoins(number: Int) {
        ScytheDatabase.resourceDao()!!.getOwnedResourcesOfType( -1, listOf(CapitalResourceType.COINS.id))?.also { list ->
            val total = if(list.size < number) list.size else number
            TurnHolder.updateResource(*list.subList(0, total).map { coin -> coin.owner = playerId; coin }.toTypedArray())
        }
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
            playerInstance.initialize()

            ScytheDatabase.playerDao()!!.addPlayer(playerData)

            return playerInstance
        }

        fun loadPlayer(playerName: String): PlayerInstance {
            return PlayerInstance(ScytheDatabase.playerDao()!!.getPlayer(playerName)!!)
        }

        fun loadPlayer(playerId: Int): PlayerInstance {
            return PlayerInstance(ScytheDatabase.playerDao()!!.getPlayer(playerId)!!)
        }

        private var _activeFactions: List<Int>? = null
        fun activeFactions() : List<Int>? {
            if(_activeFactions == null) {
                _activeFactions = ScytheDatabase.playerDao()?.getPlayers()?.map { it.factionMat.matId }
            }
            return _activeFactions
        }
    }
}