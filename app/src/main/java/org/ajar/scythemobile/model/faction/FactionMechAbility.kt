package org.ajar.scythemobile.model.faction

import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.combat.Battle
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.combat.CombatBoard
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.map.TerrainFeature

enum class RiverWalk(override val abilityName: String, override val description: String, private vararg val to: TerrainFeature) : MovementRule {
    VILLAGE_MOUNTAIN("Riverwalk: Village or Mountain", "Move across rivers to mountains or villages", TerrainFeature.MOUNTAIN, TerrainFeature.VILLAGE),
    FOREST_MOUNTAIN("Riverwalk: Forest or Mountain", "Move across rivers to forests or mountains", TerrainFeature.FOREST, TerrainFeature.MOUNTAIN),
    FARM_VILLAGE("Riverwalk: Farm or Village", "Move across rivers to farms or villages", TerrainFeature.FIELD, TerrainFeature.VILLAGE),
    FARM_TUNDRA("Riverwalk: Farm or Tundra", "Move across rivers to farms or tundra", TerrainFeature.FIELD, TerrainFeature.TUNDRA);

    override val allowsRetreat = false

    override fun canUse(player: PlayerInstance): Boolean = true

    override fun validStartingHex(hex: MapHex): Boolean = true

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return starting.matchingNeighborsIncludeRivers { to.map { terrain -> terrain.ordinal }.contains(it?.terrain) }
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
        val tunnelNeighbor = hex.matchingNeighborsIncludeRivers { it?.tunnel?: false}.isNotEmpty()
        return hex.data.tunnel || tunnelNeighbor

    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return if (starting.data.tunnel) {
            starting.nonMatchingNeighborsIncludeRivers { it?.terrain == TerrainFeature.LAKE.ordinal }
        } else {
            starting.matchingNeighborsIncludeRivers { it?.terrain == TerrainFeature.LAKE.ordinal }
        }
    }

}

class Toka : AbstractMovementRule(
        "Toka",
        "Once per turn when moving, either 1 character or " +
                "1 mech may move across a river."
) {

    override fun canUse(player: PlayerInstance): Boolean {
        return !player.playerData.flagToka
    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return starting.nonMatchingNeighborsIncludeRivers { it?.terrain == TerrainFeature.LAKE.ordinal }
    }

}

class Underpass : AbstractMovementRule(
        "Underpass",
        "For the purposes of Move actions for your character and mechs, mountains you " +
                "control and all tunnels are considered to be adjacent to each other."
) {
    override fun validStartingHex(hex: MapHex): Boolean {
        return hex.data.tunnel || hex.data.terrain == TerrainFeature.MOUNTAIN.ordinal
    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        //TODO: This is predicated on the idea that you can't move mountain to mountain.
        return if(starting.data.tunnel) {
            GameMap.currentMap.findAllMatching { it?.terrain == TerrainFeature.MOUNTAIN.ordinal }?.filter { mapHex -> mapHex.playerInControl == starting.playerInControl }
        } else {
            GameMap.currentMap.findAllMatching { it?.tunnel == true }
        }
    }

}

class Township : AbstractMovementRule(
        "Township",
        "For the purposes of Move actions for your character and mechs, villages you " +
                "control and the Factory are considered to be adjacent to each other."
) {
    override fun validStartingHex(hex: MapHex): Boolean {
        return hex.data.terrain == TerrainFeature.VILLAGE.ordinal || hex.data.terrain == TerrainFeature.FACTORY.ordinal
    }

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        // TODO: This is predicated on the idea that you can't do village to village
        return if (starting.data.terrain == TerrainFeature.FACTORY.ordinal) {
            GameMap.currentMap.findAllMatching { it?.terrain == TerrainFeature.VILLAGE.ordinal }?.filter { mapHex -> mapHex.playerInControl == starting.playerInControl }
        } else {
            GameMap.currentMap.findAllMatching { it?.terrain == TerrainFeature.FACTORY.ordinal }
        }
    }

}

class Wayfare : AbstractMovementRule(
        "Wayfare",
        "Your character and mechs may move from a territory or home base to any inactive faction’s home " +
                "base or your own regardless of the distance."
) {

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return GameMap.currentMap.vacantBases + starting.playerInControl?.let { GameMap.currentMap.findHomeBase(it) }
    }

}

class Rally : AbstractMovementRule(
        "Rally",
        "When taking a Move action, your character and mechs can move to any territory " +
                "that contains at least one of your workers or a Flag token, regardless of the distance."
) {

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return arrayOf(UnitType.WORKER.ordinal, UnitType.FLAG.ordinal).flatMap {
            type -> ScytheDatabase.unitDao()!!.getUnitsForPlayer(starting.playerInControl!!, type)?.map { it.loc }?: emptyList()
        }.toSet().map { loc -> GameMap.currentMap.findHexAtIndex(loc) }
    }

}

class Shinobi : AbstractMovementRule(
        "Shinobi",
        "Your character and mechs can move to any territory with a Trap token, regardless of the distance."
) {

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return (ScytheDatabase.unitDao()!!.getUnitsForPlayer(starting.playerInControl!!, UnitType.TRAP.ordinal)?.map { it.loc }?: emptyList()).toSet().map { loc ->
            GameMap.currentMap.findHexAtIndex(loc)
        }
    }

}

