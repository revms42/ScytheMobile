package org.ajar.scythemobile.ui.move

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.ajar.scythemobile.R

class ResolveCombatFragment : Fragment() {

    private lateinit var moveViewModel: MoveViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        moveViewModel =
                ViewModelProvider(requireActivity()).get(MoveViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        //TODO: DON'T FORGET TO LOOK AND SEE IF THERE ARE ANY ENCOUNTERS TO RESOLVE!
        return root
    }
}