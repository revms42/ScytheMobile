package org.ajar.scythemobile.ui.build

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import org.ajar.scythemobile.R
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.player.BottomRowAction
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.view.MapScreenViewModel
import org.ajar.scythemobile.ui.view.StandardSelectionModel

class BuildViewModel : MapScreenViewModel() {
    private var _action: BottomRowAction.Build? = null
    val action: BottomRowAction.Build
        get() {
            if(_action == null) {
                _action = TurnHolder.currentPlayer.playerMat.findBottomRowAction(BottomRowAction.Build::class.java)
            }
            return _action!!
        }
    val cost = action.cost
    var returnNav: Int? = null
    var unitType: Int? = null

    private val selectableStructures: List<GameUnit>
        get() = action.getBuildableStructures()?: emptyList()
    private val selectedStructureLiveData = MutableLiveData<GameUnit?>()
    var selectedStructure: GameUnit? = null

    private val selectedHexLiveData = MutableLiveData<MapHex>()
    var selectedHex: MapHex? = null

    fun canBuildStructure(requireResources: Boolean): Boolean {
        return TurnHolder.currentPlayer.canBuild(requireResources) && getValidLocations()?.size?:0 > 0
    }

    private fun getValidLocations(): List<MapHex>? {
        return ScytheDatabase.unitDao()?.getUnitsForPlayer(TurnHolder.currentPlayer.playerId, unitType!!)?.map { unit -> unit.loc }?.filter { it != -1 }?.mapNotNull { GameMap.currentMap.findHexAtIndex(it) }
    }

    fun <T> setupSelectBuildSite(activity: T, done: (Boolean) -> Unit): AlertDialog? where T: LifecycleOwner, T: Context {
        selectedHexLiveData.observe(activity) {
            selectedHex = it
            setupSelectStructureObserver(activity, done)?.show()
            selectedHexLiveData.removeObservers(activity)
        }
        return getValidLocations()?.let {
            if(it.size > 1) {
                val builder = AlertDialog.Builder(activity)
                builder.setTitle(R.string.title_choose_build_location)

                builder.setPositiveButton(R.string.button_ok) { _, _ ->
                    val selectionModel = StandardSelectionModel.SelectHexToBuildOnModel(it, selectedHexLiveData)
                    mapViewModel.setSelectionModel(selectionModel)
                }

                builder.create()
            } else {
                selectedHexLiveData.postValue(it[0])

                null
            }
        }
    }

    private fun selectStructure(context: Context, postUnit: MutableLiveData<GameUnit?>): AlertDialog? {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_choose_structure_to_build)

        return if(selectableStructures.size > 1) {
            builder.setItems(selectableStructures.map {
                // TODO: Replace with a custom view that has the icons instead.
                it.type.toString()
            }.toTypedArray()) { _, which ->
                postUnit.postValue(selectableStructures[which])
            }.create()
        } else {
            selectedStructure = selectableStructures[0]
            null
        }
    }

    fun <T> setupSelectStructureObserver(activity: T, done:(Boolean) -> Unit): AlertDialog? where T: LifecycleOwner, T: Context {
        selectedStructureLiveData.observe(activity) {
            selectedStructure = it
            done(performBuild(activity))
        }

        return selectStructure(activity, selectedStructureLiveData)
    }

    private fun performBuild(activity: LifecycleOwner): Boolean {
        return if(selectedStructure != null && selectedHex != null) {
            selectedStructureLiveData.removeObservers(activity)
            return ScytheAction.BuildStructure(selectedHex!!, selectedStructure!!).perform()
        } else {
            false
        }
    }

    fun reset() {
        returnNav = null
        unitType = null
        selectedHex = null
        selectedStructure = null
    }
}