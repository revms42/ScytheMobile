package org.ajar.scythemobile.ui.factory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.ajar.scythemobile.R

class DeployWorkerFromMechFragment : Fragment() {

    private lateinit var viewModel: DeployWorkerFromMechViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        TODO("Figure this out.")
        viewModel =
                ViewModelProvider(requireActivity()).get(DeployWorkerFromMechViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}