package org.ajar.scythemobile.model.production

class Resource(val type: ResourceType)

enum class ResourceType {
    WOOD,
    FOOD,
    METAL,
    OIL,
    WORKER
}