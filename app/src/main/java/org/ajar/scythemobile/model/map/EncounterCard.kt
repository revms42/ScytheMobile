package org.ajar.scythemobile.model.map

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import org.ajar.scythemobile.CapitalResourceType
import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.R
import org.ajar.scythemobile.Resource
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.action.ScytheAction
import org.ajar.scythemobile.model.entity.GameUnit
import org.ajar.scythemobile.model.player.Bank

interface EncounterOutcome {
    var title: String
    var description: String

    fun applyOutcome(activity: Activity, unit: GameUnit)
    fun canMeetCost(player: PlayerInstance) : Boolean
}

sealed class EncounterAction {
    class GiveResource(private val resourceType: Resource, private val resourceCount: Int) : EncounterAction() {
        override fun performAction(activity: Activity, unit: GameUnit) {
            when(resourceType) {
                is NaturalResourceType -> ScytheAction.GiveNaturalResource(unit.pos, resourceType, resourceCount).perform()
                is CapitalResourceType -> ScytheAction.GiveCapitalResourceAction(unit.controllingPlayer, resourceType, resourceCount).perform()
            }
        }
    }
    object DeployMech : EncounterAction() {
        override fun performAction(activity: Activity, unit: GameUnit) {
            activity.findNavController(R.id.nav_encounter).navigate(
                    R.id.action_nav_encounter_to_nav_mech_deploy,
                    createPaidBundle(activity.resources, R.id.action_nav_mech_deploy_to_nav_encounter)
            )
        }
    }
    object DeployWorker : EncounterAction() {
        override fun performAction(activity: Activity, unit: GameUnit) {
            ScytheAction.GiveWorkerAction(unit.pos, unit.controllingPlayer, 1)
        }
    }
    object EnlistRecruit : EncounterAction() {
        override fun performAction(activity: Activity, unit: GameUnit) {
            activity.findNavController(R.id.nav_encounter).navigate(
                    R.id.action_nav_encounter_to_nav_enlist,
                    createPaidBundle(activity.resources, R.id.action_nav_enlist_to_nav_encounter)
            )
        }
    }
    object BuildStructure : EncounterAction() {
        override fun performAction(activity: Activity, unit: GameUnit) {
            activity.findNavController(R.id.nav_encounter).navigate(
                    R.id.action_nav_encounter_to_nav_build,
                    createPaidBundle(activity.resources, R.id.action_nav_build_to_nav_encounter)
            )
        }
    }
    object UpgradeMat : EncounterAction() {
        override fun performAction(activity: Activity, unit: GameUnit) {
            activity.findNavController(R.id.nav_encounter).navigate(
                    R.id.action_nav_encounter_to_nav_upgrade,
                    createPaidBundle(activity.resources, R.id.action_nav_upgrade_to_nav_encounter)
            )
        }
    }
    class ChooseResources(private val count: Int) : EncounterAction() {
        override fun performAction(activity: Activity, unit: GameUnit) {
            val amount = activity.resources.getString(R.string.arg_amount)
            val bundle = bundleOf(amount to count)
            activity.findNavController(R.id.nav_encounter).navigate(
                    R.id.action_nav_encounter_to_nav_choose_resources,
                    bundle
            )
        }
    }
    class LookAtOpponentsCombatCards(private val count: Int) : EncounterAction() {
        override fun performAction(activity: Activity, unit: GameUnit) {
            val amount = activity.resources.getString(R.string.arg_amount)
            val bundle = bundleOf(amount to count)
            activity.findNavController(R.id.nav_encounter).navigate(
                    R.id.action_nav_encounter_to_nav_look_at_cards,
                    bundle
            )
        }
    }
    object HitchRide : EncounterAction() {
        override fun performAction(activity: Activity, unit: GameUnit) {
            activity.findNavController(R.id.nav_encounter).navigate(
                    R.id.action_nav_encounter_to_nav_hitch_ride
            )
        }
    }

    abstract fun performAction(activity: Activity, unit: GameUnit)

    protected fun createPaidBundle(resources: Resources, navId: Int) : Bundle {
        val paid = resources.getString(R.string.arg_cost_paid)
        val returnNav = resources.getString(R.string.arg_return_nav)
        return bundleOf(paid to true, returnNav to navId)
    }
}

private fun loadDesc(array: Array<String>, target: Array<out EncounterOutcome>) {
    array.forEachIndexed {
        index, s ->
        val parts = s.split(":")
        with(target[index]) {
            this.title = parts[0]
            this.description = parts[1]
        }
    }
}

