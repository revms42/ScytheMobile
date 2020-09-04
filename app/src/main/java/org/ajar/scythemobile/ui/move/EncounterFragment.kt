package org.ajar.scythemobile.ui.move

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.map.GameMap

class EncounterFragment : Fragment() {

    private lateinit var encounterViewModel: EncounterViewModel
    private val navigationArgs: EncounterFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        encounterViewModel =
                ViewModelProvider(requireActivity()).get(EncounterViewModel::class.java)
        //TODO: Get nav vars

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        if(navigationArgs.encounterResolved && encounterViewModel.done) {
            completeEncounter()
        }
    }

    private fun completeEncounter() {
        encounterViewModel.reset()
        EncounterFragmentDirections.actionNavEncounterToNavMove()
    }
}