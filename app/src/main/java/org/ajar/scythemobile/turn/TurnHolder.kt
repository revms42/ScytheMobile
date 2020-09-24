package org.ajar.scythemobile.turn

import org.ajar.scythemobile.data.*
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.GameMap
import java.lang.IllegalArgumentException

object TurnHolder {
    val currentTurn: TurnData
        get() {
            return ScytheDatabase.turnDao()?.getCurrentTurn().let { currentTurn ->
                currentTurn?.let { turn ->
                    if (turn.performedBottom) {
                        var nextPlayerId = currentTurn.playerId + 1

                        val nextPlayer = ScytheDatabase.playerDao()?.getPlayer(nextPlayerId)
                                ?: ScytheDatabase.playerDao()?.getPlayer(0)

                        nextPlayerId = nextPlayer!!.id

                        TurnData(0, nextPlayerId).also { ScytheDatabase.turnDao()?.addTurn(it) }
                    } else {
                        currentTurn
                    }
                } ?: TurnData(0, 0).also { ScytheDatabase.turnDao()?.addTurn(it) }
            }
        }
    private val cachedMoves: HashMap<Int,UnitData> = HashMap()
    private val cachedPlayerUpdates: HashMap<Int,PlayerData> = HashMap()
    private val cachedResourceUpdates: HashMap<Int,ResourceData> = HashMap()
    private val cachedEncounterUpdates: HashMap<Int,MapHexData> = HashMap()

    val currentPlayer: PlayerInstance
        get() = PlayerInstance.loadPlayer(currentTurn.playerId)

    fun addCombat(hex: Int, attacker: Int, gameUnits: List<Int>) {
        when {
            currentTurn.combatOne == null -> {
                currentTurn.combatOne = createCombatRecord(hex, attacker, gameUnits)
            }
            currentTurn.combatOne != null && currentTurn.combatOne!!.hex == hex -> {
                currentTurn.combatOne!!.attackingUnits = currentTurn.combatOne!!.attackingUnits + gameUnits
            }
            currentTurn.combatTwo == null -> {
                currentTurn.combatTwo = createCombatRecord(hex, attacker, gameUnits)
            }
            currentTurn.combatTwo != null && currentTurn.combatTwo!!.hex == hex -> {
                currentTurn.combatTwo!!.attackingUnits = currentTurn.combatTwo!!.attackingUnits + gameUnits
            }
            currentTurn.combatThree == null -> {
                currentTurn.combatThree = createCombatRecord(hex, attacker, gameUnits)
            }
            currentTurn.combatThree != null && currentTurn.combatThree!!.hex == hex -> {
                currentTurn.combatThree!!.attackingUnits = currentTurn.combatThree!!.attackingUnits + gameUnits
            }
            else -> throw IllegalArgumentException("There should never be more than three combats in a turn!")
        }
    }

    private fun createCombatRecord(hex: Int, attacker: Int, gameUnits: List<Int>) : CombatRecord {
        var defenderId = -1
        val defendingUnits = GameMap.currentMap.unitsAtHex(hex).filter {
            it.owner != currentPlayer.playerId && !UnitType.structures.contains(UnitType.valueOf(it.type))
        }.map { if(defenderId == -1) defenderId = it.owner; it.id }
        return CombatRecord(hex, attacker, defenderId, gameUnits, defendingUnits)
    }


    fun updatePlayer(vararg playerData: PlayerData) {
        playerData.forEach { cachedPlayerUpdates[it.id] = it }
    }

    fun updateMove(vararg unitData: UnitData) {
        unitData.forEach { cachedMoves[it.id] = it }
    }

    fun updateResource(vararg resourceData: ResourceData) {
        resourceData.forEach { cachedResourceUpdates[it.id] = it }
    }

    fun updateEncounter(vararg mapHexData: MapHexData) {
        mapHexData.forEach { cachedEncounterUpdates[it.loc] = it }
    }

    fun commitChanges() {
        ScytheDatabase.playerDao()?.updatePlayer(*cachedPlayerUpdates.values.toTypedArray())
        ScytheDatabase.resourceDao()?.updateResource(*cachedResourceUpdates.values.toTypedArray())
        ScytheDatabase.mapDao()?.updateMapHex(*cachedEncounterUpdates.values.toTypedArray())

        cachedPlayerUpdates.clear()
        cachedResourceUpdates.clear()
        cachedEncounterUpdates.clear()

        if(currentTurn.combatOne?.combatResolved != false && currentTurn.combatTwo?.combatResolved != false && currentTurn.combatThree?.combatResolved != false) {
            ScytheDatabase.unitDao()?.updateUnit(*cachedMoves.values.toTypedArray())
            cachedMoves.clear()
        }
    }

    fun isUpdateQueued(obj: Any): Boolean {
        return when(obj) {
            is PlayerData -> cachedPlayerUpdates.containsKey(obj.id)
            is MapHexData -> cachedEncounterUpdates.containsKey(obj.loc)
            is ResourceData -> cachedResourceUpdates.containsKey(obj.id)
            is UnitData -> cachedMoves.containsKey(obj.id)
            else -> false
        }
    }

    fun isAnyUpdateQueued(obj: Class<out Any>): Boolean {
        return when(obj) {
            PlayerData::class.java -> cachedPlayerUpdates.isNotEmpty()
            MapHexData::class.java -> cachedEncounterUpdates.isNotEmpty()
            ResourceData::class.java -> cachedResourceUpdates.isNotEmpty()
            UnitData::class.java -> cachedMoves.isNotEmpty()
            else -> false
        }
    }

    fun resetTurn() {
        cachedPlayerUpdates.clear()
        cachedResourceUpdates.clear()
        cachedEncounterUpdates.clear()
        cachedMoves.clear()
        ScytheDatabase.turnDao()?.updateTurn(TurnData(currentTurn.turn, currentTurn.playerId))
    }
}