package org.ajar.scythemobile.data

class TurnTestDataDAO : TurnDataDAO {
    private val turnData = ArrayList<TurnData>()

    override fun getTurns(): List<TurnData>? {
        return turnData
    }

    override fun getCurrentTurn(): TurnData? {
        return turnData.maxBy { it.turn }
    }

    override fun addTurn(vararg turn: TurnData) {
        turnData.addAll(turn.toList())
    }

    override fun removeTurn(vararg turn: TurnData) {
        turn.forEach { oldTurn -> turnData.remove(oldTurn) }
    }

    override fun updateTurn(vararg unit: TurnData) {
        unit.forEach { update -> this.turnData.removeIf { it.turn == update.turn }; this.turnData.add(update) }
    }
}