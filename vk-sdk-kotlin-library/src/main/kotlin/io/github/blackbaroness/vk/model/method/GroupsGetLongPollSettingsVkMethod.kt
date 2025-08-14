package io.github.blackbaroness.vk.model.method

import io.github.blackbaroness.vk.VkMethod
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://dev.vk.com/ru/method/groups.getLongPollSettings
class GroupsGetLongPollSettingsVkMethod : VkMethod<GroupsGetLongPollSettingsVkMethod.Result>() {

    override val name = "groups.getLongPollSettings"
    override val resultSerializer = Result.serializer()
    override val httpMethod = HttpMethod.Get

    var groupId by parameter<Long>("group_id")

    @Serializable
    data class Result(
        @SerialName("is_enabled") val isEnabled: Boolean,
        @SerialName("events") val events: Map<String, Int>,
    )
}
