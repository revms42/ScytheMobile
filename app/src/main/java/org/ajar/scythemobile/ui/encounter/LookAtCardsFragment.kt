package org.ajar.scythemobile.ui.encounter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import org.ajar.scythemobile.R

class LookAtCardsFragment : Fragment() {

    private lateinit var lookAtCardsViewModel: LookAtCardsViewModel
    private val navigationArgs: LookAtCardsFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        lookAtCardsViewModel =
                ViewModelProvider(requireActivity()).get(LookAtCardsViewModel::class.java)

        lookAtCardsViewModel.amount = navigationArgs.amount

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    private fun returnToEncounter() {
        LookAtCardsFragmentDirections.actionNavLookAtCardsToNavEncounter(encounterResolved = true).also { lookAtCardsViewModel.reset() }
    }
}