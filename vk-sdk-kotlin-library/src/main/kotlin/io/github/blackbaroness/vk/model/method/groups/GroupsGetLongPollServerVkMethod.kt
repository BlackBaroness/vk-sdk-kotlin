package io.github.blackbaroness.vk.model.method.groups

import io.github.blackbaroness.vk.VkMethod
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://dev.vk.com/ru/method/groups.getLongPollServer
class GroupsGetLongPollServerVkMethod : VkMethod<GroupsGetLongPollServerVkMethod.Result>() {

    override val name = "groups.getLongPollServer"
    override val resultSerializer = Result.serializer()
    override val httpMethod = HttpMethod.Get

    var groupId by parameter<Long>("group_id")

    @Serializable
    data class Result(
        @SerialName("key") val key: String,
        @SerialName("server") val server: String,
        @SerialName("ts") val ts: String,
    )
}
