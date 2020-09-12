package org.ajar.scythemobile.ui.factory

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.Resource

class ResourceChoicePaymentViewModel : ViewModel() {

    var cost: List<Resource>? = null
    var reward: List<Resource>? = null

    val chosen = ArrayList<Resource>()
    val availableChoices: List<Resource>
        get() = TODO("Pull in the right resources")

    fun performExchange() {
        TODO("Verify that the cost has been paid and you ")
        TODO("Give the reward resources.")
    }
}