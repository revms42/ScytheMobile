package org.ajar.scythemobile.model.combat

import org.ajar.scythemobile.data.CombatRecord
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.CombatRule
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.turn.TurnHolder

class CombatBoard(val playerInstance: PlayerInstance, val hex: MapHex, val attacker: Boolean, val unitsPresent: List<GameUnit>) {
    var playerPower: Int = playerInstance.power
    val playerCombatCards: MutableList<CombatCard> = (playerInstance.combatCards?: emptyList()).toMutableList()
    var cardLimit: Int = unitsPresent.count { it.type == UnitType.MECH || it.type == UnitType.CHARACTER }
    val availableAbilities: List<CombatRule> = playerInstance.getCombatRules().filter { if(attacker) it.appliesForAttack else it.appliesForDefense }
    //val unitsPresent: List<GameUnit> = ScytheDatabase.unitDao()?.getUnitsForCombat(hex.loc, playerInstance.playerId)?.map { GameUnit(it, playerInstance) }!!

    var selectedPower: Int = 0
    val selectedCards: MutableList<CombatCard> = ArrayList()
    val selectedAbilities: MutableList<CombatRule> = availableAbilities.filter { it.applyAutomatically }.toMutableList()
    val selectableAbilities: List<CombatRule> = availableAbilities.filterNot { it.applyAutomatically }

    var camaraderie = false

    val workersPresent = unitsPresent.any { it.type == UnitType.WORKER }

    fun finishSelection(combatRecord: CombatRecord) {
        if(attacker) {
            combatRecord.attackerCards = selectedCards.map { it.resourceData.id }
            combatRecord.attackerPower = selectedPower
            combatRecord.attackerAbilities = selectedAbilities.map { it.abilityName }
        } else {
            combatRecord.defenderCards = selectedCards.map { it.resourceData.id }
            combatRecord.defenderPower = selectedPower
            combatRecord.defenderAbilities = selectedAbilities.map { it.abilityName }
        }
        updateTurnHolder()
    }

    fun updateTurnHolder() {
        playerInstance.playerData.power = playerPower
        TurnHolder.updatePlayer(playerInstance.playerData)
    }

    fun cleanUp() {
        playerInstance.power = playerPower - selectedPower
        selectedCards.forEach { CombatCardDeck.currentDeck.returnCard(it) }

        TurnHolder.updatePlayer(playerInstance.playerData)
    }
}

data class CombatResults(val attackingPlayer: Int, val defendingPlayer: Int) {
    val attackerWon = attackingPlayer >= defendingPlayer
}

class Battle private constructor(private val combatRecord: CombatRecord, private val attacker: CombatBoard, private val defender: CombatBoard) {
    fun determineResults(): CombatResults {
        return CombatResults(
                attacker.selectedCards.sumBy { it.power } + attacker.selectedPower,
                defender.selectedCards.sumBy { it.power  + defender.selectedPower }
        )
    }

    fun getOpposingBoard(playerInstance: PlayerInstance): CombatBoard {
        return if(playerInstance == attacker.playerInstance) {
            defender
        } else {
            attacker
        }
    }

    fun getPlayerBoard(playerInstance: PlayerInstance) : CombatBoard {
        return if(playerInstance == attacker.playerInstance) {
            attacker
        } else {
            defender
        }
    }

    fun applyAutomaticCombatRules() {
        attacker.selectedAbilities.forEach { it.applyEffect(attacker.playerInstance, this) }
        defender.selectedAbilities.forEach { it.applyEffect(defender.playerInstance, this) }
    }

    fun applyConditionalCombatRule(playerInstance: PlayerInstance, combatRule: CombatRule) {
        getPlayerBoard(playerInstance).selectedAbilities.add(combatRule)
        combatRule.applyEffect(playerInstance, this)
    }

    fun finishSelection(playerInstance: PlayerInstance) {
        getPlayerBoard(playerInstance).finishSelection(combatRecord)
        getOpposingBoard(playerInstance).updateTurnHolder()
    }

    fun resolveCombat() {
        TODO("Actually resolve combat, drive off units, retreat units, run cleanup on the boards, call turn holder to update.")
    }

    companion object {
        fun openBattle(combatRecord: CombatRecord) : Battle {
            val hex = GameMap.currentMap.findHexAtIndex(combatRecord.hex)!!

            val attackingPlayer = PlayerInstance.loadPlayer(combatRecord.attackingPlayer)
            val attackingUnits = ScytheDatabase.unitDao()?.getUnitsFromList(combatRecord.attackingUnits)?.map { GameUnit(it, attackingPlayer) }!!
            val attackerBoard = CombatBoard(attackingPlayer, hex, true, attackingUnits)

            val defendingPlayer = PlayerInstance.loadPlayer(combatRecord.defendingPlayer)
            val defendingUnits = ScytheDatabase.unitDao()?.getUnitsFromList(combatRecord.defendingUnits)?.map { GameUnit(it, defendingPlayer) }!!
            val defenderBoard = CombatBoard(defendingPlayer, hex, false, defendingUnits)

            return Battle(combatRecord, attackerBoard, defenderBoard)
        }
    }
}