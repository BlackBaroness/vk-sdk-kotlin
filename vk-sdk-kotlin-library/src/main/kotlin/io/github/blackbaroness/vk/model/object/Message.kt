package io.github.blackbaroness.vk.model.`object`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Long,
    val date: Long,
    @SerialName("peer_id") val peerId: Long,
    @SerialName("from_id") val fromId: Long,
    val message: String,
)
