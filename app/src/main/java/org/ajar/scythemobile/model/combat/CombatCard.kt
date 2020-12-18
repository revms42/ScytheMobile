package org.ajar.scythemobile.model.combat

import org.ajar.scythemobile.model.CapitalResourceType
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.turn.TurnHolder

class CombatCardDeck(val twos: Int = 16, val threes: Int = 12, val fours: Int = 8, val fives: Int = 6) {

    private val cardList: MutableList<CombatCard> = ArrayList()

    fun init() {
        for (i in 0..twos) {
            cardList.add(CombatCard(ResourceData(0, -1, -1, CapitalResourceType.CARDS.id, 2)))
        }
        for (i in 0..threes) {
            cardList.add(CombatCard(ResourceData(0, -1, -1, CapitalResourceType.CARDS.id, 3)))
        }
        for (i in 0..fours) {
            cardList.add(CombatCard(ResourceData(0, -1, -1, CapitalResourceType.CARDS.id, 4)))
        }
        for (i in 0..fives) {
            cardList.add(CombatCard(ResourceData(0, -1, -1, CapitalResourceType.CARDS.id, 5)))
        }

        for(i in 0..cardList.size) {
            val random = (Math.random() * cardList.size).toInt()

            cardList.add((Math.random() * cardList.size).toInt(), cardList.removeAt(random))
        }

        ScytheDatabase.resourceDao()!!.addResource(*cardList.map { it.resourceData }.toTypedArray())
    }

    private fun update(combatCard: CombatCard) {
        TurnHolder.updateResource(combatCard.resourceData)
    }

    fun drawCard(playerInstance: PlayerInstance): CombatCard? {
        if(cardList.isEmpty()) return null

        val card = cardList.removeAt(0)
        card.resourceData.owner = playerInstance.playerId
        update(card)
        return card
    }

    fun returnCard(combatCard: CombatCard) {
        combatCard.resourceData.owner = -1
        cardList += combatCard
        update(combatCard)
    }

    fun cardsRemaining() : Int = cardList.size

    companion object {
        private var _currentDeck: CombatCardDeck? = null
        val currentDeck: CombatCardDeck
            get() {
                if(_currentDeck == null) {
                    _currentDeck = CombatCardDeck()

                    val cards = ScytheDatabase.resourceDao()?.getResourcesOfType(CapitalResourceType.CARDS.id)
                    if(cards != null && cards.isNotEmpty()) {
                        _currentDeck!!.cardList.addAll(cards.filter { it.owner == -1 }.map { cardData -> CombatCard(cardData) })
                    } else {
                        _currentDeck!!.init()
                    }
                }
                return _currentDeck!!
            }
    }
}

class CombatCard(val resourceData: ResourceData) {
    val power: Int
        get() = resourceData.value
}
