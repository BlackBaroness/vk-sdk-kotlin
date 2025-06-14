package io.github.blackbaroness.vk.model.method

import io.github.blackbaroness.vk.VkMethod
import io.github.blackbaroness.vk.model.response.Ok
import io.ktor.http.*
import io.ktor.util.reflect.*

// https://dev.vk.com/ru/method/groups.setLongPollSettings
class GroupsSetLongPollSettings : VkMethod<Ok>() {

    override val name = "groups.getLongPollSettings"
    override val resultTypeInfo = typeInfo<Ok>()
    override val httpMethod = HttpMethod.Post

    var groupId by parameter<Long>("group_id")
    var enabled by parameter<Boolean>("enabled")
    var messageNew by parameter<Boolean>("message_new")
    var messageEvent by parameter<Boolean>("message_event")
}