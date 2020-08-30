package org.ajar.scythemobile.old.model.turn

import org.ajar.scythemobile.old.model.Game
import org.ajar.scythemobile.old.model.entity.Player

class TurnUpdate(val player: Player) {

    val turns: List<Turn> = Game.currentGame.turnsTaken.takeLast(Game.currentGame.players.size)

    fun updateGame() {
        turns.forEach { turn -> turn.replayTurn() }
    }

    fun serialize(): String {
        TODO("Serialization")
    }

    companion object {
        fun deserialize(string: String): TurnUpdate {
            TODO("Deserialization")
        }
    }
}