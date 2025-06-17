package io.github.blackbaroness.vk.model.method

import io.github.blackbaroness.vk.VkMethod
import io.github.blackbaroness.vk.model.response.Ok
import io.ktor.http.*

// https://dev.vk.com/ru/method/groups.setLongPollSettings
class GroupsSetLongPollSettings : VkMethod<Ok>() {

    override val name = "groups.getLongPollSettings"
    override val resultSerializer = Ok.serializer()
    override val httpMethod = HttpMethod.Post

    var groupId by parameter<Long>("group_id")
    var enabled by parameter<Int>("enabled")
    var messageNew by parameter<Int>("message_new")
    var messageEvent by parameter<Int>("message_event")
}
