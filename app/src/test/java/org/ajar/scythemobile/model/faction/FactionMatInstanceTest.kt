package org.ajar.scythemobile.model.faction

import org.ajar.scythemobile.model.entity.UnitType
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FactionMatInstanceTest {

    private lateinit var factionMat: FactionMatInstance

    @Before
    fun setup() {
        factionMat = FactionMatInstance(FactionMat.CRIMEA)
    }

    @Test
    fun testUnlockMechAbilities() {
        val abilities= factionMat.model.mechAbilities

        abilities.forEach {
            factionMat.unlockMechAbility(it.abilityName)

            when {
                CombatRule::class.java.isAssignableFrom(it::class.java) -> assertTrue("${it.abilityName} did not unlock!", factionMat.getCombatAbilities().contains(it))
                MovementRule::class.java::class.java.isAssignableFrom(it::class.java) -> {
                    assertTrue("${it.abilityName} did not unlock!", factionMat.getMovementAbilities(UnitType.CHARACTER).contains(it))
                    assertTrue("${it.abilityName} did not unlock!", factionMat.getMovementAbilities(UnitType.MECH).contains(it))
                }
                else -> assertTrue("${it.abilityName} did not unlock!", factionMat.unlockedMechAbility.contains(it))
            }
        }
    }
}