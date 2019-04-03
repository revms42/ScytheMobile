package org.ajar.scythemobile.model.faction

import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.PredefinedBinaryChoice
import org.ajar.scythemobile.model.combat.CombatBoard
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.map.MapFeature
import org.ajar.scythemobile.model.map.ResourceFeature
import org.ajar.scythemobile.model.map.SpecialFeature
import org.ajar.scythemobile.model.turn.MoveTurnAction

enum class RiverWalk(override val abilityName: String, override val description: String, private vararg val to: MapFeature) : MovementRule {
    VILLAGE_MOUNTAIN("Riverwalk: Village or Mountain", "Move across rivers to mountains or villages", ResourceFeature.MOUNTAIN, ResourceFeature.VILLAGE),
    FOREST_MOUNTAIN("Riverwalk: Forest or Mountain", "Move across rivers to forests or mountains", ResourceFeature.FOREST, ResourceFeature.MOUNTAIN),
    FARM_VILLAGE("Riverwalk: Farm or Village", "Move across rivers to farms or villages", ResourceFeature.FARM, ResourceFeature.VILLAGE),
    FARM_TUNDRA("Riverwalk: Farm or Tundra", "Move across rivers to farms or tundra", ResourceFeature.FARM, ResourceFeature.TUNDRA);

    override val allowsRetreat = false

    override fun canUse(player: Player): Boolean = true

    override fun validStartingHex(hex: MapHex): Boolean = true

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return starting.matchingNeighborsIncludeRivers { to.contains(it) }
    }

    override fun validUnitType(unitType: UnitType): Boolean {
        return unitType == UnitType.CHARACTER || unitType == UnitType.MECH
    }
}

class Burrow : AbstractMovementRule(
        "Burrow",
        "Your character and mechs may cross rivers into, or out of, any adjacent tunnel territory."
) {
    override fun validStartingHex(hex: MapHex): Boolean {
        val tunnelNeighbor = hex.matchingNeighborsIncludeRivers { it == SpecialFeature.TUNNEL }.isNotEmpty()
        return hex.desc.mapFeature.contains(SpecialFeature.TUNNEL) || tunnelNeighbor

    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return if (starting.desc.mapFeature.contains(SpecialFeature.TUNNEL)) {
            starting.nonMatchingNeighborsIncludeRivers { it == SpecialFeature.LAKE }
        } else {
            starting.matchingNeighborsIncludeRivers { it == SpecialFeature.TUNNEL }
        }
    }

}

class Toka : AbstractMovementRule(
        "Toka",
        "Once per turn when moving, either 1 character or " +
                "1 mech may move across a river."
) {

    override fun canUse(player: Player): Boolean {
        return player.turn.findActionOfType(MoveTurnAction::class.java).firstOrNull { it.rule is Toka } == null
    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return starting.nonMatchingNeighborsIncludeRivers { it == SpecialFeature.LAKE }
    }

}

class Underpass : AbstractMovementRule(
        "Underpass",
        "For the purposes of Move actions for your character and mechs, mountains you " +
                "control and all tunnels are considered to be adjacent to each other."
) {
    override fun validStartingHex(hex: MapHex): Boolean {
        return hex.desc.mapFeature.firstOrNull { it == SpecialFeature.TUNNEL || it == ResourceFeature.MOUNTAIN } != null
    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return if(starting.desc.mapFeature.contains(SpecialFeature.TUNNEL)) {
            GameMap.currentMap?.findAllMatching { mapFeature -> mapFeature == ResourceFeature.MOUNTAIN }?.filter { mapHex -> mapHex.playerInControl == starting.playerInControl }
        } else {
            GameMap.currentMap?.findAllMatching { mapFeature -> mapFeature == SpecialFeature.TUNNEL }
        }
    }

}

class Township : AbstractMovementRule(
        "Township",
        "For the purposes of Move actions for your character and mechs, villages you " +
                "control and the Factory are considered to be adjacent to each other."
) {
    override fun validStartingHex(hex: MapHex): Boolean {
        return hex.desc.mapFeature.firstOrNull { mapFeature -> mapFeature == SpecialFeature.FACTORY || mapFeature == ResourceFeature.VILLAGE } != null
    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return if (starting.desc.mapFeature.contains(SpecialFeature.FACTORY)) {
            GameMap.currentMap?.findAllMatching { mapFeature -> mapFeature == ResourceFeature.VILLAGE }?.filter { mapHex -> mapHex.playerInControl == starting.playerInControl }
        } else {
            GameMap.currentMap?.findAllMatching { mapFeature -> mapFeature == SpecialFeature.FACTORY }
        }
    }

}

class Wayfare : AbstractMovementRule(
        "Wayfare",
        "Your character and mechs may move from a territory or home base to any inactive faction’s home " +
                "base or your own regardless of the distance."
) {

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return GameMap.currentMap?.homeBases?.filter { factionHomeHex -> factionHomeHex.player == null || factionHomeHex.player == starting.playerInControl}
    }

}

class Rally : AbstractMovementRule(
        "Rally",
        "When taking a Move action, your character and mechs can move to any territory " +
                "that contains at least one of your workers or a Flag token, regardless of the distance."
) {

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        val list = ArrayList<MapHex?>()
        val listW = GameMap.currentMap?.findUnit(UnitType.WORKER, starting.playerInControl)?.map { gameUnit -> GameMap.currentMap?.locateUnit(gameUnit) }?.toMutableList()
        val listF = GameMap.currentMap?.findUnit(UnitType.FLAG, starting.playerInControl)?.map { gameUnit -> GameMap.currentMap?.locateUnit(gameUnit) }?.toMutableList()

        if(listW != null) list.addAll(listW)
        if(listF != null) list.addAll(listF)

        return list
    }

}

