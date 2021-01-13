package org.ajar.scythemobile.ui.move

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.google.android.material.snackbar.Snackbar
import org.ajar.scythemobile.model.CapitalResourceType
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.player.TopRowAction
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.ScytheTurnFragment

class MoveGainFragment : ScytheTurnFragment(R.id.nav_move) {

    private lateinit var moveViewModel: MoveViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        moveViewModel = ViewModelProvider(requireActivity()).get(MoveViewModel::class.java)

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override val redirect: Boolean
        get() {
            return scytheTurnViewModel.isTopRowComplete()
        }

    override fun destinationDirections(): NavDirections {
        return when {
            TurnHolder.hasUnresolvedCombat() -> MoveGainFragmentDirections.actionNavMoveToNavStartCombat()
            TurnHolder.hasUnresolvedEncounter() != null -> TurnHolder.hasUnresolvedEncounter()!!.let { MoveGainFragmentDirections.actionNavMoveToNavEncounter(it.loc, it.encounter!!.id, false) }
            else -> ActionOnlyNavDirections(scytheTurnViewModel.currentSection!!.moveTopToBottom)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireActivity().let { activity ->
            val moveAction = TurnHolder.currentPlayer.playerMat.findTopRowAction(TopRowAction.MoveOrGain::class.java)
            val builder = AlertDialog.Builder(activity)

            builder.apply {
                setPositiveButton(getString(R.string.button_select_move, moveAction?.unitsMoved?: 2)) { _, _ ->
                    setupMoveViewModel()
                }

                setNegativeButton(getString(R.string.button_select_gain, moveAction?.coinsGained?: 1)) { _, _ ->
                    ScytheAction.GiveCapitalResourceAction(TurnHolder.currentPlayer, CapitalResourceType.COINS, moveAction?.coinsGained?: 1)
                    Snackbar.make(
                            requireView(),
                            getString(R.string.msg_gain_completed, TurnHolder.currentPlayer.factionMat.factionMat.matName, moveAction?.coinsGained?: 1),
                            Snackbar.LENGTH_LONG
                    ).setAction("Action", null).show()
                    navigateOut()
                }

                builder.setMessage(R.string.msg_choose_move_or_gain)
                builder.setTitle(R.string.title_choose_move_or_gain)
            }

            builder.create().show()
        }
    }

    private fun setupMoveViewModel() {
        moveViewModel.initialize(requireActivity())

        moveViewModel.setupObservers(requireActivity())

        moveViewModel.moveCompleted.observe(requireActivity()){ done ->
            if(done) {
                Snackbar.make(requireView(), getString(R.string.msg_move_completed), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                navigateOut()
            }
        }
    }
}