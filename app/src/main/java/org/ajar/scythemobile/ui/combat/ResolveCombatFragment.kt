package org.ajar.scythemobile.ui.combat

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.turn.TurnHolder

class ResolveCombatFragment : Fragment() {

    private lateinit var combatViewModel: CombatViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        combatViewModel = ViewModelProvider(requireActivity()).get(CombatViewModel::class.java)
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
        val attackingPlayer = PlayerInstance.loadPlayer(record.attackingPlayer).factionMat.factionMat.matName
        val defendingPlayer = PlayerInstance.loadPlayer(record.defendingPlayer).factionMat.factionMat.matName

        val results = combatViewModel.determineResults()!!

        builder.setTitle(R.string.title_resolve_combat_complete)

        val winner = if(results.attackerWon) attackingPlayer else defendingPlayer

        builder.setMessage(resources.getString(R.string.msg_combat_results, attackingPlayer, results.attackingPlayer, defendingPlayer, results.defendingPlayer, winner))

        builder.setPositiveButton(R.string.button_play_by_mail) { _, _ ->
            Snackbar.make(requireView(), "Need to deal with this.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        builder.setNegativeButton(R.string.button_hot_seat) { _, _ ->
            Snackbar.make(requireView(), "Need logic here to figure out where to go next?", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            combatViewModel.resolveCombat()
            val directions = ResolveCombatFragmentDirections.actionNavResolveCombatToNavMove()
            findNavController().navigate(directions)
        }
        builder.show()
    }
}