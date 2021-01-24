package org.ajar.scythemobile.ui.move

import android.app.Activity
import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.map.EncounterCard
import org.ajar.scythemobile.model.map.EncounterOutcome
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.turn.TurnHolder

class EncounterViewModel : ViewModel() {

    var hex: MapHex? = null
    var card: EncounterCard? = null

    var character: GameUnit? = null

    val choices: Int
        get() {
            return character?.controllingPlayer?.factionMat?.factionMat?.encounterSelections?: 1
        }
    val previouslyChosen = ArrayList<EncounterOutcome>()

    val canChoosePopular: Boolean
        get() = card!!.popularOutcome.canMeetCost(TurnHolder.currentPlayer) && !previouslyChosen.contains(card!!.popularOutcome)
    val canChooseCommercial: Boolean
        get() = card!!.commercialOutcome.canMeetCost(TurnHolder.currentPlayer) && !previouslyChosen.contains(card!!.commercialOutcome)
    val canChooseUnpopular: Boolean
        get() = card!!.unpopularOutcome.canMeetCost(TurnHolder.currentPlayer) && !previouslyChosen.contains(card!!.unpopularOutcome)

    val done: Boolean
        get() = previouslyChosen.size == choices

    fun loadPlayerData() {
        character = GameUnit.load(GameMap.currentMap.unitsAtHex(hex!!.loc).firstOrNull { it.type == UnitType.CHARACTER.ordinal }!!)
    }

    fun choose(outcome: EncounterOutcome, activity: Activity): Boolean {
        previouslyChosen.add(outcome)
        outcome.applyOutcome(activity, character!!)
        return done
    }

    fun getChoices(): List<EncounterOutcome> {
        return ArrayList<EncounterOutcome>().also {
            if(canChoosePopular) it.add(card!!.popularOutcome)
            if(canChooseCommercial) it.add(card!!.commercialOutcome)
            if(canChooseUnpopular) it.add(card!!.unpopularOutcome)
        }
    }

    fun reset() {
        hex?.also { it.data.encounter = null ; TurnHolder.updateEncounter(it.data) }
        TurnHolder.commitChanges()

        hex = null
        card = null
        character = null
        previouslyChosen.clear()
    }
}