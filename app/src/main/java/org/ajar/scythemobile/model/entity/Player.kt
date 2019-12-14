package org.ajar.scythemobile.model.entity

import org.ajar.scythemobile.model.*
import org.ajar.scythemobile.model.combat.CombatBoard
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.faction.FactionMat
import org.ajar.scythemobile.model.faction.FactionMatInstance
import org.ajar.scythemobile.model.faction.FactionMatModel
import org.ajar.scythemobile.model.map.EncounterCard
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.HomeBase
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.objective.Objective
import org.ajar.scythemobile.model.objective.ObjectiveCardDeck
import org.ajar.scythemobile.model.playermat.PlayerMatInstance
import org.ajar.scythemobile.model.playermat.PlayerMatModel
import org.ajar.scythemobile.model.playermat.SectionInstance
import org.ajar.scythemobile.model.production.*
import org.ajar.scythemobile.model.turn.CoercianTurnAction
import org.ajar.scythemobile.model.turn.Turn

open class AbstractPlayer(override val user: User, factionMat: FactionMatModel, playerMatModel: PlayerMatModel) : Player {

    override val factionMat = FactionMatInstance(factionMat)
    override val playerMat = PlayerMatInstance(playerMatModel)

    private var _power: Int = 0
    override var power: Int
        get() = _power
        set(value) {
            _power = when {
                value < 0 -> 0
                value >= 16 -> {
                    addStar(StarType.POWER)
                    16
                }
                else -> value
            }
        }

    private var _turn: Turn? = null
    override val turn: Turn
        get() {
            if(_turn == null) {
                _turn = Turn(this)
            }
            return _turn!!
        }

    val queuedCombatBoards: ArrayList<CombatBoard> = ArrayList()

    private var _lastTurn: Turn? = null
    override val lastTurn: Turn?
        get() = _lastTurn

    final override val combatCards: MutableList<CombatCard> = ArrayList()
    override val deployedUnits: MutableList<GameUnit> = ArrayList()

    private var _popularity: Int = 0
    override var popularity: Int
        get() = _popularity
        set(value) {
            _popularity = when {
                value < 0 -> 0
                value >= 18 -> {
                    addStar(StarType.POPULARITY)
                    18
                }
                else -> value
            }
        }

    private var _coins: Int = 0
    override var coins: Int
        get() = _coins
        set(value) {
            _coins = value
        }

    override val stars: HashMap<StarModel, Int> = HashMap()

    override var addStar: (starType: StarModel) -> Unit = { starType: StarModel -> addNewStar(starType) }
    override var promptForPayment: (cost: List<ResourceType>, map: MutableMap<Resource, MapHex>) -> Collection<Resource>? = { cost, map -> promptForPaymentSelection(cost, map) }
    override var doEncounter: (encounter: EncounterCard, unit: GameUnit) -> Unit = { encounter: EncounterCard, unit: GameUnit -> resolveEncounter(encounter, unit) }
    override var selectableSections: () -> List<SectionInstance> = { findSelectableSections() }

    override fun getStarCount(starType: StarModel): Int = stars[starType]?: 0
    override fun addStar(starType: StarModel) = addStar.invoke(starType)

    private fun addNewStar(starType: StarModel) {
        when(getStarCount(starType)) {
            0 -> stars[starType] = 1
            starType.limit -> return
            else -> stars[starType]!!.plus(1)
        }
    }

    final override val objectives: MutableList<Objective> = ArrayList()

    override var tokens: MutableList<out GameUnit>? = null
    override var tokenPlacementChoice: Choice? = null

    init {
        _power = factionMat.initialPower
        _popularity = playerMatModel.initialPopularity
        _coins = playerMatModel.initialCoins

        for (i in 0..factionMat.initialCombatCards) {
            combatCards.add(drawCombatCard())
        }

        for (i in 0..playerMatModel.initialObjectives) {
            objectives.add(selectObjective())
        }

        initializeFaction()
    }

    private fun initializeFaction() {
        factionMat.model.initializePlayer(this)
    }

    override fun newTurn() {
        _lastTurn = turn
        _turn = Turn(this)
        //TODO: Make sure combat is resolved by this point.
        queuedCombatBoards.clear()
    }

    override fun finalizeTurn(): List<String> {
        return turn.finalizeTurn()
    }

    override fun selectableSections() = selectableSections.invoke()
    private fun findSelectableSections() : List<SectionInstance> {
        return playerMat.sections.filter { sectionInstance -> sectionInstance.sectionDef.sectionSelectable(this) }
    }

    private fun countResources(cost: ResourceType): Int {
        return when(cost) {
            is MapResourceType -> selectResource(cost).size
            PlayerResourceType.COMBAT_CARD -> combatCards.size
            PlayerResourceType.POPULARITY -> popularity
            PlayerResourceType.COIN -> coins
            PlayerResourceType.POWER -> power
            else -> throw IllegalArgumentException("Unknown resource type '$cost'")
        }
    }

