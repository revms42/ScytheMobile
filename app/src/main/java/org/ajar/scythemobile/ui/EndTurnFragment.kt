package org.ajar.scythemobile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.ajar.scythemobile.R
import org.ajar.scythemobile.ui.view.MapFragment
import org.ajar.scythemobile.ui.view.MapView
import org.ajar.scythemobile.ui.view.MapViewModel

class EndTurnFragment : Fragment() {

    private lateinit var viewModel: ScytheTurnViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(ScytheTurnViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        // TODO: end the turn, save the data, prompt the next person (hotseat) or make a DB diff message (PBE)
        return root
    }

    override fun onResume() {
        super.onResume()
        ViewModelProvider(requireActivity()).get(MapViewModel::class.java).setSelectionModel(null)
    }
}