sealed class StandardOutcome(override var title: String, override var description: String, private vararg val encounterAction: EncounterAction): EncounterOutcome {
    class Popular(title: String, description: String, private val popularityGain: Int, vararg encounterAction: EncounterAction) : StandardOutcome(title, description, *encounterAction) {
        override fun applyOutcome(activity: Activity, unit: GameUnit) {
            unit.controllingPlayer.popularity += popularityGain
            super.applyOutcome(activity, unit)
        }

        override fun canMeetCost(player: PlayerInstance): Boolean = true

        companion object {
            private val all = arrayOf(
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.COINS, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.CARDS, 1)),
                    Popular("","", 1, EncounterAction.GiveResource(NaturalResourceType.FOOD, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(NaturalResourceType.METAL, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(NaturalResourceType.FOOD, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.COINS, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(NaturalResourceType.OIL, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.COINS, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(NaturalResourceType.OIL, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(NaturalResourceType.OIL, 2)),

                    Popular("","", 1, EncounterAction.GiveResource(NaturalResourceType.FOOD, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(NaturalResourceType.WOOD, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.COINS, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(NaturalResourceType.FOOD, 2)),
                    Popular("","", 2),
                    Popular("","", 1, EncounterAction.GiveResource(NaturalResourceType.FOOD, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.CARDS, 1)),
                    Popular("","", 1, EncounterAction.GiveResource(NaturalResourceType.FOOD, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.COINS, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.CARDS, 1)),

                    Popular("","", 2),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.COINS, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.COINS, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.CARDS, 1)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.POWER, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.POWER, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.COINS, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.COINS, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.COINS, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.POWER, 2)),

                    Popular("","", 1, EncounterAction.GiveResource(NaturalResourceType.WOOD, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.COINS, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.COINS, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.COINS, 2)),
                    Popular("","", 2),
                    Popular("","", 1, EncounterAction.GiveResource(NaturalResourceType.OIL, 2)),
                    Popular("","", 2),
                    Popular("","", 2),
                    Popular("","", 1, EncounterAction.GiveResource(NaturalResourceType.FOOD, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.CARDS, 1)),

                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.POWER, 2)),
                    Popular("","", 1, EncounterAction.GiveResource(CapitalResourceType.COINS, 2))
            )

            operator fun get(id: Int): Popular = all[id]

            fun loadDesc(context: Context) = loadDesc(context.resources.getStringArray(R.array.encounter_popular_option), all)
        }
    }
    class Commercial(title: String, description: String, private val coinCost: Int, vararg encounterAction: EncounterAction) : StandardOutcome(title, description, *encounterAction) {
        override fun applyOutcome(activity: Activity, unit: GameUnit) {
            Bank.removeCoins(unit.controllingPlayer.playerData, coinCost)
            super.applyOutcome(activity, unit)
        }

        override fun canMeetCost(player: PlayerInstance): Boolean {
           return player.coins >= coinCost
        }

