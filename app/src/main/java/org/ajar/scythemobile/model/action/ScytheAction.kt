package org.ajar.scythemobile.model.action

import org.ajar.scythemobile.model.CapitalResourceType
import org.ajar.scythemobile.model.NaturalResourceType
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
                    UnitType.WORKER.ordinal -> it.owner == player.playerId // TODO: Unclear whether you can produce on another player's mill
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
                    GiveWorkerAction(hex.loc, player, amount).perform()
                else -> false
            }
        }
    }
    class MoveNaturalResourceAction(private val resource: ResourceData, private val to: MapHex) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            return if(to.loc > 0 && !GameMap.currentMap.startingHexes.contains(to)) {
                resource.loc = to.loc
                TurnHolder.updateResource(resource)
                true
            } else false
        }
    }
    class SpendResourceAction(private val resource: ResourceData) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            return if(resource.loc != -1 || resource.owner != -1) {
                resource.loc = -1
                resource.owner = -1
                TurnHolder.updateResource(resource)
                true
            } else false
        }
    }
    class SpendPopularityAction(private val player: PlayerInstance, private val amount: Int) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            return if(amount > 0 && player.popularity >= amount) {
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
            return if(amount > 0 && player.power >= amount) {
                player.power -= amount
                TurnHolder.updatePlayer(player.playerData)
                true
            } else {
                false
            }
        }
    }
    class SpendCoinsAction(private val player: PlayerInstance, private val amount: Int) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            return if(amount > 0 && player.coins >= amount) {
                player.coins -= amount
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
            return if(hex > 0 && !GameMap.currentMap.startingHexes.contains(GameMap.currentMap.findHexAtIndex(hex))) removeFreeAndUpdateLocation(
                    hex,
                    amount,
                    fun(): List<UnitData>? = ScytheDatabase.unitDao()?.getUnitsForPlayer(player.playerId, UnitType.WORKER.ordinal),
                    fun(list: List<UnitData>) = TurnHolder.updateMove(*list.toTypedArray())
            ) else false
        }
    }
    class UpgradeSection(private val from: TopRowAction, private val leading: Boolean, private val to: BottomRowAction) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            return if(from.canUpgrade && to.canUpgrade) {
                val ok = if(leading) from.upgradeLeading() else from.upgradeFollowing()
                if(ok) {
                    to.upgrade()
                    TurnHolder.updatePlayer(from.playerInstance.playerData)
                }
                ok
            } else false
        }
    }
    class DeployMech(private val player: PlayerInstance, private val hex: MapHex, private val ability: FactionAbility) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            val mech = player.selectUnits(UnitType.MECH)?.firstOrNull { it.pos == -1 }
            return if(mech != null && !GameMap.currentMap.startingHexes.contains(hex) && player.factionMat.lockedFactionAbilities.contains(ability) && player.factionMat.unlockFactionAbility(ability)) {
                return mech.let {
                    it.pos = hex.loc
                    TurnHolder.updateMove(it.unitData)
                    TurnHolder.updatePlayer(player.playerData)
                    true
                }
            } else {
                false
            }
        }
    }
    class GiveNaturalResource(private val hex: Int, private val resource: NaturalResourceType, private val amount: Int) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            return if(hex > 0 && !GameMap.currentMap.startingHexes.contains(GameMap.currentMap.findHexAtIndex(hex)))removeFreeAndUpdateLocation(
                    hex,
                    amount,
                    fun(): List<ResourceData>? = ScytheDatabase.resourceDao()?.getUnclaimedResourcesOfType(resource.id),
                    fun(list: List<ResourceData>) = TurnHolder.updateResource(*list.toTypedArray())
            ) else false
        }
    }
    class EnlistSection(private val player: PlayerInstance, private val action: BottomRowAction, private val capitalResourceType: CapitalResourceType) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            return if(!action.enlisted && player.factionMat.getEnlistmentBonusesAvailabe().contains(capitalResourceType)) {
                action.enlisted = true
                repeat(player.factionMat.getEnlistmentBonus(capitalResourceType)) { capitalResourceType.plus(player) }
                when (capitalResourceType) {
                    CapitalResourceType.COINS -> player.playerData.factionMat.enlistCoins = true
                    CapitalResourceType.POPULARITY -> player.playerData.factionMat.enlistPop = true
                    CapitalResourceType.POWER -> player.playerData.factionMat.enlistPower = true
                    CapitalResourceType.CARDS -> player.playerData.factionMat.enlistCards = true
                }
                TurnHolder.updatePlayer(player.playerData)
                true
            } else false
        }
    }
    class BuildStructure(private val hex: MapHex, private val structure: GameUnit) : ScytheAction<Boolean>() {
        override fun perform(): Boolean {
            return if(!(structure.pos > 0 || hex.terrain == TerrainFeature.FACTORY || hex.terrain == TerrainFeature.LAKE || GameMap.currentMap.startingHexes.contains(hex))) {
                structure.pos = hex.loc
                TurnHolder.updateMove(structure.unitData)
                true
            } else false
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

        return if(changed?.isNotEmpty() == true) changed.let { update(it); true } else false
    }
}