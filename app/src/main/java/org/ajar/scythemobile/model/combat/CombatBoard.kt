package org.ajar.scythemobile.model.combat

import org.ajar.scythemobile.data.CombatRecord
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.CombatRule
import org.ajar.scythemobile.model.faction.MovementRule
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.turn.TurnHolder

class CombatBoard(val playerInstance: PlayerInstance, val hex: MapHex, val attacker: Boolean, val unitsPresent: List<GameUnit>) {
    var playerPower: Int = playerInstance.power
    val playerCombatCards: MutableList<CombatCard> = (playerInstance.combatCards?: emptyList()).toMutableList()
    var cardLimit: Int = unitsPresent.count { it.type == UnitType.MECH || it.type == UnitType.CHARACTER }
    val availableAbilities: List<CombatRule> = playerInstance.getCombatRules().filter { if(attacker) it.appliesForAttack else it.appliesForDefense }
    //val unitsPresent: List<GameUnit> = ScytheDatabase.unitDao()?.getSpecificUnitsAtLoc(hex.loc, playerInstance.playerId)?.map { GameUnit(it, playerInstance) }!!

    var selectedPower: Int = 0
    val selectedCards: MutableList<CombatCard> = ArrayList()
    val selectedAbilities: MutableList<CombatRule> = availableAbilities.filter { it.applyAutomatically }.toMutableList()
    val selectableAbilities: List<CombatRule> = availableAbilities.filterNot { it.applyAutomatically }

    var cardsAvailable = playerCombatCards.size

    var camaraderie = false

    val totalPower: Int
        get() = selectedCards.sumBy { it.power } + selectedPower

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

//    override fun toString(): String {
//        return "C:${hex.loc}," +
//                "${playerInstance.playerId}," +
//                "$attacker," +
//                "$playerPower," +
//                unitsPresent.foldRight(""){ unit, string -> if(string.isBlank()) "${unit.id}" else ":${unit.id}"} +
//                if(playerCombatCards.size > 0)
//                    ",${playerCombatCards.foldRight(""){ card, string -> if(string.isBlank()) "${card.resourceData.id}" else ":${card.resourceData.id}"}}"
//                else ""
//    }
}

data class CombatResults(val attackingPlayer: Int, val defendingPlayer: Int) {
    val attackerWon = attackingPlayer >= defendingPlayer
}

class Battle private constructor(private val combatRecord: CombatRecord?, private val playerBoard: CombatBoard, private val opponentBoard: CombatBoard) {
    private lateinit var retreatingUnits: List<GameUnit>

    fun determineResults(): CombatResults {
        return CombatResults(playerBoard.totalPower, opponentBoard.totalPower)
    }

    fun getOpposingBoard(playerInstance: PlayerInstance): CombatBoard {
        return if(playerInstance == playerBoard.playerInstance) {
            opponentBoard
        } else {
            playerBoard
        }
    }

    fun getPlayerBoard(playerInstance: PlayerInstance) : CombatBoard {
        return if(playerInstance == playerBoard.playerInstance) {
            playerBoard
        } else {
            opponentBoard
        }
    }

    fun getPlayerBoard() : CombatBoard = playerBoard

    fun getOpposingBoard() : CombatBoard = opponentBoard

    fun getAttackerBoard() : CombatBoard {
        return if(playerBoard.attacker) {
            playerBoard
        } else {
            opponentBoard
        }
    }

    fun getDefenderBoard() : CombatBoard {
        return if(playerBoard.attacker) {
            opponentBoard
        } else {
            playerBoard
        }
    }

    fun applyAutomaticCombatRules() {
        playerBoard.selectedAbilities.forEach { it.applyEffect(playerBoard.playerInstance, this) }
        opponentBoard.selectedAbilities.forEach { it.applyEffect(opponentBoard.playerInstance, this) }
    }

    fun applyConditionalCombatRule(playerInstance: PlayerInstance, combatRule: CombatRule) {
        getPlayerBoard(playerInstance).selectedAbilities.add(combatRule)
        combatRule.applyEffect(playerInstance, this)
    }

    // If this has no combat record you're doing it PBE and we don't try to update anything, we'll just send the reply back.
    fun finishSelection(playerInstance: PlayerInstance) {
        combatRecord?.also { getPlayerBoard(playerInstance).finishSelection(it) ; getOpposingBoard(playerInstance).updateTurnHolder() }
    }

    fun resolveCombat(): List<MapHex>? {
        playerBoard.playerInstance.power -= playerBoard.selectedPower
        playerBoard.selectedCards.forEach { CombatCardDeck.currentDeck.returnCard(it) }

        opponentBoard.playerInstance.power -= opponentBoard.selectedPower
        opponentBoard.selectedCards.forEach { CombatCardDeck.currentDeck.returnCard(it) }

        val losingBoard = if(determineResults().attackerWon) opponentBoard else playerBoard

        CombatCardDeck.currentDeck.drawCard(losingBoard.playerInstance)

        val type = losingBoard.unitsPresent.firstOrNull { it.type == UnitType.MECH }?.type?: UnitType.CHARACTER
        val abilities = losingBoard.playerInstance.factionMat.getMovementAbilities(type).filter { it.allowsRetreat }

        val homeBase = GameMap.currentMap.findFactionBase(losingBoard.playerInstance.factionMat.factionMat.id)
        retreatingUnits = losingBoard.unitsPresent
        return if(abilities.isNotEmpty()) {
            abilities.flatMap { it.validEndingHexes(losingBoard.hex)?.filterNotNull()?: emptyList() } + homeBase!!
        } else {
            retreatUnits(homeBase!!)
            null
        }
    }