//TODO: Need to be able to arm a trap after move. Not done yet....
class Shinobi : AbstractMovementRule(
        "Shinobi",
        "Your character and mechs can move to any territory with a Trap token, regardless of the distance."
) {

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return GameMap.currentMap?.findUnit(UnitType.TRAP, starting.playerInControl)?.map { gameUnit -> GameMap.currentMap?.locateUnit(gameUnit) }
    }

}

class Seaworthy : AbstractMovementRule(
        "Seaworthy",
        "Your character and mechs can move to and from lakes and retreat onto adjacent lakes.",
        true
) {

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return if (starting.desc.mapFeature.contains(SpecialFeature.LAKE)){
            starting.matchingNeighborsNoRivers { true }
        } else {
            starting.matchingNeighborsNoRivers { mapFeature -> mapFeature == SpecialFeature.LAKE }
        }
    }

}

class Submerge : AbstractMovementRule(
        "Submerge",
        "Your character and mechs may move to and from lakes and move from any lake to another."
) {

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return if (starting.desc.mapFeature.contains(SpecialFeature.LAKE)) {
            GameMap.currentMap?.findAllMatching { mapFeature ->  mapFeature == SpecialFeature.LAKE }?.filter { mapHex -> mapHex != starting}
        } else {
            starting.matchingNeighborsNoRivers { it == SpecialFeature.LAKE }
        }

    }

}


class Disarm : AbstractCombatRule(
        "Disarm",
        "Before you engage in combat on a territory with " +
                "a tunnel or your Mine, the combating opponent loses 2 power."
) {

    override fun validCombatHex(player: Player, combatBoard: CombatBoard): Boolean {
        return combatBoard.combatHex.desc.mapFeature.any { mapFeature -> mapFeature == SpecialFeature.TUNNEL }
    }

    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        combatBoard.getOpposingBoard(player).power -= 2
    }
}

class Artillery : AbstractCombatRule(
        "Artillery",
        "Before you engage in combat, you may pay 1 power to force the combating opponent to lose 2 power."
) {

    override fun validCombatHex(player: Player, combatBoard: CombatBoard): Boolean {
        return combatBoard.getPlayerBoard(player).power > 0
    }

    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        val apply = player.user.requester?.requestBinaryChoice(PredefinedBinaryChoice.USE_ARTILLERY)

        if(apply != null && apply) {
            combatBoard.getOpposingBoard(player).power -= 2
            combatBoard.getPlayerBoard(player).power--
        }
    }
}

class Suiton : AbstractCombatRule(
        "Suiton",
        "Your character and mechs can move to and from lakes. " +
                "If combat occurs on a lake, you may play 1 additional combat card."
), MovementRule {

    override fun canUse(player: Player): Boolean = true

    override fun validUnitType(unitType: UnitType): Boolean {
        return unitType == UnitType.CHARACTER || unitType == UnitType.MECH
    }

    override val allowsRetreat = false

    override fun validCombatHex(player: Player, combatBoard: CombatBoard): Boolean {
        return combatBoard.combatHex.desc.mapFeature. any { mapFeature -> mapFeature == SpecialFeature.LAKE }
    }

    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        combatBoard.getPlayerBoard(player).cardLimit++
    }

    override fun validStartingHex(hex: MapHex): Boolean = true

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return starting.matchingNeighborsNoRivers { it == SpecialFeature.LAKE }
    }

}

class PeoplesArmy : AbstractCombatRule(
        "People's Army",
        "In combat where you have at least 1 worker, you may play one additional combat card."
) {

    override fun validCombatHex(player: Player, combatBoard: CombatBoard): Boolean {
        return combatBoard.getPlayerBoard(player).unitsPresent.any { gameUnit -> gameUnit.type == UnitType.WORKER }
    }

    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        combatBoard.getPlayerBoard(player).cardLimit++
    }

}

class Scout : AbstractCombatRule(
        "Scout",
        "Before you engage in combat, steal one of the opponent’s combat cards at random and add it to your hand"
) {

    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        val cards = combatBoard.getOpposingBoard(player).cardsAvailable
        val cardToSteal = cards.toList()[(Math.random() * cards.size).toInt()]
        cards.remove(cardToSteal)
        combatBoard.getPlayerBoard(player).cardsAvailable.add(cardToSteal)
    }

}

class Sword : AbstractCombatRule(
        "Sword",
        "Before you engage in combat as the attacker, the defender loses 2 power.",
        appliesForDefense = false
) {
    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        combatBoard.getOpposingBoard(player).power -= 2
    }

}

class Shield : AbstractCombatRule(
        "Shield",
        "Before you engage in combat as the defender, " +
        "gain 2 power.",
        appliesForAttack = false
) {
    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        combatBoard.getPlayerBoard(player).power += 2
    }
}

class Ronin : AbstractCombatRule(
        "Ronin",
        "Before combat where you have exactly 1 unit " +
                "(0 workers and either 1 character or 1 mech), " +
                "you may gain 2 power on the Power Track."
) {

    override fun validCombatHex(player: Player, combatBoard: CombatBoard): Boolean {
        return combatBoard.getPlayerBoard(player).unitsPresent.size == 1
    }

    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        combatBoard.getPlayerBoard(player).power += 2
    }
}

class Camaraderie : AbstractCombatRule(
        "Camaraderie",
        "You do not lose popularity when forcing an opponent’s workers to retreat after winning combat as the aggressor."
) {
    override val appliesDuringUncontested: Boolean = true
    override val appliesForDefense: Boolean = false

    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        combatBoard.getPlayerBoard(player).camaraderie = true
    }

}
