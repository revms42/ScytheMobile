package org.ajar.scythemobile.ui.produce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.ajar.scythemobile.R

class ProduceFragment : Fragment() {

    private lateinit var produceViewModel: ProduceViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        produceViewModel =
                ViewModelProvider(requireActivity()).get(ProduceViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}