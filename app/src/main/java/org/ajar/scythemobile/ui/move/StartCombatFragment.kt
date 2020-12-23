package org.ajar.scythemobile.ui.move

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.model.faction.CombatRule
import org.ajar.scythemobile.turn.TurnHolder

class StartCombatFragment : Fragment() {

    private lateinit var startCombatViewModel: StartCombatViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        startCombatViewModel = ViewModelProvider(requireActivity()).get(StartCombatViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_start_combat, container, false)

        val button = view?.findViewById<Button>(R.id.start_combat_button)
        button?.setOnClickListener { _ ->
            launchSelectAbilities()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        startCombatViewModel.initialize(requireActivity())
    }

    private fun launchSelectAbilities() {
        startCombatViewModel.applyAutomaticAbilities()

        if(startCombatViewModel.attackingAbilitiesAvailable.isNotEmpty()) {
            requireActivity().also { activity ->
                val builder = AlertDialog.Builder(activity)

                builder.setTitle(R.string.title_select_abilities)

                builder.setMultiChoiceItems(startCombatViewModel.attackingAbilitiesAvailable.map { it.abilityName }.toTypedArray(), null ) { _, index, isChecked ->
                    val value = startCombatViewModel.attackingAbilitiesAvailable[index]
                    val newList = ArrayList<CombatRule>()
                    startCombatViewModel.attackingAbilitiesSelected.also { ruleList ->
                        when {
                            ruleList.contains(value) && !isChecked -> {
                                newList.addAll(ruleList)
                                newList.remove(value)
                            }
                            !ruleList.contains(value) && isChecked -> {
                                newList.addAll(ruleList)
                                newList.add(value)
                            }
                        }
                    }
                    startCombatViewModel.attackingAbilitiesSelected.also { it.clear() ; it.addAll(newList) }
                }.setPositiveButton(R.string.button_confirm_ability_selection) { _, _ ->
                    launchSelectPower()
                }.setNegativeButton(R.string.button_forego_ability_selection) { _, _ ->
                    startCombatViewModel.attackingAbilitiesSelected.clear()
                    launchSelectPower()
                }.show()
            }
        } else {
            launchSelectPower()
        }
    }

    private fun launchSelectPower() {
        startCombatViewModel.finalizeAbilitySelection()
        startCombatViewModel.setupSelectableAttackValues(requireActivity())

        val maxPower = startCombatViewModel.attackingPowerMax?:0
        if(maxPower > 0) {
            requireActivity().also { activity ->
                val builder = AlertDialog.Builder(activity)

                builder.setTitle(R.string.title_select_combat_values)

                builder.setMessage("Opponent has ${startCombatViewModel.defendingPowerMax} power and ${startCombatViewModel.defendingCardsList.size} cards.")
                builder.setPositiveButton(R.string.button_ok) { _, _ ->
                    val selectionBuilder = AlertDialog.Builder(activity)

                    selectionBuilder.setTitle(R.string.title_select_combat_values)

                    selectionBuilder.setSingleChoiceItems((0..maxPower).map { "$it" }.toTypedArray(), -1) { _, index ->
                        startCombatViewModel.attackingPowerSelected = index
                    }.setPositiveButton(R.string.button_accept_power) { _, _ ->
                        launchSelectCards()
                    }.setNegativeButton(R.string.button_cancel) { _, _ ->
                        startCombatViewModel.attackingPowerSelected = 0
                        launchSelectCards()
                    }

                    selectionBuilder.show()
                }.setNegativeButton(R.string.button_cancel) { _, _ ->
                    startCombatViewModel.attackingPowerSelected = 0
                    launchSelectCards()
                }.show()
            }
        } else {
            launchSelectCards()
        }
    }

    private fun launchSelectCards() {
        if(startCombatViewModel.attackingCardsList.isNotEmpty() && startCombatViewModel.attackingCardLimit?:0 > 0) {
            requireActivity().also { activity ->
                val builder = AlertDialog.Builder(activity)

                builder.setTitle(R.string.title_select_combat_cards)

                builder.setMessage("Opponent has ${startCombatViewModel.defendingPowerMax} power and ${startCombatViewModel.defendingCardsList.size} cards. \n" +
                        "Current Power is ${startCombatViewModel.attackingPowerSelected}")

                builder.setPositiveButton(R.string.button_ok) { _, _ ->
                    val secondBuilder = AlertDialog.Builder(activity)

                    builder.setTitle(R.string.title_select_combat_cards)

                    val items = startCombatViewModel.attackingCardsList.map { "${it.power}" }
                    val chosen = ArrayList<CombatCard>()
                    secondBuilder.setMultiChoiceItems(items.toTypedArray(), null) { _, index, checked ->
                        val card = startCombatViewModel.attackingCardsList[index]
                        if (chosen.contains(card) && !checked) {
                            chosen.remove(card)
                            startCombatViewModel.attackingCardsSelected?.clear()
                            startCombatViewModel.attackingCardsSelected?.addAll(chosen)
                        } else if (!chosen.contains(card) && checked) {
                            chosen.add(card)
                            if (chosen.size > startCombatViewModel.attackingCardLimit?: 0) {
                                chosen.removeAt(0)
                            }
                            startCombatViewModel.attackingCardsSelected?.clear()
                            startCombatViewModel.attackingCardsSelected?.addAll(chosen)
                        }
                    }.setPositiveButton(R.string.button_accept_cards) { _, _ ->
                        finalizeCombat()
                    }.setNegativeButton(R.string.button_cancel) { _, _ ->
                        startCombatViewModel.attackingCardsSelected?.clear()
                        finalizeCombat()
                    }.show()
                }.setNegativeButton(R.string.button_cancel) { _, _ ->
                    startCombatViewModel.attackingCardsSelected?.clear()
                    finalizeCombat()
                }.show()
            }
        } else {
            finalizeCombat()
        }
    }

    private fun finalizeCombat() {
        startCombatViewModel.finalizeValuesSelection()
        TurnHolder.commitChanges()

        Snackbar.make(requireView(), "Total power selected: ${startCombatViewModel.attackingTotalPower?: 0}", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

        val builder = AlertDialog.Builder(activity)

        builder.setTitle(R.string.title_start_combat_complete)

        builder.setMessage("${startCombatViewModel.getOpposingBoard()}")

        builder.setPositiveButton(R.string.button_ok) { _, _ ->
            Snackbar.make(requireView(), "Need to deal with this.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        builder.setNegativeButton(R.string.button_cancel) { _, _ ->
            Snackbar.make(requireView(), "Need to deal with this.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        builder.show()
    }
}