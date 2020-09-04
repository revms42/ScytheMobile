package org.ajar.scythemobile.ui.upgrade

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.ajar.scythemobile.R

class UpgradeChoiceFragment : Fragment() {

    private lateinit var upgradeChoiceViewModel: UpgradeChoiceViewModel
    private val navigationArgs: UpgradeChoiceFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        upgradeChoiceViewModel =
                ViewModelProvider(requireActivity()).get(UpgradeChoiceViewModel::class.java)

        if(upgradeChoiceViewModel.returnNav == null && navigationArgs.returnNav != -1) upgradeChoiceViewModel.returnNav = navigationArgs.returnNav

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onResume() {
        super.onResume()
        if(!navigationArgs.paid){
            UpgradeChoiceFragmentDirections.actionNavUpgradeChoiceToNavResourcePaymentChoice(
                    navigationArgs.costType,
                    navigationArgs.amount,
                    R.id.action_nav_resource_payment_choice_to_nav_upgrade_choice
            )
        } else {
            TODO("Actually make this enlist somewhere")
        }
    }

    private fun returnToCallingFragment() {
        view?.findNavController()?.navigate(upgradeChoiceViewModel.returnNav!!).also { upgradeChoiceViewModel.reset() }
    }
}