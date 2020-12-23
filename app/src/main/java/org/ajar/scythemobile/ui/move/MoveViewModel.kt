package org.ajar.scythemobile.ui.move

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.Resource
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.player.TopRowAction
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.view.MapScreenViewModel
import org.ajar.scythemobile.ui.view.StandardSelectionModel

class MoveViewModel : MapScreenViewModel() {

    private val selectedUnits = MutableLiveData<Set<GameUnit>>()
    private val selectedResources = MutableLiveData<Set<ResourceData>>()
    private val selectedHex = MutableLiveData<MapHex>()

    private var startHex: MapHex? = null
    private var selected: Set<GameUnit>? = null
    private var resources: Set<ResourceData>? = null

    private lateinit var selectionModel: StandardSelectionModel.SelectUnitToMoveModel

    val moveCompleted = MutableLiveData<Boolean>(false)
    val encounter = MutableLiveData<Pair<Int, Int>>()

    private var unitsToMove: Int = 0
    private var unitsMoved: Int = 0

    override fun initialize(activity: ViewModelStoreOwner) {
        super.initialize(activity)
        selectionModel = StandardSelectionModel.SelectUnitToMoveModel(selectedUnits, selectedResources, selectedHex)
        mapViewModel.setSelectionModel(selectionModel)

        unitsToMove = TurnHolder.currentPlayer.playerMat.findTopRowAction(TopRowAction.MoveOrGain::class.java)?.unitsMoved?: 2
    }

    fun <T> setupObservers(activity: T) where T : Context, T : LifecycleOwner{
        setupUnitObserver(activity)
        setupResourceObserver(activity)
        setupHexObserver(activity)
    }

    private fun setupUnitObserver(activity: LifecycleOwner) {
        selectedUnits.observe(activity) {
            selected = it.also { Log.e("ObserveUnits", "$it") }
        }
    }

    private fun setupResourceObserver(activity: LifecycleOwner) {
        selectedResources.observe(activity) {
            resources = it
        }
    }

    private fun <T> setupHexObserver(activity: T) where T : Context, T : LifecycleOwner {
        selectedHex.observe(activity) { hex ->
            if(startHex == null) {
                startHex = hex

                if(selected != null) {
                    val selectedUnits = HashSet<GameUnit>()
                    val transportRes = MutableLiveData<List<ResourceData>?>()
                    transportRes.observe(activity) {
                        selectedResources.postValue(it?.toSet()?: emptySet())
                        this@MoveViewModel.selectedUnits.value = selectedUnits
                        handleSelection()
                    }

                    val transportDialog = selectTransportedResources(activity, transportRes)

                    if(selected!!.size > 1) {
                        val movingUnit = MutableLiveData<GameUnit?>()
                        val moveDialog = selectMovingUnit(activity, movingUnit)

                        movingUnit.observe(activity) {
                            if(it != null) {
                                selectedUnits.add(it)

                                if (it.type == UnitType.MECH && selected!!.any { unit -> unit.type == UnitType.WORKER }) {
                                    val ridingUnits = MutableLiveData<List<GameUnit>?>()
                                    val ridingDialog = selectRidingUnits(activity, ridingUnits)

                                    ridingUnits.observe(activity) { riders ->
                                        if (riders?.isNotEmpty() == true) {
                                            selectedUnits.addAll(riders)
                                        }

                                        if(resources?.isNotEmpty() == true) {
                                            transportDialog.show()
                                        } else {
                                            this@MoveViewModel.selectedUnits.value = selectedUnits
                                            handleSelection()
                                        }
                                    }

                                    ridingDialog.show()
                                } else {
                                    if(resources?.isNotEmpty() == true) {
                                        transportDialog.show()
                                    } else {
                                        this@MoveViewModel.selectedUnits.value = selectedUnits
                                        handleSelection()
                                    }
                                }
                            } else {
                                handleCancel()
                            }
                        }

                        moveDialog.show()
                    } else {
                        selectedUnits.add(selected!!.first())
                        if(resources?.isNotEmpty() == true) {
                            transportDialog.show()
                        } else {
                            this@MoveViewModel.selectedUnits.value = selectedUnits
                            handleSelection()
                        }
                    }
                }
            } else {
                resources?.forEach { data -> data.loc = hex.loc }
                resources?.let { resource -> TurnHolder.updateResource(*resource.toTypedArray()) }
                //selected?.forEach { unit -> unit.move(hex.loc) }

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
                    selected?.also { selectionModel.filteredUnits.addAll(it.map { unit -> unit.id }) }
                    startHex = null
                    selected = null
                    resources = null
                    mapViewModel.setSelectionModel(selectionModel)
                }
            }
        }
    }

    private fun selectMovingUnit(context: Context, postUnit: MutableLiveData<GameUnit?>): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_choose_unit_to_move)
//        builder.setMessage(R.string.msg_choose_unit_to_move)

        val selectableTypes = selected!!.map { it.type }.toSet().sortedBy { it.ordinal }
        return builder.setItems(selectableTypes.map {
            // TODO: Replace with a custom view that has the icons instead.
            if(it == UnitType.CHARACTER) {
                TurnHolder.currentPlayer.factionMat.factionMat.heroCharacter.characterName
            } else {
                it.name.toLowerCase().capitalize()
            }
        }.toTypedArray()) { _, which ->
            postUnit.postValue(selected!!.firstOrNull { it.type == selectableTypes[which] })
        }.create()
    }

    private fun selectRidingUnits(context: Context, postUnit: MutableLiveData<List<GameUnit>?>): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_choose_unit_to_ride)
//        builder.setMessage(R.string.msg_choose_unit_to_ride)

        val selectableWorkers = selected!!.filter { it.type == UnitType.WORKER }
        var count = 0
        return builder.setMultiChoiceItems(selectableWorkers.map {
            // TODO: This is a little silly. Replace with a spinner.
            it.type.name.toLowerCase().capitalize()
        }.toTypedArray(), null) { _, _, isChecked ->
            if(!isChecked) {
                count--
            } else {
                count++
            }
        }.setPositiveButton(R.string.button_select_riders) { _, _ ->
            postUnit.postValue(selectableWorkers.subList(0,count))
        }.setNegativeButton(R.string.button_cancel) { _, _ ->
            postUnit.postValue(null)
        }.create()
    }

    private fun selectTransportedResources(context: Context, postResources: MutableLiveData<List<ResourceData>?>): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_choose_resources_to_transport)
//        builder.setMessage(R.string.msg_choose_resources_to_transport)

        val selected = HashSet<ResourceData>()
        return builder.setMultiChoiceItems(resources!!.map {
            // TODO: This is a little silly. Replace with spinners.
            Resource.valueOf(it.type)!!.displayName
        }.toTypedArray(), null) { _, which, isChecked ->
            val element = resources!!.elementAt(which)
            if(selected.contains(element) && !isChecked) {
                selected.remove(element)
            } else if(!selected.contains(element) && isChecked){
                selected.add(element)
            }
        }.setPositiveButton(R.string.button_select_riders) { _, _ ->
            postResources.postValue(selected.toList())
        }.setNegativeButton(R.string.button_cancel) { _, _ ->
            postResources.postValue(null)
        }.create()
    }

    private fun handleCancel() {
        TODO("I CAN'T CANCEL YET!!!!")
    }

    private fun handleSelection() {
        mapViewModel.setSelectionModel(selectionModel.selectDestinationModel())
    }
}