package org.ajar.scythemobile.ui.move

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.ajar.scythemobile.R
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.ScytheTurnViewModel
import org.ajar.scythemobile.ui.view.MapView
import org.ajar.scythemobile.ui.view.MapViewModel
import org.ajar.scythemobile.ui.view.StandardSelectionModel

class MoveFragment : Fragment() {

    private lateinit var moveViewModel: MoveViewModel
    private lateinit var scytheTurnViewModel: ScytheTurnViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        scytheTurnViewModel = ViewModelProvider(requireActivity()).get(ScytheTurnViewModel::class.java)
        moveViewModel = ViewModelProvider(requireActivity()).get(MoveViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        moveViewModel.initialize(requireActivity())

        moveViewModel.setupObservers(requireActivity())

        moveViewModel.moveCompleted.observe(requireActivity()){ done ->
            if(done) {
                Snackbar.make(requireView(), "Movement Complete", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                scytheTurnViewModel.finishSection(R.id.nav_move)?.also { findNavController().navigate(it) }
            }
        }
        moveViewModel.encounter.observe(requireActivity()) { done ->
            if(done != null) {
                MoveFragmentDirections.actionNavMoveToNavEncounter(done.first, done.second, false)
            }
        }
    }
}