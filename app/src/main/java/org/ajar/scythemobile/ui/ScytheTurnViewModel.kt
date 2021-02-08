package org.ajar.scythemobile.ui

import androidx.lifecycle.ViewModel
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

    fun finishSection(current: Int) {
        when(current) {
            currentSection?.topRowAction?.fragmentNav -> TurnHolder.currentTurn.performedTop = true
            currentSection?.bottomRowAction?.fragmentNav -> TurnHolder.currentTurn.performedBottom = true
        }
        TurnHolder.commitChanges()
    }

    fun isTopRowComplete() : Boolean {
        return TurnHolder.currentTurn.performedTop
    }
    fun isBottomRowComplete() : Boolean {
        return TurnHolder.currentTurn.performedBottom
    }

    fun selectSection(selection: Int) {
        TurnHolder.currentTurn.selection = selection
        ScytheDatabase.turnDao()?.updateTurn(TurnHolder.currentTurn)
    }
}