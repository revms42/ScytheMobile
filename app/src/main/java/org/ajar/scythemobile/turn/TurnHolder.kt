package org.ajar.scythemobile.turn

import org.ajar.scythemobile.data.*

object TurnHolder {
    private val turnEntry: TurnData
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

        if(turnEntry.combatOne?.combatResolved != false && turnEntry.combatTwo?.combatResolved != false && turnEntry.combatThree?.combatResolved != false) {
            ScytheDatabase.unitDao()?.updateUnit(*cachedMoves.values.toTypedArray())
            cachedMoves.clear()
        }
    }
}