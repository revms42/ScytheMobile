package org.ajar.scythemobile.old.model

import org.ajar.scythemobile.old.model.combat.*
import org.ajar.scythemobile.old.model.entity.AbstractPlayer
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.old.model.entity.Player
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.old.model.faction.FactionMat
import org.ajar.scythemobile.old.model.faction.FactionMatModel
import org.ajar.scythemobile.old.model.map.EncounterCard
import org.ajar.scythemobile.old.model.map.EncounterOutcome
import org.ajar.scythemobile.old.model.map.MapHex
import org.ajar.scythemobile.old.model.playermat.PlayerMat
import org.ajar.scythemobile.old.model.playermat.PlayerMatModel
import org.ajar.scythemobile.old.model.production.*

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
        return choices.toMutableList().subList(0, limit)
    }

    override fun requestBinaryChoice(binaryChoice: BinaryChoice): Boolean {
        return true
    }

}

class TestUser(override val human: Boolean = false, override val requester: RequestsUserInput? = TestRequester()) : User

class TestPlayer(factionMatModel: FactionMatModel = FactionMat.CRIMEA, playerMatModel: PlayerMatModel = PlayerMat.MECHANICAL ) : AbstractPlayer(TestUser(), factionMatModel, playerMatModel) {

    override fun toString(): String {
        return "${factionMat.model}/${playerMat.playerMatModel}"
    }

}

class TestUnit(override var controllingPlayer: Player, override val type: UnitType, override val heldMapResources: ArrayList<MapResource> = ArrayList()) : GameUnit {
    override fun toString(): String {
        return "TestUnit $type of $controllingPlayer with ${heldMapResources.size} resources and id ${hashCode()}"
    }
}

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

class TestEncounterCard : EncounterCard {
    override val outcomes: List<EncounterOutcome> = listOf(
            object : EncounterOutcome {
                override val title: String = "Outcome 1"
                override val description: String = "Outcome 1"

                override fun applyOutcome(unit: GameUnit) {
                    unit.controllingPlayer.coins += 1
                }

                override fun canMeetCost(player: Player): Boolean = true
            },
            object : EncounterOutcome {
                override val title: String = "Outcome 2"
                override val description: String = "Outcome 2"

                override fun applyOutcome(unit: GameUnit) {
                    unit.controllingPlayer.popularity += 1
                }

                override fun canMeetCost(player: Player): Boolean = true
            },
            object : EncounterOutcome {
                override val title: String = "Outcome 3"
                override val description: String = "Outcome 3"

                override fun applyOutcome(unit: GameUnit) {
                    unit.controllingPlayer.power += 1
                }

                override fun canMeetCost(player: Player): Boolean = true
            }
    )

}