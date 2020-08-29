package org.ajar.scythemobile.model.factory

import android.util.SparseArray
import androidx.core.util.set

interface FactoryCard {

    companion object {
        private val factoryCards = SparseArray<FactoryCard>()

        operator fun set(id: Int, mat: FactoryCard) {
            factoryCards[id] = mat
        }

        operator fun get(id: Int): FactoryCard? = factoryCards[id]
    }
}