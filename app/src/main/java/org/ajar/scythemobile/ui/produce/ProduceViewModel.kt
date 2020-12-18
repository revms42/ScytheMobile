package org.ajar.scythemobile.ui.produce

import org.ajar.scythemobile.model.Resource
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.player.TopRowAction
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.TopRowViewModel

class ProduceViewModel : TopRowViewModel<TopRowAction.Produce>() {
    private var _action: TopRowAction.Produce? = null
    override val action: TopRowAction.Produce
        get() {
            if(_action == null) {
                _action = TurnHolder.currentPlayer.playerMat.findTopRowAction(TopRowAction.Produce::class.java)
            }
            return _action!!
        }

    val cost: List<Resource>
        get() = action.cost

    var returnNav: Int? = null
    var ignoreMill: Boolean? = null
    val navigateOut: Int
        get() = TurnHolder.currentPlayer.playerMat.findSection(action::class.java)!!.moveTopToBottom

    val availableHexes: Set<MapHex>
        get() = TurnHolder.currentPlayer.selectUnits(UnitType.WORKER)?.mapNotNull { GameMap.currentMap.findHexAtIndex(it.pos) }!!.filter { it.producesResource }.toSet()

    val hexSelection = HashSet<MapHex>()

    internal var _numberOfHexes: Int? = null
    val numberOfHexes: Int
        get() {
            if(_numberOfHexes == null) {
                _numberOfHexes = action.numberOfHexes
            }
            return _numberOfHexes!!
        }

    private fun getMillHex(): MapHex? {
        return ScytheDatabase.unitDao()?.getUnitsForPlayer(TurnHolder.currentPlayer.playerId, UnitType.MILL.ordinal)?.first().let { unitData ->
            GameMap.currentMap.findHexAtIndex(unitData!!.loc)?.let { hex ->
                if (hex.playerInControl == unitData.owner) hex else null
            }
        }
    }

    fun performProduce() {
        if(ignoreMill == false) getMillHex()?.also { hexSelection.add(it) }
        hexSelection.forEach { hex -> ScytheAction.ProduceAction(TurnHolder.currentPlayer, hex).perform() }
    }

    fun reset() {
        _numberOfHexes = null
        ignoreMill = null
        returnNav = null
        _action = null
        hexSelection.clear()
    }
}