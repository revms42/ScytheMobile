package org.ajar.scythemobile

import android.content.Context
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.combat.CombatCardDeck


interface Resource {
    val id: Int
        get() = list.indexOf(this)
    var displayName: String
    var image: Int

    companion object {
        val list = arrayOf(*NaturalResourceType.values(), *CapitalResourceType.values())
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
        override fun plus(playerInstance: PlayerInstance) { playerInstance.drawCoins(1)}
        override fun minus(playerInstance: PlayerInstance) { playerInstance.takeCoins(1)}
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
    WOOD("Wood"),
    FOOD("Food"),
    OIL("Oil"),
    METAL("Metal");

    companion object {
        fun loadNames(context: Context) {
            context.resources.getStringArray(R.array.natural_resources).forEachIndexed { index, name ->
                values()[index].displayName = name
            }
        }
    }
}