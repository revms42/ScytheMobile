package org.ajar.scythemobile.ui.encounter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
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
        payViewModel =
                ViewModelProvider(requireActivity()).get(ResourcePaymentChoiceViewModel::class.java)

        payViewModel.cost = navigationArgs.cost.map { Resource.valueOf(it)!! }

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    private fun returnToCallingFragment() {
        val paid = resources.getString(R.string.arg_cost_paid)
        val returnNav = resources.getString(R.string.arg_return_nav)
        view?.findNavController()?.navigate(navigationArgs.returnNav, bundleOf(paid to true, returnNav to -1))
    }
}