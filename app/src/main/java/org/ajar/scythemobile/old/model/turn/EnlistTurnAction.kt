package org.ajar.scythemobile.old.model.turn

import org.ajar.scythemobile.old.model.playermat.BottomRowAction
import org.ajar.scythemobile.old.model.production.MapResource
import org.ajar.scythemobile.old.model.production.PlayerResourceType

class EnlistTurnAction(val paid: List<MapResource>, val enlisted: Class<out BottomRowAction>, val bonus: PlayerResourceType) : TurnAction {
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
