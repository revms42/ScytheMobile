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

class BuildChoiceFragment : Fragment() {

    private lateinit var buildChoiceViewModel: BuildChoiceViewModel
    private val navigationArgs: BuildChoiceFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        buildChoiceViewModel =
                ViewModelProvider(requireActivity()).get(BuildChoiceViewModel::class.java)

        if(buildChoiceViewModel.unitType == null && navigationArgs.deployFromUnit != -1) buildChoiceViewModel.unitType = navigationArgs.deployFromUnit
        if(buildChoiceViewModel.returnNav == null && navigationArgs.returnNav != -1) buildChoiceViewModel.returnNav = navigationArgs.returnNav

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onResume() {
        super.onResume()
        if(!navigationArgs.paid){
            BuildChoiceFragmentDirections.actionNavBuildChoiceToNavResourcePaymentChoice(
                    navigationArgs.costType,
                    navigationArgs.amount,
                    R.id.action_nav_resource_payment_choice_to_nav_build_choice
            )
        } else {
            TODO("Actually make this build somewhere")
        }
    }

    private fun returnToCallingFragment() {
        view?.findNavController()?.navigate(buildChoiceViewModel.returnNav!!).also { buildChoiceViewModel.reset() }
    }
}