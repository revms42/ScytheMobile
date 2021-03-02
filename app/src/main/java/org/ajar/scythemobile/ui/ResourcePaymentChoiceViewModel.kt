package org.ajar.scythemobile.ui

import androidx.lifecycle.MutableLiveData
import org.ajar.scythemobile.model.CapitalResourceType
import org.ajar.scythemobile.model.NaturalResourceType
import org.ajar.scythemobile.model.Resource
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.faction.DefaultFactionAbility
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.view.MapScreenViewModel
import org.ajar.scythemobile.ui.view.StandardSelectionModel

class ResourcePaymentChoiceViewModel : MapScreenViewModel() {

    private var _cost: List<Resource>? = null
    var cost: List<Resource>?
        get() = _cost
        set(value) {
            _cost = value
            this.mapViewModel.setSelectionModel(StandardSelectionModel.SelectResourceOfTypeModel(::select, ::canSelect))
        }

    private val resourcesSelected: ArrayList<ResourceData> = ArrayList()

    private var coercion: Boolean
        set(value) {
            //TODO: Verify that this is setting the turn-data value of coercion
            TurnHolder.currentPlayer.playerData.flagCoercion = value
        }
        get() {
            return TurnHolder.currentPlayer.playerData.flagCoercion
        }

    private val resourcesAvailable: List<ResourceData>?
        get() = with(TurnHolder.currentPlayer) { this.factionMat.factionMat.controlledResource(this, cost?: emptyList())?.filter { !resourcesSelected.contains(it) } }

    val resourcesSelectedLD = MutableLiveData<List<Resource>>()

    private val fulfilled: Boolean
        get() = resourcesSelected.size == cost!!.size

    val fulfilledLD = MutableLiveData<Boolean>()

    private fun canSelect(resourceData: ResourceData) : Boolean {
        if(resourcesSelected.contains(resourceData) || fulfilled) return false
        if(resourcesAvailable?.contains(resourceData) != true) return false

        val resourcesMet = resourcesSelected.map { it.type }.toMutableList()
        val resourcesNeeded = cost!!.map { it.id }

        val remaining = resourcesNeeded.filter { needed ->
            val met = resourcesMet.indexOfFirst { it == needed }
            if(met != -1) {
                resourcesMet.remove(met)
                false
            } else {
                true
            }
        }
        val remainingTyped = remaining.filter { it != NaturalResourceType.ANY.id && it != NaturalResourceType.ANY_DISSIMILAR.id }
        val remainingAny = remaining.filter { it == NaturalResourceType.ANY.id }
        val remainingDissimilar = remaining.filter { it == NaturalResourceType.ANY_DISSIMILAR.id }

        val card = resourcesMet.contains(CapitalResourceType.CARDS.id)

        if(remainingTyped.isNotEmpty()) {
            if(remainingTyped.contains(resourceData.type)) {
                return true
            } else if(!card && TurnHolder.currentPlayer.factionMat.defaultFactionAbility == DefaultFactionAbility.COERCION &&
                    Resource.valueOf(resourceData.type) == CapitalResourceType.CARDS && !coercion
            ) {
                return true
            }
        }

        if(Resource.valueOf(resourceData.type) is NaturalResourceType) {
            if(remainingDissimilar.isNotEmpty() && resourcesMet.size < remainingDissimilar.size) {
                if(resourcesMet.firstOrNull { it == resourceData.type } == null) {
                    return true
                }
            }

            if(remainingAny.isNotEmpty() && resourcesMet.size < remainingAny.size) {
                return true
            }
        }

        return false
    }

    fun select(resourceData: ResourceData) {
        //Should only be called after something has been highlighted via the "canSelect" method.
        resourcesSelected.add(resourceData)
        if(Resource.valueOf(resourceData.type) == CapitalResourceType.CARDS) {
            coercion = true
        }
        resourcesSelectedLD.postValue(resourcesSelected.mapNotNull { Resource.valueOf(it.type) })
        fulfilledLD.postValue(fulfilled)
    }

    fun payResources(): Boolean {
        return if(fulfilled) {
            TurnHolder.updateResource(*resourcesSelected.filter {
                // The only resources with an actual value are combat cards
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

    fun resetPayment() {
        resourcesSelected.clear()
        coercion = TurnHolder.currentPlayer.playerData.flagCoercion
    }
}