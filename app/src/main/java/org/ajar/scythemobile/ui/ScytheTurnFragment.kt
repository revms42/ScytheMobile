package org.ajar.scythemobile.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

abstract class ScytheTurnFragment(val nav: Int) : Fragment() {

    lateinit var scytheTurnViewModel: ScytheTurnViewModel

    abstract val redirect: Boolean

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scytheTurnViewModel = ViewModelProvider(requireActivity()).get(ScytheTurnViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

        if(redirect) navigateOut()
    }

    abstract fun destinationDirections(): NavDirections

    fun navigateOut() {
        scytheTurnViewModel.finishSection(nav)
        findNavController().navigate(destinationDirections())
    }
}