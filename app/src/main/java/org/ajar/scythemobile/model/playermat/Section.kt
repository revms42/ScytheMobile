package org.ajar.scythemobile.model.playermat

import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.production.MapResourceType
import org.ajar.scythemobile.model.production.PlayerResourceType
import org.ajar.scythemobile.model.production.ResourceType
import org.ajar.scythemobile.model.turn.TurnAction


data class SectionInstance(val sectionDef: SectionDef)

class SectionDef(val topRowAction: TopRowAction, val bottomRowAction: BottomRowAction) {

    val sectionSelectable: (player: Player) -> Boolean = { player: Player ->  isSectionSelectable(player) }

    private fun isSectionSelectable(player: Player) : Boolean {
        val all = ArrayList(topRowAction.actionClassTypes)
        all.addAll(bottomRowAction.actionClassTypes)

        return player.turn.hasAnyOfTypes(all)
    }
}

data class UpgradeDef(val name: String, val check: () -> Boolean, val perform: () -> Unit)

interface PlayerMatAction {
    val name: String
    val image:  Int
    val upgradeActions: Collection<UpgradeDef>

    var canPerform: (player: Player) -> Boolean
    var performAction: (player: Player) -> Unit

    val actionClassTypes: Collection<Class<out TurnAction>>
}

interface TopRowAction : PlayerMatAction {
    val cost: List<ResourceType>
}

interface BottomRowAction : PlayerMatAction {
    val coinsGained: Int
    val cost: List<ResourceType>

    val isEnlisted: Boolean
    fun enlist(player: Player)
}

abstract class AbstractBottomRowAction(
        private val enlistBonus: PlayerResourceType,
        costStarting: Int,
        costBottom: Int,
        private val resourceType: MapResourceType
) : BottomRowAction {
    private var _enlisted: Boolean = false

    var canUpgrade: () -> Boolean = { _cost > costBottom }
    var upgrade: () -> Unit = {_cost--}

    override val isEnlisted: Boolean
        get() = _enlisted
    override fun enlist(player: Player) {
        _enlisted = true
        when(enlistBonus) {
            PlayerResourceType.COIN -> player.coins += 2
            PlayerResourceType.POPULARITY -> player.popularity += 2
            PlayerResourceType.POWER -> player.power += 2
            PlayerResourceType.COMBAT_CARD -> {
                player.combatCards.addAll(listOf(CombatCardDeck.currentDeck.drawCard(), CombatCardDeck.currentDeck.drawCard()))
            }
        }
    }

    private var _cost: Int = costStarting
    override val cost: List<ResourceType>
        get() = (0.._cost).map { resourceType }
}