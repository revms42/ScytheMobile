package org.ajar.scythemobile.model

import org.ajar.scythemobile.model.map.MapHex
import org.ajar.scythemobile.model.production.Resource
import org.ajar.scythemobile.model.production.ResourceType

enum class PredefinedBinaryChoice(
        private val defaultMessage: String,
        private val defaultImage: Int = -1,
        private val defaultAffirmative: String = "Yes",
        private val defaultNegative: String = "No"
) : BinaryChoice {
    USE_ARTILLERY("Pay 1 power to give your opponent -2 power?"),
    ABORT_MOVEMENT("Do you want to abort this move action?"),
    END_MOVEMENT("Do you want to end your move actions or abort all completed moves this turn?", defaultAffirmative = "Abort All", defaultNegative = "End Move Actions"),
    ABORT_DESTINATION("Is this unit done moving, or do you want to abort this move?", defaultAffirmative = "Done", defaultNegative = "Abort"),
    END_DESTINATION("Moving here will end this unit's movement, continue?"),
    BOLSTER_SELECTION("Bolster for cards or for power?", defaultAffirmative = "Cards", defaultNegative = "Power"),
    MOVE_OR_GAIN_SELECTION("Perform Move or Gain Coints?", defaultAffirmative = "Move", defaultNegative = "Gain");

    private var _negative: String? = null
    override val negative: String
        get() {
            if(_negative == null) {
                _negative = Choice.messageLoader?.loadNegative(this)
            }
            return if(_negative == null) defaultNegative else _negative!!
        }

    private var _affirmative: String? = null
    override val affirmative: String
        get() {
            if (_affirmative == null) {
                _affirmative = Choice.messageLoader?.loadAffirmative(this)
            }
            return if(_affirmative == null) defaultAffirmative else _affirmative!!
        }

    private var _image: Int? = null
    override val image: Int
        get() {
            if (_image == null) {
                _image = Choice.messageLoader?.loadImage(this)
            }
            return if(_image == null) defaultImage else _image!!
        }

    private var _message: String? = null
    override val message: String
        get() {
            if (_message == null) {
                _message = Choice.messageLoader?.loadMessage(this)
            }
            return if(_message == null) defaultMessage else _message!!
        }


}

class MoveUnitChoice(private val defaultMessage: String = "Select a unit to move", private val defaultImage: Int = -1): Choice {

    private var _image: Int? = null
    override val image: Int
        get() {
            if (_image == null) {
                _image = Choice.messageLoader?.loadImage(this)
            }
            return if(_image == null) defaultImage else _image!!
        }

    private var _message: String? = null
    override val message: String
        get() {
            if (_message == null) {
                _message = Choice.messageLoader?.loadMessage(this)
            }
            return if(_message == null) defaultMessage else _message!!
        }

}

class MovementChoice(private val defaultMessage: String = "Select a destination", private val defaultImage: Int = -1): Choice {

    private var _image: Int? = null
    override val image: Int
        get() {
            if (_image == null) {
                _image = Choice.messageLoader?.loadImage(this)
            }
            return if(_image == null) defaultImage else _image!!
        }

    private var _message: String? = null
    override val message: String
        get() {
            if (_message == null) {
                _message = Choice.messageLoader?.loadMessage(this)
            }
            return if(_message == null) defaultMessage else _message!!
        }

}

class MoveWorkersChoice(private val defaultMessage: String = "Select workers to ride", private val defaultImage: Int = -1): Choice {

    private var _image: Int? = null
    override val image: Int
        get() {
            if (_image == null) {
                _image = Choice.messageLoader?.loadImage(this)
            }
            return if(_image == null) defaultImage else _image!!
        }

    private var _message: String? = null
    override val message: String
        get() {
            if (_message == null) {
                _message = Choice.messageLoader?.loadMessage(this)
            }
            return if(_message == null) defaultMessage else _message!!
        }

}

enum class CombatChoice(private val defaultMessage: String, private val defaultImage: Int = -1) : Choice {
    CHOOSE_POWER("Choose Power to spend"),
    CHOOSE_CARDS("Select Combat Cards");

    private var _image: Int? = null
    override val image: Int
        get() {
            if (_image == null) {
                _image = Choice.messageLoader?.loadImage(this)
            }
            return if(_image == null) defaultImage else _image!!
        }

    private var _message: String? = null
    override val message: String
        get() {
            if (_message == null) {
                _message = Choice.messageLoader?.loadMessage(this)
            }
            return if(_message == null) defaultMessage else _message!!
        }
}

