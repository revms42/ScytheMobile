package org.ajar.scythemobile.model.faction

enum class FactionAbility(val abilityName: String, val description: String) {
    MEANDER(
            "Meander",
            "Pick up to 2 options per encounter card."
    ),
    SWIM(
            "Swim",
            "Your workers may move across rivers."
    ),
    COERCION(
            "Coercion NYI",
            "Once per turn, you may spend 1 combat card as if it were any 1 resource token."
    ),
    RELENTLESS(
            "Relentless",
            "You may choose the same section on your Player Mat as the previous turn(s)."
    ),
    DOMINATE(
            "Dominate",
            "There is no limit to the number of stars you can place from completing objectives or winning combat."
    ),
    EXALT(
            "Exalt",
            "After ending your character’s movement, the Albion player may place a Flag token from their supply on the character’s territory."
    ),
    MAIFUKU(
            "Maifuku",
            "After ending your " +
                    "character’s movement, the Togawa player may place " +
                    "an armed Trap token of their choice from " +
                    "their supply onto the character’s territory."
    );
}
