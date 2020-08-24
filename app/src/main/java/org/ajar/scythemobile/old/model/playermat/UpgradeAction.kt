package org.ajar.scythemobile.old.model.playermat

import org.ajar.scythemobile.old.model.entity.Player
import org.ajar.scythemobile.old.model.production.MapResourceType
import org.ajar.scythemobile.old.model.production.PlayerResourceType
import org.ajar.scythemobile.old.model.turn.TurnAction
import org.ajar.scythemobile.old.model.turn.UpgradeTurnAction

class UpgradeAction(
        override val name: String = "Upgrade",
        override val image: Int = -1,
        startingCost: Int,
        costBottom: Int,
        override val coinsGained: Int,
        enlistBonus: PlayerResourceType = PlayerResourceType.POWER
) : AbstractBottomRowAction(enlistBonus, startingCost, costBottom, MapResourceType.OIL) {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(UpgradeTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(UpgradeDef("Improve Upgrade", canUpgrade, upgrade))

    private fun canPerformUpgrade(player: Player) : Boolean{
        return player.canPay(cost) && player.playerMat.sections.firstOrNull { sectionDef ->
            sectionDef.sectionDef.topRowAction.upgradeActions.firstOrNull { it.check.invoke() } != null ||
                    sectionDef.sectionDef.bottomRowAction.upgradeActions.firstOrNull { it.check.invoke() } != null
        } != null
    }

    private fun performUpgrade(player: Player) {
        TODO("PERFORM UPGRADE")
    }

    override var canPerform: (player: Player) -> Boolean = { player: Player -> canPerformUpgrade(player) }
    override var performAction: (player: Player) -> Unit = { player: Player -> performUpgrade(player) }
}