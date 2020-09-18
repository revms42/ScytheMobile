package org.ajar.scythemobile.model.objective

import android.content.Context
import android.util.SparseBooleanArray
import android.util.SparseIntArray
import androidx.collection.SparseArrayCompat
import androidx.collection.set
import androidx.core.util.set
import org.ajar.scythemobile.R
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.*
import kotlin.math.roundToInt

class ObjectiveCardDeck(private val objectives: MutableList<Objective>) {

    init {
        for(i in 0..objectives.size) {
            val first = (Math.random() * objectives.size).roundToInt()
            val second = (Math.random() * objectives.size).roundToInt()

            val firstObj = objectives[first]
            val secondObj = objectives[second]

            objectives[first] = secondObj
            objectives[second] = firstObj
        }
    }

    fun drawCard() : Objective {
        return objectives.removeAt(0)
    }

    companion object {
        private var _currentDeck: ObjectiveCardDeck? = null
        val currentDeck: ObjectiveCardDeck
            get() {
                if(_currentDeck == null) {
                    _currentDeck = ObjectiveCardDeck(DefaultObjective.values().toMutableList())
                }
                return _currentDeck!!
            }
    }
}

interface Objective {

    val id: Int
    var image: Int

    fun title(): String = names[id]

    fun text(): String = descriptions[id]

    fun evaluate(playerInstance: PlayerInstance): Boolean

    companion object {
        private lateinit var names: Array<String>
        private lateinit var descriptions: Array<String>
        
        private val objectives = SparseArrayCompat<Objective>()

        operator fun set(id: Int, obj: Objective) {
            objectives[id] = obj
        }

        operator fun get(id: Int): Objective? = objectives[id]

        fun load(context: Context) {
            descriptions = context.resources.getStringArray(R.array.objectives)
            names = context.resources.getStringArray(R.array.objective_names)
        }
    }
}

class MultipleCriteriaObjective(override val id: Int, override var image: Int, private vararg val objectives: Objective) : Objective {
    override fun evaluate(playerInstance: PlayerInstance): Boolean {
        return objectives.all { it.evaluate(playerInstance) }
    }
}

class MapFeatureControlObjective(override val id: Int, override var image: Int, private val feature: TerrainFeature, private val count: Int = 3) : Objective {
    override fun evaluate(playerInstance: PlayerInstance): Boolean {
        return GameMap.currentMap.findAllMatching { mapFeature -> mapFeature?.terrain == feature.ordinal }?.filter { it.playerInControl == playerInstance.playerId }?.count()?:0 >= count
    }
}

class TunnelControlObjective(override val id: Int, override var image: Int, private val count: Int = 3) : Objective {
    override fun evaluate(playerInstance: PlayerInstance): Boolean {
        return GameMap.currentMap.findAllMatching { mapFeature -> mapFeature?.tunnel?: false }?.filter { it.playerInControl == playerInstance.playerId }?.count()?:0 >= count
    }
}

sealed class PlayerRankObjective(override val id: Int = -1, override var image: Int = -1) : Objective {
    class HighestPower : PlayerRankObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return ScytheDatabase.playerDao()?.getHighestPower() == playerInstance.power
        }
    }
}

class Stockpile(override val id: Int = -1, override var image: Int = -1) : Objective {
    override fun evaluate(playerInstance: PlayerInstance): Boolean {
        return UnitType.controlUnits.flatMap { type ->
            ScytheDatabase.unitDao()!!.getUnitsForPlayer(playerInstance.playerId, type.ordinal)?: emptyList()
        }.firstOrNull { unitData ->
            val loc = unitData.loc.let { if(it == -1) null else it }

            loc?.let {
                val resources = ScytheDatabase.resourceDao()!!.getResourcesAt(it)

                if(resources?.size?: 0 >= 9) {
                    val types = arrayOf(false, false, false, false)

                    resources?.forEach { resource ->
                        types[resource.type] = true
                    }

                    types.all { type -> type }
                } else {
                    false
                }
            }?: false
        } != null
    }
}

class FoundationsOfEmpire(override val id: Int, override var image: Int) : Objective {
    override fun evaluate(playerInstance: PlayerInstance): Boolean {
        val countMap = SparseIntArray()
        return UnitType.values().firstOrNull { unitType ->
            ScytheDatabase.unitDao()!!.getUnitsForPlayer(playerInstance.playerId, unitType.ordinal)?.firstOrNull { unit ->
                countMap[unit.loc] = countMap[unit.loc] + 1

                countMap[unit.loc] >= 6
            } != null
        } != null
    }

}

