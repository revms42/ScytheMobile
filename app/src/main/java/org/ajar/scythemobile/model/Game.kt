package org.ajar.scythemobile.model

import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.objective.ObjectiveCardDeck
import org.ajar.scythemobile.model.turn.Turn
import org.ajar.scythemobile.model.turn.TurnUpdate

class Game {
    val players: List<Player> = ArrayList() //TODO: Temp for compile
    private var _turn: Int = 0
    val turn: Int
        get() = _turn
    val turnsTaken: List<Turn> = ArrayList() //TODO: Temp for compile

    val objectiveCardDeck: ObjectiveCardDeck = ObjectiveCardDeck.currentDeck //TODO: Temp for compile
    val combatCardDeck: CombatCardDeck = CombatCardDeck.currentDeck //TODO: Temp for compile
    //val encounterCardDeck: EncounterCardDeck = EncounterCardDeck.currentDeck //TODO: Temp for compile

    val currentPlayer: Player = players[0] //TODO: Temp for compile

    fun applyTurnUpdate(string: String) {
        TurnUpdate.deserialize(string).updateGame()
        _turn++
    }

    fun produceTurnUpdate() : String {
        return TurnUpdate(currentPlayer).serialize()
    }

    fun serialize() : String {
        TODO("Serialize")
    }

    companion object {
        private var _currentGame: Game? = null
        val currentGame: Game
            get() = _currentGame!!

        fun deserialize(string: String) : Game {
            TODO("Deserialize")
        }

        fun load(serializedGame: String) {
            _currentGame = deserialize(serializedGame)
        }

        fun new(players: List<Player>) {
            TODO("New game")
        }
    }
}