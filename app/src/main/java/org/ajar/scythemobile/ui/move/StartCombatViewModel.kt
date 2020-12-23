package org.ajar.scythemobile.ui.move

import androidx.lifecycle.*
import org.ajar.scythemobile.data.CombatRecord
import org.ajar.scythemobile.model.combat.Battle
import org.ajar.scythemobile.model.combat.CombatBoard
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.faction.CombatRule
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.view.MapScreenViewModel
import org.ajar.scythemobile.ui.view.StandardSelectionModel

class StartCombatViewModel : MapScreenViewModel() {

    private val combatRecord: CombatRecord?
        get() {
            return TurnHolder.getNextCombat()
        }

    private var _battle: Battle? = null
    private val battle: Battle?
        get() {
            if(_battle == null) {
                _battle = combatRecord?.let { Battle.openBattle(it) }
            }
            return _battle
        }

    private var _attackingBoard: CombatBoard? = null
    private val attackingBoard: CombatBoard?
        get() {
            if(_attackingBoard == null) {
                _attackingBoard = battle?.getPlayerBoard(TurnHolder.currentPlayer)
            }
            return _attackingBoard
        }

    private var _defendingBoard: CombatBoard? = null
    private val defendingBoard: CombatBoard?
        get() {
            if(_defendingBoard == null) {
                _defendingBoard = battle?.getOpposingBoard(TurnHolder.currentPlayer)
            }
            return _defendingBoard
        }
    
    val attackingAbilitiesAuto = attackingBoard?.selectedAbilities

    val attackingAbilitiesAvailable = attackingBoard?.selectableAbilities?: emptyList()
    val attackingAbilitiesSelected = ArrayList<CombatRule>()

    var attackingPowerMax: Int? = 0

    var attackingPowerSelected: Int
        get() {
            return attackingBoard?.selectedPower?: 0
        }
        set(value) {
            attackingBoard?.selectedPower = value
        }

    private val _attackingCardsList = ArrayList<CombatCard>()
    val attackingCardsList: List<CombatCard> = _attackingCardsList
    val attackingCardsSelected = attackingBoard?.selectedCards

    var attackingCardLimit: Int? = 0

    val attackingTotalPower: Int
        get() = _attackingBoard?.totalPower?: 0

    val defendingAbilitiesAuto = defendingBoard?.selectedAbilities
    val defendingAbilitiesAvailable = defendingBoard?.selectableAbilities

    var defendingPowerMax: Int
        get() {
            return defendingBoard?.playerPower?: 0
        }
        set(value) {
            defendingBoard?.playerPower = value
        }

    private val _defendingCardsList = ArrayList<CombatCard>()
    val defendingCardsList: List<CombatCard> = _defendingCardsList

    var defendingCardLimit: Int? = 0

    val defendingTotalPower = _defendingBoard?.totalPower

    // Init
    override fun initialize(activity: ViewModelStoreOwner) {
        super.initialize(activity)
        attackingBoard?.hex?.also { mapViewModel.setSelectionModel(StandardSelectionModel.HighlightSelectedHexModel(it)) }
    }

    // First
    fun applyAutomaticAbilities() {
        battle?.applyAutomaticCombatRules()
    }

    // Fourth
    fun finalizeAbilitySelection() {
        attackingBoard?.selectedAbilities?.forEach { battle?.applyConditionalCombatRule(TurnHolder.currentPlayer, it) }
    }

    // Fifth
    fun setupSelectableAttackValues(activity: LifecycleOwner) {
        defendingBoard?.also {
            defendingPowerMax = it.playerPower
            _defendingCardsList.addAll(it.playerCombatCards)
            defendingCardLimit = it.cardLimit
        }

        attackingBoard?.also {
            attackingPowerMax = it.playerPower
            _attackingCardsList.addAll(it.playerCombatCards)
            attackingCardLimit = it.cardLimit
        }
    }

    // Sixth
    fun finalizeValuesSelection() {
        battle?.finishSelection(TurnHolder.currentPlayer)
    }

    // Seventh
    fun getOpposingBoard() : CombatBoard? {
        return battle?.getOpposingBoard(TurnHolder.currentPlayer)
    }
}