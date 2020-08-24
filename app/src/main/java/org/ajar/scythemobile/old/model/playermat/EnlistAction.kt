package org.ajar.scythemobile.old.model.playermat

import org.ajar.scythemobile.old.model.entity.Player
import org.ajar.scythemobile.old.model.production.MapResourceType
import org.ajar.scythemobile.old.model.production.PlayerResourceType
import org.ajar.scythemobile.old.model.turn.EnlistTurnAction
import org.ajar.scythemobile.old.model.turn.TurnAction

class EnlistAction(
        override val name: String = "Enlist",
        override val image: Int = -1,
        costStarting: Int,
        costBottom: Int,
        override val coinsGained: Int,
        enlistBonus: PlayerResourceType = PlayerResourceType.COMBAT_CARD
) : AbstractBottomRowAction(enlistBonus, costStarting, costBottom, MapResourceType.FOOD) {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(EnlistTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(UpgradeDef("Improve Enlist", canUpgrade, upgrade))

    private fun canPerformEnlist(player: Player) : Boolean {
        return player.canPay(cost) && player.playerMat.sections.any { !it.sectionDef.bottomRowAction.isEnlisted }
    }

    private fun performEnlist(player: Player) {
        TODO("PERFORM ENLIST")
    }

    override var canPerform: (player: Player) -> Boolean = { player: Player -> canPerformEnlist(player) }
    override var performAction: (player: Player) -> Unit = { player: Player -> performEnlist(player) }

}