package org.ajar.scythemobile.model.faction

import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.map.MapHex

data class DestinationSelection (val mapHex: MapHex, val movementRule: MovementRule, val ridingUnits: List<GameUnit>)