package org.ajar.scythemobile.ui.move

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.ajar.scythemobile.R

class StartCombatFragment : Fragment() {

    private lateinit var startCombatViewModel: StartCombatViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        startCombatViewModel = ViewModelProvider(requireActivity()).get(StartCombatViewModel::class.java)
        return inflater.inflate(R.layout.fragment_start_combat, container, false)
    }

    override fun onResume() {
        super.onResume()
        startCombatViewModel.initialize(requireActivity())
    }
}