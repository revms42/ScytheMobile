package org.ajar.scythemobile.data

import androidx.lifecycle.LiveData

class PlayerTestDataDAO : PlayerDataDAO {
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removePlayer(vararg setting: PlayerData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updatePlayer(vararg setting: PlayerData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}