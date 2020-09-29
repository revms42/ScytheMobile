package org.ajar.scythemobile.model.entity

import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.UnitData
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.faction.FactionResourcePack
import org.ajar.scythemobile.model.player.Bank
import org.ajar.scythemobile.turn.TurnHolder

enum class TrapType(var description: String, var sprungPicture: Int = -1) {
    MAIFUKU_LOSE_POP("Lose 2 popularity"){
        override fun applyToPlayer(player: PlayerInstance) {
            player.popularity -= 2
        }
    },
    MAIFUKU_LOSE_MONEY("Lose $4"){
        override fun applyToPlayer(player: PlayerInstance) {
            Bank.removeCoins(player.playerData, 4)
        }
    },
    MAIFUKU_LOSE_POWER("Lose 3 power") {
        override fun applyToPlayer(player: PlayerInstance) {
            player.power -= 3
        }
    },
    MAIFUKU_LOSE_CARDS("Lose 2 combat cards at random") {
        override fun applyToPlayer(player: PlayerInstance) {
            player.takeCombatCards(2)
        }
    };

    abstract fun applyToPlayer(player: PlayerInstance)
}

class TrapUnit(unitData: UnitData, controllingPlayer: PlayerInstance) : GameUnit(unitData, controllingPlayer) {

    init {
        if (unitData.state == TrapState.NONE.ordinal) {
            unitData.state = TrapState.ARMED.ordinal
            TurnHolder.updateMove(unitData)
        }
    }

    val sprung: Boolean
        get() = unitData.state == TrapState.SPRUNG.ordinal

    fun springTrap(unit: GameUnit) {
        val trapType = TrapType.values()[unitData.subType]
        trapType.applyToPlayer(unit.controllingPlayer)
        unitData.state = TrapState.SPRUNG.ordinal
        TurnHolder.updateMove(unitData)
    }

    fun resetTrap() {
        unitData.state = TrapState.ARMED.ordinal
        TurnHolder.updateMove(unitData)
    }

    enum class TrapState {
        NONE,
        ARMED,
        SPRUNG
    }
}

enum class UnitType(val resourceFrom: (FactionResourcePack) -> Int?) {
    CHARACTER(FactionResourcePack::heroRes),
    MECH(FactionResourcePack::mechRes),
    TRAP(FactionResourcePack::trapUnsprungRes),
    FLAG(FactionResourcePack::flagRes),
    WORKER(FactionResourcePack::workerRes),
    AIRSHIP(FactionResourcePack::airshipRes),
    MILL(FactionResourcePack::millRes),
    MONUMENT(FactionResourcePack::monumentRes),
    MINE(FactionResourcePack::mineRes),
    ARMORY(FactionResourcePack::armoryRes);

    companion object {
        val structures = listOf(MILL, MONUMENT, MINE, ARMORY)
        val controlUnits = listOf(CHARACTER, WORKER, MECH, AIRSHIP, ARMORY, MILL, MINE, ARMORY, MONUMENT)
        val provokeUnits = listOf(CHARACTER, WORKER, MECH)

        fun valueOf(index: Int): UnitType {
            return values()[index]
        }
    }
}

open class GameUnit(val unitData: UnitData, val controllingPlayer: PlayerInstance) {
    var pos: Int
        get() = unitData.loc
        set(value) {
            unitData.loc = value
        }

    val id: Int
        get() = unitData.id

    val type: UnitType
        get() = UnitType.values()[unitData.type]

    fun move(loc: Int) {
        unitData.loc = loc
        TurnHolder.updateMove(unitData)
    }

    val image: Int?
        get() = type.resourceFrom(controllingPlayer.resources)

    companion object {
        fun load(unitData: UnitData): GameUnit = GameUnit(unitData, PlayerInstance.loadPlayer(unitData.owner))
    }
}