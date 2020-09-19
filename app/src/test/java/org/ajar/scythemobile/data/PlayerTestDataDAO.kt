package org.ajar.scythemobile.data

import androidx.lifecycle.LiveData

class PlayerTestDataDAO : PlayerDataDAO {
    private val playerData = ArrayList<PlayerData>()

    override fun getPlayers(): List<PlayerData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observePlayer(name: String): LiveData<PlayerData?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPlayer(name: String): PlayerData? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPlayer(id: Int): PlayerData? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getHighestPower(): Int? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addPlayer(playerData: PlayerData) {
        this.playerData.add(playerData)
    }

    override fun removePlayer(vararg setting: PlayerData) {
        setting.forEach { remove -> this.playerData.removeIf { it.id == remove.id } }
    }

    override fun updatePlayer(vararg setting: PlayerData) {
        setting.forEach { update -> this.playerData.removeIf { it.id == update.id }; this.playerData.add(update) }
    }

}