class HedgingBets(override val id: Int, override var image: Int) : Objective {
    override fun evaluate(playerInstance: PlayerInstance): Boolean {
        val resourceTypes = SparseBooleanArray()

        var hasMech = false
        var hasStructure = false
        val hasResource = UnitType.controlUnits.firstOrNull { type ->
            (ScytheDatabase.unitDao()!!.getUnitsForPlayer(playerInstance.playerId, type.ordinal)?: emptyList()).firstOrNull { unit ->
                val loc = unit.loc

                when(unit.type) {
                    UnitType.MECH.ordinal -> hasMech = true
                    UnitType.MILL.ordinal or UnitType.MINE.ordinal or UnitType.ARMORY.ordinal or UnitType.MONUMENT.ordinal -> hasStructure = true
                }

                if(loc != -1) {
                    ScytheDatabase.resourceDao()!!.getResourcesAt(loc)?.firstOrNull { resource ->
                        resourceTypes[resource.type] = true
                        resourceTypes.size() == 4
                    } != null
                } else {
                    false
                }
            } != null
        } != null

        return playerInstance.upgrades >= 1 && playerInstance.recruits >= 1 && hasMech && hasResource && hasStructure
    }
}

class BalancedWorkforce(override val id: Int, override var image: Int) : Objective {
    override fun evaluate(playerInstance: PlayerInstance): Boolean {
        val workers = ScytheDatabase.unitDao()!!.getUnitsForPlayer(playerInstance.playerId, UnitType.WORKER.ordinal)?.size?: 0

        return workers > 0 && workers == playerInstance.recruits
    }
}

class WolfAmongSheep(override val id: Int, override var image: Int) : Objective {
    override fun evaluate(playerInstance: PlayerInstance): Boolean {
        return ScytheDatabase.unitDao()!!.getUnitsForPlayer(playerInstance.playerId, UnitType.CHARACTER.ordinal)?.let { list ->
            val present = ScytheDatabase.unitDao()!!.getUnitsAtLocation(list[0].loc)

            val requiredPresent = present?.size == 3 &&
                    present.firstOrNull { unit -> unit.type == UnitType.WORKER.ordinal } != null &&
                    present.firstOrNull { unit -> unit.type == UnitType.MECH.ordinal } != null

            val resourcesPresent = ScytheDatabase.resourceDao()!!.getResourcesAt(list[0].loc)?.size?: 0 >= 5

            requiredPresent && resourcesPresent
        }?: false
    }
}

class SurroundFeature(private val specialFeature: TerrainFeature, val hexes: Int, override val id: Int = 1, override var image: Int = -1) : Objective {
    override fun evaluate(playerInstance: PlayerInstance): Boolean {
        val dao = ScytheDatabase.unitDao()!!
        return GameMap.currentMap.findAllMatching { feature -> feature?.terrain == specialFeature.ordinal }?.firstOrNull {
            it.data.neighbors.asArray().count { loc ->
                dao.getUnitsAtLocation(loc)?.firstOrNull { unitData -> UnitType.controlUnits.contains(UnitType.values()[unitData.type]) }?.owner == playerInstance.playerId
            } >= hexes
        } != null
    }
}

class MonopolizeTheMarket(override val id: Int, override var image: Int) : Objective {
    override fun evaluate(playerInstance: PlayerInstance): Boolean {
        val hexes = SparseBooleanArray()
        val map = SparseIntArray()

        val dao = ScytheDatabase.unitDao()!!
        return UnitType.controlUnits.firstOrNull { type ->
            dao.getUnitsForPlayer(playerInstance.playerId, type.ordinal)?.firstOrNull { unit ->
                if(!hexes[unit.loc]) {
                    ScytheDatabase.resourceDao()!!.getResourcesAt(unit.loc)?.let { hexes[unit.loc] = true ; it }?.firstOrNull { resourceData ->
                        map[resourceData.type]++

                        map[resourceData.type] >= 9
                    } != null
                } else {
                    false
                }
            } != null
        } != null
    }
}

class HumanShield(override val id: Int, override var image: Int) : Objective {
    override fun evaluate(playerInstance: PlayerInstance): Boolean {
        return GameMap.currentMap.findAllMatching { mapFeature -> mapFeature?.terrain == TerrainFeature.FACTORY.ordinal }?.firstOrNull()?.data?.neighbors?.asArray()?.firstOrNull { loc ->
            ScytheDatabase.unitDao()!!.getUnitsAtLocation(loc)?.count { unit -> unit.type == UnitType.WORKER.ordinal && unit.owner == playerInstance.playerId }?: 0 >= 5
        } != null
    }
}

