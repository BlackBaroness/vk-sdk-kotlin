package io.github.blackbaroness.vk.model.method.messages

import io.github.blackbaroness.vk.VkMethod
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://dev.vk.com/ru/method/messages.send
class MessagesSendVkMethod : VkMethod<MessagesSendVkMethod.Result>() {

    override val name = "messages.send"
    override val resultSerializer = Result.serializer()
    override val httpMethod = HttpMethod.Post
    override val resultStyle = ResultStyle.OPTIONAL_ERROR_FIELD

    var userId by parameter<Long>("user_id")
    var randomId by parameter<Int>("random_id")

    var peerId by parameter<Long>("peer_id")
    var message by parameter<String>("message")
    var replyTo by parameter<Long>("reply_to")
    var keyboard by parameterKeyboard("keyboard")

    init {
        randomId = 0
    }

    @Serializable
    data class Result(
        @SerialName("response") val messageId: Long,
    )
}
