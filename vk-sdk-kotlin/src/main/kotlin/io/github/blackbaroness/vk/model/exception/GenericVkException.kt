package io.github.blackbaroness.vk.model.exception

class GenericVkException(val code: Int, val originalMessage: String) : Exception() {

    override val message: String
        get() = "$code: $originalMessage"
}
