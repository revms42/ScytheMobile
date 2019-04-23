package org.ajar.scythemobile.model.entity

import org.ajar.scythemobile.model.*
import org.ajar.scythemobile.model.combat.CombatBoard
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.faction.FactionMatInstance
import org.ajar.scythemobile.model.faction.FactionMatModel
import org.ajar.scythemobile.model.map.EncounterCard
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.objective.Objective
import org.ajar.scythemobile.model.objective.ObjectiveCardDeck
import org.ajar.scythemobile.model.playermat.PlayerMatInstance
import org.ajar.scythemobile.model.playermat.PlayerMatModel
import org.ajar.scythemobile.model.playermat.SectionInstance
import org.ajar.scythemobile.model.production.*
import org.ajar.scythemobile.model.turn.CoercianTurnAction
import org.ajar.scythemobile.model.turn.Turn

class AbstractPlayer(override val user: User, factionMat: FactionMatModel, playerMatModel: PlayerMatModel) : Player {

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

    private var _lastTurn: Turn? = null
    override val lastTurn: Turn?
        get() = _lastTurn

    override val combatCards: MutableList<CombatCard> = ArrayList()
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

    override fun getStarCount(starType: StarModel): Int = stars[starType]?: 0
    override fun addStar(starType: StarModel) = addStar.invoke(starType)

    private fun addNewStar(starType: StarModel) {
        when(getStarCount(starType)) {
            0 -> stars[starType] = 1
            starType.limit -> return
            else -> stars[starType]!!.plus(1)
        }
    }

    override val objectives: MutableList<Objective> = ArrayList()

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
    }

    override fun newTurn() {
        _lastTurn = turn
        _turn = Turn(this)
    }

    override fun finalizeTurn(): List<String> {
        return turn.finalizeTurn()
    }


    override var selectableSections: () -> List<SectionInstance> = { findSelectableSections() }
    override fun selectableSections() = selectableSections.invoke()
    private fun findSelectableSections() : List<SectionInstance> {
        return playerMat.sections.filter { sectionInstance -> sectionInstance.sectionDef.sectionSelectable(this) }
    }

    override fun payResources(cost: List<ResourceType>): Boolean {
        val map = HashMap<Resource, MapHex>()
        cost.flatMap { selectResource(it).entries }.toMutableSet().forEach { map[it.key as MapResource] = it.value}

        val selection = promptForPaymentSelection(cost, map)

        return if(paymentAccepted(selection, cost)) {
            selection?.firstOrNull {
                when(it) {
                    is CrimeaCardResource -> !combatCards.remove(it.card)
                    else -> if(map[it]?.unitsPresent?.firstOrNull { unit -> unit.heldMapResources.remove(it) } == null) !map[it]?.heldMapResources?.remove(it)!! else true
                }
            } == null
        } else false
    }

    override fun canPay(cost: List<ResourceType>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun selectUnits(type: UnitType) : List<GameUnit> {
        return deployedUnits.filter { it.type == type }
    }

    override var doEncounter: (encounter: EncounterCard, unit: GameUnit) -> Unit = { encounter: EncounterCard, unit: GameUnit -> resolveEncounter(encounter, unit) }
    override fun doEncounter(encounter: EncounterCard, unit: GameUnit) = doEncounter.invoke(encounter, unit)
    private fun resolveEncounter(encounter: EncounterCard, unit: GameUnit) {
        val encounterOutcome= user.requester?.requestChoice(EncounterChoice(), encounter.outcomes)

        if(encounterOutcome != null && encounterOutcome.canMeetCost(unit.controllingPlayer)) {
            encounterOutcome.applyOutcome(unit)
        }
    }

    override var selectResources: (resourceType: ResourceType) -> MutableMap<in Resource, MapHex> = { resourceType:ResourceType -> selectResource(resourceType) }
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    val stars: HashMap<StarModel, Int>

    val turn: Turn
    val lastTurn: Turn?

    var addStar: (starType: StarModel) -> Unit
    var doEncounter: (encounter: EncounterCard, unit: GameUnit) -> Unit
    var selectableSections: () -> List<SectionInstance>
    var selectResources: (resourceType: ResourceType) -> MutableMap<in Resource, MapHex>

    fun getStarCount(starType: StarModel): Int
    fun addStar(starType: StarModel)
    fun newTurn()
    fun finalizeTurn(): List<String>

    fun selectableSections(): List<SectionInstance>

    fun canPay(cost: List<ResourceType>): Boolean
    fun payResources(cost: List<ResourceType>) : Boolean

    fun selectUnits(type: UnitType) : List<GameUnit>

    fun doEncounter(encounter: EncounterCard, unit: GameUnit)
    fun queueCombat(combatBoard: CombatBoard)
    fun doCombat()
}