    fun retreatUnits(mapHex: MapHex) {
        retreatingUnits.forEach { it.move(mapHex.loc) }
        combatRecord?.also {
            it.combatResolved = true
            TurnHolder.updateMove()
        }

        TurnHolder.commitChanges()
    }

    fun toString(attacker: Boolean): String {
        return (if(attacker) this.playerBoard else this.opponentBoard).let {
            val opponent = this.getOpposingBoard(it.playerInstance)
            "C:${it.hex.loc}," +
                    "${it.playerInstance.playerId}," +
                    "$attacker," +
                    "${it.playerPower}," +
                    it.unitsPresent.foldRight(""){ unit, string -> if(string.isBlank()) "${unit.id}" else ":${unit.id}"} +
                    if(it.playerCombatCards.size > 0)
                        ",${it.playerCombatCards.foldRight(""){ card, string -> if(string.isBlank()) "${card.resourceData.id}" else ":${card.resourceData.id}"}},"
                    else "-1," +
                    "${opponent.playerInstance.playerId}," +
                    "${opponent.playerPower}," +
                    opponent.unitsPresent.foldRight(""){ unit, string -> if(string.isBlank()) "${unit.id}" else ":${unit.id}"} +
                    ",${opponent.playerCombatCards.size}"
        }

    }

    companion object {
        fun openBattle(combatRecord: CombatRecord) : Battle {
            val hex = GameMap.currentMap.findHexAtIndex(combatRecord.hex)!!

            val attackingPlayer = PlayerInstance.loadPlayer(combatRecord.attackingPlayer)
            val attackingUnits = ScytheDatabase.unitDao()?.getUnitsFromList(combatRecord.attackingUnits)?.map { GameUnit(it, attackingPlayer) }!!
            val attackerBoard = CombatBoard(attackingPlayer, hex, true, attackingUnits)
            with(combatRecord) {
                attackerBoard.selectedPower = this.attackerPower?: 0
                attackerBoard.selectedCards.addAll(this.attackerCards?.map { CombatCard(ScytheDatabase.resourceDao()?.getResource(it)!!) }?: emptyList())
            }

            val defendingPlayer = PlayerInstance.loadPlayer(combatRecord.defendingPlayer)
            val defendingUnits = ScytheDatabase.unitDao()?.getUnitsFromList(combatRecord.defendingUnits)?.map { GameUnit(it, defendingPlayer) }!!
            val defenderBoard = CombatBoard(defendingPlayer, hex, false, defendingUnits)
            with(combatRecord) {
                defenderBoard.selectedPower = this.defenderPower?: 0
                defenderBoard.selectedCards.addAll(this.defenderCards?.map { CombatCard(ScytheDatabase.resourceDao()?.getResource(it)!!) }?: emptyList())
            }

            return Battle(combatRecord, attackerBoard, defenderBoard)
        }

        fun fromString(str: String): Battle? {
            val parts = str.takeIf { it.startsWith("C:") }?.substring(2)?.split(",")

            return if(!parts.isNullOrEmpty()) {
                val hex = GameMap.currentMap.findHexAtIndex(parts[0].toInt())!!

                val player = PlayerInstance.loadPlayer(parts[1].toInt())
                val attacking = parts[2].toBoolean()
                val powerAvailable = parts[3].toInt()
                val unitsPresent = parts[4].split(":").mapNotNull { id -> ScytheDatabase.unitDao()?.getUnit(id.toInt())?.let { GameUnit(it, player) } }
                val availableCards = parts[5].split(":").mapNotNull { id -> ScytheDatabase.resourceDao()?.getResource(id.toInt())?.let { CombatCard(it) } }

                val playerBoard = CombatBoard(player, hex, attacking, unitsPresent)
                playerBoard.playerPower = powerAvailable
                playerBoard.playerCombatCards.also { it.clear() ; it.addAll(availableCards) }

                val opponent = PlayerInstance.loadPlayer(parts[6].toInt())
                val opponentPower = parts[7].toInt()
                val opponentUnits = parts[8].split(":").mapNotNull { id -> ScytheDatabase.unitDao()?.getUnit(id.toInt())?.let { GameUnit(it, opponent) } }
                val opponentCardCount = parts[9].toInt()

                val opponentBoard = CombatBoard(opponent, hex, !attacking, opponentUnits)
                opponentBoard.playerPower = opponentPower
                opponentBoard.cardsAvailable = opponentCardCount

                Battle(null, playerBoard, opponentBoard)
            } else null
        }
    }
}