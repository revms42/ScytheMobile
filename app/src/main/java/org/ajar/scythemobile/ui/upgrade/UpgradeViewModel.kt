package org.ajar.scythemobile.ui.upgrade

import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.player.BottomRowAction
import org.ajar.scythemobile.model.player.TopRowAction
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.BottomRowViewModel

class UpgradeViewModel : BottomRowViewModel<BottomRowAction.Upgrade>() {
    private var _action: BottomRowAction.Upgrade? = null
    override val action: BottomRowAction.Upgrade
        get() {
            if(_action == null) {
                _action = TurnHolder.currentPlayer.playerMat.findBottomRowAction(BottomRowAction.Upgrade::class.java)
            }
            return _action!!
        }
    var returnNav: Int? = null

    val upgradableBottom: List<BottomRowAction>
        get() = TurnHolder.currentPlayer.playerMat.sections.map { it.bottomRowAction }.filter { it.canUpgrade }

    val upgradableTop: List<TopRowAction>
        get() = TurnHolder.currentPlayer.playerMat.sections.map { it.topRowAction }.filter { it.canUpgrade }

    var leading: Boolean? = null
    var topChosen: TopRowAction? = null
    var bottomChosen: BottomRowAction? = null

    fun performUpgrade() {
        ScytheAction.UpgradeSection(topChosen!!, leading!!, bottomChosen!!)
    }

    fun reset() {
        returnNav = null
        leading = null
        topChosen = null
        bottomChosen = null
    }
}