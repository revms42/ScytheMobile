package org.ajar.scythemobile.ui.combat

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.turn.TurnHolder

class AnswerCombatFragment : CombatSelectFragment(R.id.nav_answer_combat) {

    private val args: AnswerCombatFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initializeViewModel()
        val view = inflater.inflate(R.layout.fragment_combat_selection, container, false)

        val button = view?.findViewById<Button>(R.id.start_combat_button)
        button?.setText(R.string.button_answer_combat)
        button?.setOnClickListener { _ ->
            launchSelectAbilities()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        combatViewModel.reset()
        combatViewModel.setupCombat(requireActivity(), null, PlayerInstance.loadPlayer(args.hotseatPlayerId))
    }

    override fun destinationDirections(): NavDirections = AnswerCombatFragmentDirections.actionNavAnswerCombatToNavResolveCombat()

    override fun postSelection() {
        val builder = AlertDialog.Builder(activity)

        builder.setTitle(R.string.title_answer_combat_complete)

        builder.setMessage(resources.getString(R.string.msg_answer_combat_selection, combatViewModel.playerPowerSelected, combatViewModel.playerCardsSelected.size, combatViewModel.playerTotalPower))

        builder.setPositiveButton(R.string.button_play_by_mail) { _, _ ->
            Snackbar.make(requireView(), "Need to deal with this.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        builder.setNegativeButton(R.string.button_hot_seat) { _, _ ->
            navigateOut()
        }
        builder.show()
    }

    override val redirect: Boolean
        get() {
            return TurnHolder.getNextCombat()?.let { !it.combatResolved && it.defenderPower != null }?:true
        }
}