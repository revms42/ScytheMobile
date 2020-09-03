package org.ajar.scythemobile.ui.choose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.ajar.scythemobile.R

class MechDeployChoiceFragment : Fragment() {

    private lateinit var deployViewModel: MechDeployChoiceViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        deployViewModel =
                ViewModelProvider(requireActivity()).get(MechDeployChoiceViewModel::class.java)
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }
}