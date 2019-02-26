package org.ajar.scythemobile.model.faction

import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.LimitedUsePerTurn
import org.ajar.scythemobile.model.PredefinedBinaryChoice
import org.ajar.scythemobile.model.combat.CombatBoard
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.map.MapFeature
import org.ajar.scythemobile.model.map.ResourceFeature
import org.ajar.scythemobile.model.map.SpecialFeature

enum class RiverWalk(override val abilityName: String, private vararg val to: MapFeature) : MovementRule {
    VILLAGE_MOUNTAIN("Riverwalk: Village or Mountain", ResourceFeature.MOUNTAIN, ResourceFeature.VILLAGE),
    FOREST_MOUNTAIN("Riverwalk: Forest or Mountain", ResourceFeature.FOREST, ResourceFeature.MOUNTAIN),
    FARM_VILLAGE("Riverwalk: Farm or Village", ResourceFeature.FARM, ResourceFeature.VILLAGE),
    FARM_TUNDRA("Riverwalk: Farm or Tundra", ResourceFeature.FARM, ResourceFeature.TUNDRA);

    override val allowsRetreat = false

    override fun validStartingHex(hex: MapHex): Boolean {
        return true
    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return starting.matchingNeighborsIncludeRivers { to.contains(it) }
    }
}

class Burrow : AbstractMovementRule("Burrow") {
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

class Toka : AbstractMovementRule("Toka"), LimitedUsePerTurn {
    override val usesPerTurn: Int = 1

    private var usesThisTurn: Int = 0
    override val usesRemaining: Int
        get() = usesPerTurn - usesThisTurn

    override fun incrimentUses() {
        usesThisTurn++
    }

    override fun decrementUses() {
        usesThisTurn--
    }

    override fun resetUses() {
        usesThisTurn = 0
    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return starting.nonMatchingNeighborsIncludeRivers { it == SpecialFeature.LAKE }
    }

}

class Underpass : AbstractMovementRule("Underpass") {
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

class Township : AbstractMovementRule("Township") {
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

class Wayfare : AbstractMovementRule("Wayfare") {

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return GameMap.currentMap?.homeBases?.filter { factionHomeHex -> factionHomeHex.player == null || factionHomeHex.player == starting.playerInControl}
    }

}

class Rally : AbstractMovementRule("Rally") {

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
class Shinobi : AbstractMovementRule("Shinobi") {

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return GameMap.currentMap?.findUnit(UnitType.TRAP, starting.playerInControl)?.map { gameUnit -> GameMap.currentMap?.locateUnit(gameUnit) }
    }

}

class Seaworthy : AbstractMovementRule("Seaworthy", true) {

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return if (starting.desc.mapFeature.contains(SpecialFeature.LAKE)){
            starting.matchingNeighborsNoRivers { _ -> true }
        } else {
            starting.matchingNeighborsNoRivers { mapFeature -> mapFeature == SpecialFeature.LAKE }
        }
    }

}

class Submerge : AbstractMovementRule("Submerge") {

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return if (starting.desc.mapFeature.contains(SpecialFeature.LAKE)) {
            GameMap.currentMap?.findAllMatching { mapFeature ->  mapFeature == SpecialFeature.LAKE }?.filter { mapHex -> mapHex != starting}
        } else {
            starting.matchingNeighborsNoRivers { it == SpecialFeature.LAKE }
        }

    }

}

class StandardMove : AbstractMovementRule("Standard Move") {
    override fun validStartingHex(hex: MapHex): Boolean {
        return !hex.desc.mapFeature.contains(SpecialFeature.LAKE)
    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return starting.nonMatchingNeighborsNoRivers { it == SpecialFeature.LAKE }
    }

}

class TunnelMove : AbstractMovementRule("Tunnel") {
    override fun validStartingHex(hex: MapHex): Boolean {
        return hex.desc.mapFeature.contains(SpecialFeature.TUNNEL)
    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return GameMap.currentMap?.findAllMatching { mapFeature -> mapFeature == SpecialFeature.TUNNEL}?.filter { mapHex -> mapHex != starting }
    }

}

class Disarm : AbstractCombatRule("Disarm") {

    override fun validCombatHex(player: Player, combatBoard: CombatBoard): Boolean {
        return combatBoard.combatHex.desc.mapFeature.any { mapFeature -> mapFeature == SpecialFeature.TUNNEL }
    }

    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        combatBoard.getOpposingBoard(player).power -= 2
    }
}

class Artillery : AbstractCombatRule("Artillery") {

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

class Suiton : AbstractCombatRule("Suiton"), MovementRule {
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

class PeoplesArmy : AbstractCombatRule("People's Army") {

    override fun validCombatHex(player: Player, combatBoard: CombatBoard): Boolean {
        return combatBoard.getPlayerBoard(player).unitsPresent.any { gameUnit -> gameUnit.type == UnitType.WORKER }
    }

    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        combatBoard.getPlayerBoard(player).cardLimit++
    }

}

class Scout : AbstractCombatRule("Scout") {
    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        val cards = combatBoard.getOpposingBoard(player).cardsAvailable
        val cardToSteal = cards.toList()[(Math.random() * cards.size).toInt()]
        cards.remove(cardToSteal)
        combatBoard.getPlayerBoard(player).cardsAvailable.add(cardToSteal)
    }

}

class Sword : AbstractCombatRule("Sword", appliesForDefense = false) {
    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        combatBoard.getOpposingBoard(player).power -= 2
    }

}

class Shield : AbstractCombatRule("Shield", appliesForAttack = false) {
    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        combatBoard.getPlayerBoard(player).power += 2
    }
}

class Ronin : AbstractCombatRule("Ronin") {

    override fun validCombatHex(player: Player, combatBoard: CombatBoard): Boolean {
        return combatBoard.getPlayerBoard(player).unitsPresent.size == 1
    }

    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        combatBoard.getPlayerBoard(player).power += 2
    }
}

class Camaraderie : AbstractCombatRule("Camaraderie") {
    override val appliesDuringUncontested: Boolean = true
    override val appliesForDefense: Boolean = false

    override fun applyEffect(player: Player, combatBoard: CombatBoard) {
        combatBoard.getPlayerBoard(player).camaraderie = true
    }

}

abstract class AbstractCombatRule(
        override val abilityName: String,
        override val appliesForAttack: Boolean = true,
        override val appliesForDefense: Boolean = true,
        override val appliesDuringUncontested: Boolean = false
) : CombatRule {
    override fun validCombatHex(player: Player, combatBoard: CombatBoard): Boolean = true
}

interface CombatRule : FactionMechAbility {

    val appliesForAttack: Boolean
    val appliesForDefense : Boolean
    val appliesDuringUncontested: Boolean
    fun validCombatHex(player: Player, combatBoard: CombatBoard) : Boolean
    fun applyEffect(player: Player, combatBoard: CombatBoard)
}

abstract class AbstractMovementRule(override val abilityName: String, override val allowsRetreat: Boolean = false) : MovementRule {

    override fun validStartingHex(hex: MapHex): Boolean = true
}

interface MovementRule : FactionMechAbility {
    fun validStartingHex(hex: MapHex) : Boolean
    fun validEndingHexes(starting: MapHex) : List<MapHex?>?
    val allowsRetreat : Boolean
}

class Speed : FactionMechAbility {
    override val abilityName = "Speed"

    companion object {
        val singleton = Speed()
    }
}

interface FactionMechAbility { //TODO: Subclass of 'Rule'?
    val abilityName: String
}
