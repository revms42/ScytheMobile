package org.ajar.scythemobile.model.combat

import kotlin.math.roundToInt

class CombatCardDeck(twos: Int = 16, threes: Int = 12, fours: Int = 8, fives: Int = 6, private val regenerative: Boolean = false) {

    private val cardList: MutableList<CombatCard> = ArrayList()

    init {
        for (i in 0..twos) {
            cardList.add(CombatCard(2))
        }
        for (i in 0..threes) {
            cardList.add(CombatCard(3))
        }
        for (i in 0..fours) {
            cardList.add(CombatCard(4))
        }
        for (i in 0..fives) {
            cardList.add(CombatCard(5))
        }

        for(i in 0..cardList.size) {
            val random = (Math.random() * cardList.size).toInt()

            cardList.add((Math.random() * cardList.size).toInt(), cardList.removeAt(random))
        }
    }

    fun drawCard(): CombatCard {
        val card = cardList.removeAt(0)
        if(regenerative) {
            cardList.add(CombatCard(card.power))
        }
        return card
    }

    companion object {
        private var _currentDeck: CombatCardDeck? = null
        val currentDeck: CombatCardDeck
            get() {
                if(_currentDeck == null) {
                    _currentDeck = CombatCardDeck()
                }
                return _currentDeck!!
            }
    }
}

class CombatCard(val power: Int)
