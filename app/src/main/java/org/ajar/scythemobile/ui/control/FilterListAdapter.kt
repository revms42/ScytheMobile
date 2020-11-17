package org.ajar.scythemobile.ui.control

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.FactionMat

sealed class FilterListAdapter(context: Context, override val selected: MutableLiveData<MutableList<Int>>) : FilterList {

    class FactionFilterList(context: Context, selectedList: MutableLiveData<MutableList<Int>>) : FilterListAdapter(context, selectedList) {
        override val fullList: List<Int> by lazy { PlayerInstance.activeFactions?: listOf() }
        override val findName: (Int) -> String = { FactionMat[it]?.matName?: "Not Found" }
        override val findIcon: (Int) -> Drawable? = { FactionMat[it]?.resourcePack?.let { pack -> ResourcesCompat.getDrawable(context.resources, pack.factionIconRes , null) } }
    }
    class ResourceFilterList(context: Context, selectedList: MutableLiveData<MutableList<Int>>) : FilterListAdapter(context, selectedList) {
        override val fullList: List<Int> by lazy { NaturalResourceType.valueList.map { it.id } }
        override val findName: (Int) -> String = { NaturalResourceType.valueList[it].displayName }
        override val findIcon: (Int) -> Drawable? = { ResourcesCompat.getDrawable(context.resources, NaturalResourceType.valueList[it].image, null) }
    }
    class OtherFilterList(context: Context, selectedList: MutableLiveData<MutableList<Int>>) : FilterListAdapter(context, selectedList) {

        override val fullList: List<Int> by lazy { listOf(UnitType.FLAG.ordinal, UnitType.TRAP.ordinal) }
        override val findName: (Int) -> String = { UnitType.valueOf(it).name}
        //TODO: Still need trap and flag resources
        override val findIcon: (Int) -> Drawable? = { ResourcesCompat.getDrawable(
                context.resources,
                if(it == UnitType.TRAP.ordinal) R.drawable.ic_togawa_trap_armed else R.drawable.ic_albion_flag,
                null)
        }
    }

    abstract val fullList: List<Int>
    abstract val findName: (Int) -> String
    abstract val findIcon: (Int) -> Drawable?

    val adapter = FilterListViewAdapter(context, selected)

    inner class FilterListViewAdapter(private val context: Context, private val selected: MutableLiveData<MutableList<Int>>) : RecyclerView.Adapter<FilterRowViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterRowViewHolder {
            return FilterRowViewHolder(View.inflate(context, R.layout.component_filter_list_row, null), findName, findIcon, selected)
        }

        override fun getItemCount(): Int {
            return fullList.size
        }

        override fun onBindViewHolder(holder: FilterRowViewHolder, position: Int) {
            holder.faction = fullList[position]
        }
    }

    class FilterRowViewHolder(private val view: View, val findName: (Int) -> String, val findIcon: (Int) -> Drawable?, val selected: MutableLiveData<MutableList<Int>>) : RecyclerView.ViewHolder(view) {
        //TODO: We should get these some other way.
        private val unselectedColor = Color.WHITE
        private val selectedColor = Color.argb(255, 128, 255, 128)

        val name: TextView = view.findViewById(R.id.filter_name)
        val icon: ImageView = view.findViewById(R.id.filter_icon)

        private var _faction: Int = -1
        var faction: Int
            get() = _faction
            set(value) {
                name.text = findName(value)
                findIcon(value)?.also {
                    icon.setImageDrawable(it)
                }
                _faction = value
                highlightSelection()
                view.invalidate()
            }

        init {
            view.setOnClickListener {
                if(selected.value?.contains(faction) == true) {
                    selected.value?.remove(faction)
                    view.isSelected = false
                } else {
                    selected.value?.add(faction)
                    view.isSelected = true
                }
                highlightSelection()
                selected.postValue(selected.value)
                it.invalidate()
            }
            highlightSelection()
        }

        private fun highlightSelection() {
            if(selected.value?.contains(faction) == true) {
                Log.w("${name.text}", "selected")
                view.setBackgroundColor(selectedColor)
            } else {
                Log.w("${name.text}", "unselected")
                view.setBackgroundColor(unselectedColor)
            }
        }
    }
}