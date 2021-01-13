package org.ajar.scythemobile.ui.combat

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.faction.CombatRule
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.ScytheTurnFragment

abstract class CombatSelectFragment(nav: Int) : ScytheTurnFragment(nav) {

    lateinit var combatViewModel: CombatViewModel

    fun initializeViewModel() {
        combatViewModel = ViewModelProvider(requireActivity()).get(CombatViewModel::class.java)
    }

    fun launchSelectAbilities() {
        combatViewModel.applyAutomaticAbilities()

        if(combatViewModel.playerAbilitiesAvailable.isNotEmpty()) {
            requireActivity().also { activity ->
                val builder = AlertDialog.Builder(activity)

                builder.setTitle(R.string.title_select_abilities)

                builder.setMultiChoiceItems(combatViewModel.playerAbilitiesAvailable.map { it.abilityName }.toTypedArray(), null ) { _, index, isChecked ->
                    val value = combatViewModel.playerAbilitiesAvailable[index]
                    val newList = ArrayList<CombatRule>()
                    combatViewModel.playerAbilitiesSelected.also { ruleList ->
                        when {
                            combatViewModel.playerAbilitiesSelected.contains(value) && !isChecked -> {
                                newList.addAll(ruleList)
                                newList.remove(value)
                            }
                            !combatViewModel.playerAbilitiesSelected.contains(value) && isChecked -> {
                                newList.addAll(ruleList)
                                newList.add(value)
                            }
                        }
                    }
                    combatViewModel.playerAbilitiesSelected.also { it.clear() ; it.addAll(newList) }
                }.setPositiveButton(R.string.button_confirm_ability_selection) { _, _ ->
                    launchSelectPower()
                }.setNegativeButton(R.string.button_forego_ability_selection) { _, _ ->
                    combatViewModel.playerAbilitiesSelected.clear()
                    launchSelectPower()
                }.show()
            }
        } else {
            launchSelectPower()
        }
    }

    private fun launchSelectPower() {
        combatViewModel.finalizeAbilitySelection()
        combatViewModel.setupSelectableAttackValues()

        val maxPower = combatViewModel.playerPowerMax?:0
        if(maxPower > 0) {
            requireActivity().also { activity ->
                val builder = AlertDialog.Builder(activity)

                builder.setTitle(R.string.title_select_combat_values)

                builder.setMessage(resources.getString(R.string.msg_combat_opponent_stats, combatViewModel.opponentPowerMax, combatViewModel.opponentCardsList.size))
                builder.setPositiveButton(R.string.button_ok) { _, _ ->
                    val selectionBuilder = AlertDialog.Builder(activity)

                    selectionBuilder.setTitle(R.string.title_select_combat_values)

                    selectionBuilder.setSingleChoiceItems((0..maxPower).map { "$it" }.toTypedArray(), -1) { _, index ->
                        combatViewModel.playerPowerSelected = index
                    }.setPositiveButton(R.string.button_accept_power) { _, _ ->
                        launchSelectCards()
                    }.setNegativeButton(R.string.button_cancel) { _, _ ->
                        combatViewModel.playerPowerSelected = 0
                        launchSelectCards()
                    }

                    selectionBuilder.show()
                }.setNegativeButton(R.string.button_cancel) { _, _ ->
                    combatViewModel.playerPowerSelected = 0
                    launchSelectCards()
                }.show()
            }
        } else {
            launchSelectCards()
        }
    }

    private fun launchSelectCards() {
        if(combatViewModel.playerCardsList.isNotEmpty() && combatViewModel.playerCardLimit?:0 > 0) {
            requireActivity().also { activity ->
                val builder = AlertDialog.Builder(activity)

                builder.setTitle(R.string.title_select_combat_cards)

                builder.setMessage(resources.getString(R.string.msg_combat_opponent_stats_plus_selection, combatViewModel.opponentPowerMax, combatViewModel.opponentCardsList.size, combatViewModel.playerPowerSelected))

                builder.setPositiveButton(R.string.button_ok) { _, _ ->
                    val secondBuilder = AlertDialog.Builder(activity)

                    builder.setTitle(R.string.title_select_combat_cards)

                    val items = combatViewModel.playerCardsList.map { "${it.power}" }
                    val chosen = ArrayList<CombatCard>()
                    secondBuilder.setMultiChoiceItems(items.toTypedArray(), null) { _, index, checked ->
                        val card = combatViewModel.playerCardsList[index]
                        if (chosen.contains(card) && !checked) {
                            chosen.remove(card)
                            combatViewModel.playerCardsSelected.clear()
                            combatViewModel.playerCardsSelected.addAll(chosen)
                        } else if (!chosen.contains(card) && checked) {
                            chosen.add(card)
                            if (chosen.size > combatViewModel.playerCardLimit?: 0) {
                                chosen.removeAt(0)
                            }
                            combatViewModel.playerCardsSelected.clear()
                            combatViewModel.playerCardsSelected.addAll(chosen)
                        }
                    }.setPositiveButton(R.string.button_accept_cards) { _, _ ->
                        finalizeCombat()
                    }.setNegativeButton(R.string.button_cancel) { _, _ ->
                        combatViewModel.playerCardsSelected.clear()
                        finalizeCombat()
                    }.show()
                }.setNegativeButton(R.string.button_cancel) { _, _ ->
                    combatViewModel.playerCardsSelected.clear()
                    finalizeCombat()
                }.show()
            }
        } else {
            finalizeCombat()
        }
    }

    private fun finalizeCombat() {
        combatViewModel.finalizeValuesSelection()
        TurnHolder.commitChanges()

        postSelection()
    }

    abstract fun postSelection()
}