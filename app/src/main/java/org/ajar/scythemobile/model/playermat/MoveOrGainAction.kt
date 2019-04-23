package org.ajar.scythemobile.model.playermat

import org.ajar.scythemobile.model.MoveUnitChoice
import org.ajar.scythemobile.model.MoveWorkersChoice
import org.ajar.scythemobile.model.MovementChoice
import org.ajar.scythemobile.model.PredefinedBinaryChoice
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.DestinationSelection
import org.ajar.scythemobile.model.faction.MovementRule
import org.ajar.scythemobile.model.faction.Speed
import org.ajar.scythemobile.model.faction.StandardMove
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.production.ResourceType
import org.ajar.scythemobile.model.turn.GainTurnAction
import org.ajar.scythemobile.model.turn.MoveTurnAction
import org.ajar.scythemobile.model.turn.TurnAction

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

            performMove(numberOfUnits, max, player)
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
        private fun performSingleMove(unit: GameUnit) : DestinationSelection? {
            val rules = unit.controllingPlayer.factionMat.getMovementAbilities(unit.type)
            val ridingWorkers: MutableList<GameUnit> = mutableListOf()

            if (unit.type == UnitType.MECH) {
                val workers = GameMap.currentMap?.locateUnit(unit)!!.unitsPresent.filter { it.type == UnitType.WORKER }
                unit.controllingPlayer.user.requester?.requestSelection(MoveWorkersChoice(), workers)?.let { ridingWorkers.addAll(it) }
            }

            val starting = GameMap.currentMap?.locateUnit(unit)
            val allResults = HashMap<MapHex, MovementRule>()
            if (starting != null) {
                rules.filter { it.canUse(unit.controllingPlayer) }.forEach { mr ->
                    mr.validEndingHexes(starting)?.forEach { dest ->
                        dest?.let {
                            when {
                                !allResults.containsKey(it) -> allResults[it] = mr
                                allResults[it] !is StandardMove && mr is StandardMove -> allResults[it] = mr
                            }
                        }
                    }
                }
            }

            return unit.controllingPlayer.user.requester?.requestCancellableChoice(MovementChoice(), allResults.map { DestinationSelection(it.key, it.value, ridingWorkers) })
        }

        fun performMove(unitsAllowed: Int, maxDistance: Int, player: Player) : Boolean {
            val movements = ArrayList<MoveTurnAction>()

            var i = 0
            unitSelection@ while(i < unitsAllowed) {
                val choices = player.deployedUnits.filter { it.type != UnitType.STRUCTURE }.map { Pair(it, GameMap.currentMap?.locateUnit(it)) }

                val selection = player.user.requester?.requestCancellableChoice(MoveUnitChoice(), choices)

                if(selection != null) {
                    var j = 0
                    var previousDestination: DestinationSelection? = null

                    movementSelection@ while(j < maxDistance) {
                        val nextDestination = performSingleMove(selection.first)

                        if(nextDestination == null) {
                            if(previousDestination == null) {
                                break@unitSelection
                            } else {
                                if(player.user.requester?.requestBinaryChoice(PredefinedBinaryChoice.ABORT_DESTINATION) == true) {
                                    break@movementSelection
                                } else {
                                    break@unitSelection
                                }
                            }
                        } else {
                            if(!nextDestination.mapHex.canUnitOccupy(selection.first)) {
                                if(player.user.requester?.requestBinaryChoice(PredefinedBinaryChoice.END_DESTINATION) == true) {
                                    if(previousDestination == null){
                                        movements.add(MoveTurnAction(selection.first, selection.second!!, nextDestination.mapHex, nextDestination.ridingUnits, nextDestination.movementRule))
                                    } else {
                                        movements.add(MoveTurnAction(selection.first, previousDestination.mapHex, nextDestination.mapHex, nextDestination.ridingUnits, nextDestination.movementRule))
                                    }

                                    break@unitSelection
                                } else {
                                    break@movementSelection
                                }
                            } else {
                                if(previousDestination == null){
                                    movements.add(MoveTurnAction(selection.first, selection.second!!, nextDestination.mapHex, nextDestination.ridingUnits, nextDestination.movementRule))
                                } else {
                                    movements.add(MoveTurnAction(selection.first, previousDestination.mapHex, nextDestination.mapHex, nextDestination.ridingUnits, nextDestination.movementRule))
                                }

                                previousDestination = nextDestination
                                j++
                            }
                        }
                    }
                    i++
                } else {
                    return if(i == 0) {
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

            executeMovements(movements)

            return true
        }

        private fun executeMovements(moves: List<MoveTurnAction>) {
            val destinations = HashMap<MapHex, ArrayList<GameUnit>>()

            moves.forEach { move ->
                if(!destinations.containsKey(move.to)) {
                    destinations[move.to] = ArrayList()
                }
                destinations[move.to]!!.add(move.unit)
            }

            destinations.forEach { hex, units -> hex.moveUnitsInto(units) }
        }
    }
}