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

class EnlistChoiceFragment : Fragment() {

    private lateinit var enlistChoiceViewModel: EnlistChoiceViewModel
    private val navigationArgs: EnlistChoiceFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        enlistChoiceViewModel =
                ViewModelProvider(requireActivity()).get(EnlistChoiceViewModel::class.java)

        if(enlistChoiceViewModel.returnNav == null && navigationArgs.returnNav != -1) enlistChoiceViewModel.returnNav = navigationArgs.returnNav

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onResume() {
        super.onResume()
        if(!navigationArgs.paid){
            EnlistChoiceFragmentDirections.actionNavEnlistChoiceToNavResourcePaymentChoice(
                    navigationArgs.costType,
                    navigationArgs.amount,
                    R.id.action_nav_resource_payment_choice_to_nav_enlist_choice
            )
        } else {
            TODO("Actually make this enlist somewhere")
        }
    }

    private fun returnToCallingFragment() {
        view?.findNavController()?.navigate(enlistChoiceViewModel.returnNav!!).also { enlistChoiceViewModel.reset() }
    }
}