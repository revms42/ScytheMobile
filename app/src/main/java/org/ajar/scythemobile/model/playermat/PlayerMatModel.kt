package org.ajar.scythemobile.model.playermat

import org.ajar.scythemobile.model.Mat

enum class PlayerMat(
        override val matName: String,
        override val matImage: Int,
        override val initialPopularity: Int,
        override val initialCoins: Int,
        override val initialObjectives: Int) : PlayerMatModel
{
    MECHANICAL("Mechanical", 0, 3, 6, 2);
}

interface PlayerMatModel : Mat {
    val initialPopularity: Int
    val initialCoins: Int
    val initialObjectives: Int
}
