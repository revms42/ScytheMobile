package org.ajar.scythemobile.model

interface LimitedUsePerTurn {

    val usesPerTurn: Int
    val usesRemaining: Int

    fun incrimentUses()
    fun decrementUses()

    fun resetUses()
}