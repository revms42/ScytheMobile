package org.ajar.scythemobile.ui.move

import android.app.Activity
import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.EncounterCard
import org.ajar.scythemobile.model.map.EncounterOutcome
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.turn.TurnHolder

class EncounterViewModel : ViewModel() {

    var hex: MapHex? = null
    var card: EncounterCard? = null

    var choices: Int? = null
    val previouslyChosen = ArrayList<EncounterOutcome>()

    val canChoosePopular: Boolean
        get() = card!!.popularOutcome.canMeetCost(TurnHolder.currentPlayer) && previouslyChosen.contains(card!!.popularOutcome)
    val canChooseCommercial: Boolean
        get() = card!!.commercialOutcome.canMeetCost(TurnHolder.currentPlayer) && previouslyChosen.contains(card!!.commercialOutcome)
    val canChooseUnpopular: Boolean
        get() = card!!.unpopularOutcome.canMeetCost(TurnHolder.currentPlayer) && previouslyChosen.contains(card!!.unpopularOutcome)

    val done: Boolean
        get() = previouslyChosen.size == choices

    fun choose(outcome: EncounterOutcome, activity: Activity): Boolean {
        val unitData = ScytheDatabase.unitDao()?.getUnitsForPlayer(TurnHolder.currentPlayer.playerId, UnitType.CHARACTER.ordinal)!![0]
        val unit = GameUnit(unitData, TurnHolder.currentPlayer)
        previouslyChosen += outcome
        outcome.applyOutcome(activity, unit)
        return done
    }

    fun reset() {
        hex = null
        card = null
        choices = null
        previouslyChosen.clear()
    }
}