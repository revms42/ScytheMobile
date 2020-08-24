package org.ajar.scythemobile.old.model.playermat

import org.ajar.scythemobile.old.model.PredefinedBinaryChoice
import org.ajar.scythemobile.old.model.TradeLocationChoice
import org.ajar.scythemobile.old.model.TradeResourceChoice
import org.ajar.scythemobile.old.model.entity.Player
import org.ajar.scythemobile.old.model.production.MapResource
import org.ajar.scythemobile.old.model.production.MapResourceType
import org.ajar.scythemobile.old.model.production.PlayerResourceType
import org.ajar.scythemobile.old.model.production.ResourceType
import org.ajar.scythemobile.old.model.turn.TradeTurnAction
import org.ajar.scythemobile.old.model.turn.TurnAction

class TradeAction (
        override val name: String = "Trade Action",
        override val image: Int = -1,
        override val cost: List<ResourceType> = listOf(PlayerResourceType.COIN),
        resourceGainStart: Int = 2,
        popularityGainStart: Int = 1,
        private val resourceGainTop: Int = 2,
        private val popularityGainTop: Int = 2
) : TopRowAction {
    override val actionClassTypes: Collection<Class<out TurnAction>> = listOf(TradeTurnAction::class.java)

    override val upgradeActions: Collection<UpgradeDef>
        get() = listOf(
                UpgradeDef("Upgrade Resources Gained", canUpgradeResource, upgradeResources),
                UpgradeDef("Upgrade Popularity Gained", canUpgradePopularity, upgradePopularity)
        )

    private var _resourceGain: Int = resourceGainStart
    private val resourceGain: Int
        get() = _resourceGain

    private var _popularityGain: Int = popularityGainStart
    private val popularityGain: Int
        get() = _popularityGain

    private val canUpgradeResource: () -> Boolean = {resourceGain < resourceGainTop}
    private val canUpgradePopularity: () -> Boolean = {popularityGain < popularityGainTop}

    private val upgradeResources: () -> Unit = {_resourceGain++}
    private val upgradePopularity: () -> Unit = {_popularityGain++}

    private fun canPerformTrade(player: Player) : Boolean {
        return player.canPay(cost) && player.selectInteractableWorkers().isNotEmpty()
    }

    private fun performTrade(player: Player) {
        if(player.user.requester!!.requestBinaryChoice(PredefinedBinaryChoice.ACQUIRE_POPULARITY)) {
            player.popularity++
        } else {
            val workers = player.selectInteractableWorkers()

            val first = player.user.requester!!.requestSelection(TradeResourceChoice.FIRST_CHOICE, MapResourceType.values().toList(), 1)
            val firstWorker = player.user.requester!!.requestSelection(TradeLocationChoice(), workers, 1)

            val second = player.user.requester!!.requestSelection(TradeResourceChoice.SECOND_CHOICE, MapResourceType.values().toList(), 1)
            val secondWorker = player.user.requester!!.requestSelection(TradeLocationChoice(), workers, 1)

            firstWorker.first().heldMapResources.add(MapResource(first.first()))
            secondWorker.first().heldMapResources.add(MapResource(second.first()))
        }
        player.payResources(cost)
    }

    override var canPerform: (player: Player) -> Boolean = { player: Player ->  canPerformTrade(player)}
    override var performAction: (player: Player) -> Unit = { player: Player -> performTrade(player) }
}