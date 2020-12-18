package org.ajar.scythemobile.ui.enlist

import org.ajar.scythemobile.model.CapitalResourceType
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.player.BottomRowAction
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.BottomRowViewModel

class EnlistViewModel : BottomRowViewModel<BottomRowAction.Enlist>() {
    private var _action: BottomRowAction.Enlist? = null
    override val action: BottomRowAction.Enlist
        get() {
            if(_action == null) {
                _action = TurnHolder.currentPlayer.playerMat.findBottomRowAction(BottomRowAction.Enlist::class.java)
            }
            return _action!!
        }

    var returnNav: Int? = null

    val unrecruited: List<BottomRowAction>
        get() = TurnHolder.currentPlayer.playerMat.sections.map { it.bottomRowAction }.filter { !it.enlisted }

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