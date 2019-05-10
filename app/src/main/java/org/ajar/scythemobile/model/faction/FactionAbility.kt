package org.ajar.scythemobile.model.faction

import org.ajar.scythemobile.model.combat.CombatBoard
import org.ajar.scythemobile.model.entity.*
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.map.SpecialFeature

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
            "Coercion NYI",
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
        return starting.matchingNeighborsIncludeRivers { it != SpecialFeature.LAKE }
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
        return !hex.desc.mapFeature.contains(SpecialFeature.LAKE)
    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return starting.nonMatchingNeighborsNoRivers { it == SpecialFeature.LAKE }
    }
}

class TunnelMove : AbstractMovementRule(
        "Tunnel",
        "For the purposes of the Move action for any unit, all territories with the tunnel icon are considered to be adjacent to each other."
) {

    override fun validStartingHex(hex: MapHex): Boolean {
        return hex.desc.mapFeature.contains(SpecialFeature.TUNNEL)
    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return GameMap.currentMap?.findAllMatching { mapFeature -> mapFeature == SpecialFeature.TUNNEL}?.filter { mapHex -> mapHex != starting }
    }

}

//@Deprecated("Redone")
//class Exalt: AbstractTokenPlacementAbility (
//        "Exalt",
//        "After ending your character’s movement, the you may place a Flag token from your supply on the character’s territory."
//) {
//    override val choice: Choice = exaltChoice
//
//    override fun createTokens(player: Player): MutableList<GameUnit> {
//        return mutableListOf(
//                FlagUnit(player),
//                FlagUnit(player),
//                FlagUnit(player),
//                FlagUnit(player)
//        )
//    }
//
//    companion object {
//        private var _exaltChoice: ExaltChoice? = null
//        val exaltChoice: ExaltChoice
//            get() {
//                if(_exaltChoice == null) {
//                    _exaltChoice = ExaltChoice()
//                }
//                return _exaltChoice!!
//            }
//    }
//}
//
//@Deprecated("Redone")
//class Maifuku : AbstractTokenPlacementAbility (
//        "Maifuku",
//        "After ending your " +
//                "character’s movement, the Togawa player may place " +
//                "an armed Trap token of their choice from " +
//                "their supply onto the character’s territory."
//) {
//    override fun createTokens(player: Player): MutableList<GameUnit> {
//        return mutableListOf(
//                TrapUnit(player, TrapType.MAIFUKU_LOSE_CARDS),
//                TrapUnit(player, TrapType.MAIFUKU_LOSE_MONEY),
//                TrapUnit(player, TrapType.MAIFUKU_LOSE_POP),
//                TrapUnit(player, TrapType.MAIFUKU_LOSE_POWER)
//        )
//    }
//
//    override val choice: Choice
//        get() = maifukuChoice
//
//    companion object {
//        private var _maifukuChoice: MaifukuChoice? = null
//        val maifukuChoice: MaifukuChoice
//            get() {
//                if(_maifukuChoice == null) {
//                    _maifukuChoice = MaifukuChoice()
//                }
//                return _maifukuChoice!!
//            }
//    }
//}
//
//abstract class AbstractTokenPlacementAbility(
//        override val abilityName: String,
//        override val description: String
//) : TokenPlacementAbility {
//
//    abstract val choice: Choice
//
//    override fun validLocation(player: Player, unit: GameUnit, hex: MapHex): Boolean {
//        return unit.type == UnitType.CHARACTER &&
//                hex.playerInControl == player &&
//                hex.unitsPresent.firstOrNull { it.type == UnitType.TRAP || it.type == UnitType.FLAG } == null
//    }
//
//    override fun selectToken(player: Player, tokens: MutableCollection<GameUnit>): GameUnit? {
//        return player.user.requester?.requestCancellableChoice(choice, tokens)
//    }
//}
//
//interface TokenPlacementAbility : FactionAbility {
//    fun validLocation(player: Player, unit: GameUnit, hex:MapHex) : Boolean
//    fun selectToken(player: Player, tokens: MutableCollection<GameUnit>): GameUnit?
//    fun createTokens(player: Player): MutableList<GameUnit>
//}

abstract class AbstractCombatRule(
        override val abilityName: String,
        override val description: String,
        override val appliesForAttack: Boolean = true,
        override val appliesForDefense: Boolean = true,
        override val appliesDuringUncontested: Boolean = false
) : CombatRule {

    override fun validCombatHex(player: Player, combatBoard: CombatBoard): Boolean = true
}

interface CombatRule : FactionAbility {

    val appliesForAttack: Boolean
    val appliesForDefense : Boolean
    val appliesDuringUncontested: Boolean
    fun validCombatHex(player: Player, combatBoard: CombatBoard) : Boolean
    fun applyEffect(player: Player, combatBoard: CombatBoard)
}

abstract class AbstractMovementRule(
        override val abilityName: String,
        override val description: String,
        override val allowsRetreat: Boolean = false
) : MovementRule {

    override fun validStartingHex(hex: MapHex): Boolean = true
    override fun canUse(player: Player) = true
    override fun validUnitType(unitType: UnitType): Boolean {
        return unitType == UnitType.MECH || unitType == UnitType.CHARACTER
    }
}

interface MovementRule : FactionAbility {
    fun canUse(player: Player) : Boolean
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
