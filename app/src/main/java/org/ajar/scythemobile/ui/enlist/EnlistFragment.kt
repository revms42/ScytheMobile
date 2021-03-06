package org.ajar.scythemobile.ui.enlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.ajar.scythemobile.R

class EnlistFragment : Fragment() {

    private lateinit var enlistViewModel: EnlistViewModel
    private val navigationArgs: EnlistFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        enlistViewModel =
                ViewModelProvider(requireActivity()).get(EnlistViewModel::class.java)

        if(enlistViewModel.returnNav == null && navigationArgs.returnNav != -1) enlistViewModel.returnNav = navigationArgs.returnNav

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onResume() {
        super.onResume()
        if(!navigationArgs.paid){
            val cost = if(navigationArgs.cost == null) {
                enlistViewModel.cost.map { it.id }.toIntArray()
            } else {
                navigationArgs.cost
            }?: IntArray(0)
            EnlistFragmentDirections.actionNavEnlistToNavResourcePaymentChoice(cost, R.id.action_nav_resource_payment_choice_to_nav_enlist)
        } else {
            TODO("Actually make this enlist somewhere")
        }
    }

    private fun navigateOut() {
        //TODO: view?.findNavController()?.navigate(enlistViewModel.returnNav?:enlistViewModel.navigateOut).also { enlistViewModel.reset() }
    }
}