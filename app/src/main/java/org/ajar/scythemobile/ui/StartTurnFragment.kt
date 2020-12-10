package org.ajar.scythemobile.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.player.TopRowAction

class StartTurnFragment : Fragment() {

    private lateinit var viewModel: ScytheTurnViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(ScytheTurnViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        // TODO: look up the selectable sections, then make someone select one.
        return root
    }

    override fun onResume() {
        super.onResume()
        // TODO: Temporary in order to test.
        if(viewModel.currentSection == null) {
            viewModel.selectSection(viewModel.selectableSections.indexOfFirst { it.topRowAction is TopRowAction.MoveOrGain })
        }


        findNavController().navigate(viewModel.currentNav)
    }
}