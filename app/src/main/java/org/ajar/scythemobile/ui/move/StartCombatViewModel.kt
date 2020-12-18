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
    
    private val _attackingAbilitiesAuto = MutableLiveData<List<CombatRule>>()
    val attackingAbilitiesAuto: LiveData<List<CombatRule>> = _attackingAbilitiesAuto

    private val _attackingAbilities = MutableLiveData<List<CombatRule>>()
    val attackingAbilitiesAvailable: LiveData<List<CombatRule>> = _attackingAbilities
    val attackingAbilitiesSelected = MutableLiveData<List<CombatRule>>()
    
    private val _attackingPowerMax = MutableLiveData<Int>()
    val attackingPowerMax: LiveData<Int> = _attackingPowerMax
    val attackingPowerSelected = MutableLiveData(0)

    private val _attackingCardsList = MutableLiveData<List<CombatCard>>()
    val attackingCardsList: LiveData<List<CombatCard>> = _attackingCardsList
    val attackingCardsSelected = MutableLiveData(ArrayList<CombatCard>())

    private val _attackingCardLimit = MutableLiveData<Int>()
    val attackingCardLimit: LiveData<Int> = _attackingCardLimit

    private val _defendingAbilitiesAuto = MutableLiveData<List<CombatRule>>()
    val defendingAbilitiesAuto: LiveData<List<CombatRule>> = _defendingAbilitiesAuto

    private val _defendingAbilities = MutableLiveData<List<CombatRule>>()
    val defendingAbilitiesAvailable: LiveData<List<CombatRule>> = _defendingAbilities

    private val _defendingPowerMax = MutableLiveData<Int>()
    val defendingPowerMax: LiveData<Int> = _defendingPowerMax

    private val _defendingCardsList = MutableLiveData<List<CombatCard>>()
    val defendingCardsList: LiveData<List<CombatCard>> = _defendingCardsList

    private val _defendingCardLimit = MutableLiveData<Int>()
    val defendingCardLimit: LiveData<Int> = _defendingCardLimit

    // Init
    override fun initialize(activity: ViewModelStoreOwner) {
        super.initialize(activity)
        attackingBoard?.hex?.also { mapViewModel.setSelectionModel(StandardSelectionModel.HighlightSelectedHexModel(it)) }
    }

    // First
    fun setupAutomaticAbilities() {
        _attackingBoard?.also {
            _attackingAbilitiesAuto.postValue(it.selectableAbilities)
        }
        _defendingBoard?.also {
            _defendingAbilitiesAuto.postValue(it.selectedAbilities)
        }
    }

    // Second
    fun applyAutomaticAbilities() {
        battle?.applyAutomaticCombatRules()
    }

    // Third
    fun setupSelectableAbilities(activity: LifecycleOwner) {
        _attackingBoard?.also {
            _attackingAbilities.postValue(it.selectableAbilities)
        }
        
        attackingAbilitiesSelected.observe(activity, Observer { values -> 
            _attackingBoard?.also { 
                it.selectedAbilities.clear()
                it.selectedAbilities.addAll(values)
            }
        })

        _defendingBoard?.also {
            _defendingAbilities.postValue(it.selectableAbilities)
        }
    }

    // Fourth
    fun finalizeAbilitySelection(activity: LifecycleOwner) {
        attackingAbilitiesSelected.removeObservers(activity)
        attackingBoard?.selectedAbilities?.forEach { battle?.applyConditionalCombatRule(TurnHolder.currentPlayer, it) }
    }

    // Fifth
    fun setupSelectableAttackValues(activity: LifecycleOwner) {
        _attackingBoard?.also {
            _attackingPowerMax.postValue(it.playerPower)
            _attackingCardsList.postValue(it.playerCombatCards)
            _attackingCardLimit.postValue(it.cardLimit)
        }

        attackingPowerSelected.observe(activity, Observer { value ->
            _attackingBoard?.also {
                it.selectedPower = value
            }
        })
        attackingCardsSelected.observe(activity, Observer { value ->
            _attackingBoard?.also {
                it.selectedCards.clear()
                it.selectedCards.addAll(value)
            }
        })

        _defendingBoard?.also {
            _defendingPowerMax.postValue(it.playerPower)
            _defendingCardsList.postValue(it.playerCombatCards)
            _defendingCardLimit.postValue(it.cardLimit)
        }
    }

    // Sixth
    fun finalizeValuesSelection(activity: LifecycleOwner) {
        attackingPowerSelected.removeObservers(activity)
        attackingCardsSelected.removeObservers(activity)
        battle?.finishSelection(TurnHolder.currentPlayer)
    }

    // Seventh
    fun getOpposingBoard() : CombatBoard? {
        return battle?.getOpposingBoard(TurnHolder.currentPlayer)
    }
}