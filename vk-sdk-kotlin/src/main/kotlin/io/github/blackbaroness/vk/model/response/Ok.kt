package io.github.blackbaroness.vk.model.response

import kotlinx.serialization.Serializable

@Serializable
data class Ok(val response: Int) {

    val asBoolean
        get() = when (response) {
            0 -> false
            1 -> true
            else -> throw IllegalStateException("Ok must have 'response' = 0 or 1, but got $response")
        }
}
