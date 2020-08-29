package org.ajar.scythemobile.ui.combat

import androidx.lifecycle.ViewModel
import org.ajar.scythemobile.model.combat.CombatCard
import org.ajar.scythemobile.old.model.entity.AbstractPlayer

class CombatViewModel : ViewModel() {

    val playerCombatPower: Int
        get() = player.power
    val playerCombatCards: MutableList<CombatCard>
        get() = player.combatCards

    private val playerCardsSelected = ArrayList<CombatCard>()
    private var powerSelected = 0

    lateinit var player: AbstractPlayer
    lateinit var opponent: AbstractPlayer

    lateinit var incomingAction: String
    lateinit var incomingData: String

    fun setSelected(combatCard: CombatCard) {
        if(playerCardsSelected.contains(combatCard)) {
            playerCardsSelected.remove(combatCard)
        } else {
            playerCardsSelected.add(combatCard)
        }
    }

    fun setPower(power: Int = 0) {
        powerSelected = when {
            power > player.power -> player.power
            power < 0 -> 0
            else -> power
        }
    }

    /**
     * TODO
     * This needs to generate a URI that opens ScytheMobile for
     * the combat screens and has the player pick out their values.
     */
    fun generateCombatRequest(): String {
        TODO("Generate a Request")
    }
}
