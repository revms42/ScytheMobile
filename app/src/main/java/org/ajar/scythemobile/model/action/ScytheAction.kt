package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.data.*
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.FactionAbility
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.map.TerrainFeature
import org.ajar.scythemobile.model.player.BottomRowAction
import org.ajar.scythemobile.model.player.TopRowAction
import org.ajar.scythemobile.turn.TurnHolder

sealed class ScytheAction<R> {
    class MoveUnitAction(private val units: List<GameUnit>, private val to: MapHex) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            if(to.provokesCombat && to.playerInControl != units[0].controllingPlayer.playerId) {
                TurnHolder.addCombat(to.loc, units[0].controllingPlayer.playerId, units.map { it.id })
            }
            TurnHolder.updateMove(*units.map { it.pos = to.loc; it.unitData }.toTypedArray())
            return true
        }
    }
    class ProduceAction(private val player: PlayerInstance, private val hex: MapHex) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            val amount = GameMap.currentMap.unitsAtHex(hex.loc).filter {
                when(it.type) {
                    UnitType.MILL.ordinal -> it.owner == player.playerId
                    UnitType.WORKER.ordinal -> true
                    else -> false
                }
            }.count()

            return when(hex.terrain) {
                TerrainFeature.FOREST, TerrainFeature.FIELD, TerrainFeature.MOUNTAIN, TerrainFeature.TUNDRA ->
                    removeFreeAndUpdateLocation(
                            hex.loc,
                            amount,
                            fun(): List<ResourceData>? = ScytheDatabase.resourceDao()?.getUnclaimedResourcesOfType(hex.terrain.resource!!.id),
                            fun(list: List<ResourceData>) = TurnHolder.updateResource(*list.toTypedArray())
                    )
                TerrainFeature.VILLAGE ->
                    DeployWorkerAction(player, hex, amount).perform()
                else -> false
            }
        }
    }
    class DeployWorkerAction(private val player: PlayerInstance, private val hex: MapHex, private val amount: Int = 1) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            return removeFreeAndUpdateLocation(
                    hex.loc,
                    amount,
                    fun(): List<UnitData>? = ScytheDatabase.unitDao()?.getUnitsForPlayer(player.playerId, UnitType.WORKER.ordinal),
                    fun(list: List<UnitData>) = TurnHolder.updateMove(*list.toTypedArray())
            )
        }
    }
    class MoveNaturalResourceAction(private val resource: ResourceData, private val to: MapHex) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            resource.loc = to.loc
            TurnHolder.updateResource(resource)
            return true
        }
    }
    class SpendResourceAction(private val resource: ResourceData) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            resource.loc = -1
            resource.owner = -1
            TurnHolder.updateResource(resource)
            return true
        }
    }
    class SpendPopularityAction(private val player: PlayerInstance, private val amount: Int) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            return if(player.popularity >= amount) {
                player.popularity -= amount
                TurnHolder.updatePlayer(player.playerData)
                true
            } else {
                false
            }
        }
    }
    class SpendPowerAction(private val player: PlayerInstance, private val amount: Int) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            return if(player.power >= amount) {
                player.power -= amount
                TurnHolder.updatePlayer(player.playerData)
                true
            } else {
                false
            }
        }
    }
    class GiveCapitalResourceAction(private val player: PlayerInstance, private val type: CapitalResourceType, private val amount: Int) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            repeat(amount) { type.plus(player) }
            return true
        }
    }
    class GiveWorkerAction(private val hex: Int, private val player: PlayerInstance, private val amount: Int) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            return removeFreeAndUpdateLocation(
                    hex,
                    amount,
                    fun(): List<UnitData>? = ScytheDatabase.unitDao()?.getUnitsForPlayer(player.playerId, UnitType.WORKER.ordinal),
                    fun(list: List<UnitData>) = TurnHolder.updateMove(*list.toTypedArray())
            )
        }
    }
    class UpgradeSection(private val from: TopRowAction, private val leading: Boolean, private val to: BottomRowAction) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            val ok = if(leading) from.upgradeLeading() else from.upgradeFollowing()
            if(ok) {
                to.upgrade()
                TurnHolder.updatePlayer(from.playerInstance.playerData)
            }
            return ok
        }
    }
    class DeployMech(private val player: PlayerInstance, private val hex: MapHex, private val ability: FactionAbility) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            return if(player.factionMat.unlockFactionAbility(ability)) {
                return ScytheDatabase.unitDao()?.getUnitsForPlayer(player.playerId, UnitType.MECH.ordinal)?.firstOrNull { it.loc == -1 }?.let {
                    it.loc = hex.loc
                    TurnHolder.updateMove(it)
                    TurnHolder.updatePlayer(player.playerData)
                    true
                }?: false
            } else {
                false
            }
        }
    }
    class GiveNaturalResource(private val hex: Int, private val resource: NaturalResourceType, private val amount: Int) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            return removeFreeAndUpdateLocation(
                    hex,
                    amount,
                    fun(): List<ResourceData>? = ScytheDatabase.resourceDao()?.getUnclaimedResourcesOfType(resource.id),
                    fun(list: List<ResourceData>) = TurnHolder.updateResource(*list.toTypedArray())
            )
        }
    }
    class EnlistSection(private val player: PlayerInstance, private val action: BottomRowAction, private val capitalResourceType: CapitalResourceType) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            action.recruited = true
            repeat(player.factionMat.getEnlistmentBonus(capitalResourceType)) { capitalResourceType.plus(player) }
            TurnHolder.updatePlayer(player.playerData)
            return true
        }
    }
    class BuildStructure(private val hex: MapHex, private val structure: GameUnit) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            structure.pos = hex.loc
            TurnHolder.updateMove(structure.unitData)
            return true
        }
    }

    abstract fun perform(): R

    protected fun <T: Mappable> removeFreeAndUpdateLocation(hex: Int, amount: Int, retrieve: () -> List<T>?, update: (List<T>) -> Unit): Boolean {
        val free = retrieve()?.filter { it.loc == -1 }

        val changed = free?.let { list ->
            (if(list.size < amount) list else list.subList(0, amount)).map { data ->
                data.loc = hex
                data
            }
        }

        return changed?.let { update(it); true }?: false
    }
}