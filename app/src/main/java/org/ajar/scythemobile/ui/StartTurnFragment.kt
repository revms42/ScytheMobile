package org.ajar.scythemobile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.player.TopRowAction

class StartTurnFragment : ScytheTurnFragment(R.id.nav_start) {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // TODO: look up the selectable sections, then make someone select one.
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override val redirect: Boolean
        get() = true

    override fun onResume() {
        // TODO: Temporary in order to test.
        if(scytheTurnViewModel.currentSection == null) {
            scytheTurnViewModel.selectSection(scytheTurnViewModel.selectableSections.indexOfFirst { it.topRowAction is TopRowAction.MoveOrGain })
        }

        super.onResume()
    }

    override fun destinationDirections(): NavDirections {
        //TODO: Temporary in order to test
        return StartTurnFragmentDirections.actionNavStartToNavMove()
    }
}