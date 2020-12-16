package org.ajar.scythemobile.data

interface Versioned {
    var version: Int

    override fun toString(): String
    fun toStringCompressed(): String

    companion object {
        const val COLUMN_VERSION = "ver"

        private val deserializers = ArrayList<(String) -> Versioned?>()

        fun <A : Versioned> addVersionedDeserializer(deserializer: (String) -> A?) {
            deserializers.add(deserializer)
        }

        fun fromString(str: String): Versioned? {
            return deserializers.map { it(str) }.firstOrNull { it != null }
        }
    }
}