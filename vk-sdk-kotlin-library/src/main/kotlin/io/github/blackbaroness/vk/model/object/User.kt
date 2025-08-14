package io.github.blackbaroness.vk.model.`object`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id") val id: Long,
    @SerialName("first_name") val firstName: Long,
    @SerialName("last_name") val lastName: Long,
)
