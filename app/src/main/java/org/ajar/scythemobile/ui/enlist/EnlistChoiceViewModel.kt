package org.ajar.scythemobile.ui.enlist

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.player.BottomRowAction
import org.ajar.scythemobile.turn.TurnHolder

class EnlistChoiceViewModel : ViewModel() {
    var returnNav: Int? = null

    val unrecruited: List<BottomRowAction>
        get() = TurnHolder.currentPlayer.playerMat.sections.map { it.bottomRowAction }.filter { !it.recruited }

    val oneTimeBenefist: List<CapitalResourceType>
        get() = TurnHolder.currentPlayer.factionMat.getEnlistmentBonusesAvailabe()

    var bonusType: CapitalResourceType? = null
    var sectionChosen: BottomRowAction? = null

    fun performEnlist() {
        ScytheAction.EnlistSection(TurnHolder.currentPlayer, sectionChosen!!, bonusType!!).perform()
    }

    fun reset() {
        returnNav = null
        bonusType = null
        sectionChosen = null
    }
}