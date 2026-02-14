package io.github.blackbaroness.vk.model.`object`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Group(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String
)
