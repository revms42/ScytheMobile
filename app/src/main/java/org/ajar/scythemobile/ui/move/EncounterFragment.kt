package org.ajar.scythemobile.ui.move

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.ajar.scythemobile.R
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.model.Resource
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.CombatRule
import org.ajar.scythemobile.model.map.EncounterDeck
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.turn.TurnHolder

class EncounterFragment : Fragment() {

    private lateinit var encounterViewModel: EncounterViewModel
    private val navigationArgs: EncounterFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        encounterViewModel = ViewModelProvider(requireActivity()).get(EncounterViewModel::class.java)
        if(encounterViewModel.card == null && navigationArgs.card != -1) encounterViewModel.card = EncounterDeck.getCard(navigationArgs.card)
        if(encounterViewModel.hex == null && navigationArgs.hex != -1) encounterViewModel.hex = GameMap.currentMap.findHexAtIndex(navigationArgs.hex)
        encounterViewModel.loadPlayerData()

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        if(navigationArgs.encounterResolved && encounterViewModel.done) {
            completeEncounter()
        } else {
            //TODO: Do introductory text first then do this.
            displayChoiceDialog()
        }
    }

    private fun displayChoiceDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_encounter)

        builder.setItems(encounterViewModel.getChoices().map { it.description }.toTypedArray()) { _, which ->
            encounterViewModel.card!!.also {
                val choice = when(which) {
                    1 -> it.commercialOutcome
                    2 -> it.unpopularOutcome
                    else -> it.popularOutcome
                }

                if(encounterViewModel.choose(choice, requireActivity())) {
                    completeEncounter()
                } else {
                    displayChoiceDialog()
                }
            }
        }.show()
    }

    private fun completeEncounter() {
        encounterViewModel.reset()
        findNavController().navigate(EncounterFragmentDirections.actionNavEncounterToNavMove())
    }
}