package org.ajar.scythemobile.model.faction

import androidx.collection.SparseArrayCompat
import androidx.collection.set
import androidx.collection.valueIterator
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.R
import org.ajar.scythemobile.data.*
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.StarType
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.entity.*

enum class CharacterDescription(override val characterName: String) : HeroCharacter {
    BJORN("Bjorn & Mox"),
    GUNTER("Gunter, Nacht, & Tag"),
    ANNA("Anna & Wojtek"),
    ZERHA("Zerha & Kar"),
    OLGA("Olga & Changa"),
    CONNER("Conner & Max"),
    AKIKO("Akiko & Jiro")
}

interface HeroCharacter {
    val characterName: String
}

interface FactionResourcePack {
    val primaryColorRes: Int
    val secondaryColorRes: Int
    val factionIconRes: Int
    val diamondRes: Int
    val heroRes: Int?
    val mechRes: Int?
    val workerRes: Int?
    val millRes: Int?
    val mineRes: Int?
    val monumentRes: Int?
    val armoryRes: Int?
    val airshipRes: Int?
    val trapUnsprungRes: Int?
    val flagRes: Int?
    val matImage: Int?
}

enum class StandardFactionResourcePack(
        override val primaryColorRes: Int,
        override val secondaryColorRes: Int,
        override val factionIconRes: Int,
        override val diamondRes: Int,
        override val mechRes: Int,
        override val workerRes: Int,
        override val heroRes: Int,
        override val monumentRes: Int,
        override val millRes: Int,
        override val mineRes: Int,
        override val armoryRes: Int,
        override val airshipRes: Int? = null,
        override val trapUnsprungRes: Int? = null,
        override val flagRes: Int? = null,
        override val matImage: Int? = null
) : FactionResourcePack {
    NODIC(
            R.color.colorNordicPrimary,
            R.color.colorNordicSecondary,
            R.drawable.ic_nordic,
            R.drawable.ic_nordic_circle,
            R.drawable.ic_nordic_mech,
            R.drawable.ic_nordic_worker,
            R.drawable.ic_nordic_hero,
            R.drawable.ic_nordic_monument,
            R.drawable.ic_nordic_mill,
            R.drawable.ic_nordic_mine,
            R.drawable.ic_nordic_armory
    ),
    SAXONY(
            R.color.colorSaxonyPrimary,
            R.color.colorSaxonySecondary,
            R.drawable.ic_saxony,
            R.drawable.ic_saxony_circle,
            R.drawable.ic_saxony_mech,
            R.drawable.ic_saxony_worker,
            R.drawable.ic_saxony_hero,
            R.drawable.ic_saxony_monument,
            R.drawable.ic_saxony_mill,
            R.drawable.ic_saxony_mine,
            R.drawable.ic_saxony_armory
    ),
    POLONIA(
            R.color.colorPoloniaPrimary,
            R.color.colorPoloniaSecondary,
            R.drawable.ic_polonia,
            R.drawable.ic_polonia_circle,
            R.drawable.ic_polonia_mech,
            R.drawable.ic_polonia_worker,
            R.drawable.ic_polonia_hero,
            R.drawable.ic_polonia_monument,
            R.drawable.ic_polonia_mill,
            R.drawable.ic_polonia_mine,
            R.drawable.ic_polonia_armory
    ),
    CRIMEA(
            R.color.colorCrimeaPrimary,
            R.color.colorCrimeaSecondary,
            R.drawable.ic_crimea,
            R.drawable.ic_crimea_circle,
            R.drawable.ic_crimea_mech,
            R.drawable.ic_crimea_worker,
            R.drawable.ic_crimea_hero,
            R.drawable.ic_crimea_monument,
            R.drawable.ic_crimea_mill,
            R.drawable.ic_crimea_mine,
            R.drawable.ic_crimea_armory
    ),
    RUSVIET(
            R.color.colorRusvietPrimary,
            R.color.colorRusvietSecondary,
            R.drawable.ic_rusviet,
            R.drawable.ic_rusviet_circle,
            R.drawable.ic_rusviet_mech,
            R.drawable.ic_rusviet_worker,
            R.drawable.ic_rusviet_hero,
            R.drawable.ic_rusviet_monument,
            R.drawable.ic_rusviet_mill,
            R.drawable.ic_rusviet_mine,
            R.drawable.ic_rusviet_armory
    ),
    ALBION(
            R.color.colorAlbionPrimary,
            R.color.colorAlbionSecondary,
            R.drawable.ic_albion,
            R.drawable.ic_albion_circle,
            R.drawable.ic_albion_mech,
            R.drawable.ic_albion_worker,
            R.drawable.ic_albion_hero,
            R.drawable.ic_albion_monument,
            R.drawable.ic_albion_mill,
            R.drawable.ic_albion_mine,
            R.drawable.ic_albion_armory,
            null,
            null,
            R.drawable.ic_albion_flag
    ),
    TOGAWA(
            R.color.colorTogawaPrimary,
            R.color.colorTogawaSecondary,
            R.drawable.ic_togawa,
            R.drawable.ic_togawa_circle,
            R.drawable.ic_togawa_mech,
            R.drawable.ic_togawa_worker,
            R.drawable.ic_togawa_hero,
            R.drawable.ic_togawa_monument,
            R.drawable.ic_togawa_mill,
            R.drawable.ic_togawa_mine,
            R.drawable.ic_togawa_armory,
            null,
            R.drawable.ic_togawa_trap_armed
    )
}