class Seaworthy : AbstractMovementRule(
        "Seaworthy",
        "Your character and mechs can move to and from lakes and retreat onto adjacent lakes.",
        true
) {

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return if (starting.data.terrain == TerrainFeature.LAKE.ordinal){
            starting.matchingNeighborsNoRivers { true }
        } else {
            starting.matchingNeighborsNoRivers { it?.terrain == TerrainFeature.LAKE.ordinal }
        }
    }

}

class Submerge : AbstractMovementRule(
        "Submerge",
        "Your character and mechs may move to and from lakes and move from any lake to another."
) {

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return if (starting.data.terrain == TerrainFeature.LAKE.ordinal) {
            GameMap.currentMap.findAllMatching { it?.terrain == TerrainFeature.LAKE.ordinal }?.filter { mapHex -> mapHex != starting}
        } else {
            starting.matchingNeighborsNoRivers { it?.terrain == TerrainFeature.LAKE.ordinal }
        }
    }

}


class Disarm : AbstractCombatRule(
        "Disarm",
        "Before you engage in combat on a territory with " +
                "a tunnel or your Mine, the combating opponent loses 2 power."
) {

    override fun validCombatHex(player: PlayerInstance, combatBoard: CombatBoard): Boolean {
        return combatBoard.hex.data.tunnel
    }

    override fun applyEffect(player: PlayerInstance, battle: Battle) {
        battle.getOpposingBoard(player).playerPower -= 2
    }
}

class Artillery : AbstractCombatRule(
        "Artillery",
        "Before you engage in combat, you may pay 1 power to force the combating opponent to lose 2 power.",
        applyAutomatically = false
) {

    override fun validCombatHex(player: PlayerInstance, combatBoard: CombatBoard): Boolean {
        return combatBoard.playerPower > 0
    }

    override fun applyEffect(player: PlayerInstance, battle: Battle) {
        battle.getOpposingBoard(player).playerPower -= 2
        battle.getPlayerBoard(player).playerPower -= 1
    }
}

class Suiton : AbstractCombatRule(
        "Suiton",
        "Your character and mechs can move to and from lakes. " +
                "If combat occurs on a lake, you may play 1 additional combat card."
), MovementRule {

    override fun canUse(player: PlayerInstance): Boolean = true

    override fun validUnitType(unitType: UnitType): Boolean {
        return unitType == UnitType.CHARACTER || unitType == UnitType.MECH
    }

    override val allowsRetreat = false

    override fun validCombatHex(player: PlayerInstance, combatBoard: CombatBoard): Boolean {
        return combatBoard.hex.data.terrain == TerrainFeature.LAKE.ordinal
    }

    override fun applyEffect(player: PlayerInstance, battle: Battle) {
        battle.getPlayerBoard(player).cardLimit += 1
    }

    override fun validStartingHex(hex: MapHex): Boolean = true

    override fun validEndingHexes(starting: MapHex): List<MapHex?>? {
        return starting.matchingNeighborsNoRivers { it?.terrain == TerrainFeature.LAKE.ordinal }
    }

}

class PeoplesArmy : AbstractCombatRule(
        "People's Army",
        "In combat where you have at least 1 worker, you may play one additional combat card."
) {

    override fun validCombatHex(player: PlayerInstance, combatBoard: CombatBoard): Boolean {
        return combatBoard.unitsPresent.any { gameUnit -> gameUnit.type == UnitType.WORKER }
    }

    override fun applyEffect(player: PlayerInstance, battle: Battle) {
        battle.getPlayerBoard(player).cardLimit += 1
    }

}

class Scout : AbstractCombatRule(
        "Scout",
        "Before you engage in combat, steal one of the opponent’s combat cards at random and add it to your hand"
) {

    override fun applyEffect(player: PlayerInstance, battle: Battle) {
        val opponent = battle.getOpposingBoard(player).playerInstance

        opponent.takeCombatCards(1, false)?.also {
            player.giveCombatCards(*it.toTypedArray())
            battle.getPlayerBoard(player).playerCombatCards.addAll(it)
        }
    }

}

class Sword : AbstractCombatRule(
        "Sword",
        "Before you engage in combat as the attacker, the defender loses 2 power.",
        appliesForDefense = false
) {
    override fun applyEffect(player: PlayerInstance, battle: Battle) {
        battle.getOpposingBoard(player).playerPower -= 2
    }

}

class Shield : AbstractCombatRule(
        "Shield",
        "Before you engage in combat as the defender, " +
        "gain 2 power.",
        appliesForAttack = false
) {
    override fun applyEffect(player: PlayerInstance, battle: Battle) {
        battle.getPlayerBoard(player).playerPower += 2
    }
}

class Ronin : AbstractCombatRule(
        "Ronin",
        "Before combat where you have exactly 1 unit " +
                "(0 workers and either 1 character or 1 mech), " +
                "you may gain 2 power on the Power Track."
) {

    override fun validCombatHex(player: PlayerInstance, combatBoard: CombatBoard): Boolean {
        return combatBoard.unitsPresent.size == 1
    }

    override fun applyEffect(player: PlayerInstance, battle: Battle) {
        battle.getPlayerBoard(player).playerPower += 2
    }
}

class Camaraderie : AbstractCombatRule(
        "Camaraderie",
        "You do not lose popularity when forcing an opponent’s workers to retreat after winning combat as the aggressor."
) {
    override val appliesDuringUncontested: Boolean = true
    override val appliesForDefense: Boolean = false

    override fun applyEffect(player: PlayerInstance, battle: Battle) {
        battle.getPlayerBoard(player).camaraderie = true
    }

}
