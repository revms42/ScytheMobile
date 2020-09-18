package org.ajar.scythemobile.model.faction

import org.ajar.scythemobile.data.FactionMatData
import org.ajar.scythemobile.model.entity.UnitType
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FactionMatTest {

    private lateinit var factionMat: FactionMatInstance

    @Before
    fun setup() {
        val factionMatData = FactionMatData(StandardFactionMat.CRIMEA.id)
        factionMat = FactionMatInstance(factionMatData)
    }

    @Test
    fun testUnlockMechAbilities() {
        val abilities= factionMat.lockedFactionAbilities

        abilities.forEach {
            factionMat.unlockFactionAbility(it)

            when {
                CombatRule::class.java.isAssignableFrom(it::class.java) -> assertTrue("${it.abilityName} did not unlock!", factionMat.getCombatAbilities().contains(it))
                MovementRule::class.java::class.java.isAssignableFrom(it::class.java) -> {
                    assertTrue("${it.abilityName} did not unlock!", factionMat.getMovementAbilities(UnitType.CHARACTER).contains(it))
                    assertTrue("${it.abilityName} did not unlock!", factionMat.getMovementAbilities(UnitType.MECH).contains(it))
                }
                else -> assertTrue("${it.abilityName} did not unlock!", factionMat.unlockedFactionAbilities.contains(it))
            }
        }
    }
}