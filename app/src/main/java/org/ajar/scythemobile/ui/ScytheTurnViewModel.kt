package org.ajar.scythemobile.ui

import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import org.ajar.scythemobile.R
import org.ajar.scythemobile.data.CombatRecord
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.player.Section
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.move.AnswerCombatFragmentDirections
import org.ajar.scythemobile.ui.move.MoveFragmentDirections
import org.ajar.scythemobile.ui.move.ResolveCombatFragmentDirections
import org.ajar.scythemobile.ui.move.StartCombatFragmentDirections

class ScytheTurnViewModel : ViewModel() {

    val selectableSections: List<Section>
        get() = currentPlayer.selectableSections()

    val currentPlayer: PlayerInstance
        get() = TurnHolder.currentPlayer

    val currentSection: Section?
        get() = TurnHolder.currentTurn.selection?.let { currentPlayer.selectableSections()[it] }

    val currentNav: Int
        get() {
            return when {
                TurnHolder.currentTurn.selection == null -> R.id.nav_start
                !TurnHolder.currentTurn.performedTop -> currentSection!!.topRowAction.fragmentNav
                TurnHolder.currentTurn.combatOne?.combatResolved == false -> determineCombatPhase(1)
                TurnHolder.currentTurn.combatTwo?.combatResolved == false -> determineCombatPhase(2)
                TurnHolder.currentTurn.combatThree?.combatResolved == false -> determineCombatPhase(3)
                !TurnHolder.currentTurn.performedBottom -> currentSection!!.bottomRowAction.fragmentNav
                else -> R.id.nav_end
            }
        }

    fun finishSection(current: Int): NavDirections? {
        return when(current) {
            R.id.nav_start -> currentSection!!.topRowAction.actionInto
            currentSection!!.topRowAction.fragmentNav -> {
                when {
                    TurnHolder.currentTurn.combatOne?.combatResolved == false -> MoveFragmentDirections.actionNavMoveToNavStartCombat()
                    TurnHolder.currentTurn.combatTwo?.combatResolved == false -> MoveFragmentDirections.actionNavMoveToNavStartCombat()
                    TurnHolder.currentTurn.combatThree?.combatResolved == false -> MoveFragmentDirections.actionNavMoveToNavStartCombat()
                    else -> currentSection!!.topRowAction.actionOutOf
                }
            }
            R.id.nav_start_combat -> StartCombatFragmentDirections.actionNavStartCombatToNavAnswerCombat()
            R.id.nav_answer_combat -> AnswerCombatFragmentDirections.actionNavAnswerCombatToNavResolveCombat()
            R.id.nav_resolve_combat -> ResolveCombatFragmentDirections.actionNavResolveCombatToNavMove()
            R.id.nav_pass_off -> currentSection!!.bottomRowAction.actionInto.also { TurnHolder.commitChanges() }
            currentSection!!.bottomRowAction.fragmentNav -> currentSection!!.bottomRowAction.actionOutOf.also { TurnHolder.commitChanges() }
            else -> null
        }
    }

    fun selectSection(selection: Int) {
        TurnHolder.currentTurn.selection = selection
        ScytheDatabase.turnDao()?.updateTurn(TurnHolder.currentTurn)
    }

    private fun getCombatData(round: Int) : CombatRecord? {
        return when(round) {
            1 -> TurnHolder.currentTurn.combatOne
            2 -> TurnHolder.currentTurn.combatTwo
            else -> TurnHolder.currentTurn.combatThree
        }
    }

    private fun determineCombatPhase(round: Int): Int {
        val record = getCombatData(round)

        return when {
            record?.attackerPower == null -> R.id.nav_start_combat
            record.defenderPower == null -> R.id.nav_answer_combat
            else -> R.id.nav_resolve_combat
        }
    }
}