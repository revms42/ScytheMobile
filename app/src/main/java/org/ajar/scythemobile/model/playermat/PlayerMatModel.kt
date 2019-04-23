package org.ajar.scythemobile.model.playermat

import org.ajar.scythemobile.model.Mat

class PlayerMatInstance(playerMatModel: PlayerMatModel) {
    val sections: Set<SectionInstance> = playerMatModel.sections.map { SectionInstance(it) }.toSet()
}

interface PlayerMatModel : Mat {
    val initialPopularity: Int
    val initialCoins: Int
    val initialObjectives: Int
    val sections: Set<SectionDef>

}

enum class PlayerMat(
        override val matName: String,
        override val matImage: Int,
        override val initialPopularity: Int,
        override val initialCoins: Int,
        override val initialObjectives: Int = 2) : PlayerMatModel
{
    AGRICULTURAL("Agricultural", -1, 4, 7) {
        override val sections: Set<SectionDef>
            get() = setOf(
                    SectionDef(
                            MoveOrGainAction(),
                            UpgradeAction(startingCost = 2, costBottom = 2, coinsGained = 1)
                    ),
                    SectionDef(
                            TradeAction(),
                            DeployAction(costStarting = 4, costBottom = 2, coinsGained = 0)
                    ),
                    SectionDef(
                            ProduceAction(),
                            BuildAction(costStarting = 4, costBottom = 2, coinsGained = 2)

                    ),
                    SectionDef(
                            BolsterAction(),
                            EnlistAction(costStarting = 3, costBottom = 1, coinsGained = 3)
                    )
            )

    },
    ENGINEERING("Engineering", -1, 2, 5) {
        override val sections: Set<SectionDef>
            get() = setOf(
                    SectionDef(
                            ProduceAction(),
                            UpgradeAction(startingCost = 3, costBottom = 2, coinsGained = 2)
                    ),
                    SectionDef(
                            TradeAction(),
                            DeployAction(costStarting = 4, costBottom = 2, coinsGained = 0)
                    ),
                    SectionDef(
                            BolsterAction(),
                            BuildAction(costStarting = 3, costBottom = 1, coinsGained = 3)

                    ),
                    SectionDef(
                            MoveOrGainAction(),
                            EnlistAction(costStarting = 3, costBottom = 2, coinsGained = 1)
                    )
            )

    },
    INDUSTRIAL("Industrial", -1, 2, 4) {
        override val sections: Set<SectionDef>
            get() = setOf(
                    SectionDef(
                            BolsterAction(),
                            UpgradeAction(startingCost = 3, costBottom = 2, coinsGained = 3)
                    ),
                    SectionDef(
                            ProduceAction(),
                            DeployAction(costStarting = 3, costBottom = 1, coinsGained = 2)
                    ),
                    SectionDef(
                            MoveOrGainAction(),
                            BuildAction(costStarting = 3, costBottom = 2, coinsGained = 1)

                    ),
                    SectionDef(
                            TradeAction(),
                            EnlistAction(costStarting = 4, costBottom = 2, coinsGained = 0)
                    )
            )

    },
    MECHANICAL("Mechanical", -1, 3, 6) {
        override val sections: Set<SectionDef>
            get() = setOf(
                    SectionDef(
                            TradeAction(),
                            UpgradeAction(startingCost = 3, costBottom = 2, coinsGained = 0)
                    ),
                    SectionDef(
                            BolsterAction(),
                            DeployAction(costStarting = 3, costBottom = 1, coinsGained = 2)
                    ),
                    SectionDef(
                            MoveOrGainAction(),
                            BuildAction(costStarting = 3, costBottom = 2, coinsGained = 2)

                    ),
                    SectionDef(
                            ProduceAction(),
                            EnlistAction(costStarting = 4, costBottom = 2, coinsGained = 2)
                    )
            )

    },
    PATRIOTIC("Patriotic", -1, 2, 6) {
        override val sections: Set<SectionDef>
            get() = setOf(
                    SectionDef(
                            MoveOrGainAction(),
                            UpgradeAction(startingCost = 2, costBottom = 2, coinsGained = 1)
                    ),
                    SectionDef(
                            BolsterAction(),
                            DeployAction(costStarting = 4, costBottom = 1, coinsGained = 3)
                    ),
                    SectionDef(
                            TradeAction(),
                            BuildAction(costStarting = 4, costBottom = 2, coinsGained = 0)

                    ),
                    SectionDef(
                            ProduceAction(),
                            EnlistAction(costStarting = 3, costBottom = 2, coinsGained = 0)
                    )
            )

    },
    MILITANT("Militant", -1, 3, 4) {
        override val sections: Set<SectionDef>
            get() = setOf(
                    SectionDef(
                            BolsterAction(),
                            UpgradeAction(startingCost = 3, costBottom = 1, coinsGained = 0)
                    ),
                    SectionDef(
                            MoveOrGainAction(),
                            DeployAction(costStarting = 3, costBottom = 2, coinsGained = 3)
                    ),
                    SectionDef(
                            ProduceAction(),
                            BuildAction(costStarting = 4, costBottom = 3, coinsGained = 1)

                    ),
                    SectionDef(
                            TradeAction(),
                            EnlistAction(costStarting = 3, costBottom = 1, coinsGained = 2)
                    )
            )

    },
    INNOVATIVE("Innovative", -1, 3, 5) {
        override val sections: Set<SectionDef>
            get() = setOf(
                    SectionDef(
                            TradeAction(),
                            UpgradeAction(startingCost = 3, costBottom = 3, coinsGained = 3)
                    ),
                    SectionDef(
                            ProduceAction(),
                            DeployAction(costStarting = 3, costBottom = 2, coinsGained = 1)
                    ),
                    SectionDef(
                            BolsterAction(),
                            BuildAction(costStarting = 4, costBottom = 1, coinsGained = 2)

                    ),
                    SectionDef(
                            MoveOrGainAction(),
                            EnlistAction(costStarting = 3, costBottom = 1, coinsGained = 0)
                    )
            )

    };
}
