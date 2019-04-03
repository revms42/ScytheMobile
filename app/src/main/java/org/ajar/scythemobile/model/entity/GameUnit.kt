package org.ajar.scythemobile.model.entity

import org.ajar.scythemobile.model.production.MapResource

enum class TrapType(val description: String, val sprungPicture: Int = -1) {
    MAIFUKU_LOSE_POP("Lose 2 popularity"){
        override fun applyToPlayer(player: Player) {
            player.popularity -= 2
        }
    },
    MAIFUKU_LOSE_MONEY("Lose $4"){
        override fun applyToPlayer(player: Player) {
            player.coins -= 4
        }
    },
    MAIFUKU_LOSE_POWER("Lose 3 power") {
        override fun applyToPlayer(player: Player) {
            player.power -= 3
        }
    },
    MAIFUKU_LOSE_CARDS("Lose 2 combat cards at random") {
        override fun applyToPlayer(player: Player) {
            val cards = player.combatCards

            for (i in 0..1){
                if(cards.size > 1) {
                    val cardToSteal = cards.toList()[(Math.random() * cards.size).toInt()]
                    cards.remove(cardToSteal)
                }
            }
        }
    };

    abstract fun applyToPlayer(player: Player)
}

class TrapUnit(override val controllingPlayer: Player, val trapType: TrapType) : GameUnit {
    override val type: UnitType = UnitType.FLAG

    override val heldMapResources: MutableList<MapResource> = ArrayList()

    var _sprung: Boolean? = null
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
}

class FlagUnit(override val controllingPlayer: Player) : GameUnit {
    override val type: UnitType = UnitType.FLAG
    override val heldMapResources: MutableList<MapResource> = ArrayList()
}

enum class UnitType {
    CHARACTER,
    MECH,
    TRAP,
    FLAG,
    WORKER,
    AIRSHIP,
    STRUCTURE
}

interface GameUnit : ResourceHolder{

    val controllingPlayer: Player
    val type: UnitType
}