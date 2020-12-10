package org.ajar.scythemobile.ui.view

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.faction.FactionMat
import org.ajar.scythemobile.ui.control.MapFilterTab
import org.ajar.scythemobile.ui.control.MapFilterTab.Companion.paintResource
import org.ajar.scythemobile.ui.control.MapFilterTab.Companion.paintUnit

class MapViewModel : ViewModel() {

    private lateinit var mapView: MapView
    val selectedUnits: MutableLiveData<Set<GameUnit>> = MutableLiveData()
    val selectedResources: MutableLiveData<Set<ResourceData>> = MutableLiveData()

    private var selectionModel: MapSelectionModel? = null

    fun setMapView(mapView: MapView, lifecycleOwner: LifecycleOwner) {
        this.mapView = mapView
        mapView.paintUnit = ::paintUnit
        mapView.paintResource = ::paintResource

        if(selectionModel != null) {
            setSelectionModel(selectionModel)
        }

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

    fun setSelectionModel(model: MapSelectionModel?) {
        selectionModel = model
        if(::mapView.isInitialized) {
            if(model != null) {
                mapView.selectable = true
                mapView.hexHighLight = model::canSelect
                mapView.onHexSelect = model::onSelection
                invalidate()
            } else {
                mapView.selectable = false
                mapView.hexHighLight = null
                mapView.onHexSelect = null
                selectedUnits.postValue(emptySet())
                selectedResources.postValue(emptySet())
                invalidate()
            }
        }
    }

    private fun invalidate() = mapView.invalidate()
}
