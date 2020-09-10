package org.ajar.scythemobile.ui.deploy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.ajar.scythemobile.R

class DeployFragment : Fragment() {

    private lateinit var deployViewModel: DeployViewModel
    private val navigationArgs: DeployFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        deployViewModel =
                ViewModelProvider(requireActivity()).get(DeployViewModel::class.java)

        if(deployViewModel.unitType == null && navigationArgs.deployFromUnit != -1) deployViewModel.unitType = navigationArgs.deployFromUnit
        if(deployViewModel.returnNav == null && navigationArgs.returnNav != -1) deployViewModel.returnNav = navigationArgs.returnNav

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onResume() {
        super.onResume()
        if(!navigationArgs.paid){
            val cost = if(navigationArgs.amount == -1) {
                deployViewModel.cost.size
            } else {
                navigationArgs.amount
            }

            val type = if(navigationArgs.costType == -1) {
                deployViewModel.cost[0].id
            } else {
                navigationArgs.costType
            }
            DeployFragmentDirections.actionNavDeployToNavResourcePaymentChoice(type, cost, R.id.action_nav_resource_payment_choice_to_nav_deploy)
        } else {
            TODO("Actually make this deploy somewhere")
        }
    }

    private fun navigateOut() {
        view?.findNavController()?.navigate(deployViewModel.returnNav?: deployViewModel.navigateOut).also { deployViewModel.reset() }
    }
}