package org.ajar.scythemobile

import android.content.Context
import org.ajar.scythemobile.data.PlayerData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.SettingData
import org.ajar.scythemobile.model.CapitalResourceType
import org.ajar.scythemobile.model.NaturalResourceType
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.map.EncounterDeck
import org.ajar.scythemobile.model.objective.Objective
import org.ajar.scythemobile.ui.control.MapFilterTab
import java.lang.NumberFormatException

object ScytheMobile {

    private var _gameType: GameType? = null
    val gameType: GameType?
        get() {
            if(_gameType == null) {
                _gameType = GameType.loadSetting()
            }
            return _gameType
        }

    enum class GameType(private val id: Int) {
        PBE(0),
        HOT_SEAT(1);

        override fun toString(): String {
            return id.toString()
        }

        internal fun saveSetting() {
            ScytheDatabase.settingDao()?.addSetting(SettingData(GAME_TYPE_SETTING, toString()))
        }

        companion object {
            const val GAME_TYPE_SETTING = "playType"
            private fun fromString(string: String): GameType? {
                return values().firstOrNull { it.id == try {
                    string.toInt()
                } catch (_: NumberFormatException) { -1} }
            }

            fun loadSetting(): GameType? {
                return ScytheDatabase.settingDao()?.getValue(GAME_TYPE_SETTING)?.let { fromString(it) }
            }

            fun clearSetting() {
                with(ScytheDatabase.settingDao()) {
                    this?.getSetting(GAME_TYPE_SETTING)?.also { this.deleteSetting(it) }
                }
            }
        }
    }

    fun loadDatabase(context: Context) {
        ScytheDatabase.init(context)
    }

    fun loadLocalizedNames(context: Context) {
        Objective.load(context)
        CapitalResourceType.loadNames(context)
        NaturalResourceType.loadNames(context)
        EncounterDeck.loadDescs(context)
        MapFilterTab.populateNames(context.resources)
    }

    fun clearGameData() {
        ScytheDatabase.reset()
        GameType.clearSetting()
    }

    fun startGame(gameType: GameType) {
        _gameType = gameType.also { it.saveSetting() }
    }
}