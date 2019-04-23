package org.ajar.scythemobile.model.playermat

import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.production.PlayerResourceType
import org.ajar.scythemobile.model.production.ResourceType
import org.ajar.scythemobile.model.turn.ProduceTurnAction
import org.ajar.scythemobile.model.turn.TurnAction

class ProduceAction (
        override val name: String = "Produce Action",
        override val image: Int = -1,
        numberofTerritoriesStart: Int = 2,
        private val numberOfTerritoriesTop: Int = 3
) : TopRowAction {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(ProduceTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(
                UpgradeDef("Upgrade Territories Harvested", canUpgradeTerritories, upgradeTerritories)
        )

    private val numberOfWorkersTop: Int = 6
    private val _numberOfWorkersProduced: Int = 0
    private val numberOfWorkersProduced: Int
        get() = _numberOfWorkersProduced

    override val cost: List<ResourceType>
        get() {
            return (0..numberOfWorkersProduced).filter { it > 2 && it % 2 == 0 }.map {
                when (it) {
                    2 -> PlayerResourceType.POWER
                    4 -> PlayerResourceType.POPULARITY
                    else -> PlayerResourceType.COIN
                }
            }
        }


    private var _numberOfTerritories: Int = numberofTerritoriesStart
    private val numberOfTerritories: Int
        get() = _numberOfTerritories

    private val canProduceWorker: () -> Boolean = { numberOfWorkersProduced < numberOfTerritoriesTop }
    private val canUpgradeTerritories: () -> Boolean = { numberOfTerritories < numberOfTerritoriesTop }


    var upgradeTerritories: () -> Unit = { _numberOfTerritories++ }

    private fun canProduce(player: Player): Boolean {
        return player.canPay(cost)
    }

    private fun performProduce(player: Player) {
        TODO("PRODUCE")
    }

    private fun produceWorker(player: Player) {
        TODO("PRODUCE WORKER")
    }

    override var canPerform: (player: Player) -> Boolean = { player: Player -> canProduce(player) }
    override var performAction: (player: Player) -> Unit = { player: Player -> performProduce(player) }
}