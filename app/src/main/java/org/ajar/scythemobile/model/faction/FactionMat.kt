package org.ajar.scythemobile.model.faction

import android.util.SparseArray
import androidx.core.util.set
import org.ajar.scythemobile.data.FactionMatData
import org.ajar.scythemobile.data.PlayerData
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.FlagUnit
import org.ajar.scythemobile.model.entity.TrapType
import org.ajar.scythemobile.model.entity.TrapUnit
import org.ajar.scythemobile.old.model.ExaltChoice
import org.ajar.scythemobile.old.model.MaifukuChoice
import org.ajar.scythemobile.model.StarType
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.old.model.faction.*
import org.ajar.scythemobile.old.model.map.GameMap
import org.ajar.scythemobile.old.model.map.MapHex
import org.ajar.scythemobile.old.model.production.CrimeaCardResource
import org.ajar.scythemobile.old.model.production.Resource
import org.ajar.scythemobile.old.model.production.ResourceType
import org.ajar.scythemobile.old.model.turn.CoercianTurnAction

interface FactionMat {
    val matName: String
    val heroCharacter: HeroCharacter
    val factionAbility: FactionAbility
    val color: Int
    val initialPower: Int
    val initialCombatCards: Int
    val symbol: Int
    val matImage: Int
    val encounterSelections: Int

    val id: Int
        get() = factionMats.indexOfValue(this)

    val mechAbilities: Collection<FactionAbility>

    fun getFactionMovementRules(): List<MovementRule> {
        return emptyList()
    }

    fun addStarFunction(starType: StarType, playerData: PlayerData) {
        starType.apply(playerData)
    }

    fun selectableSections(playerData: PlayerData): List<Int> {
        val top = if(playerData.factoryCard != null) 4 else 3
        return (0..top).filter { it != playerData.playerMat.lastSection }
    }

    fun initializePlayer(player: PlayerInstance)

    companion object {
        private val factionMats = SparseArray<FactionMat>()

        operator fun set(id: Int, mat: FactionMat) {
            factionMats[id] = mat
        }

        operator fun get(id: Int): FactionMat? = factionMats[id]
    }
}