        companion object {
            private val all = arrayOf(
                    Commercial("","", 2, EncounterAction.GiveResource(NaturalResourceType.FOOD, 4)),
                    Commercial("","", 2, EncounterAction.GiveResource(NaturalResourceType.WOOD, 4)),
                    Commercial("","", 2, EncounterAction.GiveResource(NaturalResourceType.FOOD, 3), EncounterAction.DeployWorker),
                    Commercial("","", 3, EncounterAction.EnlistRecruit),
                    Commercial("","", 4, EncounterAction.DeployMech),
                    Commercial("","", 3, EncounterAction.EnlistRecruit),
                    Commercial("","", 2, EncounterAction.UpgradeMat),
                    Commercial("","", 2, EncounterAction.GiveResource(NaturalResourceType.OIL, 4)),
                    Commercial("","", 3, EncounterAction.BuildStructure),
                    Commercial("","", 4, EncounterAction.DeployMech),

                    Commercial("","", 2, EncounterAction.GiveResource(NaturalResourceType.FOOD, 3), EncounterAction.DeployWorker),
                    Commercial("","", 3, EncounterAction.BuildStructure),
                    Commercial("","", 2, EncounterAction.GiveResource(CapitalResourceType.POWER, 2), EncounterAction.GiveResource(CapitalResourceType.POPULARITY, 2)),
                    Commercial("","", 2, EncounterAction.ChooseResources(3)),
                    Commercial("","", 2, EncounterAction.ChooseResources(3)),
                    Commercial("","", 2, EncounterAction.ChooseResources(2), EncounterAction.DeployWorker),
                    Commercial("","", 2, EncounterAction.GiveResource(CapitalResourceType.POWER, 2), EncounterAction.GiveResource(CapitalResourceType.CARDS, 2)),
                    Commercial("","", 2, EncounterAction.ChooseResources(3)),
                    Commercial("","", 2, EncounterAction.GiveResource(NaturalResourceType.FOOD, 4)),
                    Commercial("","", 2, EncounterAction.GiveResource(CapitalResourceType.POPULARITY, 3)),

                    Commercial("","", 4, EncounterAction.DeployMech),
                    Commercial("","", 2, EncounterAction.GiveResource(NaturalResourceType.METAL, 4)),
                    Commercial("","", 3, EncounterAction.EnlistRecruit),
                    Commercial("","", 2, EncounterAction.ChooseResources(3)),
                    Commercial("","", 2, EncounterAction.GiveResource(CapitalResourceType.POWER, 4)),
                    Commercial("","", 2, EncounterAction.GiveResource(CapitalResourceType.POPULARITY, 3)),
                    Commercial("","", 2, EncounterAction.GiveResource(NaturalResourceType.METAL, 4)),
                    Commercial("","", 2, EncounterAction.ChooseResources(2), EncounterAction.DeployWorker),
                    Commercial("","", 3, EncounterAction.UpgradeMat),
                    Commercial("","", 2, EncounterAction.GiveResource(CapitalResourceType.POWER, 4)),

                    Commercial("","", 2, EncounterAction.GiveResource(CapitalResourceType.POPULARITY, 3)),
                    Commercial("","", 2, EncounterAction.GiveResource(CapitalResourceType.POWER, 2), EncounterAction.GiveResource(CapitalResourceType.CARDS, 2)),
                    Commercial("","", 2, EncounterAction.GiveResource(CapitalResourceType.POWER, 2), EncounterAction.GiveResource(CapitalResourceType.CARDS, 2)),
                    Commercial("","", 2, EncounterAction.GiveResource(NaturalResourceType.METAL, 4)),
                    Commercial("","", 2, EncounterAction.GiveResource(NaturalResourceType.WOOD, 4)),
                    Commercial("","", 4, EncounterAction.DeployMech),
                    Commercial("","", 2, EncounterAction.GiveResource(NaturalResourceType.FOOD, 2), EncounterAction.GiveResource(NaturalResourceType.OIL, 2)),
                    Commercial("","", 4, EncounterAction.DeployMech),
                    Commercial("","", 2, EncounterAction.GiveResource(NaturalResourceType.FOOD, 4)),
                    Commercial("","", 2, EncounterAction.GiveResource(CapitalResourceType.POPULARITY, 3)),

                    Commercial("","", 2, EncounterAction.GiveResource(CapitalResourceType.CARDS, 4)),
                    Commercial("","", 2, EncounterAction.HitchRide
                )
            )

            operator fun get(id: Int): Commercial = all[id]

            fun loadDesc(context: Context) = loadDesc(context.resources.getStringArray(R.array.encounter_commercial_option), all)
        }
    }
    class Unpopular(title: String, description: String, private val popCost: Int, vararg encounterAction: EncounterAction) : StandardOutcome(title, description, *encounterAction) {
        override fun applyOutcome(activity: Activity, unit: GameUnit) {
            unit.controllingPlayer.popularity -= popCost
            super.applyOutcome(activity, unit)
        }

        override fun canMeetCost(player: PlayerInstance): Boolean {
            return player.popularity >= popCost
        }

