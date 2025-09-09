package io.github.blackbaroness.vk.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VkResponse<T>(
    val response: T? = null,
    val error: Error? = null,
) {

    @Serializable
    data class Error(
        @SerialName("error_code") val code: Int,
        @SerialName("error_msg") val message: String,
    )
}
