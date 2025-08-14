package io.github.blackbaroness.vk.model.`object`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    @SerialName("id") val id: Long,
    @SerialName("date") val date: Long,
    @SerialName("peer_id") val peerId: Long,
    @SerialName("from_id") val fromId: Long,
    @SerialName("text") val text: String,
)
