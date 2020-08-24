package org.ajar.scythemobile.old.model.map

import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.TrapUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.old.model.PredefinedBinaryChoice
import org.ajar.scythemobile.old.model.combat.DefaultCombatBoard
import org.ajar.scythemobile.old.model.entity.*
import org.ajar.scythemobile.old.model.production.MapResource
import org.ajar.scythemobile.old.model.turn.DeployTokenTurnAction
import org.ajar.scythemobile.old.model.turn.ResetTrapAction
import org.ajar.scythemobile.old.model.turn.TrapSprungAction

class FactionHomeHex(desc: MapHexDesc, val player: Player?) : MapHex(desc)

open class MapHex(val desc: MapHexDesc) : ResourceHolder {

    val unitsPresent: ArrayList<GameUnit> = ArrayList()
    override val heldMapResources: MutableList<MapResource> = ArrayList()
    var encounterCard: EncounterCard? = null

    init {
        if(desc.mapFeature.contains(SpecialFeature.ENCOUNTER)) {
            encounterCard = EncounterDeck.currentDeck.drawCard()
        }
    }

    val playerInControl : Player?
        get() {
            return if(unitsPresent.isEmpty()) {
                null
            } else {
                findControllingUnit()?.controllingPlayer
            } //TODO: Flesh this out when airships come into play.
        }

    private fun findControllingUnit() : GameUnit? =
            unitsPresent.firstOrNull { it.type == UnitType.CHARACTER || it.type == UnitType.MECH || it.type == UnitType.WORKER }?:
            unitsPresent.firstOrNull { it.type == UnitType.STRUCTURE}

    fun moveUnitsInto(units: List<GameUnit>) {
        if(units.any { it.type == UnitType.CHARACTER || it.type == UnitType.MECH }) {
            moveInCombatUnits(units)
        } else {
            moveInMobileUnits(units)
        }
    }

    private fun doEncounterCheck(unit: GameUnit) {
        if(encounterCard != null) {
            unit.controllingPlayer.doEncounter(encounterCard!!, unit)
            encounterCard = null
        }
    }

    private fun moveInCombatUnits(units: List<GameUnit>) {
        if(playerInControl != units[0].controllingPlayer && willMoveProvokeFight()) {
            moveInMobileUnits(units)
            val combatBoard = DefaultCombatBoard(this, units[0].controllingPlayer, playerInControl!!)
            units[0].controllingPlayer.queueCombat(combatBoard)
        } else {
            moveInMobileUnits(units)
            resolveMove(units)
        }
    }

    private fun moveInMobileUnits(units: List<GameUnit>) {
        unitsPresent.addAll(units)

        unitsPresent.firstOrNull { it is TrapUnit && !it.sprung && units[0].controllingPlayer != it.controllingPlayer}?.let { triggerTrap(it as TrapUnit, units[0]) }
        // If there is combat going on it needs to happen before we resolve the move.
    }

    private fun triggerTrap(trap: TrapUnit, victim: GameUnit) {
        trap.springTrap(victim)
        victim.controllingPlayer.turn.performAction(TrapSprungAction(victim, trap))
    }

    fun resolveMove(units: List<GameUnit>) {
        units.firstOrNull { it.type == UnitType.CHARACTER }?.also { doEncounterCheck(it) }

        val player = units[0].controllingPlayer

        val token = unitsPresent.firstOrNull { it.type == UnitType.TRAP || it.type == UnitType.FLAG }
        if(token == null && player.tokens?.isNotEmpty() == true) {
            if(player.user.requester?.requestBinaryChoice(PredefinedBinaryChoice.PLACE_TOKEN) == true) {
                val selectedToken = player.user.requester?.requestCancellableChoice(player.tokenPlacementChoice!!, player.tokens!!)

                if(selectedToken != null) {
                    unitsPresent.add(selectedToken)
                    player.deployedUnits.add(selectedToken)
                    player.turn.performAction(DeployTokenTurnAction(selectedToken))
                }
            }
        } else {
            if(token is TrapUnit && token.controllingPlayer == player && token.sprung) {
                if(player.user.requester?.requestBinaryChoice(PredefinedBinaryChoice.RESET_TRAP) == true) {
                    token.resetTrap()
                    player.turn.performAction(ResetTrapAction(token))
                }
            }
        }
    }

    fun canUnitOccupy(unit: GameUnit) : Boolean {
        val controllingUnit = findControllingUnit()
        return if(controllingUnit != null && controllingUnit.type != UnitType.STRUCTURE) unit.controllingPlayer == playerInControl else true
    }

    fun willMoveProvokeFight() : Boolean {
        return unitsPresent.firstOrNull { it.type == UnitType.CHARACTER || it.type == UnitType.MECH } != null
    }

    @Deprecated("Currently no coverage")
    fun dropResource(unit: GameUnit, mapResource: MapResource) {
        if (unit.heldMapResources.remove(mapResource)) heldMapResources.add(mapResource)
    }

    fun dropAll(unit: GameUnit) {
        heldMapResources.addAll(unit.heldMapResources)
        unit.heldMapResources.clear()
    }

    @Deprecated("Currently no coverage")
    fun loadResource(unit: GameUnit, mapResource: MapResource) {
        if(heldMapResources.remove(mapResource)) unit.heldMapResources.add(mapResource)
    }

    private fun matchingNeighbors(riversBlock: Boolean, predicate: (MapFeature) -> Boolean) : List<MapHex?> {
        return Direction.values().map { direction ->  neighbor(direction, riversBlock) }.filter { neighbor -> neighbor?.desc?.mapFeature?.firstOrNull { feature -> predicate(feature) } != null }
    }

    private fun nonMatchingNeighbors(riversBlock: Boolean, predicate: (MapFeature) -> Boolean) : List<MapHex?> {
        return Direction.values().map { direction ->  neighbor(direction, riversBlock)}.filter { neighbor -> neighbor?.desc?.mapFeature?.none { feature -> predicate(feature) }?: false }
    }

    fun matchingNeighborsNoRivers(predicate: (MapFeature) -> Boolean) : List<MapHex?> = matchingNeighbors(true, predicate)
    fun matchingNeighborsIncludeRivers(predicate: (MapFeature) -> Boolean) : List<MapHex?> = matchingNeighbors(false, predicate)

    fun nonMatchingNeighborsNoRivers(predicate: (MapFeature) -> Boolean) : List<MapHex?> = nonMatchingNeighbors(true, predicate)
    fun nonMatchingNeighborsIncludeRivers(predicate: (MapFeature) -> Boolean) : List<MapHex?> = nonMatchingNeighbors(false, predicate)

    private fun neighbor(direction: Direction, riversBlock: Boolean = true) : MapHex? {
        if(riversBlock && desc.mapFeature.find { it is RiverFeature && it.direction == direction } != null) return null

        val index = when(direction) {
            Direction.NW -> desc.hexNeighbors.nw
            Direction.NE -> desc.hexNeighbors.ne
            Direction.E -> desc.hexNeighbors.e
            Direction.SE -> desc.hexNeighbors.se
            Direction.SW -> desc.hexNeighbors.sw
            Direction.W -> desc.hexNeighbors.w
        }

        return GameMap.currentMap?.findHexAtIndex(index)
    }

    override fun toString(): String {
        return "${desc.mapFeature[0]} ${desc.location}"
    }
}
