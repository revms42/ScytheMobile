package org.ajar.scythemobile.model.objective

class ObjectiveCardDeck(private val objectives: MutableList<Objective>) {

    inner class NullObjective : Objective

    init {
        //TODO: Shuffle here.
    }

    fun drawCard() : Objective {
        return if (objectives.isEmpty()) {
            NullObjective()
        } else {
            objectives.removeAt(0)
        }
    }

    companion object {
        private var _currentDeck: ObjectiveCardDeck? = null
        val currentDeck: ObjectiveCardDeck
            get() {
                if(_currentDeck == null) {
                    //TODO: Here is where we would make sure to load the default.
                    _currentDeck = ObjectiveCardDeck(ArrayList())
                }
                return _currentDeck!!
            }
    }
}

interface Objective {

}
