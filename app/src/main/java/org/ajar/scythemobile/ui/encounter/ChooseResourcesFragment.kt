package org.ajar.scythemobile.ui.encounter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        if(chooseResourcesViewModel.returnNav == null && navigationArgs.returnNav != -1) chooseResourcesViewModel.returnNav = navigationArgs.returnNav
        if(chooseResourcesViewModel.unitType == null && navigationArgs.deployFromUnit != -1) chooseResourcesViewModel.unitType = navigationArgs.deployFromUnit

        TODO("Rework this so we can select to put resources at the locations of selected units.")

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    private fun returnToEncounter() {
        view?.findNavController()?.navigate(chooseResourcesViewModel.returnNav?:R.id.action_nav_choose_resources_to_nav_encounter).also { chooseResourcesViewModel.reset() }
    }
}