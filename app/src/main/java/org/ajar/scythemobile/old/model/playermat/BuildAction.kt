package org.ajar.scythemobile.old.model.playermat

import org.ajar.scythemobile.old.model.entity.Player
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.old.model.production.MapResourceType
import org.ajar.scythemobile.old.model.production.PlayerResourceType
import org.ajar.scythemobile.old.model.turn.BuildTurnAction
import org.ajar.scythemobile.old.model.turn.TurnAction

class BuildAction(
        override val name: String = "Build",
        override val image: Int = -1,
        costStarting: Int,
        costBottom: Int,
        override val coinsGained: Int,
        enlistBonus: PlayerResourceType = PlayerResourceType.POPULARITY
) : AbstractBottomRowAction(enlistBonus, costStarting, costBottom, MapResourceType.WOOD) {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(BuildTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(UpgradeDef("Improve Deploy", canUpgrade, upgrade))

    private fun canPerformBuild(player: Player) : Boolean {
        return player.canPay(cost) && player.selectUnits(UnitType.STRUCTURE).size < 4
    }

    private fun performBuild(player: Player) {
        TODO("PERFORM BUILD")
    }

    override var canPerform: (player: Player) -> Boolean = { player: Player -> canPerformBuild(player) }
    override var performAction: (player: Player) -> Unit = { player: Player -> performBuild(player) }

}