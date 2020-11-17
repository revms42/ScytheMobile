package org.ajar.scythemobile.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.ajar.scythemobile.R
import org.ajar.scythemobile.ui.control.MapFilterTab

class MapFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel

    inner class MapFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return if(position == 0) {
                MapViewFragment()
            } else {
                MapFilterFragment()
            }
        }
    }

    class MapViewFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val map = inflater.inflate(R.layout.component_map_view, container, false) as MapView
            val viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
            viewModel.setMapView(map, requireActivity())
            return map
        }
    }

    class MapFilterFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val filter = inflater.inflate(R.layout.control_map_filter, container, false)

            // Filters
            val filterViewPager = filter.findViewById<ViewPager2>(R.id.map_filter_pager)
            filterViewPager.adapter = MapFilterTabStateAdapter(this)

            val tabLayout = filter.findViewById<TabLayout>(R.id.map_filter_tabs)
            TabLayoutMediator(tabLayout, filterViewPager) { tab, position ->
                val icon = ResourcesCompat.getDrawable(resources, MapFilterTab[position].icon, null)

                val imageView = ImageView(requireContext())
                imageView.setImageDrawable(icon)
                imageView.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)

                tab.customView = imageView
                tab.contentDescription = MapFilterTab[position].title
            }.attach()

            return filter
        }
    }

    class MapFilterTabStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = MapFilterTab.values().size

        override fun createFragment(position: Int): Fragment {
            return MapFilterTabFragment(MapFilterTab[position])
        }
    }

    class MapFilterTabFragment(private val filterTab: MapFilterTab) : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.component_filter_list, container, false)

            val recyclerView = view.findViewById<RecyclerView>(R.id.filter_list)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = filterTab.makeFilterListAdapter(requireContext()).adapter

            return view
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mapViewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_map, container, false)

        // Map
        val fragmentViewPager = root.findViewById<ViewPager2>(R.id.map_fragment_pager)
        fragmentViewPager.adapter = MapFragmentStateAdapter(this)

        return root
    }
}