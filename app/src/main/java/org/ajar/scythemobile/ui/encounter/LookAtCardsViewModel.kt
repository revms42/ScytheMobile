package org.ajar.scythemobile.ui.encounter

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.model.CapitalResourceType
import org.ajar.scythemobile.data.PlayerData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.turn.TurnHolder

class LookAtCardsViewModel : ViewModel() {

    var amount: Int? = null
    var chosenPlayer: Int? = null

    val playerChoices: List<PlayerData>
        get() = ScytheDatabase.playerDao()?.getPlayers()?.filter { it.id != TurnHolder.currentPlayer.playerId }?: emptyList()

    fun getCards(): List<CombatCard> {
        return ScytheDatabase.resourceDao()?.getOwnedResourcesOfType(chosenPlayer!!, listOf(CapitalResourceType.CARDS.id))?.map { CombatCard(it) }?: emptyList()
    }

    fun reset() {
        amount = null
        chosenPlayer = null
    }
}