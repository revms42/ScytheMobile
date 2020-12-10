package org.ajar.scythemobile.ui.control

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.ajar.scythemobile.R
import org.ajar.scythemobile.Resource
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType

enum class MapFilterTab(var title: String, var icon: Int, val selected: MutableLiveData<MutableList<Int>> = MutableLiveData(ArrayList())) {
    WORKERS("Workers", R.drawable.ic_worker) {
        override fun makeFilterListAdapter(context: Context): FilterListAdapter {
            return FilterListAdapter.FactionFilterList(context, selected)
        }
    },
    MECHS("Mechs", R.drawable.ic_mech){
        override fun makeFilterListAdapter(context: Context): FilterListAdapter {
            return FilterListAdapter.FactionFilterList(context, selected)
        }
    },
    HEROES("Heroes", R.drawable.ic_hero){
        override fun makeFilterListAdapter(context: Context): FilterListAdapter {
            return FilterListAdapter.FactionFilterList(context, selected)
        }
    },
    BUILDINGS("Buildings", R.drawable.ic_building){
        override fun makeFilterListAdapter(context: Context): FilterListAdapter {
            return FilterListAdapter.FactionFilterList(context, selected)
        }
    },
    RESOURCES("Resources", R.drawable.ic_resources){
        override fun makeFilterListAdapter(context: Context): FilterListAdapter {
            return FilterListAdapter.ResourceFilterList(context, selected)
        }
    },
    OTHER("Other", R.drawable.ic_map_token){
        override fun makeFilterListAdapter(context: Context): FilterListAdapter {
            return FilterListAdapter.OtherFilterList(context, selected)
        }
    };

    abstract fun makeFilterListAdapter(context: Context): FilterListAdapter

    fun watch(lifecycleOwner: LifecycleOwner, work: () -> Unit) {
        selected.observe(lifecycleOwner, Observer { work() })
    }

    companion object {
        fun populateNames(resources: Resources) {
            resources.getStringArray(R.array.map_filter_tabs).forEachIndexed { index, s ->
                values()[index].title = s
            }
        }

        operator fun get(index: Int): MapFilterTab {
            return values()[index]
        }

        fun paintUnit(unit: GameUnit) : Boolean {
            return (when(unit.type) {
                UnitType.MECH -> MECHS.selected.value?.contains(unit.controllingPlayer.factionMat.factionMat.id)
                UnitType.CHARACTER -> HEROES.selected.value?.contains(unit.controllingPlayer.factionMat.factionMat.id)
                UnitType.WORKER -> WORKERS.selected.value?.contains(unit.controllingPlayer.factionMat.factionMat.id)
                UnitType.ARMORY, UnitType.MONUMENT, UnitType.MILL, UnitType.MINE -> BUILDINGS.selected.value?.contains(unit.controllingPlayer.factionMat.factionMat.id)
                UnitType.TRAP, UnitType.FLAG -> OTHER.selected.value?.contains(unit.controllingPlayer.factionMat.factionMat.id)
                UnitType.AIRSHIP -> true
            }?: false)
        }

        fun paintResource(res: Resource): Boolean {
            return RESOURCES.selected.value?.contains(res.id)?: false
        }
    }
}

