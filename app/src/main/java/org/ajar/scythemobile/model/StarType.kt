package org.ajar.scythemobile.model

enum class StarType : StarModel {
    UPGRADE,
    DEPLOY,
    BUILD,
    ENLIST,
    WORKERS,
    OBJECTIVE,
    POPULARITY,
    POWER,
    COMBAT {
        override val limit: Int = 2
    };

    override val limit: Int = 1
}

interface StarModel {
    val limit: Int
}