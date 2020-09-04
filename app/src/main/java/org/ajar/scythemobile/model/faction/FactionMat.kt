package org.ajar.scythemobile.model.faction

import android.util.SparseArray
import androidx.core.util.set
import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.Resource
import org.ajar.scythemobile.data.*
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.StarType
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.entity.*
import org.ajar.scythemobile.old.model.faction.*

interface FactionMat {
    val matName: String
    val heroCharacter: HeroCharacter
    val factionAbility: FactionAbility
    val color: Int
    val initialPower: Int
    val initialCombatCards: Int
    val enlistPower: Int
    val enlistCoins: Int
    val enlistCards: Int
    val enlistPopularity: Int
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

    fun placeableTokens(player: PlayerInstance): List<GameUnit>?
    fun controlledResource(player: PlayerInstance, type: Int): List<ResourceData>?

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
        override val encounterSelections: Int = 1,
        override val enlistPower: Int = 2,
        override val enlistCoins: Int = 2,
        override val enlistCards: Int = 2,
        override val enlistPopularity: Int = 2
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

        override fun controlledResource(player: PlayerInstance, type: Int): List<ResourceData>? {
            val default = super.controlledResource(player, type)?.toMutableList()
            if(!player.playerData.flagCoercion) {
                val card = ScytheDatabase.resourceDao()!!.getOwnedResourcesOfType(CapitalResourceType.CARDS.id, player.playerId)?.minBy {
                    it.value
                }
                if(card != null) default?.add(card)
            }
            return default
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
            ScytheDatabase.unitDao()!!.addUnit(
                    *(0..3).map { UnitData(0, player.playerId, -1, UnitType.FLAG.ordinal, -1, -1) }.toTypedArray()
            )
        }

        override fun placeableTokens(player: PlayerInstance): List<GameUnit>? {
            return ScytheDatabase.unitDao()?.getUnitsForPlayer(player.playerId, UnitType.FLAG.ordinal)?.filter { it.loc == -1 }?.map { GameUnit(it, player) }
        }
    },
    TOGAWA("Togawa Shogunate", CharacterDescription.AKIKO, DefaultFactionAbility.MAIFUKU, 0x00DD00DD, 0, 2, 0, 0) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                Toka(), Shinobi(), Ronin(), Suiton()
        )

        override fun initializePlayer(player: PlayerInstance) {
            ScytheDatabase.unitDao()!!.addUnit(
                    *TrapType.values().map { type -> UnitData(0, player.playerId, -1, UnitType.TRAP.ordinal, TrapUnit.TrapState.NONE.ordinal, type.ordinal) }.toTypedArray()
            )
        }

        override fun placeableTokens(player: PlayerInstance): List<GameUnit>? {
            return ScytheDatabase.unitDao()?.getUnitsForPlayer(player.playerId, UnitType.TRAP.ordinal)?.filter { it.loc == -1 }?.map { TrapUnit(it, player) }
        }
    };

    override fun initializePlayer(player: PlayerInstance) {
        for(i in 1..initialCombatCards) {
            CombatCardDeck.currentDeck.drawCard(player)
        }
    }

    override fun placeableTokens(player: PlayerInstance): List<GameUnit>? = null
    override fun controlledResource(player: PlayerInstance, type: Int): List<ResourceData>? {
        return ScytheDatabase.resourceDao()?.getOwnedResourcesOfType(type, player.playerId)?: emptyList()
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

    fun unlockFactionAbility(ability: FactionAbility) : Boolean {
        return factionMat.mechAbilities.indexOf(ability).takeIf { it > -1 }?.let {
            when(it) {
                0 -> factionMatData.upgradeOne = true
                1 -> factionMatData.upgradeTwo = true
                2 -> factionMatData.upgradeThree = true
                3 -> factionMatData.upgradeFour = true
            }
            true
        }?: false
    }

    fun getEnlistmentBonus(capitalResourceType: CapitalResourceType) : Int {
        return when(capitalResourceType) {
            CapitalResourceType.CARDS -> factionMat.enlistCards
            CapitalResourceType.COINS -> factionMat.enlistCoins
            CapitalResourceType.POPULARITY -> factionMat.enlistPopularity
            CapitalResourceType.POWER -> factionMat.enlistPower
        }
    }

    fun getEnlistmentBonusesAvailabe() : List<CapitalResourceType> {
        return CapitalResourceType.values().filter {
            !when(it) {
                CapitalResourceType.CARDS -> factionMatData.enlistCards
                CapitalResourceType.COINS -> factionMatData.enlistCoins
                CapitalResourceType.POPULARITY -> factionMatData.enlistPop
                CapitalResourceType.POWER -> factionMatData.enlistPower
            }
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