package org.ajar.scythemobile.model.entity

import org.ajar.scythemobile.model.StarModel
import org.ajar.scythemobile.model.StarType
import org.ajar.scythemobile.model.User
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.faction.FactionMatInstance
import org.ajar.scythemobile.model.faction.FactionMatModel
import org.ajar.scythemobile.model.objective.Objective
import org.ajar.scythemobile.model.objective.ObjectiveCardDeck
import org.ajar.scythemobile.model.playermat.PlayerMatInstance
import org.ajar.scythemobile.model.playermat.PlayerMatModel

class AbstractPlayer(override val user: User, factionMat: FactionMatModel, playerMatModel: PlayerMatModel) : Player {

    override val factionMat = FactionMatInstance(factionMat)
    override val playerMat = PlayerMatInstance(playerMatModel)

    private var _power: Int = 0
    override var power: Int
        get() = _power
        set(value) {
            _power = when {
                value < 0 -> 0
                value >= 16 -> {
                    factionMat.model.addStar(StarType.POWER, this)
                    16
                }
                else -> value
            }
        }

    override val combatCards: MutableList<CombatCard> = ArrayList()

    private var _popularity: Int = 0
    override var popularity: Int
        get() = _popularity
        set(value) {
            _popularity = when {
                value < 0 -> 0
                value >= 18 -> {
                    factionMat.model.addStar(StarType.POPULARITY, this)
                    18
                }
                else -> value
            }
        }


    private var _coins: Int = 0
    override var coins: Int
        get() = _coins
        set(value) {
            _coins = value
        }

    override val stars: HashMap<StarModel, Int> = HashMap()

    override val objectives: MutableList<Objective> = ArrayList()

    init {
        _power = factionMat.initialPower
        _popularity = playerMatModel.initialPopularity
        _coins = playerMatModel.initialCoins

        for (i in 0..factionMat.initialCombatCards) {
            combatCards.add(drawCombatCard())
        }

        for (i in 0..playerMatModel.initialObjectives) {
            objectives.add(selectObjective())
        }
    }

    override fun getStarCount(starType: StarModel): Int = stars[starType]?: 0
    override fun addStar(starType: StarModel) = factionMat.model.addStar(starType, this)

    companion object {
        fun selectObjective() : Objective {
            return ObjectiveCardDeck.currentDeck.drawCard()
        }
        fun drawCombatCard() : CombatCard {
            return CombatCardDeck.currentDeck.drawCard()
        }
    }
}

interface Player {

    val user: User
    val factionMat: FactionMatInstance
    var power: Int
    val combatCards: MutableList<CombatCard>

    val playerMat: PlayerMatInstance
    var popularity: Int
    var coins: Int
    val objectives: MutableList<Objective>

    val stars: HashMap<StarModel, Int>

    fun getStarCount(starType: StarModel): Int
    fun addStar(starType: StarModel)
}
