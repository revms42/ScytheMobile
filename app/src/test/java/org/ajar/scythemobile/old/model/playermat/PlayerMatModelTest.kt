package org.ajar.scythemobile.old.model.playermat

import org.ajar.scythemobile.old.model.TestPlayer
import org.ajar.scythemobile.old.model.TestUnit
import org.ajar.scythemobile.old.model.entity.Player
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.RiverWalk
import org.ajar.scythemobile.old.model.map.*
import org.ajar.scythemobile.old.model.production.PlayerResourceType
import org.ajar.scythemobile.old.model.turn.*
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class PlayerMatModelTest(val mat: PlayerMat) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun getBoards() : Collection<Array<PlayerMat>> = PlayerMat.values().map { arrayOf(it) }
    }

    private lateinit var player: Player

    private val topRow = HashMap<Class<out TopRowAction>, SectionDef>()
    private val bottomRow = HashMap<Class<out BottomRowAction>, SectionDef>()

    @Before
    fun setup() {
        player = TestPlayer()

        mat.sections.forEach {
            topRow[it.topRowAction.javaClass] = it
            bottomRow[it.bottomRowAction.javaClass] = it
        }
    }

    @Test
    fun testMoveAction() {
        player.turn.performAction(makeMoveTurnAction())
        player.newTurn()

        assertNull(player.selectableSections().firstOrNull { it.sectionDef == topRow[MoveOrGainAction::class.java] })
    }

    @Test
    fun testGainAction() {
        player.turn.performAction(makeGainTurnAction())
        player.newTurn()

        assertNull(player.selectableSections().firstOrNull { it.sectionDef == topRow[MoveOrGainAction::class.java] })
    }

    @Test
    fun testProduceAction() {
        player.turn.performAction(makeProduceTurnAction())
        player.newTurn()

        assertNull(player.selectableSections().firstOrNull { it.sectionDef == topRow[ProduceAction::class.java] })
    }

    @Test
    fun testBolsterAction() {
        player.turn.performAction(makeBolsterAction())
        player.newTurn()

        assertNull(player.selectableSections().firstOrNull { it.sectionDef == topRow[BolsterAction::class.java] })
    }

    @Test
    fun testTradeAction() {
        player.turn.performAction(makeTradeAction())
        player.newTurn()

        assertNull(player.selectableSections().firstOrNull { it.sectionDef == topRow[TradeAction::class.java] })
    }

    @Test
    fun testBuildAction() {
        player.turn.performAction(makeBuildAction())
        player.newTurn()

        assertNull(player.selectableSections().firstOrNull { it.sectionDef == bottomRow[BuildAction::class.java] })
    }

    @Test
    fun testDeployAction() {
        player.turn.performAction(makeDeployAction())
        player.newTurn()

        assertNull(player.selectableSections().firstOrNull { it.sectionDef == bottomRow[DeployAction::class.java] })
    }

    @Test
    fun testEnlistAction() {
        player.turn.performAction(makeEnlistAction())
        player.newTurn()

        assertNull(player.selectableSections().firstOrNull { it.sectionDef == bottomRow[EnlistAction::class.java] })
    }

    @Test
    fun testUpgradeAction() {
        player.turn.performAction(makeUpgradeAction())
        player.newTurn()

        assertNull(player.selectableSections().firstOrNull { it.sectionDef == bottomRow[UpgradeAction::class.java] })
    }

    private fun makeMoveTurnAction() : MoveTurnAction {
        return MoveTurnAction(
                TestUnit(player, UnitType.CHARACTER),
                MapHex(
                        MapHexDesc(1, HexNeighbors(), SpecialFeature.ANY)
                ),
                MapHex(
                        MapHexDesc(2, HexNeighbors(), SpecialFeature.ANY)
                ),
                rule = RiverWalk.FARM_VILLAGE
        )
    }

    private fun makeGainTurnAction() : GainTurnAction {
        return GainTurnAction(2)
    }

    private fun makeProduceTurnAction() : ProduceTurnAction {
        return ProduceTurnAction(emptyList())
    }

    private fun makeBolsterAction() : BolsterTurnAction {
        return BolsterTurnAction(2, false)
    }

    private fun makeTradeAction() : TradeTurnAction {
        return TradeTurnAction(emptyList())
    }

    private fun makeBuildAction() : BuildTurnAction {
        return BuildTurnAction(emptyList(), TestUnit(player, UnitType.STRUCTURE), MapHex(MapHexDesc(1, HexNeighbors(), SpecialFeature.ANY)))
    }

    private fun makeDeployAction() : DeployTurnAction {
        return DeployTurnAction(emptyList(), RiverWalk.FARM_VILLAGE)
    }

    private fun makeEnlistAction() : EnlistTurnAction {
        return EnlistTurnAction(emptyList(), EnlistAction::class.java, PlayerResourceType.POPULARITY)
    }

    private fun makeUpgradeAction() : UpgradeTurnAction {
        val boolPart: () -> Boolean = {true}
        val unitPart: () -> Unit = {}
        return UpgradeTurnAction(emptyList(), UpgradeDef("Top", boolPart, unitPart), UpgradeDef("Bottom", boolPart, unitPart))
    }
}