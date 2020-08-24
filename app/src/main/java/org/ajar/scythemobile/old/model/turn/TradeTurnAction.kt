package org.ajar.scythemobile.old.model.turn

import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.old.model.production.ResourceType

class TradeTurnAction(val resourcesGained: List<ResourceType>, val workerUnit: GameUnit? = null) : TurnAction {
    override fun serialize(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deserialize(from: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun applyTurnAction() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}