package org.ajar.scythemobile.ui.view

import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.ajar.scythemobile.model.Resource
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.MovementRule
import org.ajar.scythemobile.model.faction.Speed
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.turn.TurnHolder

interface MapSelectionModel {
    fun canSelect(mapHex: MapHex): Boolean

    fun onSelection(mapHex: MapHex)
}


private fun MapHex.selectMovableHexes(rules: Collection<MovementRule>) : List<MapHex> {
    return rules.filter { it.validStartingHex(this) }.flatMap { hexRule ->
        hexRule.validEndingHexes(this)?.filterNotNull() ?: emptyList()
    }
}

sealed class StandardSelectionModel : MapSelectionModel {

    class SelectUnitToMoveModel(private val selectedUnits: MutableLiveData<Set<GameUnit>>, private val selectedResources: MutableLiveData<Set<ResourceData>>, private val selectedHex: MutableLiveData<MapHex>) : StandardSelectionModel() {
        val filteredUnits = HashSet<Int>()

        override fun canSelect(mapHex: MapHex): Boolean {
            return TurnHolder.updatedUnitsAtPosition(mapHex).any { UnitType.provokeUnits.contains(UnitType.valueOf(it.type)) && !filteredUnits.contains(it.id)} &&
                    mapHex.playerInControl == TurnHolder.currentPlayer.playerId
        }

        override fun onSelection(mapHex: MapHex) {
            val moveableUnits = ScytheDatabase.unitDao()?.getSpecificUnitsAtLoc(mapHex.loc, TurnHolder.currentPlayer.playerId)?.toMutableList()

            moveableUnits?.removeIf { filteredUnits.contains(it.id) }

            moveableUnits?.takeIf { it.size > 0 }?.also {
                selectedResources.postValue(ScytheDatabase.resourceDao()?.getResourcesAt(mapHex.loc)?.toSet()?: emptySet())
                selectedUnits.postValue(it.map { data -> GameUnit.load(data) }.toSet())
                selectedHex.postValue(mapHex)
            }
        }

        private fun getMovableHexes(): List<MapHex> {
            Log.e("GetMovableHexes", "${selectedUnits.value}")
            return (if (selectedHex.value != null && selectedUnits.value != null)
                (selectedUnits.value!!.firstOrNull { it.type == UnitType.MECH || it.type == UnitType.CHARACTER }
                        ?: selectedUnits.value!!.first()).let { unit ->
                    unit.controllingPlayer.factionMat.getMovementAbilities(unit.type).let { rules ->
                        var firstPass = selectedHex.value!!.selectMovableHexes(rules)
                        if(unit.type == UnitType.WORKER) firstPass = firstPass.filter { it.playerInControl == unit.controllingPlayer.playerId || !it.provokesCombat }

                        if ((unit.type == UnitType.MECH || unit.type == UnitType.CHARACTER) && unit.controllingPlayer.factionMat.unlockedFactionAbilities.contains(Speed.singleton)) {
                            firstPass.filter { !it.provokesCombat }.flatMap { newHex ->
                                newHex.selectMovableHexes(rules)
                            } + firstPass
                        } else firstPass
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

    class HighlightSelectedHexModel(private val selectedHex: MutableLiveData<MapHex>? = null, private vararg val targetHex: MapHex) : StandardSelectionModel() {
        override fun canSelect(mapHex: MapHex): Boolean {
            return targetHex.any { it.loc == mapHex.loc }
        }

        override fun onSelection(mapHex: MapHex) {
            selectedHex?.postValue(mapHex)
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