class PaymentChoice(private val defaultMessage: String = "Select payment", private val defaultImage: Int = -1): Choice {

    private var _image: Int? = null
    override val image: Int
        get() {
            if (_image == null) {
                _image = Choice.messageLoader?.loadImage(this)
            }
            return if(_image == null) defaultImage else _image!!
        }

    private var _message: String? = null
    override val message: String
        get() {
            if (_message == null) {
                _message = Choice.messageLoader?.loadMessage(this)
            }
            return if(_message == null) defaultMessage else _message!!
        }

}

class RetreatChoice(private val defaultMessage: String = "Choose a hex to retreat to", private val defaultImage: Int = -1): Choice {

    private var _image: Int? = null
    override val image: Int
        get() {
            if (_image == null) {
                _image = Choice.messageLoader?.loadImage(this)
            }
            return if(_image == null) defaultImage else _image!!
        }

    private var _message: String? = null
    override val message: String
        get() {
            if (_message == null) {
                _message = Choice.messageLoader?.loadMessage(this)
            }
            return if(_message == null) defaultMessage else _message!!
        }
}

class MaifukuChoice(private val defaultMessage: String = "Deploy a trap?", private val defaultImage: Int = -1) : Choice {

    private var _image: Int? = null
    override val image: Int
        get() {
            if (_image == null) {
                _image = Choice.messageLoader?.loadImage(this)
            }
            return if(_image == null) defaultImage else _image!!
        }

    private var _message: String? = null
    override val message: String
        get() {
            if (_message == null) {
                _message = Choice.messageLoader?.loadMessage(this)
            }
            return if(_message == null) defaultMessage else _message!!
        }
}

class ExaltChoice(private val defaultMessage: String = "Deploy a flag?", private val defaultImage: Int = -1) : Choice {

    private var _image: Int? = null
    override val image: Int
        get() {
            if (_image == null) {
                _image = Choice.messageLoader?.loadImage(this)
            }
            return if(_image == null) defaultImage else _image!!
        }

    private var _message: String? = null
    override val message: String
        get() {
            if (_message == null) {
                _message = Choice.messageLoader?.loadMessage(this)
            }
            return if(_message == null) defaultMessage else _message!!
        }
}

class EncounterChoice(private val defaultMessage: String = "Encounter", private val defaultImage: Int = -1) : Choice {

    private var _image: Int? = null
    override val image: Int
        get() {
            if (_image == null) {
                _image = Choice.messageLoader?.loadImage(this)
            }
            return if(_image == null) defaultImage else _image!!
        }

    private var _message: String? = null
    override val message: String
        get() {
            if (_message == null) {
                _message = Choice.messageLoader?.loadMessage(this)
            }
            return if(_message == null) defaultMessage else _message!!
        }
}

interface Choice {
    val message: String
    val image: Int

    companion object {
        var messageLoader: MessageLoader? = null
    }
}

interface BinaryChoice : Choice {
    val affirmative: String
    val negative: String
}

interface MessageLoader {
    fun loadMessage(choice: Choice): String
    fun loadAffirmative(choice: Choice): String
    fun loadNegative(choice: Choice): String
    fun loadImage(choice: Choice): Int
}

interface RequestsUserInput {

    fun requestPayment(choice: Choice, cost: List<ResourceType>, choices: Map<Resource, MapHex>) : Collection<Resource>
    fun <T> requestChoice(choice: Choice, choices: Collection<T> ) : T
    fun <T> requestCancellableChoice(choice: Choice, choices: Collection<T>) : T?
    fun <T> requestSelection(choice: Choice, choices: Collection<T>, limit: Int = choices.size) : Collection<T>
    fun requestBinaryChoice(binaryChoice: BinaryChoice) : Boolean

    companion object {
        var requestor: RequestsUserInput? = null

        fun <T> requestCancellableChoice(choice: Choice, choices: Collection<T>) = requestor!!.requestCancellableChoice(choice, choices)
        fun <T> requestChoice(choice: Choice, choices: List<T>) = requestor!!.requestChoice(choice, choices)
        fun requestBinaryChoice(binaryChoice: BinaryChoice) = requestor!!.requestBinaryChoice(binaryChoice)
        fun <T> requestSelection(choice: Choice, choices: List<T>) = requestor!!.requestSelection(choice, choices)
        fun requestPayment(choice: Choice, cost: List<ResourceType>, choices: Map<Resource, MapHex>) = requestor!!.requestPayment(choice, cost, choices)
    }
}