    // You need to run canPay before this to ensure that you've got enough non-map resources to make it work.
    override fun payResources(cost: List<ResourceType>): Boolean {
        val map = HashMap<Resource, MapHex>()
        val mapResourceCost = cost.filter { it is MapResourceType }.map { it as MapResourceType }
        mapResourceCost.flatMap { selectResource(it).entries }.toMutableSet().forEach { map[it.key as MapResource] = it.value}

        val resourcesPaid = if(mapResourceCost.isNotEmpty()) {
            val selection = promptForPayment.invoke(cost, map)

            if(paymentAccepted(selection, cost.filter { it is MapResourceType })) {
                selection?.all {
                    when(it) {
                        is CrimeaCardResource -> combatCards.remove(it.card)
                        else -> if(map[it]?.unitsPresent?.any { unit -> unit.heldMapResources.remove(it) } == false) {
                            map[it]?.heldMapResources?.remove(it)!!
                        } else {
                            true
                        }
                    }
                } == true
            } else false
        } else {
            true
        }

        if (resourcesPaid) {
            cost.filter { it is PlayerResourceType }.forEach {
                when(it) {
                    PlayerResourceType.POWER -> power--
                    PlayerResourceType.COIN -> coins--
                    PlayerResourceType.POPULARITY -> popularity--
                    PlayerResourceType.COMBAT_CARD -> {
                        combatCards.remove(user.requester?.requestChoice(PayCombatCardChoice(), combatCards))
                    }
                }
            }
        }

        return resourcesPaid
    }

    override fun canPay(cost: List<ResourceType>): Boolean {
        val resourceRequestMap = HashMap<ResourceType, Int>()

        cost.forEach {
            if(!resourceRequestMap.containsKey(it)) {
                resourceRequestMap[it] = 0
            }
            resourceRequestMap[it] = resourceRequestMap[it]!! + 1
        }

        var crimeaUsed = false
        resourceRequestMap.forEach{ (resourceType: ResourceType, count: Int) ->
            val available = countResources(resourceType)

            if(available < count) {
                if(factionMat.model == FactionMat.CRIMEA && !crimeaUsed && (available == count - 1)) {
                    crimeaUsed = true
                } else {
                    return false
                }
            }
        }
        return true
    }

    override fun selectUnits(type: UnitType) : List<GameUnit> {
        return deployedUnits.filter { it.type == type }
    }

    override fun selectInteractableWorkers(): List<GameUnit> {
        return selectUnits(UnitType.WORKER).filter { GameMap.currentMap!!.locateUnit(it)!!.desc.mapFeature.none { mapFeature -> mapFeature is HomeBase }}
    }

    override fun doEncounter(encounter: EncounterCard, unit: GameUnit) = doEncounter.invoke(encounter, unit)
    private fun resolveEncounter(encounter: EncounterCard, unit: GameUnit) {
        val encounterOutcome= user.requester?.requestChoice(EncounterChoice(), encounter.outcomes)

        if(encounterOutcome != null && encounterOutcome.canMeetCost(unit.controllingPlayer)) {
            encounterOutcome.applyOutcome(unit)
        }
    }

    private fun selectResource(typeMap: ResourceType) : MutableMap<in Resource, MapHex> {
        val collection = HashMap<Resource, MapHex>()
        var currentHex: MapHex?

        deployedUnits.forEach { unit ->
            currentHex = GameMap.currentMap!!.locateUnit(unit)
            if (currentHex != null) {
                unit.heldMapResources.filter { it.type == typeMap }.forEach { collection[it] = currentHex!! }
                currentHex?.heldMapResources?.filter { it.type == typeMap }?.forEach { collection[it] = currentHex!! }
            }
        }

        return collection
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

    private fun promptForPaymentSelection(cost: List<ResourceType>, map: Map<Resource, MapHex>) : Collection<Resource>? {
        val selected = user.requester?.requestPayment(PaymentChoice(), cost, map)

        //Here we preview whether or not payment will be accepted so we don't have to remove the action if it isn't.
        return if(paymentAccepted(selected, cost)) {
            if(selected?.firstOrNull { it::class == CrimeaCardResource::class } != null) {
                turn.performAction(CoercianTurnAction(this))
            }

            selected
        } else {
            null
        }
    }

    override fun queueCombat(combatBoard: CombatBoard) {
        queuedCombatBoards.removeIf { board -> board.combatHex == combatBoard.combatHex }
        queuedCombatBoards.add(combatBoard)
    }

    override fun doCombat() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun selectObjective() : Objective {
            return ObjectiveCardDeck.currentDeck.drawCard()
        }
        fun drawCombatCard() : CombatCard {
            return CombatCardDeck.currentDeck.drawCard()
        }
    }
}

interface Player {

    val user: User
    val factionMat: FactionMatInstance
    var power: Int
    val combatCards: MutableList<CombatCard>

    val deployedUnits: MutableList<GameUnit>

    val playerMat: PlayerMatInstance
    var popularity: Int
    var coins: Int
    val objectives: MutableList<Objective>

    var tokens: MutableList<out GameUnit>?
    var tokenPlacementChoice: Choice?

    val stars: HashMap<StarModel, Int>

    val turn: Turn
    val lastTurn: Turn?

    var addStar: (starType: StarModel) -> Unit
    var doEncounter: (encounter: EncounterCard, unit: GameUnit) -> Unit
    var selectableSections: () -> List<SectionInstance>
    var promptForPayment: (cost: List<ResourceType>, map: MutableMap<Resource, MapHex>) -> Collection<Resource>?

    fun getStarCount(starType: StarModel): Int
    fun addStar(starType: StarModel)
    fun newTurn()
    fun finalizeTurn(): List<String>

    fun selectableSections(): List<SectionInstance>

    fun canPay(cost: List<ResourceType>): Boolean
    fun payResources(cost: List<ResourceType>) : Boolean

    fun selectUnits(type: UnitType) : List<GameUnit>
    fun selectInteractableWorkers() : List<GameUnit>

    fun doEncounter(encounter: EncounterCard, unit: GameUnit)
    fun queueCombat(combatBoard: CombatBoard)
    fun doCombat()
}
