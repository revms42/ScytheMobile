package org.ajar.scythemobile.ui.view

import androidx.lifecycle.MutableLiveData
import org.ajar.scythemobile.Resource
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.turn.TurnHolder

interface MapSelectionModel {
    fun canSelect(mapHex: MapHex): Boolean

    fun onSelection(mapHex: MapHex)
}

sealed class StandardSelectionModel : MapSelectionModel {

    class SelectUnitToMoveModel(private val selectedUnits: MutableLiveData<Set<GameUnit>>, private val selectedResources: MutableLiveData<Set<ResourceData>>, private val selectedHex: MutableLiveData<MapHex>) : StandardSelectionModel() {
        val filteredUnits = HashSet<Int>()

        override fun canSelect(mapHex: MapHex): Boolean {
            return GameMap.currentMap.unitsAtHex(mapHex.loc).any { UnitType.provokeUnits.contains(UnitType.valueOf(it.type)) && !filteredUnits.contains(it.id)} &&
                    mapHex.playerInControl == TurnHolder.currentPlayer.playerId
        }

        override fun onSelection(mapHex: MapHex) {
            val moveableUnits =
                    ScytheDatabase.unitDao()?.getSpecificUnitsAtLoc(mapHex.loc, TurnHolder.currentPlayer.playerId, UnitType.provokeUnits.map { it.ordinal })?.toMutableList()

            moveableUnits?.removeIf { filteredUnits.contains(it.id) }

            val resourcesPresent =
                    ScytheDatabase.resourceDao()?.getResourcesAt(mapHex.loc)

            if(moveableUnits?.size == 1) {
                selectedHex.postValue(mapHex)
                if(resourcesPresent?.size == 0) {
                    // Easy case: One unit, no resources, e.g. no sub-selection
                    selectedResources.postValue(emptySet())
                    selectedUnits.postValue(setOf(GameUnit(moveableUnits[0], TurnHolder.currentPlayer)))
                    filteredUnits.add(moveableUnits[0].id)
                } else {
                    TODO("Deal with moving resources here. This will need a UI.")
                }
            } else {
                TODO("Deal with independent selection here. This will need a UI.")
            }
        }

        fun setSelectedUnits(units: Collection<GameUnit>) {
            selectedUnits.postValue(units.toSet())
        }

        fun setSelectedResources(resources: Collection<ResourceData>) {
            selectedResources.postValue(resources.toSet())
        }

        private fun getMovableHexes(): List<MapHex> {
            return (if(selectedHex.value != null && selectedUnits.value != null) (selectedUnits.value!!.firstOrNull { it.type == UnitType.MECH }?: selectedUnits.value!!.first()).let { unit ->
                unit.controllingPlayer.factionMat.getMovementAbilities(unit.type).filter {
                    it.validStartingHex(selectedHex.value!!)
                }.flatMap { hexRule ->
                    hexRule.validEndingHexes(selectedHex.value!!)?.filterNotNull()?: emptyList()
                }
            } else emptyList())
        }

        fun selectDestinationModel() : SelectHexToMoveToModel {
            return SelectHexToMoveToModel(getMovableHexes(), selectedHex)
        }
    }

    class SelectResourceOfTypeModel(private val selectResource: (Resource?) -> Boolean) : StandardSelectionModel() {
        override fun canSelect(mapHex: MapHex): Boolean {
            return ScytheDatabase.resourceDao()?.getResourcesAt(mapHex.loc)?.any { selectResource(Resource.valueOf(it.type)) }?: false &&
                    mapHex.playerInControl == TurnHolder.currentPlayer.playerId
        }

        override fun onSelection(mapHex: MapHex) {
            TODO("NYI")
        }

    }

    class SelectHexToMoveToModel(private val validHexes: List<MapHex>, private val selectedHex: MutableLiveData<MapHex>) : StandardSelectionModel() {
        override fun canSelect(mapHex: MapHex): Boolean {
            return validHexes.contains(mapHex)
        }

        override fun onSelection(mapHex: MapHex) {
            selectedHex.postValue(mapHex)
        }
    }

    class SelectHexToBuildOnModel(private val workerHexes: List<MapHex>, private val selectedHex: MutableLiveData<MapHex>) : StandardSelectionModel() {
        override fun canSelect(mapHex: MapHex): Boolean {
            return workerHexes.contains(mapHex) && GameMap.currentMap.unitsAtHex(mapHex.loc).none { UnitType.structures.none { type -> it.type == type.ordinal }}
        }

        override fun onSelection(mapHex: MapHex) {
            selectedHex.postValue(mapHex)
        }
    }

    class SelectHexToDeploy(private val targetHexes: List<MapHex>, private val selectedHex: MutableLiveData<MapHex>) : StandardSelectionModel() {
        override fun canSelect(mapHex: MapHex): Boolean {
            return targetHexes.contains(mapHex)
        }

        override fun onSelection(mapHex: MapHex) {
            selectedHex.postValue(mapHex)
        }
    }

    class SelectHexToProduce(private val workerHexes: List<MapHex>, private val selectedHex: MutableLiveData<MapHex>) : StandardSelectionModel() {
        override fun canSelect(mapHex: MapHex): Boolean {
            return workerHexes.contains(mapHex) && mapHex.producesResource
        }

        override fun onSelection(mapHex: MapHex) {
            selectedHex.postValue(mapHex)
        }
    }
}