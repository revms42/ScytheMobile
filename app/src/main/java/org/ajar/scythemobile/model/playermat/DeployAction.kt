package org.ajar.scythemobile.model.playermat

import org.ajar.scythemobile.model.DeployLocationChoice
import org.ajar.scythemobile.model.DeployMechChoice
import org.ajar.scythemobile.model.entity.MechUnit
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.production.MapResourceType
import org.ajar.scythemobile.model.production.PlayerResourceType
import org.ajar.scythemobile.model.turn.DeployTurnAction
import org.ajar.scythemobile.model.turn.TurnAction

class DeployAction(
        override val name: String = "Deploy",
        override val image: Int = -1,
        costStarting: Int,
        costBottom: Int,
        override val coinsGained: Int,
        enlistBonus: PlayerResourceType = PlayerResourceType.COIN
) : AbstractBottomRowAction(enlistBonus, costStarting, costBottom, MapResourceType.METAL) {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(DeployTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(UpgradeDef("Improve Deploy", canUpgrade, upgrade))

    private fun canPerformDeploy(player: Player) : Boolean {
        return player.canPay(cost) && player.factionMat.unlockedMechAbility.size < 4 && player.selectInteractableWorkers().isNotEmpty()
    }

    private fun performDeploy(player: Player) {
        val workers = player.selectInteractableWorkers()
        val abilities = player.factionMat.lockedMechAbilities

        val ability = player.user.requester!!.requestChoice(DeployMechChoice(), abilities)
        val choice = player.user.requester!!.requestChoice(DeployLocationChoice(), workers)

        player.factionMat.unlockMechAbility(ability)

        GameMap.currentMap!!.locateUnit(choice)!!.moveUnitsInto(listOf(MechUnit(player)))
    }

    override var canPerform: (player: Player) -> Boolean = { player: Player -> canPerformDeploy(player) }
    override var performAction: (player: Player) -> Unit = { player: Player -> performDeploy(player) }

}