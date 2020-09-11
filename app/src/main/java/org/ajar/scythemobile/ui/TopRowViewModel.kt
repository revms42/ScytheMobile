package org.ajar.scythemobile.ui

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.model.player.TopRowAction
import org.ajar.scythemobile.turn.TurnHolder

abstract class TopRowViewModel<A: TopRowAction> : ViewModel() {

    abstract val action: A

    val bottomRowNav: Int
        get() = TurnHolder.currentPlayer.playerMat.findSection(action::class.java)!!.moveTopToBottom
}