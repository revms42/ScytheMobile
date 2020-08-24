package org.ajar.scythemobile

import android.content.Context
import org.ajar.scythemobile.old.model.objective.Objective

object ScytheMoble {

    fun loadLocalizedNames(context: Context) {
        Objective.load(context)
        CapitalResourceType.loadNames(context)
        NaturalResourceType.loadNames(context)
    }
}