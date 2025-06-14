package io.github.blackbaroness.vk.model.method

import io.github.blackbaroness.vk.VkMethod
import io.ktor.http.*
import kotlinx.serialization.Serializable

// https://dev.vk.com/ru/method/groups.getLongPollServer
class GroupsGetLongPollServer : VkMethod<GroupsGetLongPollServer.Result>() {

    override val name = "groups.getLongPollServer"
    override val resultSerializer = Result.serializer()
    override val httpMethod = HttpMethod.Get

    var groupId by parameter<Long>("group_id")

    @Serializable
    data class Result(
        val key: String,
        val server: String,
        val ts: String,
    )
}
