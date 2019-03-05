package org.ajar.scythemobile.model.faction

import org.ajar.scythemobile.model.Mat
import org.ajar.scythemobile.model.StarModel
import org.ajar.scythemobile.model.StarType
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.EncounterCard
import org.ajar.scythemobile.model.playermat.PlayerMatInstance
import org.ajar.scythemobile.model.playermat.SectionInstance
import org.ajar.scythemobile.model.production.Resource
import org.ajar.scythemobile.model.production.ResourceType

enum class FactionMat(
        override val matName: String,
        override val heroCharacter: HeroCharacter,
        override val factionAbility: FactionAbility,
        override val color: Int,
        override val initialPower: Int,
        override val initialCombatCards: Int,
        override val symbol: Int,
        override val matImage: Int
) : FactionMatModel {
    NORDIC("Nordic Kingdoms", CharacterDescription.BJORN, FactionAbility.SWIM, 0x000000FF, 4, 1, 0, 0) {
        override val mechAbilities: Collection<FactionMechAbility> = listOf(
                RiverWalk.FOREST_MOUNTAIN, Seaworthy(), Speed.singleton, Artillery()
        )

        override fun getMovementRules(unitType: UnitType): List<MovementRule> {
            TODO("NYI")
        }
    },
    SAXONY("Saxony Empire", CharacterDescription.GUNTER, FactionAbility.DOMINATE,0x00000000, 1, 4, 0, 0) {
        override val mechAbilities: Collection<FactionMechAbility> = listOf(
                RiverWalk.FOREST_MOUNTAIN, Underpass(), Speed.singleton, Disarm()
        )

        override fun addStar(starType: StarModel, player: Player) {
            if (starType == StarType.COMBAT) {
                when(player.getStarCount(starType)) {
                    0 -> player.stars[starType] = 1
                    starType.limit -> return
                    else -> player.stars[starType]!!.plus(1)
                }
            } else {
                super.addStar(starType, player)
            }
        }
    },
    POLONIA("Republic of Polonia", CharacterDescription.ANNA, FactionAbility.MEANDER, 0x00FFFFFF, 2, 3, 0, 0) {
        override val mechAbilities: Collection<FactionMechAbility> = listOf(
                RiverWalk.VILLAGE_MOUNTAIN, Submerge(), Speed.singleton, Camaraderie()
        )

        override fun doEncounter(encounter: EncounterCard, unit: GameUnit) {
            TODO("NYI")
        }
    },
    CRIMEA("Crimean Khanate", CharacterDescription.ZERHA, FactionAbility.COERCION, 0x00FFFF00, 5, 0, 0, 0) {
        override val mechAbilities: Collection<FactionMechAbility> = listOf(
                RiverWalk.FARM_TUNDRA, Wayfare(), Speed.singleton, Scout()
        )

        override fun getResourcesAvailable(resourceType: ResourceType, player: Player): List<Resource> {
            TODO("NYI")
        }
    },
    RUSVIET("Rusviet Union", CharacterDescription.OLGA, FactionAbility.RELENTLESS, 0x00FF0000, 3, 2, 0, 0) {
        override val mechAbilities: Collection<FactionMechAbility> = listOf(
                RiverWalk.FARM_VILLAGE, Township(), Speed.singleton, PeoplesArmy()
        )

        override fun getAvailableMatSelections(playerMat: PlayerMatInstance): Set<SectionInstance> {
            return playerMat.sections
        }
    },
    ALBION("Clan Albion", CharacterDescription.CONNER, FactionAbility.EXALT, 0x0000AA00, 3, 0, 0, 0) {
        override val mechAbilities: Collection<FactionMechAbility> = listOf(
                Burrow(), Rally(), Sword(), Shield()
        )

        override fun getMovementRules(unitType: UnitType): List<MovementRule> {
            TODO("Deal with flags")
        }
    },
    TOGAWA("Togawa Shogunate", CharacterDescription.AKIKO, FactionAbility.MAIFUKU, 0x00DD00DD, 0, 2, 0, 0) {
        override val mechAbilities: Collection<FactionMechAbility> = listOf(
                Toka(), Shinobi(), Ronin(), Suiton()
        )

        override fun getMovementRules(unitType: UnitType): List<MovementRule> {
            TODO("Deal with traps")
        }
    };

    override fun addStar(starType: StarModel, player: Player) {
        when(player.getStarCount(starType)) {
            0 -> player.stars[starType] = 1
            starType.limit -> return
            else -> player.stars[starType]!!.plus(1)
        }
    }

    override fun getAvailableMatSelections(playerMat: PlayerMatInstance) : Set<SectionInstance> {
        return playerMat.sections.filter { it != playerMat.currentSection }.toSet()
    }

    override fun getResourcesAvailable(resourceType: ResourceType, player: Player): List<Resource> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMovementRules(unitType: UnitType): List<MovementRule> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doEncounter(encounter: EncounterCard, unit: GameUnit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class FactionMatInstance(val model: FactionMatModel) {
    private val abilityMap = mapOf(*model.mechAbilities.map { Pair(it.abilityName, it) }.toTypedArray())

    private val unlockedMechAbility: MutableList<FactionMechAbility> = ArrayList()

    fun getMovementAbilities() : List<MovementRule> {
        return unlockedMechAbility.filter { it is MovementRule }.map { it as MovementRule }
    }

    fun getCombatAbilities() : List<CombatRule> {
        return unlockedMechAbility.filter { it is CombatRule }.map { it as CombatRule }
    }

    fun unlockMechAbility(name: String) {
        abilityMap[name]?.also { unlockedMechAbility.add(it) }
    }
}

interface FactionMatModel : Mat {
    val symbol: Int

    val heroCharacter: HeroCharacter
    val color: Int
    val initialPower: Int
    val initialCombatCards: Int

    val mechAbilities: Collection<FactionMechAbility>
    val factionAbility: FactionAbility

    fun addStar(starType: StarModel, player: Player)
    fun getAvailableMatSelections(playerMat: PlayerMatInstance) : Set<SectionInstance>
    fun getResourcesAvailable(resourceType: ResourceType, player: Player) : List<Resource>
    fun getMovementRules(unitType: UnitType) : List<MovementRule>
    fun doEncounter(encounter: EncounterCard, unit: GameUnit)
}