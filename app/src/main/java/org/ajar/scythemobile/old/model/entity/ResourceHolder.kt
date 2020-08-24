package org.ajar.scythemobile.old.model.entity

import org.ajar.scythemobile.old.model.production.MapResource

interface ResourceHolder {
    val heldMapResources: MutableList<MapResource>
}