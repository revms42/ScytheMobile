package org.ajar.scythemobile.model.entity

import org.ajar.scythemobile.model.production.Resource

interface ResourceHolder {
    val heldResources: MutableList<Resource>
}