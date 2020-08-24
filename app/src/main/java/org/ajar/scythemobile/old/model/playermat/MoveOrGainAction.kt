package org.ajar.scythemobile.old.model.playermat

import org.ajar.scythemobile.old.model.*
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.old.model.entity.Player
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.MovementRule
import org.ajar.scythemobile.model.faction.Speed
import org.ajar.scythemobile.model.faction.StandardMove
import org.ajar.scythemobile.old.model.map.GameMap
import org.ajar.scythemobile.old.model.map.MapHex
import org.ajar.scythemobile.old.model.production.ResourceType
import org.ajar.scythemobile.old.model.turn.GainTurnAction
import org.ajar.scythemobile.old.model.turn.MoveTurnAction
import org.ajar.scythemobile.old.model.turn.TransportResourcesAction
import org.ajar.scythemobile.old.model.turn.TurnAction

class MoveOrGainAction(
        override val name: String = "Move/Gain Action",
        override val image: Int = -1,
        override val cost: List<ResourceType> = emptyList(),
        numberOfUnitsStart: Int = 2,
        numberOfCoinsStart: Int = 1,
        private val numberOfUnitsTop: Int = 3,
        private val numberOfCoinsTop: Int = 2

) : TopRowAction {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(MoveTurnAction::class.java, GainTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(
                UpgradeDef("Upgrade Number of Units to Move", canUpgradeUnits, upgradeNumberOfUnits),
                UpgradeDef("Upgrade Number of Coins to Gain", canUpgradeCoins, upgradeNumberOfCoins)
        )
    private var _numberOfUnits: Int = numberOfUnitsStart
    private val numberOfUnits: Int
        get() = _numberOfUnits

    private var _numberOfCoins: Int = numberOfCoinsStart
    private val numberOfCoins: Int
        get() = _numberOfCoins

    private val canUpgradeUnits: () -> Boolean = {numberOfUnits < numberOfUnitsTop}
    private val canUpgradeCoins: () -> Boolean = {numberOfCoins < numberOfCoinsTop}

    private val upgradeNumberOfUnits: () -> Unit = {_numberOfUnits++}
    private val upgradeNumberOfCoins: () -> Unit = {_numberOfCoins++}

    private fun performMoveOrGain(player: Player) {
        val move = player.user.requester?.requestBinaryChoice(PredefinedBinaryChoice.MOVE_OR_GAIN_SELECTION)

        if(move == true) {
            val max = if(player.factionMat.unlockedMechAbility.any { it is Speed } ) {
                2
            } else {
                1
            }

            performMove(player, numberOfUnits, max)
        } else {
            performGain(player)
        }
    }

    private fun performGain(player: Player) {
        player.coins += numberOfCoins
    }

    override var canPerform: (player: Player) -> Boolean = { _: Player -> true }
    override var performAction: (player: Player) -> Unit = { player: Player -> performMoveOrGain(player) }

    companion object {
        //TODO: Note that it's not dealing with airships.
        fun performMove(player: Player, unitsAllowed: Int, maxDistance: Int): Boolean {
            val choices = player.deployedUnits.filter { it.type != UnitType.STRUCTURE }.map { Pair(it, GameMap.currentMap?.locateUnit(it)) }.toMutableList()

            var unitsMoved = 0
            while(unitsMoved < unitsAllowed) {
                val selection = player.user.requester?.requestCancellableChoice(MoveUnitChoice(), choices)

                if(selection != null) {
                    moveSelectedUnit(selection.first, maxDistance)

                    if(player.turn.findActionOfType(MoveTurnAction::class.java).lastOrNull { it.unit === selection.first } != null) {
                        unitsMoved++
                        choices.remove(selection)
                    }
                } else {
                    return if(unitsMoved == 0) {
                        false
                    } else {
                        if (player.user.requester?.requestBinaryChoice(PredefinedBinaryChoice.ABORT_MOVEMENT) == true) {
                            if(player.user.requester?.requestBinaryChoice(PredefinedBinaryChoice.END_MOVEMENT) == true) {
                                false
                            } else {
                                break
                            }
                        } else {
                            continue
                        }
                    }
                }
            }
            return true
        }

        private fun moveSelectedUnit(unit: GameUnit, maxDistance: Int) {
            var starting = GameMap.currentMap?.locateUnit(unit)!!
            var moveDone: Boolean?  = false

            while(moveDone == false) {
                val choices = findValidDestinations(unit, starting, unit.controllingPlayer.factionMat.getMovementAbilities(unit.type))
                moveDone = moveUnit(unit, starting, choices)

                // If move done is true, break and do the next unit. If move done is false, see if you've run out of movement for this unit.
                // If move done is null, you aborted, try again.
                val turns = unit.controllingPlayer.turn.findActionOfType(MoveTurnAction::class.java).filter { it.unit === unit }

                turns.lastOrNull()?.also { moveTurn(it) }.also { _ ->
                    unit.controllingPlayer.turn.findActionOfType(TransportResourcesAction::class.java).findLast { it.unit === unit }?.also { executeTrasportResources(it) }
                }?.also { starting = it.to }

                if (moveDone == false && turns.size >= maxDistance){
                    moveDone = true
                }
            }
        }

        private fun findValidDestinations(unit: GameUnit, starting: MapHex, rules: Collection<MovementRule>): Map<MapHex, MovementRule> {
            val allResults = HashMap<MapHex, MovementRule>()
            rules.filter { it.canUse(unit.controllingPlayer) }.forEach { mr ->
                mr.validEndingHexes(starting)?.forEach { dest ->
                    dest?.let {
                        if(unit.type != UnitType.WORKER || !it.willMoveProvokeFight()) { // Prefilter for workers.
                            when {
                                !allResults.containsKey(it) -> allResults[it] = mr
                                allResults[it] !is StandardMove && mr is StandardMove -> allResults[it] = mr
                            }
                        }
                    }
                }
            }
            return allResults
        }

        private fun moveUnit(unit: GameUnit, from: MapHex, destinations: Map<MapHex, MovementRule>): Boolean? {
            val player = unit.controllingPlayer
            val requester = player.user.requester
            val to = requester?.requestCancellableChoice(MovementChoice(), destinations.keys)

            var movementEnds = to?.willMoveProvokeFight()
            if(to != null) {
                var transported: Collection<GameUnit> = listOf()

                when(unit.type) {
                    UnitType.MECH -> {
                        val workers = from.unitsPresent.filter { it.type == UnitType.WORKER }

                        if(workers.isNotEmpty()) {
                            transported = requester.requestSelection(MoveWorkersChoice(), workers)
                        }
                    }
                    UnitType.WORKER -> {
                        movementEnds = true //workers never move more than one on their own.
                    }
                    else -> {}
                }

                val resources = from.heldMapResources

                if(resources.isNotEmpty()) {
                    val loading = requester.requestSelection(LoadResourcesChoice(), resources)

                    if(loading.isNotEmpty()) {
                        from.heldMapResources.removeAll(loading)
                        unit.heldMapResources.addAll(loading)
                    }
                }

                player.turn.performAction(MoveTurnAction(unit, from, to, transported.toList(), destinations[to]!!))
                // TODO("Pick this up at the end and *actually* perform it")
            } else {
                movementEnds = if(unit.controllingPlayer.user.requester?.requestBinaryChoice(PredefinedBinaryChoice.ABORT_DESTINATION) == true) {
                    true
                } else {
                    null
                }
            }

            return movementEnds
        }

        private fun moveTurn(move: MoveTurnAction) {
            val player = move.unit.controllingPlayer
            val units = executeMovement(move)

            val resources = units.flatMap { gameUnit -> gameUnit.heldMapResources }

            if(resources.isNotEmpty()) {
                val selected = player.user.requester?.requestSelection(UnloadResourcesChoice(), resources)

                selected?.also {
                    it.forEach {resource ->
                        units.first { unit -> unit.heldMapResources.contains(resource) }.heldMapResources.remove(resource)

                        move.to.heldMapResources.add(resource)
                    }
                    player.turn.performAction(TransportResourcesAction(move.unit, move.from, move.to, selected.toList()))
                }
            }
        }

        fun executeMovement(move: MoveTurnAction): Collection<GameUnit> {
            val units = listOf(move.unit, *move.transported.toTypedArray())

            move.to.moveUnitsInto(units)
            // For some reason this doesn't work....
            // move.from.unitsPresent.removeAll(units)
            units.forEach { move.from.unitsPresent.remove(it) }

            return units
        }

        fun executeTrasportResources(transport: TransportResourcesAction) {
            val resources = transport.transported

            transport.from.heldMapResources.removeAll(resources)
            transport.to.heldMapResources.addAll(resources)
        }
    }
}