package org.ajar.scythemobile.turn

import android.util.Log
import androidx.collection.SparseArrayCompat
import androidx.collection.isNotEmpty
import androidx.collection.set
import org.ajar.scythemobile.data.*
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import java.lang.IllegalArgumentException
import java.lang.NullPointerException

object TurnHolder {
    private var _currentTurn: TurnData? = null
    val currentTurn: TurnData
        get() {
            if(_currentTurn == null) {
                _currentTurn = ScytheDatabase.turnDao()?.getCurrentTurn().let { currentTurn ->
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
            return _currentTurn!!
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

    fun hasUnresolvedCombat(): Boolean {
        return getNextCombat() != null
    }

    fun getNextCombat() : CombatRecord? {
        return when {
            currentTurn.combatOne?.combatResolved == false -> currentTurn.combatOne
            currentTurn.combatTwo?.combatResolved == false -> currentTurn.combatTwo
            currentTurn.combatThree?.combatResolved == false -> currentTurn.combatThree
            else -> null
        }
    }

    private fun createCombatRecord(hex: Int, attacker: Int, gameUnits: List<Int>) : CombatRecord {
        var defenderId = -1
        val defendingUnits = GameMap.currentMap.unitsAtHex(hex).filter {
            it.owner != currentPlayer.playerId && !UnitType.structures.contains(UnitType.valueOf(it.type))
        }.map { if(defenderId == -1) defenderId = it.owner; it.id }
        return CombatRecord(hex, attacker, defenderId, gameUnits, defendingUnits)
    }

    fun hasUnresolvedEncounter() : MapHex? {
        return ((currentPlayer.selectUnits(UnitType.CHARACTER)?: emptyList()) + (currentPlayer.selectUnits(UnitType.MECH)?: emptyList())).mapNotNull { GameMap.currentMap.findHexAtIndex(it.pos) }.firstOrNull {
            it.encounter != null
        }
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
        ScytheDatabase.playerDao()?.updatePlayerAndIncrement(*cachedPlayerUpdates.values.toTypedArray())
        ScytheDatabase.resourceDao()?.updateResourceAndIncrement(*cachedResourceUpdates.values.toTypedArray())
        ScytheDatabase.mapDao()?.updateMapHexAndIncrement(*cachedEncounterUpdates.values.toTypedArray())

        cachedPlayerUpdates.clear()
        cachedResourceUpdates.clear()
        cachedEncounterUpdates.clear()

        if(currentTurn.combatOne?.combatResolved != false && currentTurn.combatTwo?.combatResolved != false && currentTurn.combatThree?.combatResolved != false) {
            ScytheDatabase.unitDao()?.updateUnitAndIncrement(*cachedMoves.values.toTypedArray())
            cachedMoves.clear()
        } else {
            //TODO("Store incomplete moves somewhere?")
        }

        ScytheDatabase.turnDao()?.updateTurn(currentTurn)
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

    fun updatedUnitPositions(map: SparseArrayCompat<MutableList<GameUnit>> = SparseArrayCompat()) : SparseArrayCompat<MutableList<GameUnit>> {
        if(map.isNotEmpty()) map.clear()

        GameMap.currentMap.mapHexes.forEach { mapHex ->
            map[mapHex.loc] = ArrayList()
        }

        ScytheDatabase.unitDao()?.getUnits()?.filter { it.loc > 0 && !cachedMoves.containsKey(it.id)}?.forEach { unitData ->
            map[unitData.loc]!!.add(GameUnit.load(unitData))
        }

        cachedMoves.forEach { (_, unitData) ->
            map[unitData.loc]!!.add(GameUnit.load(unitData))
        }

        return map
    }

    fun updatedUnitsAtPosition(mapHex: MapHex) : List<UnitData> {
        return GameMap.currentMap.unitsAtHex(mapHex.loc).filter { !cachedMoves.containsValue(it) } + cachedMoves.values.filter { it.loc == mapHex.loc }
    }

    fun updatedResourcePositions(map: SparseArrayCompat<MutableList<ResourceData>>) : SparseArrayCompat<MutableList<ResourceData>> {
        if(map.isNotEmpty()) map.clear()

        GameMap.currentMap.mapHexes.forEach { mapHex ->
            map[mapHex.loc] = ArrayList()
        }
        ScytheDatabase.resourceDao()?.getResources()?.filter { it.loc > 0 && !cachedResourceUpdates.containsKey(it.id) }?.forEach { resourceData ->
            map[resourceData.loc]!!.add(resourceData)
        }
        cachedResourceUpdates.forEach{ (_, resourceData ) ->
            map[resourceData.loc]?.add(resourceData)
        }

        return map
    }

    fun debugDatabase() {
        ScytheDatabase.playerDao()?.getPlayers()?.forEach { Log.w("Player", "$it") }
        ScytheDatabase.unitDao()?.getUnits()?.forEach { Log.w("Unit", "$it") }
        ScytheDatabase.resourceDao()?.getResources()?.forEach { Log.w("Resource", "$it") }
        ScytheDatabase.turnDao()?.getTurns()?.forEach { Log.w("Turn", "$it") }
    }
}