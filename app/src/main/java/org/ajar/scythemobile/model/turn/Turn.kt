package org.ajar.scythemobile.model.turn

import org.ajar.scythemobile.model.entity.Player

interface TurnAction {

    fun serialize(): String
    fun deserialize(from: String)

    fun applyTurnAction()
}

class Turn(val player: Player) {
    private val actionsThisTurn = ArrayList<TurnAction>()

    fun performAction(action: TurnAction) {
        actionsThisTurn.add(action)
    }

    fun <T: TurnAction> findActionOfType(clazz: Class<out T>) : List<T> {
        return actionsThisTurn.filter { it.javaClass == clazz } as List<T>
    }

    fun <T: TurnAction> findFirstOfType(clazz: Class<out TurnAction>) : T? {
        return actionsThisTurn.firstOrNull { it.javaClass == clazz } as T?
    }

    fun hasAnyOfTypes(classes: Collection<Class<out TurnAction>>) : Boolean {
        return actionsThisTurn.any { classes.contains(it::class.java) }
    }

    fun checkIfActionTypePerformed(clazz: Class<out TurnAction>) : Boolean {
        return findFirstOfType<TurnAction>(clazz) != null
    }

    fun removeAction(action: TurnAction) {
        actionsThisTurn.remove(action)
    }

    fun removeLastActionOfType(clazz: Class<out TurnAction>) {
        val lastAction = findActionOfType(clazz)

        if(lastAction.isNotEmpty()) {
            actionsThisTurn.remove(lastAction.get(lastAction.size -1))
        }
    }

    fun replayTurn() {
        TODO("Apply all the things done in this turn to a new game object")
    }

    fun finalizeTurn(): List<String> {
        return actionsThisTurn.map { it.serialize() }
    }

    fun serialize(): String {
        TODO("Serialization")
    }

    companion object {
        fun deserialize(string: String) : Turn {
            TODO("Deserialization")
        }
    }
}