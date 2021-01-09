package org.ajar.scythemobile.ui

import androidx.lifecycle.ViewModel
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import org.ajar.scythemobile.R
import org.ajar.scythemobile.data.CombatRecord
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.map.GameMap
import org.ajar.scythemobile.model.player.Section
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.combat.ResolveCombatFragmentDirections

class ScytheTurnViewModel : ViewModel() {

    val selectableSections: List<Section>
        get() = currentPlayer.selectableSections()

    val currentPlayer: PlayerInstance
        get() = TurnHolder.currentPlayer

    val currentSection: Section?
        get() = TurnHolder.currentTurn.selection?.let { currentPlayer.selectableSections()[it] }

    val currentNav: Int
        get() {
            return when {
                TurnHolder.currentTurn.selection == null -> R.id.nav_start
                !TurnHolder.currentTurn.performedTop -> currentSection!!.topRowAction.fragmentNav
                TurnHolder.currentTurn.combatOne?.combatResolved == false -> determineCombatPhase(1)
                TurnHolder.currentTurn.combatTwo?.combatResolved == false -> determineCombatPhase(2)
                TurnHolder.currentTurn.combatThree?.combatResolved == false -> determineCombatPhase(3)
                !TurnHolder.currentTurn.performedBottom -> currentSection!!.bottomRowAction.fragmentNav
                else -> R.id.nav_end
            }
        }

    fun finishSection(current: Int): NavDirections? {
        return when(current) {
            R.id.nav_start -> ActionOnlyNavDirections(currentSection!!.topRowAction.actionInto)
            currentSection!!.topRowAction.fragmentNav -> determineCombatNav(currentSection!!.moveTopToBottom)
            R.id.nav_start_combat -> ActionOnlyNavDirections(R.id.action_nav_start_combat_to_nav_answer_combat)
            R.id.nav_answer_combat -> ActionOnlyNavDirections(R.id.action_nav_answer_combat_to_nav_resolve_combat)
            R.id.nav_resolve_combat -> determineCombatNav(R.id.action_nav_resolve_combat_to_nav_move)
            currentSection!!.bottomRowAction.fragmentNav -> ActionOnlyNavDirections(currentSection!!.bottomRowAction.actionOutOf).also { TurnHolder.currentTurn.performedBottom = true ; TurnHolder.commitChanges() }
            else -> null
        }
    }

    fun isTopRowComplete() : Boolean = TurnHolder.currentTurn.performedTop
    fun isBottomRowComplete() : Boolean = TurnHolder.currentTurn.performedBottom

    private fun determineCombatNav(fallThrough: Int): NavDirections {
        return with(TurnHolder.currentTurn){
            when {
                this.combatOne?.combatResolved == false -> ActionOnlyNavDirections(R.id.action_nav_move_to_nav_start_combat)
                this.combatTwo?.combatResolved == false -> ActionOnlyNavDirections(R.id.action_nav_resolve_combat_to_nav_start_combat)
                this.combatThree?.combatResolved == false -> ActionOnlyNavDirections(R.id.action_nav_resolve_combat_to_nav_start_combat)
                else -> {
                    this.combatOne?.hex?.let {
                        hex -> GameMap.currentMap.findHexAtIndex(hex)?.encounter?.let {
                            ResolveCombatFragmentDirections.actionNavResolveCombatToNavEncounter(hex, it.id, false)
                        }
                    }?: this.combatTwo?.hex?.let {
                        hex -> GameMap.currentMap.findHexAtIndex(hex)?.encounter?.let {
                            ResolveCombatFragmentDirections.actionNavResolveCombatToNavEncounter(hex, it.id, false)
                        }
                    }?: this.combatThree?.hex?.let {
                        hex -> GameMap.currentMap.findHexAtIndex(hex)?.encounter?.let {
                            ResolveCombatFragmentDirections.actionNavResolveCombatToNavEncounter(hex, it.id, false)
                        }
                    }?: ActionOnlyNavDirections(fallThrough).also { TurnHolder.currentTurn.performedTop = true ; TurnHolder.commitChanges() }
                }
            }
        }
    }

    fun selectSection(selection: Int) {
        TurnHolder.currentTurn.selection = selection
        ScytheDatabase.turnDao()?.updateTurn(TurnHolder.currentTurn)
    }

    private fun getCombatData(round: Int) : CombatRecord? {
        return when(round) {
            1 -> TurnHolder.currentTurn.combatOne
            2 -> TurnHolder.currentTurn.combatTwo
            else -> TurnHolder.currentTurn.combatThree
        }
    }

    private fun determineCombatPhase(round: Int): Int {
        val record = getCombatData(round)

        return when {
            record?.attackerPower == null -> R.id.nav_start_combat
            record.defenderPower == null -> R.id.nav_answer_combat
            else -> R.id.nav_resolve_combat
        }
    }
}