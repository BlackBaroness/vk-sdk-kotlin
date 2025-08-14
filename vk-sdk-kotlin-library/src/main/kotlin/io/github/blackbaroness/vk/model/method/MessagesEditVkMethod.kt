package io.github.blackbaroness.vk.model.method

import io.github.blackbaroness.vk.VkMethod
import io.github.blackbaroness.vk.model.response.Ok
import io.ktor.http.*

// https://dev.vk.com/ru/method/messages.edit
class MessagesEditVkMethod : VkMethod<Ok>() {

    override val name = "messages.edit"
    override val resultSerializer = Ok.serializer()
    override val httpMethod = HttpMethod.Post

    var peerId by parameter<Long>("peer_id")
    var message by parameter<String>("message")

    var groupId by parameter<Long>("group_id")
    var messageId by parameter<Long>("message_id")
    var keyboard by parameterKeyboard("keyboard")
}
