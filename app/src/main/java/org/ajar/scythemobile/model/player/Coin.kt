package org.ajar.scythemobile.model.player

import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.model.PlayerInstance

class Bank(val circulation: Int = 495) {

    private val vault: MutableList<Coin> = ArrayList()

    fun init() {
        vault.addAll((0..circulation).map { Coin(ResourceData(0, -1, -1, CapitalResourceType.COINS.id)) })
        ScytheDatabase.instance!!.resourceDao().addResource(*vault.map { it.resourceData }.toTypedArray())
    }

    private fun update(coin: Coin) {
        ScytheDatabase.instance!!.resourceDao().updateResource(coin.resourceData)
    }

    fun withDrawCoin(playerInstance: PlayerInstance): Coin? {
        if(vault.isEmpty()) return null
        val coin = vault.removeAt(0)
        coin.resourceData.owner = playerInstance.playerId
        update(coin)
        return coin
    }

    fun depositCoin(coin: Coin) {
        coin.resourceData.owner = -1
        update(coin)
        vault.add(coin)
    }

    fun coinsRemaining(): Int = vault.size

    companion object {
        private var _currentBank: Bank? = null
        val currentBank: Bank
            get() {
                if(_currentBank  == null) {
                    val coins = ScytheDatabase.instance!!.resourceDao().getResourcesOfType(CapitalResourceType.COINS.id)

                    _currentBank = Bank()
                    if(coins != null && coins.isNotEmpty()) {
                        _currentBank!!.vault.addAll(coins.filter { it.owner == -1 }.map { Coin(it) })
                    } else {
                        _currentBank!!.init()
                    }
                }
                return _currentBank!!
            }
    }
}

class Coin(val resourceData: ResourceData)