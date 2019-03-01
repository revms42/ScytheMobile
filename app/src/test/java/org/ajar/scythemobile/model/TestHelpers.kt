package org.ajar.scythemobile.model

import org.ajar.scythemobile.model.combat.*
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.FactionMat
import org.ajar.scythemobile.model.faction.FactionMatInstance
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.objective.Objective
import org.ajar.scythemobile.model.playermat.PlayerMat
import org.ajar.scythemobile.model.playermat.PlayerMatModel
import org.ajar.scythemobile.model.production.Resource

class TestRequester : RequestsUserInput {
    override fun <T> requestSelection(choice: Choice, choices: List<T>): List<T> {
        return choices
    }

    override fun <T> requestChoice(choice: Choice, choices: List<T>): T {
        return choices[0]
    }

    override fun requestBinaryChoice(binaryChoice: BinaryChoice): Boolean {
        return true
    }

}

class TestUser(override val human: Boolean = false, override val requester: RequestsUserInput? = TestRequester()) : User

class TestPlayer : Player {
    override val user: User = TestUser()
    override val combatCards: MutableList<CombatCard> = ArrayList()
    override var popularity: Int = 5
    override var coins: Int = 0
    override val objectives: MutableList<Objective> = ArrayList()
    override val factionMat: FactionMatInstance = FactionMatInstance(FactionMat.CRIMEA)
    override val playerMat: PlayerMatModel = PlayerMat.MECHANICAL
    override var power: Int = 2

    val stars: ArrayList<StarType> = ArrayList()

    companion object {
        val player: TestPlayer = TestPlayer()
        val enemy: TestPlayer = TestPlayer()
    }

    override fun addStar(starType: StarType) {
        stars.add(starType)
    }

    override fun getStarCount(starType: StarType): Int {
        return stars.filter { it == starType }.size
    }
}

data class TestUnit(override var controllingPlayer: Player, override val type: UnitType, override val heldResources: ArrayList<Resource>? = ArrayList()) : GameUnit

class TestPlayerCombatBoard(player: Player, unitsPresent: List<GameUnit>) : AbstractPlayerCombatBoard(player, unitsPresent) {
    override fun requestCombatDecision() {}
}

class TestCombatBoard(combatHex: MapHex, attackingPlayer: PlayerCombatBoard, defendingPlayer: PlayerCombatBoard) : DefaultCombatBoard(combatHex, attackingPlayer, defendingPlayer) {
    constructor(combatHex: MapHex, attacker: Player, defender: Player) :
            this(   combatHex,
                    TestPlayerCombatBoard(attacker, combatHex.unitsPresent.filter { gameUnit -> gameUnit.controllingPlayer == attacker }),
                    TestPlayerCombatBoard(defender, combatHex.unitsPresent.filter { gameUnit -> gameUnit.controllingPlayer == defender })
            )
}