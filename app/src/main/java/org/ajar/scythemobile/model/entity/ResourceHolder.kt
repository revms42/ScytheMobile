package org.ajar.scythemobile.model.entity

import org.ajar.scythemobile.model.production.MapResource

interface ResourceHolder {
    val heldMapResources: MutableList<MapResource>
}