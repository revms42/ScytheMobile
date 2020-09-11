package org.ajar.scythemobile.ui.factory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.ajar.scythemobile.R

class FactoryFragment : Fragment() {

    private lateinit var factoryViewModel: FactoryViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        factoryViewModel =
                ViewModelProvider(requireActivity()).get(FactoryViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}