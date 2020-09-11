package org.ajar.scythemobile.ui.build

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.ajar.scythemobile.R

class BuildFragment : Fragment() {

    private lateinit var buildViewModel: BuildViewModel
    private val navigationArgs: BuildFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        buildViewModel =
                ViewModelProvider(requireActivity()).get(BuildViewModel::class.java)

        if(buildViewModel.unitType == null && navigationArgs.deployFromUnit != -1) buildViewModel.unitType = navigationArgs.deployFromUnit
        if(buildViewModel.returnNav == null && navigationArgs.returnNav != -1) buildViewModel.returnNav = navigationArgs.returnNav

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onResume() {
        super.onResume()
        if(!navigationArgs.paid){
            val cost = if(navigationArgs.cost == null) {
                buildViewModel.cost.map { it.id }.toIntArray()
            } else {
                navigationArgs.cost
            }?: IntArray(0)
            BuildFragmentDirections.actionNavBuildToNavResourcePaymentChoice(cost, R.id.action_nav_resource_payment_choice_to_nav_build)
        } else {
            TODO("Actually make this build somewhere")
        }
    }

    private fun navigateOut() {
        view?.findNavController()?.navigate(buildViewModel.returnNav?:buildViewModel.navigateOut).also { buildViewModel.reset() }
    }
}