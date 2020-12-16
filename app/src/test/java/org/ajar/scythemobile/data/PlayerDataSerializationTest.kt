package org.ajar.scythemobile.data

import org.ajar.scythemobile.data.VersionedTest.Companion.isIdentical
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.faction.StandardFactionMat
import org.ajar.scythemobile.model.objective.ObjectiveCardDeck
import org.ajar.scythemobile.model.player.StandardPlayerMat
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class PlayerDataSerializationTest(private val playerData: PlayerData) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): List<PlayerData> {
            ScytheTestDatabase.setTestingDatabase()
            var int = 0
            return StandardPlayerMat.values().flatMap { playerMat ->
                StandardFactionMat.values().map { factionMat ->
                    ObjectiveCardDeck.resetDeck()
                    PlayerInstance.makePlayer("Player ${int++}", playerMat.id, factionMat.id).playerData
                }
            }
        }

        fun PlayerData.isPlayerIdentical(other: PlayerData) : Boolean {
            return listOf(
                    this.id == other.id,
                    this.name == other.name,
                    this.coins == other.coins,
                    this.power == other.power,
                    this.popularity == other.popularity,
                    this.objectiveOne == other.objectiveOne,
                    this.objectiveTwo == other.objectiveTwo,
                    this.starUpgrades == other.starUpgrades,
                    this.starMechs == other.starMechs,
                    this.starStructures == other.starStructures,
                    this.starRecruits == other.starRecruits,
                    this.starWorkers == other.starWorkers,
                    this.starObjectives == other.starObjectives,
                    this.starCombat == other.starCombat,
                    this.starPopularity == other.starPopularity,
                    this.starPower == other.starPower,
                    this.flagRetreat == other.flagRetreat,
                    this.flagCoercion == other.flagCoercion,
                    this.flagToka == other.flagToka,
                    this.factoryCard == other.factoryCard,
                    this.version == other.version,
                    this.playerMat.let { mat ->
                        listOf(
                                mat.bolsterSection.cardsGain == other.playerMat.bolsterSection.cardsGain,
                                mat.bolsterSection.powerGain == other.playerMat.bolsterSection.powerGain,
                                mat.moveGainSection.coinsGained == other.playerMat.moveGainSection.coinsGained,
                                mat.moveGainSection.unitsMoved == other.playerMat.moveGainSection.unitsMoved,
                                mat.tradeSection.popularityGain == other.playerMat.tradeSection.popularityGain,
                                mat.tradeSection.resourceGain == other.playerMat.tradeSection.resourceGain,
                                mat.enlistSection.enlisted == other.playerMat.enlistSection.enlisted,
                                mat.enlistSection.foodCost == other.playerMat.enlistSection.foodCost,
                                mat.buildSection.enlisted == other.playerMat.buildSection.enlisted,
                                mat.buildSection.woodCost == other.playerMat.buildSection.woodCost,
                                mat.deploySection.enlisted == other.playerMat.deploySection.enlisted,
                                mat.deploySection.metalCost == other.playerMat.deploySection.metalCost,
                                mat.upgradeSection.enlisted == other.playerMat.upgradeSection.enlisted,
                                mat.upgradeSection.oilCost == other.playerMat.upgradeSection.oilCost,
                                mat.produceSection.territories == other.playerMat.produceSection.territories,
                                mat.lastSection == other.playerMat.lastSection,
                                mat.matId == other.playerMat.matId
                        ).all { it }
                    },
                    this.factionMat.let { mat ->
                        listOf(
                                mat.enlistCards == other.factionMat.enlistCards,
                                mat.enlistPop == other.factionMat.enlistPop,
                                mat.enlistCoins == other.factionMat.enlistCoins,
                                mat.enlistPower == other.factionMat.enlistPower,
                                mat.upgradeOne == other.factionMat.upgradeOne,
                                mat.upgradeTwo == other.factionMat.upgradeOne,
                                mat.upgradeThree == other.factionMat.upgradeThree,
                                mat.upgradeFour == other.factionMat.upgradeFour,
                                mat.matId == other.factionMat.matId
                        ).all { it }
                    }
            ).all { it }
        }
    }

    @Test
    fun testSerializePlayer() {
        val serialized = playerData.toString()

        var deserialized = PlayerData.fromString(serialized)!!

        assertTrue("MapHexData does not match: $playerData vs. $deserialized", playerData.isPlayerIdentical(deserialized))

        val short = playerData.toStringCompressed()

        deserialized = PlayerData.fromString(short)!!

        assertTrue("Shortened description does not match: $playerData vs. $deserialized", playerData.isIdentical(deserialized))
    }
}