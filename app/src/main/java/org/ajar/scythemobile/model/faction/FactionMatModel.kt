package org.ajar.scythemobile.model.faction

import org.ajar.scythemobile.model.*
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.EncounterCard
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.playermat.PlayerMatInstance
import org.ajar.scythemobile.model.playermat.SectionInstance
import org.ajar.scythemobile.model.production.CrimeaCardResource
import org.ajar.scythemobile.model.production.Resource
import org.ajar.scythemobile.model.production.ResourceType
import org.ajar.scythemobile.model.turn.CoercianTurnAction

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
                    8 -> return
                    else -> player.stars[starType] = player.stars[starType]!!.plus(1)
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

        override fun promptForPaymentSelection(cost: List<ResourceType>, map: Map<Resource, MapHex>, player: Player) : Collection<Resource>? {
            val selected = super.promptForPaymentSelection(cost, map, player)

            //Here we preview whether or not payment will be accepted so we don't have to remove the action if it isn't.
            if(selected?.firstOrNull { it::class == CrimeaCardResource::class } != null && super.paymentAccepted(selected, cost)) {
                player.turn.performAction(CoercianTurnAction(player))
            }

            return selected
        }

        override fun selectResource(type: ResourceType, player: Player): MutableMap<in Resource, MapHex> {
            val mapRes = super.selectResource(type, player)

            if(!player.turn.checkIfActionTypePerformed(CoercianTurnAction::class.java)) {
                player.combatCards.sortedBy { it.power }.firstOrNull { true }?.let {mapRes[CrimeaCardResource(it)] = GameMap.currentMap!!.findHomeBase(player) as MapHex }
            }

            return mapRes
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

    override fun doEncounter(encounter: EncounterCard, unit: GameUnit) {
        val encounterOutcome= unit.controllingPlayer.user.requester?.requestChoice(EncounterChoice(), encounter.outcomes)

        if(encounterOutcome != null && encounterOutcome.canMeetCost(unit.controllingPlayer)) {
            encounterOutcome.applyOutcome(unit)
        }
    }

    override fun selectResource(type: ResourceType, player: Player) : MutableMap<in Resource, MapHex> {
        val collection = HashMap<Resource, MapHex>()
        var currentHex: MapHex?

        player.deployedUnits.forEach { unit ->
            currentHex = GameMap.currentMap!!.locateUnit(unit)
            if (currentHex != null) {
                unit.heldResources.filter { it.type == type }.forEach { collection[it] = currentHex!! }
                currentHex?.heldResources?.filter { it.type == type }?.forEach { collection[it] = currentHex!! }
            }
        }

        return collection
    }

    internal open fun promptForPaymentSelection(cost: List<ResourceType>, map: Map<Resource, MapHex>, player: Player) : Collection<Resource>? {
        return player.user.requester?.requestPayment(PaymentChoice(), cost, map)
    }

    override fun collectPayment(cost: List<ResourceType>, player: Player) : Boolean {
        val map = HashMap<Resource, MapHex>()
        cost.flatMap { selectResource(it, player).entries }.toMutableSet().forEach { map[it.key as Resource] = it.value}

        val selection = promptForPaymentSelection(cost, map, player)

        return if(paymentAccepted(selection, cost)) {
            selection?.firstOrNull {
                when(it) {
                    is CrimeaCardResource -> !player.combatCards.remove(it.card)
                    else -> if(map[it]?.unitsPresent?.firstOrNull { unit -> unit.heldResources.remove(it) } == null) !map[it]?.heldResources?.remove(it)!! else true
                }
            } == null
        } else false
    }

    private fun paymentAccepted(selection: Collection<Resource>?, cost: List<ResourceType>) : Boolean {
        if(selection == null) return false

        val takePayment = ArrayList(selection)
        val uncounted = ArrayList<ResourceType>()
        for(c in cost) {
            val found = takePayment.firstOrNull { it.type == c }

            if(found == null) {
                uncounted.add(c)
            } else {
                takePayment.remove(found)
            }
        }

        return when {
            uncounted.size == 0 -> true
            uncounted.size == 1 && takePayment.size == 1 && takePayment[0]::class.java == CrimeaCardResource::class.java -> true
            else -> false
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


    fun collectPayment(cost: List<ResourceType>, player: Player) : Boolean {
        return model.collectPayment(cost, player)
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
    /**
     * I'm Really intending this to be a drag and drop manuever where you move the resources to the cost area of the screen.
     */
    fun selectResource(type: ResourceType, player: Player) : MutableMap<in Resource, MapHex>
    fun collectPayment(cost: List<ResourceType>, player: Player) : Boolean
    fun getFactionMovementRules() : List<MovementRule>
    fun doEncounter(encounter: EncounterCard, unit: GameUnit)
}