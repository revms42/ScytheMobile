package org.ajar.scythemobile.ui.encounter

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.turn.TurnHolder

class ChooseResourcesViewModel : ViewModel() {

    var amount: Int? = null
    val hex: Int = ScytheDatabase.unitDao()!!.getUnitsForPlayer(TurnHolder.currentPlayer.playerId, UnitType.CHARACTER.ordinal)!![0].loc

    private val woodCap = ScytheDatabase.resourceDao()?.getUnclaimedResourcesOfType(NaturalResourceType.WOOD.id)?.size?:0
    private val foodCap = ScytheDatabase.resourceDao()?.getUnclaimedResourcesOfType(NaturalResourceType.FOOD.id)?.size?:0
    private val metalCap = ScytheDatabase.resourceDao()?.getUnclaimedResourcesOfType(NaturalResourceType.METAL.id)?.size?:0
    private val oilCap = ScytheDatabase.resourceDao()?.getUnclaimedResourcesOfType(NaturalResourceType.OIL.id)?.size?:0

    var woodSelected: Int = 0
    var foodSelected: Int = 0
    var metalSelected: Int = 0
    var oilSelected: Int = 0

    val woodLimit: Int
        get() = if(amount?:0 < woodCap) amount?:0 else woodCap

    val foodLimit: Int
        get() = if(amount?:0 < foodCap) amount?:0 else foodCap

    val metalLimit: Int
        get() = if(amount?:0 < metalCap) amount?:0 else metalCap

    val oilLimit: Int
        get() = if(amount?:0 < oilCap) amount?:0 else oilCap

    val ready: Boolean
        get() = woodSelected + foodSelected + metalSelected + oilSelected == amount

    fun apply(): Boolean {
        return if(ready) {
            if(woodSelected > 0) ScytheAction.GiveNaturalResource(hex, NaturalResourceType.WOOD, woodSelected)
            if(foodSelected > 0) ScytheAction.GiveNaturalResource(hex, NaturalResourceType.FOOD, foodSelected)
            if(metalSelected > 0) ScytheAction.GiveNaturalResource(hex, NaturalResourceType.METAL, metalSelected)
            if(oilSelected > 0) ScytheAction.GiveNaturalResource(hex, NaturalResourceType.WOOD, oilSelected)
            true
        } else {
            false
        }
    }

    fun reset() {
        woodSelected = 0
        foodSelected = 0
        metalSelected = 0
        oilSelected = 0
    }
}