class DiversifyProduction(override val id: Int, override var image: Int) : Objective {
    override fun evaluate(playerInstance: PlayerInstance): Boolean {
        val types = SparseBooleanArray()
        return ScytheDatabase.unitDao()!!.getUnitsForPlayer(playerInstance.playerId, UnitType.WORKER.ordinal)?.firstOrNull { worker ->
            GameMap.currentMap.findHexAtIndex(worker.loc)?.data?.terrain?.also {
                if(it < 5) types[it] = true
            }
            types.size() >= 5
        } != null
    }
}

class BuildLocalInfrastructure(override val id: Int, override var image: Int) : Objective {
    override fun evaluate(playerInstance: PlayerInstance): Boolean {
        val forbidden = GameMap.currentMap.findHomeBase(playerInstance)!!.data.neighbors.asArray()

        var forbiddenCount = 0
        var allowedCount = 0
        UnitType.structures.firstOrNull {
            val loc = ScytheDatabase.unitDao()!!.getUnitsForPlayer(playerInstance.playerId, UnitType.MILL.ordinal)?.firstOrNull()?.loc

            if(loc != null) {
                if(forbidden.contains(loc)) {
                    forbiddenCount++
                } else {
                    allowedCount++
                }
            }

            forbiddenCount >= 2 || allowedCount >= 3
        }

        return allowedCount >= 3
    }
}

sealed class PlayerQualityObjective(override val id: Int = -1, override var image: Int = -1) : Objective {
    class ForcedRetreat : PlayerQualityObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return playerInstance.playerData.flagRetreat
        }
    }
    class AtLeastPower(private val threshold: Int) : PlayerQualityObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return playerInstance.playerData.power >= threshold
        }
    }
    class AtMostPower(private val threshold: Int) : PlayerQualityObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return playerInstance.playerData.power <= threshold
        }
    }
    class AtLeastUnits(private val threshold: Int, private val unitType: UnitType) : PlayerQualityObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return playerInstance.selectUnits(unitType)?.size?: 0 >= threshold
        }
    }
    class AtMostUnits(private val threshold: Int, private val unitType: UnitType) : PlayerQualityObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return playerInstance.selectUnits(unitType)?.size?: 0 <= threshold
        }
    }
    class AtLeastStructures(private val threshold: Int) : PlayerQualityObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            val dao = ScytheDatabase.unitDao()!!

            return UnitType.structures.sumBy { unitType -> dao.getUnitsForPlayer(playerInstance.playerId, unitType.ordinal)?.size?: 0 } >= threshold
        }
    }
    class HasFactoryCard : PlayerQualityObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return playerInstance.playerData.factoryCard != null
        }
    }
    class AtLeastCoins(private val threshold: Int, id: Int = -1, image: Int = -1) : PlayerQualityObjective(id, image) {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return playerInstance.coins?.size?: 0 >= threshold
        }
    }
    class AtMostCoins(private val threshold: Int) : PlayerQualityObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return playerInstance.coins?.size?: 0 <= threshold
        }
    }
    class AtLeastPop(private val threshold: Int) : PlayerQualityObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return playerInstance.popularity >= threshold
        }
    }
    class AtMostPop(private val threshold: Int) : PlayerQualityObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return playerInstance.popularity <= threshold
        }
    }
    class NoFactoryCard : PlayerQualityObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return playerInstance.playerData.factoryCard?: -1 == -1
        }
    }
    class AtMostUpgrades(private val threshold: Int) : PlayerQualityObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return playerInstance.upgrades <= threshold
        }
    }
    class AtLeastUpgrades(private val threshold: Int) : PlayerQualityObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return playerInstance.upgrades <= threshold
        }
    }
    class AtLeastCombatStars(private val threshold: Int) : PlayerQualityObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return playerInstance.playerData.starCombat >= threshold
        }
    }
    class AtLeastCombatCards(private val threshold: Int) : PlayerQualityObjective() {
        override fun evaluate(playerInstance: PlayerInstance): Boolean {
            return playerInstance.combatCards?.size?: 0 >= threshold
        }
    }
}

enum class DefaultObjective(private val wrappedObjective: Objective) : Objective {
    HIGHER_GROUND_ADVANTAGE(MapFeatureControlObjective(0, -1, TerrainFeature.MOUNTAIN)),
    UNDERWORLD_ADVANTAGE(TunnelControlObjective(1, -1)),
    HARVEST_ADVANTAGE(MapFeatureControlObjective(2, -1, TerrainFeature.FIELD)),
    NORTHERN_ADVANTAGE(MapFeatureControlObjective(3, -1, TerrainFeature.TUNDRA)),
    KING_OF_THE_HILL(MultipleCriteriaObjective(4, -1, PlayerRankObjective.HighestPower(), MapFeatureControlObjective(-1, -1, TerrainFeature.FACTORY))),

