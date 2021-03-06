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

class UpgradeFragment : Fragment() {

    private lateinit var upgradeViewModel: UpgradeViewModel
    private val navigationArgs: UpgradeFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        upgradeViewModel =
                ViewModelProvider(requireActivity()).get(UpgradeViewModel::class.java)

        if(upgradeViewModel.returnNav == null && navigationArgs.returnNav != -1) upgradeViewModel.returnNav = navigationArgs.returnNav

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onResume() {
        super.onResume()
        if(!navigationArgs.paid){
            val cost = if(navigationArgs.cost == null) {
                upgradeViewModel.cost.map { it.id }.toIntArray()
            } else {
                navigationArgs.cost
            }?: IntArray(0)
            UpgradeFragmentDirections.actionNavUpgradeToNavResourcePaymentChoice(cost, R.id.action_nav_resource_payment_choice_to_nav_upgrade)
        } else {
            TODO("Actually make this enlist somewhere")
        }
    }

    private fun navigateOut() {
        //TODO: view?.findNavController()?.navigate(upgradeViewModel.returnNav?:upgradeViewModel.navigateOut).also { upgradeViewModel.reset() }
    }
}