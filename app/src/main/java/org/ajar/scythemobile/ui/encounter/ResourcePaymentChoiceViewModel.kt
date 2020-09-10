package org.ajar.scythemobile.ui.encounter

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.turn.TurnHolder

class ResourcePaymentChoiceViewModel : ViewModel() {

    var resourceType: Int? = null
    var amount: Int? = null

    val resourcesSelected = ArrayList<ResourceData>()
    val resourcesAvailable: List<ResourceData>?
        get() = resourceType?.let { type -> with(TurnHolder.currentPlayer) { this.factionMat.factionMat.controlledResource(this, type) } }

    fun payResources(): Boolean {
        return if(resourcesSelected.size == amount) {
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