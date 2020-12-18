package org.ajar.scythemobile.ui

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.model.Resource
import org.ajar.scythemobile.model.player.BottomRowAction

abstract class BottomRowViewModel<A: BottomRowAction> : ViewModel() {
    abstract val action: A

    val cost: List<Resource>
        get() = action.cost

    val navigateOut: Int
        get() = action.actionOutOf
}