package org.ajar.scythemobile.ui.choose

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.Resource
import org.ajar.scythemobile.ScytheMoble
import org.ajar.scythemobile.data.PlayerData
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.FactionAbility
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.turn.TurnHolder

class LookAtCardsViewModel : ViewModel() {

    var amount: Int? = null
    var chosenPlayer: Int? = null

    val playerChoices: List<PlayerData>
        get() = ScytheDatabase.playerDao()?.getPlayers()?.filter { it.id != TurnHolder.currentPlayer.playerId }?: emptyList()

    fun getCards(): List<CombatCard> {
        return ScytheDatabase.resourceDao()?.getOwnedResourcesOfType(CapitalResourceType.CARDS.id, chosenPlayer!!)?.map { CombatCard(it) }?: emptyList()
    }
}