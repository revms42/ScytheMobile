package org.ajar.scythemobile.ui.combat

import androidx.lifecycle.*
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.combat.Battle
import org.ajar.scythemobile.model.combat.CombatBoard
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.combat.CombatResults
import org.ajar.scythemobile.model.faction.CombatRule
import org.ajar.scythemobile.model.faction.MovementRule
import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.view.MapScreenViewModel
import org.ajar.scythemobile.ui.view.StandardSelectionModel

class CombatViewModel : MapScreenViewModel() {

    private var battle: Battle? = null

    private lateinit var playerBoard: CombatBoard
    private lateinit var opposingBoard: CombatBoard
    
    //val playerAbilitiesAuto = playerBoard.selectedAbilities

    val playerAbilitiesAvailable
        get() = playerBoard.selectableAbilities
    val playerAbilitiesSelected
        get() = playerBoard.selectedAbilities

    var playerPowerMax: Int? = 0

    var playerPowerSelected: Int
        get() {
            return playerBoard.selectedPower
        }
        set(value) {
            playerBoard.selectedPower = value
        }

    private val _playerCardsList = ArrayList<CombatCard>()
    val playerCardsList: List<CombatCard> = _playerCardsList
    val playerCardsSelected
        get() = playerBoard.selectedCards

    var playerCardLimit: Int? = 0

    val playerTotalPower: Int
        get() = playerBoard.totalPower

    //val opponentAbilitiesAuto = opposingBoard.selectedAbilities
    //val opponentAbilitiesAvailable = opposingBoard.selectableAbilities

    var opponentPowerMax: Int
        get() {
            return opposingBoard.playerPower
        }
        set(value) {
            opposingBoard.playerPower = value
        }

    private val _opponentCardsList = ArrayList<CombatCard>()
    val opponentCardsList: List<CombatCard> = _opponentCardsList

    var opponentCardLimit: Int? = 0

    //val opponentTotalPower = opposingBoard.totalPower
    val opposingPlayerId: Int
        get() = opposingBoard.playerInstance.playerId

    // Init
    fun setupCombat(activity: ViewModelStoreOwner, importedBattle: String? = null, hotSeatPlayer: PlayerInstance? = null) {
        super.initialize(activity)

        if(battle == null) {
            battle = if(importedBattle.isNullOrBlank()) {
                val currentPlayer = hotSeatPlayer?: TurnHolder.currentPlayer

                TurnHolder.getNextCombat()?.let { Battle.openBattle(it) }?.also {
                    playerBoard = it.getPlayerBoard(currentPlayer)
                    opposingBoard = it.getOpposingBoard(currentPlayer)
                }
            } else {
                Battle.fromString(importedBattle)?.also {
                    playerBoard = it.getPlayerBoard()
                    opposingBoard = it.getOpposingBoard()
                }
            }
        }

        battle?.getPlayerBoard()?.hex?.also { mapViewModel.setSelectionModel(StandardSelectionModel.HighlightSelectedHexModel(null, it))  }
    }

    // First
    fun applyAutomaticAbilities() {
        battle?.applyAutomaticCombatRules()
    }

    // Fourth
    fun finalizeAbilitySelection() {
        playerBoard.selectedAbilities.also { it.forEach { ability -> battle?.applyConditionalCombatRule(playerBoard.playerInstance, ability) } }
    }

    // Fifth
    fun setupSelectableAttackValues() {
        opposingBoard.also {
            opponentPowerMax = it.playerPower
            _opponentCardsList.addAll(it.playerCombatCards)
            opponentCardLimit = it.cardLimit
        }

        playerBoard.also {
            playerPowerMax = it.playerPower
            _playerCardsList.addAll(it.playerCombatCards)
            playerCardLimit = it.cardLimit
        }
    }

    // Sixth
    fun finalizeValuesSelection() {
        battle?.finishSelection(playerBoard.playerInstance)
    }

    fun reset() {
        battle = null
        _playerCardsList.clear()
        _opponentCardsList.clear()
    }

    fun determineResults(): CombatResults? = battle?.determineResults()

    fun resolveCombat(): LiveData<MapHex>? = battle?.resolveCombat()?.let { highlightRetreatSpaces(it) }

    private fun highlightRetreatSpaces(hexes: List<MapHex>): LiveData<MapHex> {
        val selectedHex = MutableLiveData<MapHex>()

        mapViewModel.setSelectionModel(StandardSelectionModel.HighlightSelectedHexModel(selectedHex, *hexes.toTypedArray()))

        return selectedHex
    }

    fun retreatUnits(mapHex: MapHex) = battle?.retreatUnits(mapHex)
}