package org.ajar.scythemobile.old.model.faction

import org.ajar.scythemobile.model.StarType
import org.ajar.scythemobile.model.entity.FlagUnit
import org.ajar.scythemobile.model.entity.TrapType
import org.ajar.scythemobile.model.entity.TrapUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.*
import org.ajar.scythemobile.old.model.*
import org.ajar.scythemobile.old.model.entity.*
import org.ajar.scythemobile.old.model.map.GameMap
import org.ajar.scythemobile.old.model.map.MapHex
import org.ajar.scythemobile.old.model.production.CrimeaCardResource
import org.ajar.scythemobile.old.model.production.Resource
import org.ajar.scythemobile.old.model.production.ResourceType
import org.ajar.scythemobile.old.model.turn.CoercianTurnAction

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

        override fun initializePlayer(player: Player) {
            val originalAddStar = player.addStar

            player.addStar = { starType ->
                if (starType == StarType.COMBAT) {
                    when(player.getStarCount(starType)) {
                        0 -> player.stars[starType] = 1
                        8 -> Unit
                        else -> player.stars[starType] = player.stars[starType]!!.plus(1)
                    }
                } else {
                    originalAddStar.invoke(starType)
                }
            }
        }
    },
    POLONIA("Republic of Polonia", CharacterDescription.ANNA, DefaultFactionAbility.MEANDER, 0x00FFFFFF, 2, 3, 0, 0) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                RiverWalk.VILLAGE_MOUNTAIN, Submerge(), Speed.singleton, Camaraderie()
        )

        override fun initializePlayer(player: Player) {
            player.doEncounter = { encounter, unit ->
                val encounterOutcomes = player.user.requester?.requestSelection(EncounterChoice(), encounter.outcomes, 2)?.toMutableList()

                if (encounterOutcomes != null && encounterOutcomes.size > 0) {
                    if (!encounterOutcomes[0].canMeetCost(player)) {
                        val switch = encounterOutcomes.removeAt(0)
                        encounterOutcomes.add(switch)
                    }

                    for (encounterOutcome in encounterOutcomes) {
                        if (encounterOutcome.canMeetCost(player)) {
                            encounterOutcome.applyOutcome(unit)
                        }
                    }
                }
            }
        }
    },
    CRIMEA("Crimean Khanate", CharacterDescription.ZERHA, DefaultFactionAbility.COERCION, 0x00FFFF00, 5, 0, 0, 0) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                RiverWalk.FARM_TUNDRA, Wayfare(), Speed.singleton, Scout()
        )

        override fun initializePlayer(player: Player) {
            val originalPrompt = player.promptForPayment

            player.promptForPayment = {cost: List<ResourceType>, map: MutableMap<Resource, MapHex> ->
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

        override fun initializePlayer(player: Player) {
            player.selectableSections = { player.playerMat.sections.toList() }
        }
    },
    ALBION("Clan Albion", CharacterDescription.CONNER, DefaultFactionAbility.EXALT, 0x0000AA00, 3, 0, 0, 0) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                Burrow(), Rally(), Sword(), Shield()
        )

        override fun initializePlayer(player: Player) {
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

        override fun initializePlayer(player: Player) {
            player.tokens = mutableListOf(
                    TrapUnit(player, TrapType.MAIFUKU_LOSE_CARDS),
                    TrapUnit(player, TrapType.MAIFUKU_LOSE_MONEY),
                    TrapUnit(player, TrapType.MAIFUKU_LOSE_POP),
                    TrapUnit(player, TrapType.MAIFUKU_LOSE_POWER)
            )
            player.tokenPlacementChoice = MaifukuChoice()
        }
    };

    override fun getFactionMovementRules(): List<MovementRule> {
        return emptyList()
    }

    override fun initializePlayer(player: Player) {}

}

class FactionMatInstance(val model: FactionMatModel) {
    private val abilityMap = mapOf(*model.mechAbilities.map { Pair(it.abilityName, it) }.toTypedArray())
    private val standardMovementRules : List<MovementRule> = listOf(StandardMove(), TunnelMove())

    val unlockedMechAbility: MutableList<FactionAbility> = ArrayList()
    val lockedMechAbilities: List<String>
        get() {
            return abilityMap.filter { pair -> !unlockedMechAbility.contains(pair.value) }.map { pair -> pair.key }
        }

    fun getMovementAbilities(unitType: UnitType) : List<MovementRule> {
        val rules = unlockedMechAbility.filter { it is MovementRule && it.validUnitType(unitType)}.map { it as MovementRule }.toMutableList()
        rules.addAll(standardMovementRules)
        rules.addAll(model.getFactionMovementRules().filter { it.validUnitType(unitType) })

        return rules
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

    val mechAbilities: Collection<FactionAbility>
    val factionAbility: FactionAbility

    /**
     * I'm Really intending this to be a drag and drop manuever where you move the resources to the cost area of the screen.
     */
    fun getFactionMovementRules() : List<MovementRule>

    fun initializePlayer(player: Player)

}