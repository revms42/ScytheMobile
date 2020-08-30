package org.ajar.scythemobile.old.model.combat

import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.StarType
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.MovementRule
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex

abstract class AbstractPlayerCombatBoard(final override val player: PlayerInstance, final override val unitsPresent: List<GameUnit>) : PlayerCombatBoard {

    override val cardsAvailable: Collection<CombatCard> = player.combatCards?: emptyList()

    private var _power: Int = player.power
    override var power: Int
        get() = _power
        set(value) {
            _power = when {
                value < 0 -> 0
                value > 16 -> 16
                else -> value
            }
        }

    override var cardLimit: Int = unitsPresent.filter { gameUnit -> gameUnit.type == UnitType.MECH || gameUnit.type == UnitType.CHARACTER }.size
    private val cardsSelected: MutableSet<CombatCard> = HashSet()

    private var _powerSelected: Int = 0
    override var powerSelected: Int
        get() = _powerSelected
        set(value) {
            _powerSelected = when {
                value < 0 -> 0
                value > 7 -> 7
                value > power -> power
                else -> value
            }
        }
    override var camaraderie: Boolean = false


    override fun concludeCombat(winner: Boolean, workersDrivenOff: Int) {
        player.power = power - powerSelected

        player.combatCards.clear()
        cardsAvailable.removeAll(cardsSelected)
        player.combatCards.addAll(cardsAvailable)

        if (!winner && powerSelected > 0) player.combatCards.add(AbstractPlayer.drawCombatCard())
        if (winner && !camaraderie) player.popularity -= workersDrivenOff
    }

    override fun finalPower(): Int {
        return powerSelected + cardsSelected.map { it.power }.sum()
    }

    override fun selectCard(card: CombatCard) {
        if(cardsAvailable.contains(card) && !cardsSelected.contains(card) && cardsSelected.size < cardLimit) {
            cardsSelected.add(card)
        }
    }

    override fun deselectCard(card: CombatCard) {
        if(cardsSelected.contains(card)) {
            cardsSelected.remove(card)
        }
    }

    override fun retreatUnits(fromHex: MapHex) {
        var ability: MovementRule? = null
        for (unit in unitsPresent) {
            ability = player.factionMat.getMovementAbilities(unit.type).firstOrNull { it.allowsRetreat }

            if (ability != null) break
        }

        if(ability != null ) {
            //TODO: Right now there is only one retreat specified ability. If anyone gains more than one this needs to be changed.
            val retreatList = ability.validEndingHexes(fromHex)?.filter { !(it?.willMoveProvokeFight()?: true) }?.toMutableList()?: ArrayList()
            retreatList.add(GameMap.currentMap?.findHomeBase(player))

            player.user.requester?.requestChoice(RetreatChoice(), retreatList)?.unitsPresent?.addAll(unitsPresent)
            fromHex.unitsPresent.removeAll(unitsPresent)
        } else {
            GameMap.currentMap?.findHomeBase(player)?.unitsPresent?.addAll(unitsPresent)
            fromHex.unitsPresent.removeAll(unitsPresent)
        }
    }
}

interface PlayerCombatBoard {
    val player: PlayerInstance
    val cardsAvailable: MutableCollection<CombatCard>
    var power: Int

    val unitsPresent: List<GameUnit>

    var cardLimit: Int

    var powerSelected: Int

    var camaraderie: Boolean

    fun selectCard(card: CombatCard)
    fun deselectCard(card: CombatCard)
    fun concludeCombat(winner: Boolean, workersDrivenOff: Int)
    fun finalPower() : Int
    fun retreatUnits(fromHex: MapHex)
    fun requestCombatDecision()
}

open class DefaultCombatBoard(final override val combatHex: MapHex, override val attackingPlayer: PlayerCombatBoard, override val defendingPlayer: PlayerCombatBoard) : CombatBoard {

    internal class DefaultPlayerCombatBoard(player: Player, unitsPresent: List<GameUnit>) : AbstractPlayerCombatBoard(player, unitsPresent) {
        override fun requestCombatDecision() {
            powerSelected = player.user.requester?.requestChoice(CombatChoice.CHOOSE_POWER, (0..this.power).toList())?: 0
            player.user.requester?.requestSelection(CombatChoice.CHOOSE_CARDS, this.cardsAvailable.toList())?.forEach { selectCard(it) }
        }
    }

    constructor(combatHex: MapHex, attacker: Player, defender: Player) :
            this(
                    combatHex,
                    createPlayerBoard(attacker, combatHex.unitsPresent.filter { it.controllingPlayer == attacker }),
                    createPlayerBoard(defender, combatHex.unitsPresent.filter { it.controllingPlayer == defender })
            )


    private var results: CombatResults? = null

    override fun getPlayerBoard(player: PlayerInstance): PlayerCombatBoard {
        return if(player == attackingPlayer.player) {
            attackingPlayer
        } else {
            defendingPlayer
        }
    }

    override fun getOpposingBoard(player: PlayerInstance): PlayerCombatBoard {
        return if(player == defendingPlayer.player) {
            attackingPlayer
        } else {
            defendingPlayer
        }
    }

    override fun determineResults(): CombatResults {
        if(results == null) {
            results = CombatResults(attackingPlayer.finalPower(), defendingPlayer.finalPower())
        }
        return results!!
    }

    override fun resolveCombat() {
        val defenseWon = results!!.defenderResult > results!!.attackerResult
        if (defenseWon) {
            defendingPlayer.concludeCombat(true, 0)
            attackingPlayer.concludeCombat(false, 0)

            attackingPlayer.retreatUnits(combatHex)

            defendingPlayer.player.addStar(StarType.COMBAT)

            attackingPlayer.unitsPresent
        } else {
            attackingPlayer.concludeCombat(true, defendingPlayer.unitsPresent.count { it.type == UnitType.WORKER })
            defendingPlayer.concludeCombat(false, 0)

            defendingPlayer.retreatUnits(combatHex)

            attackingPlayer.player.addStar(StarType.COMBAT)

            defendingPlayer.unitsPresent
        }.forEach { gameUnit -> combatHex.dropAll(gameUnit)}

        if(!defenseWon) combatHex.resolveMove(attackingPlayer.unitsPresent)
    }

    companion object {

        private var _createPlayerBoard: ((PlayerInstance, List<GameUnit>) -> PlayerCombatBoard)? = null
        val createPlayerBoard: (PlayerInstance, List<GameUnit>) -> PlayerCombatBoard
            get() {
                if(_createPlayerBoard == null) {
                    _createPlayerBoard = ::DefaultPlayerCombatBoard
                }

                return _createPlayerBoard!!
            }
    }
}

data class CombatResults(val attackerResult: Int, val defenderResult: Int)

interface CombatBoard {

    val combatHex: MapHex
    val attackingPlayer: PlayerCombatBoard
    val defendingPlayer: PlayerCombatBoard

    fun getPlayerBoard(player: PlayerInstance): PlayerCombatBoard
    fun getOpposingBoard(player: PlayerInstance): PlayerCombatBoard

    fun determineResults(): CombatResults
    fun resolveCombat()
    //TODO: Make an implementation of this and have it actually do the combat resolution.
}