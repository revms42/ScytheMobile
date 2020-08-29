package org.ajar.scythemobile.old.model.turn

import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.faction.MovementRule
import org.ajar.scythemobile.old.model.map.MapHex
import org.ajar.scythemobile.old.model.production.MapResource

class MoveTurnAction(val unit: GameUnit, val from: MapHex, val to: MapHex, val transported: List<GameUnit> = emptyList(), val rule: MovementRule) : TurnAction {
    override fun serialize(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deserialize(from: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun applyTurnAction() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toString(): String {
        return "Unit $unit moved from $from to $to transporting ${transported.size} units using $rule"
    }
}

class TransportResourcesAction(val unit: GameUnit, val from: MapHex, val to: MapHex, val transported: List<MapResource> = emptyList()) : TurnAction {
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