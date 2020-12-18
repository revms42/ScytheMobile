package org.ajar.scythemobile.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

open class MapScreenViewModel : ViewModel() {

    lateinit var mapViewModel: MapViewModel

    open fun initialize(activity: ViewModelStoreOwner) {
        mapViewModel = ViewModelProvider(activity).get(MapViewModel::class.java)
    }
}