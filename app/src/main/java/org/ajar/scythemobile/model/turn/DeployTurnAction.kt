package org.ajar.scythemobile.model.turn

import org.ajar.scythemobile.model.faction.FactionAbility
import org.ajar.scythemobile.model.production.MapResource

class DeployTurnAction(val paid: List<MapResource>, val factionAbility: FactionAbility) : TurnAction {
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
