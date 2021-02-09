package org.ajar.scythemobile.ui

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.Resource

class ResourcePaymentChoiceFragment : Fragment() {

    private lateinit var payViewModel: ResourcePaymentChoiceViewModel
    private val navigationArgs: ResourcePaymentChoiceFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        payViewModel = ViewModelProvider(requireActivity()).get(ResourcePaymentChoiceViewModel::class.java)

        payViewModel.initialize(requireActivity())
        payViewModel.cost = navigationArgs.cost.map { Resource.valueOf(it)!! }

        return inflater.inflate(R.layout.resource_payment_choice, container, false)
    }

    override fun onResume() {
        super.onResume()

        payViewModel.resetPayment()

        val costTextField = view?.findViewById<TextView>(R.id.resources_required)
        costTextField?.text = getString(R.string.textview_required, payViewModel.cost?.joinToString { it.displayName })

        val paymentTextField = view?.findViewById<TextView>(R.id.resources_selected)
        paymentTextField?.text = getString(R.string.textview_required, payViewModel.resourcesSelectedLD.value?.joinToString { it.displayName }?: "")
        payViewModel.resourcesSelectedLD.observe(requireActivity(), Observer {
            paymentTextField?.text = getString(R.string.textview_selected, it.joinToString { res -> res.displayName })
        })

        val acceptButton = view?.findViewById<Button>(R.id.accept_payment)

        payViewModel.fulfilledLD.observe(requireActivity(), Observer {
            acceptButton?.isEnabled = it
        })

        view?.findViewById<Button>(R.id.undo_payment)?.setOnClickListener {
            payViewModel.resetPayment()
        }

        acceptButton?.setOnClickListener {
            if(payViewModel.payResources()) {
                returnToCallingFragment()
            }
        }
    }

    private fun returnToCallingFragment() {
        val paid = resources.getString(R.string.arg_cost_paid)
        val returnNav = resources.getString(R.string.arg_return_nav)
        view?.findNavController()?.navigate(navigationArgs.returnNav, bundleOf(paid to true, returnNav to -1))
    }

    override fun onPause() {
        super.onPause()

        payViewModel.fulfilledLD.removeObservers(requireActivity())
        payViewModel.resourcesSelectedLD.removeObservers(requireActivity())
    }
}