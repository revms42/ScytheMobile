package org.ajar.scythemobile.model.faction

import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.old.model.combat.CombatBoard
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.map.TerrainFeature

enum class DefaultFactionAbility(override val abilityName: String, override val description: String) : FactionAbility {
    MEANDER(
            "Meander",
            "Pick up to 2 options per encounter card."
    ),
    SWIM(
            "Swim",
            "Your workers may move across rivers."
    ),
    COERCION(
            "Coercion",
            "Once per turn, you may spend 1 combat card as if it were any 1 mapResource token."
    ),
    RELENTLESS(
            "Relentless",
            "You may choose the same section on your Player Mat as the previous turn(s)."
    ),
    DOMINATE(
            "Dominate",
            "There is no limit to the number of stars you can place from completing objectives or winning combat."
    ),
    EXALT(
            "Exalt",
            "After ending your character’s movement, the Albion player may place a Flag token from their supply on the character’s territory."
    ),
    MAIFUKU(
            "Maifuku",
            "After ending your " +
                    "character’s movement, the Togawa player may place " +
                    "an armed Trap token of their choice from " +
                    "their supply onto the character’s territory."
    );
}

class Swim : AbstractMovementRule(
        "Swim",
        "Your workers may move across rivers."
) {
    override fun validUnitType(unitType: UnitType): Boolean {
        return unitType == UnitType.WORKER
    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return starting.matchingNeighborsIncludeRivers { it?.terrain != TerrainFeature.LAKE.ordinal }
    }

}

class StandardMove : AbstractMovementRule(
        "Standard Move",
        "Standard movement"
) {
    override fun validUnitType(unitType: UnitType): Boolean {
        return true
    }

    override fun validStartingHex(hex: MapHex): Boolean {
        return hex.data.terrain != TerrainFeature.LAKE.ordinal
    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return starting.nonMatchingNeighborsNoRivers { it?.terrain == TerrainFeature.LAKE.ordinal }
    }
}

class TunnelMove : AbstractMovementRule(
        "Tunnel",
        "For the purposes of the Move action for any unit, all territories with the tunnel icon are considered to be adjacent to each other."
) {

    override fun validStartingHex(hex: MapHex): Boolean {
        return hex.data.tunnel
    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return GameMap.currentMap.findAllMatching { mapFeature -> mapFeature?.tunnel?: false}?.filter { mapHex -> mapHex != starting }
    }

}

abstract class AbstractCombatRule(
        override val abilityName: String,
        override val description: String,
        override val appliesForAttack: Boolean = true,
        override val appliesForDefense: Boolean = true,
        override val appliesDuringUncontested: Boolean = false
) : CombatRule {

    override fun validCombatHex(player: PlayerInstance, combatBoard: CombatBoard): Boolean = true
}

interface CombatRule : FactionAbility {

    val appliesForAttack: Boolean
    val appliesForDefense : Boolean
    val appliesDuringUncontested: Boolean
    fun validCombatHex(player: PlayerInstance, combatBoard: CombatBoard) : Boolean
    fun applyEffect(player: PlayerInstance, combatBoard: CombatBoard)
}

abstract class AbstractMovementRule(
        override val abilityName: String,
        override val description: String,
        override val allowsRetreat: Boolean = false
) : MovementRule {

    override fun validStartingHex(hex: MapHex): Boolean = true
    override fun canUse(player: PlayerInstance) = true
    override fun validUnitType(unitType: UnitType): Boolean {
        return unitType == UnitType.MECH || unitType == UnitType.CHARACTER
    }
}

interface MovementRule : FactionAbility {
    fun canUse(player: PlayerInstance) : Boolean
    fun validStartingHex(hex: MapHex) : Boolean
    fun validEndingHexes(starting: MapHex) : List<MapHex?>?
    fun validUnitType(unitType: UnitType) : Boolean
    val allowsRetreat : Boolean
}

class Speed : FactionAbility {
    override val abilityName = "Speed"
    override val description = "Your character and mechs may move one additional territory when moving."

    companion object {
        val singleton = Speed()
    }
}

interface FactionAbility {
    val abilityName: String
    val description: String
}
