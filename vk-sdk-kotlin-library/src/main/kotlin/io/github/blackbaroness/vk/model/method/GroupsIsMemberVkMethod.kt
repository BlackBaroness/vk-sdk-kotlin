package io.github.blackbaroness.vk.model.method

import io.github.blackbaroness.vk.VkMethod
import io.github.blackbaroness.vk.model.response.Ok
import io.ktor.http.*

// https://dev.vk.com/ru/method/groups.isMember
class GroupsIsMemberVkMethod : VkMethod<Ok>() {

    override val name = "groups.isMember"
    override val resultSerializer = Ok.serializer()
    override val httpMethod = HttpMethod.Get

    var groupId by parameter<String>("group_id")
    var userId by parameter<Long>("user_id")
}
