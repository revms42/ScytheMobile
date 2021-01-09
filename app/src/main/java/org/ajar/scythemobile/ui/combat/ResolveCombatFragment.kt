package org.ajar.scythemobile.ui.combat

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.ScytheTurnViewModel

class ResolveCombatFragment : Fragment() {

    private lateinit var combatViewModel: CombatViewModel
    private lateinit var turnViewModel: ScytheTurnViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        combatViewModel = ViewModelProvider(requireActivity()).get(CombatViewModel::class.java)
        turnViewModel = ViewModelProvider(requireActivity()).get(ScytheTurnViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_combat_selection, container, false)
        val button = view?.findViewById<Button>(R.id.start_combat_button)

        button?.setText(R.string.button_finish_combat)
        button?.setOnClickListener { _ ->
            resolveCombat()
        }

        return view
    }

    private fun resolveCombat() {
        val builder = AlertDialog.Builder(activity)

        val record = TurnHolder.getNextCombat()!!
        val attackingPlayer = PlayerInstance.loadPlayer(record.attackingPlayer)
        val defendingPlayer = PlayerInstance.loadPlayer(record.defendingPlayer)

        val results = combatViewModel.determineResults()!!

        builder.setTitle(R.string.title_resolve_combat_complete)

        val winner = (if(results.attackerWon) attackingPlayer else defendingPlayer).factionMat.factionMat.matName
        val loser = (if(results.attackerWon) defendingPlayer else attackingPlayer).factionMat.factionMat.matName

        builder.setMessage(resources.getString(R.string.msg_combat_results, attackingPlayer.factionMat.factionMat.matName, results.attackingPlayer, defendingPlayer.factionMat.factionMat.matName, results.defendingPlayer, winner))

        builder.setPositiveButton(R.string.button_play_by_mail) { _, _ ->
            Snackbar.make(requireView(), "Need to deal with this.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        builder.setNegativeButton(R.string.button_hot_seat) { _, _ ->
            Snackbar.make(requireView(), "Need logic here to figure out where to go next?", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            val selectedHex = combatViewModel.resolveCombat()

            if(selectedHex != null) {
                selectedHex.observe(requireActivity()) {
                    combatViewModel.retreatUnits(it)
                    navigateOut()
                }

                val retreatChoiceBuilder = AlertDialog.Builder(activity)

                retreatChoiceBuilder.setTitle(R.string.title_choose_retreat)
                retreatChoiceBuilder.setMessage(resources.getString(R.string.msg_choose_retreat_hex, loser))
                retreatChoiceBuilder.setPositiveButton(getString(R.string.button_ok)) { _, _ -> Unit }

                retreatChoiceBuilder.show()
            } else {
                navigateOut()
            }
        }
        builder.show()
    }

    private fun navigateOut() {
        val directions: NavDirections = turnViewModel.finishSection(R.id.nav_resolve_combat)!!
        findNavController().navigate(directions)
    }
}