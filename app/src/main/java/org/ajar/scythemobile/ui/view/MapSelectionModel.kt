package org.ajar.scythemobile.ui.view

import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.CapitalResourceType
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.DefaultFactionAbility
import org.ajar.scythemobile.model.faction.FactionMat
import org.ajar.scythemobile.model.faction.MovementRule
import org.ajar.scythemobile.model.faction.Speed
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.HomeBase
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

    class SelectResourceOfTypeModel(private val selectResource: (ResourceData) -> Unit, private val canSelect: (ResourceData) -> Boolean) : StandardSelectionModel() {

        private val coerce: Boolean
            get() {
                return TurnHolder.currentPlayer.factionMat.defaultFactionAbility == DefaultFactionAbility.COERCION && !TurnHolder.currentPlayer.playerData.flagCoercion
            }

        override fun canSelect(mapHex: MapHex): Boolean {
            val mapResource = canSelectMapResource(mapHex)

            return when {
                mapResource -> true
                coerce -> canSelectCoercian(mapHex)
                else -> false
            }
        }

        override fun onSelection(mapHex: MapHex) {
            val mapResources = ScytheDatabase.resourceDao()?.getResourcesAt(mapHex.loc)

            when {
                !mapResources.isNullOrEmpty() -> mapResources
                coerce && mapHex.faction?.faction == TurnHolder.currentPlayer.factionMat.factionMat -> ScytheDatabase.resourceDao()?.getOwnedResourcesOfType(TurnHolder.currentPlayer.playerId, listOf(CapitalResourceType.CARDS.id))
                else -> emptyList()
            }?.firstOrNull { canSelect(it) }?.also { selectResource(it) }
        }

        private fun canSelectMapResource(mapHex: MapHex) : Boolean {
            return ScytheDatabase.resourceDao()?.getResourcesAt(mapHex.loc)?.any { canSelect(it) }?: false &&
                    mapHex.playerInControl == TurnHolder.currentPlayer.playerId
        }

        private fun canSelectCoercian(mapHex: MapHex) : Boolean {
            return if(mapHex.faction?.faction == TurnHolder.currentPlayer.factionMat.factionMat) {
                ScytheDatabase.resourceDao()?.getOwnedResourcesOfType(TurnHolder.currentPlayer.playerId, listOf(CapitalResourceType.CARDS.id)).also { Log.e("Cards", "$it") }?.any {
                    canSelect(it)
                }?: false
            } else {
                false
            }
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