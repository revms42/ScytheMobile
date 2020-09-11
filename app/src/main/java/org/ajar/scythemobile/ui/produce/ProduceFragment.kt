package org.ajar.scythemobile.ui.produce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.ajar.scythemobile.R

class ProduceFragment : Fragment() {

    private lateinit var produceViewModel: ProduceViewModel
    private val navigationArgs: ProduceFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        produceViewModel =
                ViewModelProvider(requireActivity()).get(ProduceViewModel::class.java)

        if(produceViewModel.returnNav == null && navigationArgs.returnNav != -1) produceViewModel.returnNav = navigationArgs.returnNav
        if(produceViewModel._numberOfHexes == null && navigationArgs.numberOfHexes != -1) produceViewModel._numberOfHexes = navigationArgs.numberOfHexes
        if(produceViewModel.ignoreMill == null) produceViewModel.ignoreMill = navigationArgs.ignoreMill

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        if(!navigationArgs.paid){
            val cost = if(navigationArgs.cost == null) {
                produceViewModel.cost.map { it.id }.toIntArray()
            } else {
                navigationArgs.cost
            }?: IntArray(0)
            ProduceFragmentDirections.actionNavProduceToNavResourcePaymentChoice(cost, R.id.action_nav_resource_payment_choice_to_nav_produce)
        } else {
            TODO("Actually make this produce somewhere")
        }
    }

    private fun navigateOut() {
        view?.findNavController()?.navigate(produceViewModel.returnNav?:produceViewModel.navigateOut).also { produceViewModel.reset() }
    }
}