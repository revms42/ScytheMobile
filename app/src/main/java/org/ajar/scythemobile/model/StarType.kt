package org.ajar.scythemobile.model

import org.ajar.scythemobile.data.PlayerData
import kotlin.reflect.KMutableProperty1

enum class StarType(private val field: KMutableProperty1<PlayerData, Int>) : StarModel {
    UPGRADE(PlayerData::starUpgrades),
    DEPLOY(PlayerData::starMechs),
    BUILD(PlayerData::starStructures),
    ENLIST(PlayerData::starRecruits),
    WORKERS(PlayerData::starWorkers),
    OBJECTIVE(PlayerData::starObjectives),
    POPULARITY(PlayerData::starPopularity),
    POWER(PlayerData::starPower),
    COMBAT(PlayerData::starCombat) {
        override val limit: Int = 2
    };

    override val limit: Int = 1

    fun apply(data: PlayerData) {
        if(field.get(data) < limit) {
            field.set(data, field.get(data) + 1)
        }
    }
}

interface StarModel {
    val limit: Int
}