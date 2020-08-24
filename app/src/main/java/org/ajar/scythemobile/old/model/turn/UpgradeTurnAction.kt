package org.ajar.scythemobile.old.model.turn

import org.ajar.scythemobile.old.model.playermat.UpgradeDef
import org.ajar.scythemobile.old.model.production.MapResource

class UpgradeTurnAction(val paid: List<MapResource>, val topRow: UpgradeDef, val bottomRow: UpgradeDef) : TurnAction {
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