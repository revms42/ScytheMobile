package org.ajar.scythemobile.ui.move

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.ajar.scythemobile.model.CapitalResourceType
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.player.TopRowAction
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.ScytheTurnViewModel

class MoveGainFragment : Fragment() {

    private lateinit var moveViewModel: MoveViewModel
    private lateinit var scytheTurnViewModel: ScytheTurnViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        scytheTurnViewModel = ViewModelProvider(requireActivity()).get(ScytheTurnViewModel::class.java)
        moveViewModel = ViewModelProvider(requireActivity()).get(MoveViewModel::class.java)

        return inflater.inflate(R.layout.fragment_home, container, false)
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
                    scytheTurnViewModel.finishSection(R.id.nav_move)?.also { findNavController().navigate(it) }
                }
            }
            builder.setMessage(R.string.msg_choose_move_or_gain)
            builder.setTitle(R.string.title_choose_move_or_gain)

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
                scytheTurnViewModel.finishSection(R.id.nav_move)?.also { findNavController().navigate(it) }
            }
        }
        moveViewModel.encounter.observe(requireActivity()) { done ->
            if(done != null) {
                MoveGainFragmentDirections.actionNavMoveToNavEncounter(done.first, done.second, false)
            }
        }
    }
}