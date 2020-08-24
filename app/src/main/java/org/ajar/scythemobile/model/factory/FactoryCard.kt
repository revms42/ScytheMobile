package org.ajar.scythemobile.model.factory

import android.util.SparseArray
import androidx.core.util.set

interface FactoryCard {

    companion object {
        private val factionMats = SparseArray<FactoryCard>()

        operator fun set(id: Int, mat: FactoryCard) {
            factionMats[id] = mat
        }

        operator fun get(id: Int): FactoryCard? = factionMats[id]
    }
}