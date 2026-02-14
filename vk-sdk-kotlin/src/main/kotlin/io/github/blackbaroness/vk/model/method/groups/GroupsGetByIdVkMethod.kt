package io.github.blackbaroness.vk.model.method.groups

import io.github.blackbaroness.vk.VkMethod
import io.github.blackbaroness.vk.model.`object`.Group
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://dev.vk.com/ru/method/groups.getById
class GroupsGetByIdVkMethod : VkMethod<GroupsGetByIdVkMethod.Result>() {

    override val name = "groups.getById"
    override val resultSerializer = Result.serializer()
    override val httpMethod = HttpMethod.Get
    override val resultStyle = ResultStyle.WRAPPED_IN_RESPONSE

    @Serializable
    data class Result(
        @SerialName("groups") val groups: List<Group>
    )
}
