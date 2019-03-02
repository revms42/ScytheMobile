package org.ajar.scythemobile.model

enum class PredefinedBinaryChoice(
        private val defaultMessage: String,
        private val defaultImage: Int = -1,
        private val defaultAffirmative: String = "Yes",
        private val defaultNegative: String = "No"
) : BinaryChoice {
    USE_ARTILLERY("Pay 1 power to give your opponent -2 power?");

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

    fun <T> requestChoice(choice: Choice, choices: List<T> ) : T
    fun <T> requestSelection(choice: Choice, choices: List<T>) : List<T>
    fun requestBinaryChoice(binaryChoice: BinaryChoice) : Boolean

    companion object {
        var requestor: RequestsUserInput? = null

        fun <T> requestChoice(choice: Choice, choices: List<T>) = requestor!!.requestChoice(choice, choices)
        fun requestBinaryChoice(binaryChoice: BinaryChoice) = requestor!!.requestBinaryChoice(binaryChoice)
        fun <T> requestSelection(choice: Choice, choices: List<T>) = requestor!!.requestSelection(choice, choices)
    }
}