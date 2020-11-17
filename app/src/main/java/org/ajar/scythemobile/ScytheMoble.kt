package org.ajar.scythemobile

import android.content.Context
import org.ajar.scythemobile.model.map.EncounterDeck
import org.ajar.scythemobile.model.objective.Objective
import org.ajar.scythemobile.ui.control.MapFilterTab

object ScytheMoble {

    fun loadLocalizedNames(context: Context) {
        Objective.load(context)
        CapitalResourceType.loadNames(context)
        NaturalResourceType.loadNames(context)
        EncounterDeck.loadDescs(context)
        MapFilterTab.populateNames(context.resources)
    }
}