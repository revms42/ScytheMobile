package org.ajar.scythemobile.ui.encounter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.ajar.scythemobile.R

class HitchRideFragment : Fragment() {

    private lateinit var hitchRideViewModel: HitchRideViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        hitchRideViewModel =
                ViewModelProvider(requireActivity()).get(HitchRideViewModel::class.java)

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    private fun returnToEncounter() {
        HitchRideFragmentDirections.actionNavHitchRideToNavEncounter(encounterResolved = true).also { hitchRideViewModel.reset() }
    }
}