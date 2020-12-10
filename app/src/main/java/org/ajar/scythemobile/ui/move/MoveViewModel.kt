package org.ajar.scythemobile.ui.move

import androidx.lifecycle.*
import org.ajar.scythemobile.data.CombatRecord
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.player.TopRowAction
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.view.MapViewModel
import org.ajar.scythemobile.ui.view.StandardSelectionModel

class MoveViewModel : ViewModel() {

    private val selectedUnits = MutableLiveData<Set<GameUnit>>()
    private val selectedResources = MutableLiveData<Set<ResourceData>>()
    private val selectedHex = MutableLiveData<MapHex>()

    private var startHex: MapHex? = null
    private var selected: Set<GameUnit>? = null
    private var resources: Set<ResourceData>? = null

    private lateinit var selectionModel: StandardSelectionModel.SelectUnitToMoveModel
    private lateinit var mapViewModel: MapViewModel

    val moveCompleted = MutableLiveData<Boolean>(false)
    val encounter = MutableLiveData<Pair<Int, Int>>()

    private var unitsToMove: Int = 0
    private var unitsMoved: Int = 0

    fun initialize(activity: ViewModelStoreOwner) {
        mapViewModel = ViewModelProvider(activity).get(MapViewModel::class.java)
        selectionModel = StandardSelectionModel.SelectUnitToMoveModel(selectedUnits, selectedResources, selectedHex)
        mapViewModel.setSelectionModel(selectionModel)

        unitsToMove = TurnHolder.currentPlayer.playerMat.findTopRowAction(TopRowAction.MoveOrGain::class.java)?.unitsMoved?: 2
    }

    fun setupObservers(activity: LifecycleOwner) {
        setupUnitObserver(activity)
        setupResourceObserver(activity)
        setupHexObserver(activity)
    }

    private fun setupUnitObserver(activity: LifecycleOwner) {
        selectedUnits.observe(activity) {
            selected = it
            mapViewModel.setSelectionModel(selectionModel.selectDestinationModel())
        }
    }

    private fun setupResourceObserver(activity: LifecycleOwner) {
        selectedResources.observe(activity) {
            resources = it
        }
    }

    private fun setupHexObserver(activity: LifecycleOwner) {
        selectedHex.observe(activity) { hex ->
            if(startHex == null) {
                startHex = hex
            } else {
                resources?.forEach { data -> data.loc = hex.loc }
                resources?.let { resource -> TurnHolder.updateResource(*resource.toTypedArray()) }
                selected?.forEach { unit -> unit.move(hex.loc) }

                ScytheAction.MoveUnitAction(selected!!.toList(), hex).perform()

                if(!TurnHolder.hasUnresolvedCombat() && hex.encounter != null) {
                    encounter.postValue(Pair(hex.loc, hex.encounter!!.id))
                }

                if(++unitsMoved >= unitsToMove) {
                    moveCompleted.postValue(true)
                    selectedHex.removeObservers(activity)
                    selectedResources.removeObservers(activity)
                    selectedUnits.removeObservers(activity)
                    mapViewModel.setSelectionModel(null)
                } else {
                    startHex = null
                    selected = null
                    resources = null
                    mapViewModel.setSelectionModel(selectionModel)
                }
            }
        }
    }
}