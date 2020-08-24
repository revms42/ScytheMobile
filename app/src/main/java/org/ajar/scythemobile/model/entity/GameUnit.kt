package org.ajar.scythemobile.model.entity

import android.util.SparseArray
import androidx.core.util.set
import org.ajar.scythemobile.data.PlayerData
import org.ajar.scythemobile.data.UnitData
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.old.model.entity.ResourceHolder
import org.ajar.scythemobile.old.model.production.MapResource

enum class TrapType(val description: String, val sprungPicture: Int = -1) {
    MAIFUKU_LOSE_POP("Lose 2 popularity"){
        override fun applyToPlayer(player: PlayerInstance) {
            player.popularity -= 2
        }
    },
    MAIFUKU_LOSE_MONEY("Lose $4"){
        override fun applyToPlayer(player: PlayerInstance) {
            player.coins -= 4
        }
    },
    MAIFUKU_LOSE_POWER("Lose 3 power") {
        override fun applyToPlayer(player: PlayerInstance) {
            player.power -= 3
        }
    },
    MAIFUKU_LOSE_CARDS("Lose 2 combat cards at random") {
        override fun applyToPlayer(player: PlayerInstance) {
            val cards = player.combatCards

            for (i in 0..1){
                if(cards.size > 1) {
                    val cardToSteal = cards.toList()[(Math.random() * cards.size).toInt()]
                    cards.remove(cardToSteal)
                }
            }
        }
    };

    abstract fun applyToPlayer(player: PlayerInstance)
}

class TrapUnit(override val controllingPlayer: PlayerInstance, val trapType: TrapType) : GameUnit {
    override val type: UnitType = UnitType.FLAG

    override val heldMapResources: MutableList<MapResource> = ArrayList()

    private var _sprung: Boolean? = null
    val sprung: Boolean
        get() {
            if(_sprung == null) {
                this._sprung = false
            }
            return _sprung!!
        }

    fun springTrap(unit: GameUnit) {
        trapType.applyToPlayer(unit.controllingPlayer)
        _sprung = true
    }

    fun resetTrap() {
        _sprung = false
    }
}

enum class UnitType {
    CHARACTER,
    MECH,
    TRAP,
    FLAG,
    WORKER,
    AIRSHIP,
    MILL,
    MONUMENT,
    MINE,
    ARMORY;

    companion object {
        val structures = listOf(MILL, MONUMENT, MINE, ARMORY)
        val controlUnits = listOf(CHARACTER, WORKER, MECH, AIRSHIP, ARMORY, MILL, MINE, ARMORY, MONUMENT)
    }
}

class GameUnit private constructor(val unitData: UnitData, val controllingPlayer: PlayerInstance, var image: Int = -1) : ResourceHolder {
    override val heldMapResources: MutableList<MapResource> = ArrayList()

    companion object {
        private val units = SparseArray<GameUnit>()

        val unitPainter: (PlayerData) -> Int = fun(data: PlayerData): Int { return -1 }

        fun get(unitData: UnitData, controllingPlayer: PlayerInstance) : GameUnit {
            if(units[unitData.id] == null) {
                units[unitData.id] = GameUnit(unitData, controllingPlayer, unitPainter(controllingPlayer.playerData))
            }
            return units[unitData.id]
        }


    }
}