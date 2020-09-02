package org.ajar.scythemobile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.ajar.scythemobile.R

class PassOffFragment : Fragment() {

    private lateinit var viewModel: ScytheTurnViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(ScytheTurnViewModel::class.java)
        // TODO: this is just a temporary central place to navigate to and from
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}