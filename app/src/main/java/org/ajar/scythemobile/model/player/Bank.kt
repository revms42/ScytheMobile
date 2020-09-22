package org.ajar.scythemobile.model.player

import org.ajar.scythemobile.data.PlayerData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.turn.TurnHolder

object Bank {
    var circulation: Int = 495

    fun addCoins(playerData: PlayerData, amount: Int) {
        val total = ScytheDatabase.playerDao()?.getPlayers()?.sumBy { it.coins }?: 0
        playerData.coins += if(total + amount > circulation) {
            circulation - total
        } else {
            amount
        }
        TurnHolder.updatePlayer(playerData)
    }

    fun removeCoins(playerData: PlayerData, amount: Int) {
        playerData.coins = if(playerData.coins < amount) 0 else playerData.coins - amount
        TurnHolder.updatePlayer(playerData)
    }
}