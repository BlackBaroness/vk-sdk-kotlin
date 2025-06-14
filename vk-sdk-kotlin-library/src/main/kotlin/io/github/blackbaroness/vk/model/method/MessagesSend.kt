package io.github.blackbaroness.vk.model.method

import io.github.blackbaroness.vk.VkClient
import io.github.blackbaroness.vk.VkMethod
import io.github.blackbaroness.vk.model.`object`.Keyboard
import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlinx.serialization.Serializable

class MessagesSend : VkMethod<MessagesSend.Result>() {

    override val name = "messages.send"
    override val resultTypeInfo = typeInfo<Result>()
    override val httpMethod = HttpMethod.Post

    var userId by parameter<Long>("user_id")
    var randomId by parameter<Int>("random_id")

    var peerId by parameter<Long>("peer_id")
    var message by parameter<String>("message")
    var replyTo by parameter<Long>("reply_to")

    /* not supported since will return a different result
    var peerIds: List<Long>
        get() = (parameters["peer_ids"] as? String)?.split(',')?.map { it.toLong() }.orEmpty()
        set(value) {
            parameters["peer_ids"] = value.joinToString(",")
        }
     */

    var keyboard: Keyboard
        set(value) {
            parameters["keyboard"] = VkClient.json.encodeToString(value)
        }
        get() = VkClient.json.decodeFromString(
            (parameters["keyboard"] ?: error("Keyboard not set or invalid")) as String
        )

    init {
        randomId = 0
    }

    @Serializable
    data class Result(
        val response: Long,
    )
}