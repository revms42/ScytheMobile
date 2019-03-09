package org.ajar.scythemobile.model.faction

import org.ajar.scythemobile.model.EncounterChoice
import org.ajar.scythemobile.model.Mat
import org.ajar.scythemobile.model.StarModel
import org.ajar.scythemobile.model.StarType
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.entity.ResourceHolder
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.EncounterCard
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.playermat.PlayerMatInstance
import org.ajar.scythemobile.model.playermat.SectionInstance
import org.ajar.scythemobile.model.production.CrimeaCardResource
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
    POLONIA("Republic of Polonia", CharacterDescription.ANNA, DefaultFactionAbility.MEANDER, 0x00FFFFFF, 2, 3, 0, 0) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                RiverWalk.VILLAGE_MOUNTAIN, Submerge(), Speed.singleton, Camaraderie()
        )

        override fun doEncounter(encounter: EncounterCard, unit: GameUnit) {
            val encounterOutcomes= unit.controllingPlayer.user.requester?.requestSelection(EncounterChoice(), encounter.outcomes, 2)?.toMutableList()

            if(encounterOutcomes != null && encounterOutcomes.size > 0) {
                val player = unit.controllingPlayer
                if(!encounterOutcomes[0].canMeetCost(player)) {
                    val switch = encounterOutcomes.removeAt(0)
                    encounterOutcomes.add(switch)
                }

                for (encounterOutcome in encounterOutcomes) {
                    if(encounterOutcome.canMeetCost(player)) {
                        encounterOutcome.applyOutcome(unit)
                    }
                }
            }
        }
    },
    CRIMEA("Crimean Khanate", CharacterDescription.ZERHA, DefaultFactionAbility.COERCION, 0x00FFFF00, 5, 0, 0, 0) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                RiverWalk.FARM_TUNDRA, Wayfare(), Speed.singleton, Scout()
        )

        override fun getFactionResourceBonus(resourceType: ResourceType, player: Player): Map<Resource, ResourceHolder?> {
            return mapOf(*player.combatCards.map { Pair(CrimeaCardResource(resourceType, it), null) }.toTypedArray())
        }
    },
    RUSVIET("Rusviet Union", CharacterDescription.OLGA, DefaultFactionAbility.RELENTLESS, 0x00FF0000, 3, 2, 0, 0) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                RiverWalk.FARM_VILLAGE, Township(), Speed.singleton, PeoplesArmy()
        )

        override fun getAvailableMatSelections(playerMat: PlayerMatInstance): Set<SectionInstance> {
            return playerMat.sections
        }
    },
    ALBION("Clan Albion", CharacterDescription.CONNER, DefaultFactionAbility.EXALT, 0x0000AA00, 3, 0, 0, 0) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                Burrow(), Rally(), Sword(), Shield()
        )

        override val tokenPlacementAbilities: Collection<TokenPlacementAbility> = listOf(Exalt())
    },
    TOGAWA("Togawa Shogunate", CharacterDescription.AKIKO, DefaultFactionAbility.MAIFUKU, 0x00DD00DD, 0, 2, 0, 0) {
        override val mechAbilities: Collection<FactionAbility> = listOf(
                Toka(), Shinobi(), Ronin(), Suiton()
        )

        override val tokenPlacementAbilities: Collection<TokenPlacementAbility> = listOf(Maifuku())
    };

    override val tokenPlacementAbilities: Collection<TokenPlacementAbility> = emptyList()

    override fun getFactionMovementRules(): List<MovementRule> {
        return emptyList()
    }

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

    override fun getFactionResourceBonus(resourceType: ResourceType, player: Player) : Map<Resource, ResourceHolder?> {
        return emptyMap()
    }

    override fun doEncounter(encounter: EncounterCard, unit: GameUnit) {
        val encounterOutcome= unit.controllingPlayer.user.requester?.requestChoice(EncounterChoice(), encounter.outcomes)

        if(encounterOutcome != null && encounterOutcome.canMeetCost(unit.controllingPlayer)) {
            encounterOutcome.applyOutcome(unit)
        }
    }
}

class FactionMatInstance(val model: FactionMatModel) {
    private val abilityMap = mapOf(*model.mechAbilities.map { Pair(it.abilityName, it) }.toTypedArray())
    private val standardMovementRules : List<MovementRule> = listOf(StandardMove(), TunnelMove())

    private val unlockedMechAbility: MutableList<FactionAbility> = ArrayList()
    private var tokens: HashMap<TokenPlacementAbility,MutableList<GameUnit>>? = null

    //TODO: This will need to be populated with structures as well.
    val unitsDeployed: Collection<GameUnit> = ArrayList()

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

    fun getResourcesAvailable(resourceType: ResourceType, player: Player) : Map<Resource, ResourceHolder?> {
        val allResources = HashMap<Resource,ResourceHolder?>()

        for (unit in unitsDeployed) {
            val mapHex = GameMap.currentMap?.locateUnit(unit)

            if(mapHex?.playerInControl == player) {
                mapHex.heldResources.forEach { if (it.type == resourceType) allResources[it] = mapHex}
            }

            unit.heldResources.forEach { if (it.type == resourceType) allResources[it] = mapHex}
        }

        allResources.putAll(model.getFactionResourceBonus(resourceType, player))

        return allResources
    }

    fun doTokenPlacement(unit: GameUnit, hex: MapHex) {
        if(tokens == null) {
            tokens = HashMap()

            for(rule in model.tokenPlacementAbilities) {
                tokens!![rule] = rule.createTokens(unit.controllingPlayer)
            }
        }

        for(rule in model.tokenPlacementAbilities) {
            val remainingTokens = tokens!![rule]

            if(remainingTokens != null && remainingTokens.size > 0) {
                val token = rule.selectToken(unit.controllingPlayer, remainingTokens)

                if(token != null && tokens!![rule]?.remove(token)!!) {
                    hex.unitsPresent.add(token)
                }
            }
        }
    }

    fun doEncounter(encounter: EncounterCard, gameUnit: GameUnit) {
        model.doEncounter(encounter, gameUnit)
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
    val tokenPlacementAbilities: Collection<TokenPlacementAbility>

    fun addStar(starType: StarModel, player: Player)
    fun getAvailableMatSelections(playerMat: PlayerMatInstance) : Set<SectionInstance>
    fun getFactionResourceBonus(resourceType: ResourceType, player: Player) : Map<Resource,ResourceHolder?>
    fun getFactionMovementRules() : List<MovementRule>
    fun doEncounter(encounter: EncounterCard, unit: GameUnit)
}