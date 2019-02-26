package org.ajar.scythemobile.model.entity

import org.ajar.scythemobile.model.production.Resource

enum class UnitType {
    CHARACTER,
    MECH,
    TRAP,
    FLAG,
    WORKER,
    AIRSHIP
}

interface GameUnit {

    val controllingPlayer: Player
    val type: UnitType

    val heldResources: ArrayList<Resource>?
}