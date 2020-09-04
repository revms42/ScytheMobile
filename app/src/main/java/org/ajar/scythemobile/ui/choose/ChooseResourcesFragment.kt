package org.ajar.scythemobile.ui.choose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.ajar.scythemobile.R

class ChooseResourcesFragment : Fragment() {

    private lateinit var chooseResourcesViewModel: ChooseResourcesViewModel
    private val navigationArgs: ChooseResourcesFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        chooseResourcesViewModel =
                ViewModelProvider(requireActivity()).get(ChooseResourcesViewModel::class.java)

        chooseResourcesViewModel.amount = navigationArgs.amount

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    private fun returnToEncounter() {
        ChooseResourcesFragmentDirections.actionNavChooseResourcesToNavEncounter(true)
    }
}