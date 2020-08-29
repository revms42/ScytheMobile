package org.ajar.scythemobile.old.model.map

import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.old.model.entity.Player

interface EncounterOutcome {
    val title: String
    val description: String

    fun applyOutcome(unit: GameUnit)
    fun canMeetCost(player: Player) : Boolean
}

interface EncounterCard {
    val outcomes: List<EncounterOutcome>
}

class EncounterDeck {

    private val deck: ArrayList<EncounterCard> = ArrayList()

    init {
        //TODO: Init the deck
    }

    fun drawCard() : EncounterCard? {
        return if (deck.isNotEmpty()) deck.removeAt((Math.random() * deck.size).toInt()) else null
    }

    companion object {
        private var _currentDeck: EncounterDeck? = null
        val currentDeck: EncounterDeck
            get() {
                if(_currentDeck == null) {
                    //TODO: Need to load the deck localized.
                    _currentDeck = EncounterDeck()
                }
                return _currentDeck!!
            }
    }
}
