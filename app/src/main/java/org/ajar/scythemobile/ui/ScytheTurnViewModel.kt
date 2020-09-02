package org.ajar.scythemobile.ui

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.R
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.player.Section
import org.ajar.scythemobile.turn.TurnHolder

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

    fun selectSection(selection: Int) {
        TurnHolder.currentTurn.selection = selection
        ScytheDatabase.turnDao()?.updateTurn(TurnHolder.currentTurn)
    }

    private fun determineCombatPhase(round: Int): Int {
        //TODO: Make this figure out combat phase.
        return R.id.nav_start_combat
    }
}