        companion object {
            private val all = arrayOf(
                    Unpopular("","",2, EncounterAction.BuildStructure),
                    Unpopular("","",2, EncounterAction.EnlistRecruit),
                    Unpopular("","",3, EncounterAction.DeployMech),
                    Unpopular("","",2, EncounterAction.GiveResource(NaturalResourceType.METAL, 4)),
                    Unpopular("","",2, EncounterAction.GiveResource(NaturalResourceType.FOOD, 2), EncounterAction.GiveResource(NaturalResourceType.METAL, 2)),
                    Unpopular("","",2, EncounterAction.GiveResource(NaturalResourceType.FOOD, 4)),
                    Unpopular("","",2, EncounterAction.GiveResource(NaturalResourceType.OIL, 4)),
                    Unpopular("","",2, EncounterAction.EnlistRecruit),
                    Unpopular("","",2, EncounterAction.GiveResource(NaturalResourceType.OIL, 4)),
                    Unpopular("","",2, EncounterAction.GiveResource(CapitalResourceType.COINS, 2), EncounterAction.ChooseResources(2)),

                    Unpopular("","",2, EncounterAction.BuildStructure),
                    Unpopular("","",2, EncounterAction.GiveResource(NaturalResourceType.FOOD, 3), EncounterAction.DeployWorker),
                    Unpopular("","",3, EncounterAction.ChooseResources(5)),
                    Unpopular("","",2, EncounterAction.GiveResource(NaturalResourceType.METAL, 3), EncounterAction.DeployWorker),
                    Unpopular("","",3, EncounterAction.ChooseResources(5)),
                    Unpopular("","",2, EncounterAction.EnlistRecruit),
                    Unpopular("","",3, EncounterAction.DeployMech),
                    Unpopular("","",2, EncounterAction.GiveResource(NaturalResourceType.WOOD, 3), EncounterAction.DeployWorker),
                    Unpopular("","",2, EncounterAction.GiveResource(CapitalResourceType.CARDS, 2), EncounterAction.GiveResource(CapitalResourceType.POWER, 3)),
                    Unpopular("","",2, EncounterAction.GiveResource(NaturalResourceType.WOOD, 4)),

                    Unpopular("","",2, EncounterAction.GiveResource(CapitalResourceType.CARDS, 2), EncounterAction.GiveResource(CapitalResourceType.POWER, 3)),
                    Unpopular("","",2, EncounterAction.BuildStructure),
                    Unpopular("","",2, EncounterAction.GiveResource(NaturalResourceType.FOOD, 3), EncounterAction.DeployWorker),
                    Unpopular("","",3, EncounterAction.DeployMech),
                    Unpopular("","",2, EncounterAction.UpgradeMat, EncounterAction.ChooseResources(2)),
                    Unpopular("","",2, EncounterAction.BuildStructure),
                    Unpopular("","",3, EncounterAction.DeployMech),
                    Unpopular("","",2, EncounterAction.BuildStructure),
                    Unpopular("","",2, EncounterAction.GiveResource(NaturalResourceType.FOOD, 1), EncounterAction.GiveResource(NaturalResourceType.OIL, 3)),
                    Unpopular("","",2, EncounterAction.BuildStructure),

                    Unpopular("","",2, EncounterAction.GiveResource(CapitalResourceType.CARDS, 3), EncounterAction.LookAtOpponentsCombatCards(1)),
                    Unpopular("","",2, EncounterAction.UpgradeMat, EncounterAction.ChooseResources(2)),
                    Unpopular("","",2, EncounterAction.UpgradeMat, EncounterAction.GiveResource(NaturalResourceType.FOOD, 2)),
                    Unpopular("","",2, EncounterAction.EnlistRecruit),
                    Unpopular("","",2, EncounterAction.GiveResource(NaturalResourceType.OIL, 3), EncounterAction.DeployWorker),
                    Unpopular("","",2, EncounterAction.GiveResource(NaturalResourceType.OIL, 3), EncounterAction.DeployWorker),
                    Unpopular("","",2, EncounterAction.EnlistRecruit),
                    Unpopular("","",2, EncounterAction.GiveResource(NaturalResourceType.FOOD, 4)),
                    Unpopular("","",2, EncounterAction.GiveResource(CapitalResourceType.POWER, 5)),
                    Unpopular("","",2, EncounterAction.BuildStructure),

                    Unpopular("","",3, EncounterAction.EnlistRecruit, EncounterAction.GiveResource(NaturalResourceType.WOOD, 2)),
                    Unpopular("","",3, EncounterAction.ChooseResources(5))
            )

            fun loadDesc(context: Context) = loadDesc(context.resources.getStringArray(R.array.encounter_unpopular_option), all)

            operator fun get(id: Int): Unpopular = all[id]
        }
    }

    override fun applyOutcome(activity: Activity, unit: GameUnit) {
        encounterAction.forEach { it.performAction(activity, unit) }
    }
}

interface EncounterCard {
    val id: Int
    val popularOutcome: EncounterOutcome
    val commercialOutcome: EncounterOutcome
    val unpopularOutcome: EncounterOutcome
}
class StandardEncounter(
        override val id: Int,
        override val popularOutcome: EncounterOutcome,
        override val commercialOutcome: EncounterOutcome,
        override val unpopularOutcome: EncounterOutcome
) : EncounterCard

class EncounterDeck {

    private val deck: List<EncounterCard> = (0..41).map { makeCard(it) }

    fun drawCard() : EncounterCard? {
        return if (deck.isNotEmpty()) deck[((Math.random() * deck.size).toInt())] else null
    }

    companion object {
        private var _currentDeck: EncounterDeck? = null
        val currentDeck: EncounterDeck
            get() {
                if(_currentDeck == null) {
                    //TODO: Need to load the deck localized.
                    _currentDeck = EncounterDeck()
                }
                return _currentDeck!!
            }

        fun loadDescs(context: Context) {
            StandardOutcome.Popular.loadDesc(context)
            StandardOutcome.Commercial.loadDesc(context)
            StandardOutcome.Unpopular.loadDesc(context)
        }
        
        fun getCard(number: Int): EncounterCard = currentDeck.deck[number]

        private fun makeCard(number: Int) : EncounterCard = StandardEncounter(number, StandardOutcome.Popular[number], StandardOutcome.Commercial[number], StandardOutcome.Unpopular[number])
    }
}
