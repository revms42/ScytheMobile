package org.ajar.scythemobile.model.faction

enum class CharacterDescription(override val characterName: String) : HeroCharacter {
    BJORN("Bjorn & Mox"),
    GUNTER("Gunter, Nacht, & Tag"),
    ANNA("Anna & Wojtek"),
    ZERHA("Zerha & Kar"),
    OLGA("Olga & Changa"),
    CONNER("Conner & Max"),
    AKIKO("Akiko & Jiro")
}

interface HeroCharacter {
    val characterName: String
}
