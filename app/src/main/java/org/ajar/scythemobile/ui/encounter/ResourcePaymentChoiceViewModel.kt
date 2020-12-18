package org.ajar.scythemobile.ui.encounter

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.model.CapitalResourceType
import org.ajar.scythemobile.model.NaturalResourceType
import org.ajar.scythemobile.model.Resource
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.faction.DefaultFactionAbility
import org.ajar.scythemobile.turn.TurnHolder

class ResourcePaymentChoiceViewModel : ViewModel() {

    var cost: List<Resource>? = null

    val resourcesSelected: ArrayList<ResourceData?> = ArrayList()

    private var coercion = TurnHolder.currentPlayer.playerData.flagCoercion

    val resourcesAvailable: List<ResourceData>?
        get() = with(TurnHolder.currentPlayer) { this.factionMat.factionMat.controlledResource(this, cost!!.map { it.id } ) }


    val fullfilled: Boolean
        get() = resourcesSelected.filterNotNull().size == cost!!.size

    fun select(index: Int, resourceData: ResourceData) : String? {
        val type = cost!![index]

        if(resourcesSelected.contains(resourceData)) return "That resource has been previously selected"

        return if(resourceData.type == type.id) {
            resourcesSelected[index] = resourceData
            null
        } else if (type is NaturalResourceType && resourceData.type == CapitalResourceType.CARDS.id) {
            if (TurnHolder.currentPlayer.factionMat.defaultFactionAbility == DefaultFactionAbility.COERCION) {
                if (!coercion) {
                    resourcesSelected[index] = resourceData
                    coercion = true
                    null
                } else {
                    "Coercion can only be used once per turn"
                }
            } else {
                "Only Crimea can use Coercion for resources"
            }
        } else if(type == NaturalResourceType.ANY && Resource.valueOf(resourceData.type) is NaturalResourceType) {
            resourcesSelected[index] = resourceData
            null
        } else if(type == NaturalResourceType.ANY_DISSIMILAR && Resource.valueOf(resourceData.type) is NaturalResourceType) {
            if(resourcesSelected.none { it?.type == resourceData.type }) {
                resourcesSelected[index] = resourceData
                null
            } else {
                "Dissimilar resources cannot match each other"
            }
        } else {
            "Resource does not match"
        }
    }

    fun unselect(index: Int) {
        val type = cost!![index]

        val data = resourcesSelected[index]
        resourcesSelected[index] = null
        if(data?.type != type.id && data?.type == CapitalResourceType.CARDS.id) {
            coercion = TurnHolder.currentPlayer.playerData.flagCoercion
        }
    }

    fun payResources(): Boolean {
        return if(fullfilled) {
            TurnHolder.updateResource(*resourcesSelected.filterNotNull().filter {
                if(it.value != 1) {
                    CombatCardDeck.currentDeck.returnCard(CombatCard(it))
                    false
                } else {
                    it.loc = -1
                    it.owner = -1
                    true
                }
            }.toTypedArray())

            if(coercion) {
                TurnHolder.currentPlayer.playerData.flagCoercion = true
                TurnHolder.updatePlayer(TurnHolder.currentPlayer.playerData)
            }
            return true
        } else {
            false
        }
    }
}