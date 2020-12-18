package org.ajar.scythemobile.ui.factory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.Resource

class ResourceChoicePaymentFragment : Fragment() {

    private lateinit var viewModel: ResourceChoicePaymentViewModel
    private val navigationArgs: ResourceChoicePaymentFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel =
                ViewModelProvider(requireActivity()).get(ResourceChoicePaymentViewModel::class.java)
        viewModel.cost = navigationArgs.cost.map { Resource.valueOf(it)!! }
        viewModel.reward = navigationArgs.reward.map { Resource.valueOf(it)!! }

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun navigateOut() {
        ResourceChoicePaymentFragmentDirections.actionNavResourceChoicePaymentToNavFactoryMove()
    }
}