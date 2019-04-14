package org.ajar.scythemobile.model.turn

import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.production.MapResource

class BuildTurnAction(val paid: List<MapResource>, val building: GameUnit, val hex: MapHex) : TurnAction {
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