interface FactionMat {
    val matName: String
    val heroCharacter: HeroCharacter
    val factionAbility: FactionAbility
    val initialPower: Int
    val initialCombatCards: Int
    val enlistPower: Int
    val enlistCoins: Int
    val enlistCards: Int
    val enlistPopularity: Int
    val resourcePack: FactionResourcePack
    val encounterSelections: Int

    val id: Int
        get() = factionMats.indexOfValue(this)

    val symbol: Int
        get() = resourcePack.factionIconRes

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
    fun controlledResource(player: PlayerInstance, type: List<Int>): List<ResourceData>?

    fun initializePlayer(player: PlayerInstance)

    companion object {
        private val factionMats = SparseArrayCompat<FactionMat>()

        operator fun set(id: Int, mat: FactionMat) {
            factionMats[id] = mat
        }

        operator fun get(id: Int): FactionMat? = factionMats[id]

        fun list() : List<FactionMat> {
            return ArrayList<FactionMat>().also { list -> factionMats.valueIterator().forEach { list.add(it) } }
        }
    }
}

enum class StandardFactionMat(
        override val matName: String,
        override val heroCharacter: HeroCharacter,
        override val factionAbility: FactionAbility,
        override val resourcePack: FactionResourcePack,
        override val initialPower: Int,
        override val initialCombatCards: Int,
        override val encounterSelections: Int = 1,
        override val enlistPower: Int = 2,
        override val enlistCoins: Int = 2,
        override val enlistCards: Int = 2,
        override val enlistPopularity: Int = 2
) : FactionMat {
    NORDIC("Nordic Kingdoms",
            CharacterDescription.BJORN,
            DefaultFactionAbility.SWIM,
            StandardFactionResourcePack.NODIC,
            4,
            1
    ) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                RiverWalk.FOREST_MOUNTAIN, Seaworthy(), Speed.singleton, Artillery()
        )

        override fun getFactionMovementRules(): List<MovementRule> {
            return listOf(Swim())
        }
    },
    SAXONY("Saxony Empire",
            CharacterDescription.GUNTER,
            DefaultFactionAbility.DOMINATE,
            StandardFactionResourcePack.SAXONY,
            1,
            4
    ) {
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
    POLONIA("Republic of Polonia",
            CharacterDescription.ANNA,
            DefaultFactionAbility.MEANDER,
            StandardFactionResourcePack.POLONIA,
            2,
            3,
            2
    ) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                RiverWalk.VILLAGE_MOUNTAIN, Submerge(), Speed.singleton, Camaraderie()
        )
    },
    CRIMEA("Crimean Khanate",
            CharacterDescription.ZERHA,
            DefaultFactionAbility.COERCION,
            StandardFactionResourcePack.CRIMEA,
            5,
            0
    ) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                RiverWalk.FARM_TUNDRA, Wayfare(), Speed.singleton, Scout()
        )

        override fun controlledResource(player: PlayerInstance, type: List<Int>): List<ResourceData>? {
            val default = super.controlledResource(player, type)?.toMutableList()
            if(!player.playerData.flagCoercion) {
                val card = ScytheDatabase.resourceDao()!!.getOwnedResourcesOfType(player.playerId, listOf(CapitalResourceType.CARDS.id))?.minBy {
                    it.value
                }
                if(card != null) default?.add(card)
            }
            return default
        }
    },
    RUSVIET("Rusviet Union",
            CharacterDescription.OLGA,
            DefaultFactionAbility.RELENTLESS,
            StandardFactionResourcePack.RUSVIET,
            3,
            2
    ) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                RiverWalk.FARM_VILLAGE, Township(), Speed.singleton, PeoplesArmy()
        )

        override fun selectableSections(playerData: PlayerData): List<Int> {
            val top = if(playerData.factoryCard != null) 4 else 3
            return (0..top).toList()
        }
    },
    ALBION("Clan Albion",
            CharacterDescription.CONNER,
            DefaultFactionAbility.EXALT,
            StandardFactionResourcePack.ALBION,
            3,
            0
    ) {
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
    TOGAWA("Togawa Shogunate",
            CharacterDescription.AKIKO,
            DefaultFactionAbility.MAIFUKU,
            StandardFactionResourcePack.TOGAWA,
            0,
            2
    ) {
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
    override fun controlledResource(player: PlayerInstance, type: List<Int>): List<ResourceData>? {
        return ScytheDatabase.resourceDao()?.getOwnedResourcesOfType(player.playerId, type)?: emptyList()
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
            } + factionMat.getFactionMovementRules()
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

    val defaultFactionAbility: DefaultFactionAbility = factionMat.factionAbility as DefaultFactionAbility

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

    fun initialize(playerInstance: PlayerInstance) = factionMat.initializePlayer(playerInstance)

    companion object {
        private val standardMovementRules : List<MovementRule> = listOf(StandardMove(), TunnelMove())
    }
}