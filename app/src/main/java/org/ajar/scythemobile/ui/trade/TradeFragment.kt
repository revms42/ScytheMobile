package org.ajar.scythemobile.ui.trade

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.ajar.scythemobile.R

class TradeFragment : Fragment() {

    private lateinit var tradeViewModel: TradeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        tradeViewModel =
                ViewModelProvider(requireActivity()).get(tradeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}