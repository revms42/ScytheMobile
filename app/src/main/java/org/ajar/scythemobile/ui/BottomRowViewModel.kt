package org.ajar.scythemobile.ui

import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.Resource
import org.ajar.scythemobile.model.player.BottomRowAction

abstract class BottomRowViewModel<A: BottomRowAction> : ViewModel() {
    abstract val action: A

    val cost: List<Resource>
        get() = action.cost

    val navigateOut: Int
        get() = action.actionOutOf
}