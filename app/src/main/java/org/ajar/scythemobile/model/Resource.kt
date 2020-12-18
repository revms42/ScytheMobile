package org.ajar.scythemobile.model

import android.content.Context
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.player.Bank


interface Resource {
    val id: Int
        get() = list.indexOf(this)
    var displayName: String
    var image: Int

    companion object {
        val list: Array<out Resource> = arrayOf(*NaturalResourceType.values(), *CapitalResourceType.values())

        fun valueOf(id: Int) : Resource? {
            return list.firstOrNull { resource -> resource.id == id }
        }
    }
}

enum class CapitalResourceType(override var displayName: String, override var image: Int = -1) : Resource {
    POWER("Power"){
        override fun plus(playerInstance: PlayerInstance) { playerInstance.power += 1 }
        override fun minus(playerInstance: PlayerInstance) { playerInstance.power -= 1 }
    },
    POPULARITY("Popularity"){
        override fun plus(playerInstance: PlayerInstance) { playerInstance.popularity += 1 }
        override fun minus(playerInstance: PlayerInstance) { playerInstance.popularity -= 1 }
    },
    COINS("Coins"){
        override fun plus(playerInstance: PlayerInstance) { Bank.addCoins(playerInstance.playerData, 1)}
        override fun minus(playerInstance: PlayerInstance) { Bank.removeCoins(playerInstance.playerData, 1)}
    },
    CARDS("Combat Cards"){
        override fun plus(playerInstance: PlayerInstance) {
            CombatCardDeck.currentDeck.drawCard(playerInstance)
        }
        override fun minus(playerInstance: PlayerInstance) {
            playerInstance.combatCards?.minBy { card -> card.resourceData.value }?.also {
                CombatCardDeck.currentDeck.returnCard(it)
            }
        }
    };


    abstract operator fun plus(playerInstance: PlayerInstance)
    abstract operator fun minus(playerInstance: PlayerInstance)

    companion object {
        fun loadNames(context: Context) {
            context.resources.getStringArray(R.array.capital_resources).forEachIndexed { index, name ->
                values()[index].displayName = name
            }
        }
    }
}

enum class NaturalResourceType(override var displayName: String, override var image: Int = -1) : Resource {
    WOOD("Wood", R.drawable.ic_wood_icon),
    FOOD("Food", R.drawable.ic_food_icon),
    OIL("Oil", R.drawable.ic_oil_icon),
    METAL("Metal", R.drawable.ic_metal_icon),
    ANY("(Any)"),
    ANY_DISSIMILAR("(Dissimilar)");

    companion object {
        val valueList = listOf(WOOD, FOOD, OIL, METAL)
        fun loadNames(context: Context) {
            context.resources.getStringArray(R.array.natural_resources).forEachIndexed { index, name ->
                values()[index].displayName = name
            }
        }
    }
}