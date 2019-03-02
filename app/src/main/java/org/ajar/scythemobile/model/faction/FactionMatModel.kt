package org.ajar.scythemobile.model.faction

import android.support.annotation.VisibleForTesting
import org.ajar.scythemobile.model.Mat

enum class FactionMat(
        override val matName: String,
        override val heroCharacter: HeroCharacter,
        override val color: Int,
        override val initialPower: Int,
        override val initialCombatCards: Int,
        override val symbol: Int,
        override val matImage: Int
) : FactionMatModel {
    NORDIC("Nordic Kingdoms", CharacterDescription.BJORN, 0x000000FF, 4, 1, 0, 0) {
        override val mechAbilities: Collection<FactionMechAbility> = listOf(
                RiverWalk.FOREST_MOUNTAIN, Seaworthy(), Speed.singleton, Artillery()
        )
        override val factionAbility: FactionAbility
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    },
    SAXONY("Saxony Empire", CharacterDescription.GUNTER, 0x00000000, 1, 4, 0, 0) {
        override val mechAbilities: Collection<FactionMechAbility> = listOf(
                RiverWalk.FOREST_MOUNTAIN, Underpass(), Speed.singleton, Disarm()
        )
        override val factionAbility: FactionAbility
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    },
    POLONIA("Republic of Polonia", CharacterDescription.ANNA, 0x00FFFFFF, 2, 3, 0, 0) {
        override val mechAbilities: Collection<FactionMechAbility> = listOf(
                RiverWalk.VILLAGE_MOUNTAIN, Submerge(), Speed.singleton, Camaraderie()
        )
        override val factionAbility: FactionAbility
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    },
    CRIMEA("Crimean Khanate", CharacterDescription.ZERHA, 0x00FFFF00, 5, 0, 0, 0) {
        override val mechAbilities: Collection<FactionMechAbility> = listOf(
                RiverWalk.FARM_TUNDRA, Wayfare(), Speed.singleton, Scout()
        )
        override val factionAbility: FactionAbility
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    },
    RUSVIET("Rusviet Union", CharacterDescription.OLGA, 0x00FF0000, 3, 2, 0, 0) {
        override val mechAbilities: Collection<FactionMechAbility> = listOf(
                RiverWalk.FARM_VILLAGE, Township(), Speed.singleton, PeoplesArmy()
        )
        override val factionAbility: FactionAbility
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    },
    ALBION("Clan Albion", CharacterDescription.CONNER, 0x0000AA00, 3, 0, 0, 0) {
        override val mechAbilities: Collection<FactionMechAbility> = listOf(
                Burrow(), Rally(), Sword(), Shield()
        )
        override val factionAbility: FactionAbility
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    },
    TOGAWA("Togawa Shogunate", CharacterDescription.AKIKO, 0x00DD00DD, 0, 2, 0, 0) {
        override val mechAbilities: Collection<FactionMechAbility> = listOf(
                Toka(), Shinobi(), Ronin(), Suiton()
        )
        override val factionAbility: FactionAbility
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    }
}

class FactionMatInstance(val model: FactionMatModel) {
    private val abilityMap = mapOf(*model.mechAbilities.map { Pair(it.abilityName, it) }.toTypedArray())

    private val unlockedMechAbility: MutableList<FactionMechAbility> = ArrayList()

    fun getMovementAbilities() : List<MovementRule> {
        return unlockedMechAbility.filter { it is MovementRule }.map { it as MovementRule }
    }

    fun getCombatAbilities() : List<CombatRule> {
        return unlockedMechAbility.filter { it is CombatRule }.map { it as CombatRule }
    }

    fun unlockMechAbility(name: String) {
        abilityMap[name]?.also { unlockedMechAbility.add(it) }
    }
}

interface FactionMatModel : Mat {
    val symbol: Int

    val heroCharacter: HeroCharacter
    val color: Int
    val initialPower: Int
    val initialCombatCards: Int

    val mechAbilities: Collection<FactionMechAbility>
    val factionAbility: FactionAbility
}