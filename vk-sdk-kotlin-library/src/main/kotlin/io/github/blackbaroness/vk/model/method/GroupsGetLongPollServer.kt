package io.github.blackbaroness.vk.model.method

import io.github.blackbaroness.vk.VkMethod
import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlinx.serialization.Serializable

// https://dev.vk.com/ru/method/groups.getLongPollServer
class GroupsGetLongPollServer : VkMethod<GroupsGetLongPollServer.Result>() {

    override val name = "groups.getLongPollServer"
    override val resultTypeInfo = typeInfo<Result>()
    override val httpMethod = HttpMethod.Get

    var groupId by parameter<Long>("group_id")

    @Serializable
    data class Result(
        val key: String,
        val server: String,
        val ts: String,
    )
}