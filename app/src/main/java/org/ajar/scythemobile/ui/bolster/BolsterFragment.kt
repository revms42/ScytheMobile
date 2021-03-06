package org.ajar.scythemobile.ui.bolster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.ajar.scythemobile.R

class BolsterFragment : Fragment() {

    private lateinit var bolsterViewModel: BolsterViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        bolsterViewModel =
                ViewModelProvider(requireActivity()).get(BolsterViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}