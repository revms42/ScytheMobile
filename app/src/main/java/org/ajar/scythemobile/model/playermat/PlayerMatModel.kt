package org.ajar.scythemobile.model.playermat

import org.ajar.scythemobile.model.Mat


class PlayerMatInstance(val playerMatModel: PlayerMatModel) {

    val sections: Set<SectionInstance> = playerMatModel.sections.map { SectionInstance(it) }.toSet()

    var currentSection: SectionInstance? = null
}

class SectionInstance(val sectionDef: SectionDef) {

}

enum class PlayerMat(
        override val matName: String,
        override val matImage: Int,
        override val initialPopularity: Int,
        override val initialCoins: Int,
        override val initialObjectives: Int) : PlayerMatModel
{
    MECHANICAL("Mechanical", 0, 3, 6, 2) {
        override val sections: Set<SectionDef>
            get() = setOf() //TODO: Put stuff here.
    };
}

interface SectionDef {
    //Do Stuff.
}

interface PlayerMatModel : Mat {
    val initialPopularity: Int
    val initialCoins: Int
    val initialObjectives: Int
    val sections: Set<SectionDef>
}
