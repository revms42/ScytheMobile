package org.ajar.scythemobile.ui.view

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.model.faction.FactionMat
import org.ajar.scythemobile.ui.control.MapFilterTab
import org.ajar.scythemobile.ui.control.MapFilterTab.Companion.paintResource
import org.ajar.scythemobile.ui.control.MapFilterTab.Companion.paintUnit

class MapViewModel : ViewModel() {

    private lateinit var mapView: MapView

    fun setMapView(mapView: MapView, lifecycleOwner: LifecycleOwner) {
        this.mapView = mapView
        mapView.paintUnit = ::paintUnit
        mapView.paintResource = ::paintResource

        MapFilterTab.values().forEach {
            if(it == MapFilterTab.RESOURCES) {
                it.selected.postValue(NaturalResourceType.valueList.map { type -> type.id }.toMutableList())
                it.watch(lifecycleOwner, ::invalidate)
            } else {
                it.selected.postValue(FactionMat.list().map { type -> type.id }.toMutableList())
                it.watch(lifecycleOwner, ::invalidate)
            }
        }
    }

    private fun invalidate() = mapView.invalidate()
}