    SEND_ONE_BACK_AS_A_WARNING(MultipleCriteriaObjective(5, -1, PlayerQualityObjective.AtLeastPower(7), PlayerQualityObjective.ForcedRetreat())),
    MACHINE_OVER_MUSCLE(
            MultipleCriteriaObjective(6, -1,
                    PlayerQualityObjective.HasFactoryCard(),
                    PlayerQualityObjective.AtLeastUnits(1, UnitType.MECH),
                    PlayerQualityObjective.AtMostUnits(3, UnitType.WORKER)
            )
    ),
    ROLL_UP_YOUR_SLEEVES_WITH_THE_COMMON_MAN(
            MultipleCriteriaObjective(7, -1,
                    PlayerQualityObjective.AtMostCoins(2),
                    PlayerQualityObjective.AtLeastUnits(4, UnitType.WORKER),
                    PlayerQualityObjective.AtLeastPop(7)
            )
    ),
    STOCKPILE_FOR_THE_WINTER(Stockpile(8, -1)),
    WOODLAND_ADVANTAGE(MapFeatureControlObjective(9, -1, TerrainFeature.FOREST)),

    POPULATION_ADVANTAGE(MapFeatureControlObjective(10, -1, TerrainFeature.VILLAGE)),
    GET_RICH_OR_CRY_TRYING(PlayerQualityObjective.AtLeastCoins(20, 11, -1)),
    FOUNDATIONS_OF_THE_EMPIRE(FoundationsOfEmpire(12, -1)),
    HEDGE_YOUR_BETS(HedgingBets(13, -1)),
    BALANCED_WORKFORCE(BalancedWorkforce(14, -1)),

    A_WOLF_AMONG_THE_SHEEP(WolfAmongSheep(15, -1)),
    DIVIDE_AND_CONQUER(
            MultipleCriteriaObjective(16, -1,
                    PlayerQualityObjective.NoFactoryCard(),
                    SurroundFeature(TerrainFeature.FACTORY, 2)
            )
    ),
    BECOME_A_BELOVED_PACIFIST(
            MultipleCriteriaObjective(17, -1,
                    PlayerQualityObjective.AtMostPower(0),
                    PlayerQualityObjective.AtLeastPop(13),
                    PlayerQualityObjective.AtLeastUnits(5, UnitType.WORKER)
            )
    ),
    SHORE_UP_THE_SHORE(SurroundFeature(TerrainFeature.LAKE, 5, 18, -1)),
    CREATE_A_PERMANENT_FOOTHOLD(
            MultipleCriteriaObjective(19, -1,
                    PlayerQualityObjective.AtLeastPop(7),
                    PlayerQualityObjective.AtMostUnits(0, UnitType.MECH),
                    PlayerQualityObjective.AtLeastStructures(3)
            )
    ),

    MONOPOLIZE_THE_MARKET(MonopolizeTheMarket(20, -1)),
    TECHNOLOGICAL_BREAKTHROUGH(
            MultipleCriteriaObjective(21, -1,
                    PlayerQualityObjective.AtMostUpgrades(0),
                    PlayerQualityObjective.HasFactoryCard()
            )
    ),
    ACHIEVE_TACTICAL_MASTERY(
            MultipleCriteriaObjective(22, -1,
                    PlayerQualityObjective.AtLeastCombatStars(1),
                    PlayerQualityObjective.AtLeastCombatCards(8)
            )
    ),
    ESTABLISH_A_HUMAN_SHIELD(HumanShield(23, -1)),
    BECOME_A_DESPISED_WARMONGER(
            MultipleCriteriaObjective(24, -1,
                    PlayerQualityObjective.AtMostPop(3),
                    PlayerQualityObjective.AtLeastPower(13),
                    PlayerQualityObjective.AtLeastUnits(2, UnitType.MECH)
            )
    ),

    DIVERSIFY_PRODUCTION(DiversifyProduction(25, -1)),
    BUILD_A_LOCAL_INFRASTRUCTURE(BuildLocalInfrastructure(26, -1));

    override val id: Int
        get() = wrappedObjective.id
    override var image: Int
        get() = wrappedObjective.image
        set(value) { wrappedObjective.image = value }

    override fun evaluate(playerInstance: PlayerInstance): Boolean = wrappedObjective.evaluate(playerInstance)
    
    companion object {
        init {
            load()
        }
        
        private fun load() {
            values().forEach { Objective[it.id] = it }
        }
    }
}
