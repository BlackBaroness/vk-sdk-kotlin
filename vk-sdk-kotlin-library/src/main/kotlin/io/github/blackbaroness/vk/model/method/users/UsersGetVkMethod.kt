package io.github.blackbaroness.vk.model.method.users

import io.github.blackbaroness.vk.VkMethod
import io.github.blackbaroness.vk.model.`object`.User
import io.ktor.http.*
import kotlinx.serialization.serializer

// https://dev.vk.com/ru/method/users.get
class UsersGetVkMethod : VkMethod<List<User>>() {

    override val name = "users.get"
    override val resultSerializer = serializer<List<User>>()
    override val httpMethod = HttpMethod.Get

    var userIds by parameter<String>("user_ids")
}
