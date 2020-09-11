package org.ajar.scythemobile.ui.encounter

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.Resource
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.turn.TurnHolder

class ResourcePaymentChoiceViewModel : ViewModel() {

    var cost: List<Resource>? = null

    val resourcesSelected = ArrayList<ResourceData>()
    val resourcesAvailable: List<ResourceData>?
        get() = with(TurnHolder.currentPlayer) { this.factionMat.factionMat.controlledResource(this, cost!!.map { it.id } ) }

    fun payResources(): Boolean {
        return if(resourcesSelected.size == cost!!.size) {
            TODO("Assert that you have enough of each")
            TurnHolder.updateResource(*resourcesSelected.filter {
                if(it.value != 1) {
                    CombatCardDeck.currentDeck.returnCard(CombatCard(it))
                    false
                } else {
                    it.loc = -1
                    true
                }
            }.toTypedArray())
            return true
        } else {
            false
        }
    }
}