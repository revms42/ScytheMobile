package org.ajar.scythemobile.model.combat

interface CombatBoard {
    val playerPower: Int
    val playerCombatCards: MutableList<CombatCard>

    val selectedPower: Int
    val selectedCards: MutableList<CombatCard>

    val cardLimit: Int
}