enum class StandardFactionMat(
        override val matName: String,
        override val heroCharacter: HeroCharacter,
        override val factionAbility: FactionAbility,
        override val color: Int,
        override val initialPower: Int,
        override val initialCombatCards: Int,
        override val symbol: Int,
        override val matImage: Int,
        override val encounterSelections: Int = 1
) : FactionMat {
    NORDIC("Nordic Kingdoms", CharacterDescription.BJORN, DefaultFactionAbility.SWIM, 0x000000FF, 4, 1, 0, 0) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                RiverWalk.FOREST_MOUNTAIN, Seaworthy(), Speed.singleton, Artillery()
        )

        override fun getFactionMovementRules(): List<MovementRule> {
            return listOf(Swim())
        }
    },
    SAXONY("Saxony Empire", CharacterDescription.GUNTER, DefaultFactionAbility.DOMINATE,0x00000000, 1, 4, 0, 0) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                RiverWalk.FOREST_MOUNTAIN, Underpass(), Speed.singleton, Disarm()
        )

        override fun addStarFunction(starType: StarType, playerData: PlayerData) {
            if(starType == StarType.COMBAT) {
                if(playerData.starCombat < 6) playerData.starCombat++
            } else {
                super.addStarFunction(starType, playerData)
            }
        }
    },
    POLONIA("Republic of Polonia", CharacterDescription.ANNA, DefaultFactionAbility.MEANDER, 0x00FFFFFF, 2, 3, 0, 0, 2) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                RiverWalk.VILLAGE_MOUNTAIN, Submerge(), Speed.singleton, Camaraderie()
        )
    },
    CRIMEA("Crimean Khanate", CharacterDescription.ZERHA, DefaultFactionAbility.COERCION, 0x00FFFF00, 5, 0, 0, 0) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                RiverWalk.FARM_TUNDRA, Wayfare(), Speed.singleton, Scout()
        )

        override fun initializePlayer(player: PlayerInstance) {
            val originalPrompt = player.promptForPayment

            player.promptForPayment = { cost: List<ResourceType>, map: MutableMap<Resource, MapHex> ->
                if(!player.turn.checkIfActionTypePerformed(CoercianTurnAction::class.java)) {
                    player.combatCards.sortedBy { it.power }.firstOrNull { true }?.let {map[CrimeaCardResource(it)] = GameMap.currentMap!!.findHomeBase(player) as MapHex }
                }

                originalPrompt.invoke(cost, map)
            }
        }
    },
    RUSVIET("Rusviet Union", CharacterDescription.OLGA, DefaultFactionAbility.RELENTLESS, 0x00FF0000, 3, 2, 0, 0) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                RiverWalk.FARM_VILLAGE, Township(), Speed.singleton, PeoplesArmy()
        )

        override fun selectableSections(playerData: PlayerData): List<Int> {
            val top = if(playerData.factoryCard != null) 4 else 3
            return (0..top).toList()
        }
    },
    ALBION("Clan Albion", CharacterDescription.CONNER, DefaultFactionAbility.EXALT, 0x0000AA00, 3, 0, 0, 0) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                Burrow(), Rally(), Sword(), Shield()
        )

        override fun initializePlayer(player: PlayerInstance) {
            player.tokens = mutableListOf(
                    FlagUnit(player),
                    FlagUnit(player),
                    FlagUnit(player),
                    FlagUnit(player)
            )
            player.tokenPlacementChoice = ExaltChoice()
        }
    },
    TOGAWA("Togawa Shogunate", CharacterDescription.AKIKO, DefaultFactionAbility.MAIFUKU, 0x00DD00DD, 0, 2, 0, 0) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                Toka(), Shinobi(), Ronin(), Suiton()
        )

        override fun initializePlayer(player: PlayerInstance) {
            player.tokens = mutableListOf(
                    TrapUnit(player, TrapType.MAIFUKU_LOSE_CARDS),
                    TrapUnit(player, TrapType.MAIFUKU_LOSE_MONEY),
                    TrapUnit(player, TrapType.MAIFUKU_LOSE_POP),
                    TrapUnit(player, TrapType.MAIFUKU_LOSE_POWER)
            )
            player.tokenPlacementChoice = MaifukuChoice()
        }
    };

    override fun initializePlayer(player: PlayerInstance) {
        for(i in 1..initialCombatCards) {
            CombatCardDeck.currentDeck.drawCard(player)
        }
    }

    companion object {
        init {
            load()
        }

        fun load() {
            values().forEach {
                FactionMat[it.ordinal] = it
            }
        }
    }
}

class FactionMatInstance(val factionMat: FactionMat, val factionMatData: FactionMatData) {
    constructor(factionMatData: FactionMatData) : this(FactionMat[factionMatData.matId]!!, factionMatData)

    val unlockedFactionAbilities: Collection<FactionAbility>
            get() = factionMat.mechAbilities.filterIndexed { index, _ ->
                when(index) {
                    0 -> factionMatData.upgradeOne
                    1 -> factionMatData.upgradeTwo
                    2 -> factionMatData.upgradeThree
                    3 -> factionMatData.upgradeFour
                    else -> false
                }
            }
    val lockedFactionAbilities: Collection<FactionAbility>
        get() = factionMat.mechAbilities.filterIndexed { index, _ ->
            when(index) {
                0 -> !factionMatData.upgradeOne
                1 -> !factionMatData.upgradeTwo
                2 -> !factionMatData.upgradeThree
                3 -> !factionMatData.upgradeFour
                else -> false
            }
        }

    fun getMovementAbilities(type: UnitType) : Collection<MovementRule> {
        return unlockedFactionAbilities.filterIsInstance<MovementRule>().filter { it.validUnitType(type) } + standardMovementRules
    }

    fun getCombatAbilities() : Collection<CombatRule> {
        return unlockedFactionAbilities.filterIsInstance<CombatRule>()
    }

    fun addStar(starType: StarType, playerData: PlayerData) = factionMat.addStarFunction(starType, playerData)

    fun selectableSections(playerData: PlayerData) = factionMat.selectableSections(playerData)

    fun initializePlayer(player: PlayerInstance) = factionMat.initializePlayer(player)

    companion object {
        private val standardMovementRules : List<MovementRule> = listOf(StandardMove(), TunnelMove())
    }
}