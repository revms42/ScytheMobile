package org.ajar.scythemobile.model

import org.ajar.scythemobile.model.combat.*
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.FactionMat
import org.ajar.scythemobile.model.faction.FactionMatInstance
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.objective.Objective
import org.ajar.scythemobile.model.playermat.PlayerMat
import org.ajar.scythemobile.model.playermat.PlayerMatInstance
import org.ajar.scythemobile.model.playermat.PlayerMatModel
import org.ajar.scythemobile.model.production.CrimeaCardResource
import org.ajar.scythemobile.model.production.Resource
import org.ajar.scythemobile.model.production.ResourceType
import org.ajar.scythemobile.model.turn.Turn

class TestRequester : RequestsUserInput {
    override fun requestPayment(choice: Choice, cost: List<ResourceType>, choices: Map<Resource, MapHex>): Collection<Resource> {
        val collection = ArrayList<Resource>()

        cost.forEach{
            val chosen = choices.asSequence().firstOrNull { pair -> pair.key.type == it && !collection.contains(pair.key)}
            chosen?.let { collection.add(it.key) }
        }

        if(collection.size < cost.size) {
            choices.asSequence().firstOrNull { it.key is CrimeaCardResource }?.key?.let { collection.add(it) }
        }

        return collection
    }

    override fun <T> requestChoice(choice: Choice, choices: Collection<T>): T {
        return choices.first()
    }

    override fun <T> requestCancellableChoice(choice: Choice, choices: Collection<T>): T? {
        return choices.first()
    }

    override fun <T> requestSelection(choice: Choice, choices: Collection<T>, limit: Int): Collection<T> {
        return choices.toMutableList().subList(0, limit-1)
    }

    override fun requestBinaryChoice(binaryChoice: BinaryChoice): Boolean {
        return true
    }

}

class TestUser(override val human: Boolean = false, override val requester: RequestsUserInput? = TestRequester()) : User

class TestPlayer(faction: FactionMat = FactionMat.CRIMEA, playerMatModel: PlayerMatModel = PlayerMat.MECHANICAL ) : Player {
    var _deployedUnits: MutableList<GameUnit>? = null
    override val deployedUnits: MutableList<GameUnit>
        get() {
            if(_deployedUnits == null) {
                _deployedUnits = ArrayList()
            }
            return _deployedUnits!!
        }
    private var _turn: Turn? = null
    override val turn: Turn
        get() {
            if(_turn == null) {
                _turn = Turn(this)
            }
            return _turn!!
        }

    override fun newTurn() {
        _turn = Turn(this)
    }

    override fun finalizeTurn(): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val user: User = TestUser()
    override val combatCards: MutableList<CombatCard> = ArrayList()
    override var popularity: Int = 5
    override var coins: Int = 0
    override val objectives: MutableList<Objective> = ArrayList()
    override val factionMat: FactionMatInstance = FactionMatInstance(faction)
    override val playerMat: PlayerMatInstance = PlayerMatInstance(playerMatModel)
    override var power: Int = 2

    override val stars: HashMap<StarModel, Int> = HashMap()

    companion object {
        var player: TestPlayer = TestPlayer()
        var enemy: TestPlayer = TestPlayer()
    }

    override fun getStarCount(starType: StarModel): Int = stars[starType]?: 0
    override fun addStar(starType: StarModel) = factionMat.model.addStar(starType, this)
}

data class TestUnit(override var controllingPlayer: Player, override val type: UnitType, override val heldResources: ArrayList<Resource> = ArrayList()) : GameUnit

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